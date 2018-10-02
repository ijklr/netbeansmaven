package com.mycompany.netbeansmaven;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 *
 * @author scheng
 */
public class CacheStorage {
    private final Map<String, Optional<String>> storage;
    public CacheStorage() {
        this.storage = new HashMap<>();
    }
    public CompletionStage<Optional<String>> write(final String key, 
            final Optional<String> value) {
     if(key == null) {
         return CompletableFuture.completedFuture(Optional.empty());
     }
     storage.put(key, value);
     return CompletableFuture.completedFuture(value);
    }
    
    public CompletionStage<Optional<String>> read(final String key) {
        return CompletableFuture.completedFuture(
                storage.getOrDefault(key, Optional.empty()));
    }
}
