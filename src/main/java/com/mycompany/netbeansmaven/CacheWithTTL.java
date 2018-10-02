package com.mycompany.netbeansmaven;

import com.google.common.util.concurrent.ListenableFuture;
import com.spotify.futures.CompletableFuturesExtra;
import com.spotify.futures.FuturesExtra;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author scheng
 */
public class CacheWithTTL {
    private final CacheStorage storage;
    private final SlowAPI slowApi;
    private final long storageTTL;
    private final long slowAPITTL;
     private final ScheduledThreadPoolExecutor executorService 
             = new ScheduledThreadPoolExecutor(1000);


    public CacheWithTTL(final CacheStorage storage, final SlowAPI slowApi, 
            final long storageTTL, 
            final long slowAPITTL) {
        this.storage = storage;
        this.slowApi = slowApi;
        this.storageTTL = storageTTL;
        this.slowAPITTL = slowAPITTL;
    }
    
    public CompletionStage<Optional<String>> read(final String key) {
        CompletableFuture<Optional<String>> storageReadF = 
                makeTimeoutFuture(storage.read(key), storageTTL, 
                        TimeUnit.MILLISECONDS, executorService)
                        .exceptionally(ex -> Optional.empty());
        
        return storageReadF.thenCompose(storageRead ->
                storageRead.isEmpty() ?
                        makeTimeoutFuture(slowApi.read(key), slowAPITTL, 
                        TimeUnit.MILLISECONDS, executorService)
                        .exceptionally(ex -> Optional.empty()) 
                        :
                        CompletableFuture.completedFuture(storageRead)
        );
    }
    
    public static <V extends Object> CompletableFuture<V> makeTimeoutFuture(
            final CompletionStage<V> delegate,
            final long time, TimeUnit unit, 
             final ScheduledExecutorService scheduledExecutor) {
        
        ListenableFuture<V> listenableFuture = 
                CompletableFuturesExtra.toListenableFuture(delegate);
        
        ListenableFuture<V> timeoutFuture = 
                FuturesExtra.makeTimeoutFuture(scheduledExecutor, 
                        listenableFuture, time, unit);
        return CompletableFuturesExtra.toCompletableFuture(timeoutFuture);
    }
    
    public static void main(final String[] args) {
        CacheStorage cacheStorage = new CacheStorage();
        SlowAPI slowApi = (key) -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                Logger.getLogger(CacheWithTTL.class.getName()).log(Level.SEVERE, null, ex);
            }
            return CompletableFuture.completedFuture(Optional.of("found " + key));        
        };
        
        CacheWithTTL cacheWithTTL = new CacheWithTTL(cacheStorage, slowApi, 50, 100);
        CompletionStage<Optional<String>> read = cacheWithTTL.read("some key");
        Optional<String> result = read.toCompletableFuture().join();
        System.out.println("result====" + result.orElse("timedout"));
    }
    
    
}
