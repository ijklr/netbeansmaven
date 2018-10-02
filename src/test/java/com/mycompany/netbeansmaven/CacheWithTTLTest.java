package com.mycompany.netbeansmaven;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;


/**
 *
 * @author scheng
 */
public class CacheWithTTLTest {
    private SlowAPI slowApi;
    static private final String validAnswerFromSlowApi = "validAnswerFromSlowApi";
    static private final String key = "some key";
    static private final String timeout = "timeout";
    @Before
    public void setup() {
            slowApi = (key) ->               
              CompletableFuture.supplyAsync(
                () -> {
            try {
                Thread.sleep(300);
            } catch (InterruptedException ex) {
                Logger.getLogger(CacheWithTTL.class.getName()).log(Level.SEVERE, null, ex);
            }
            return Optional.of(validAnswerFromSlowApi);        
        });
        
    }
    
   
  @Test
  public void noCacheButSlowApiReturnsOnTime() {  
        CacheStorage cacheStorage = new CacheStorage();
       CacheWithTTL cacheWithTTL = new CacheWithTTL(cacheStorage, slowApi, 50, 600);
        CompletionStage<Optional<String>> read = cacheWithTTL.read(key);
        String result = read.toCompletableFuture().join().orElse(timeout);
        assertTrue(result.equals(validAnswerFromSlowApi));
  }

   @Test
  public void noCacheButSlowApiReturnsNotOnTime() throws InterruptedException {
      CacheStorage cacheStorage = new CacheStorage();
      String newKey = "some randome key";
       CompletionStage<Optional<String>> readOpt = cacheStorage.read(newKey);
       //key should not exist
       assertTrue(!readOpt.toCompletableFuture().join().isPresent());
       CacheWithTTL cacheWithTTL = new CacheWithTTL(cacheStorage, slowApi, 20, 100);    
       CompletionStage<Optional<String>> read = cacheWithTTL.read(newKey);
       Optional<String> resultOpt = read.toCompletableFuture().join();
       String result = read.toCompletableFuture().join().orElse(timeout);
       assertTrue(result.equals(timeout));
       Thread.sleep(3000);
       CompletionStage<Optional<String>> readOpt2 = cacheStorage.read(newKey);
       assertTrue(readOpt2.toCompletableFuture().join().get().equals(validAnswerFromSlowApi));
       
     
  }
  
}
