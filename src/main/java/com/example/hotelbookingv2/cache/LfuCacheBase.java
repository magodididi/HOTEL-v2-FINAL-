package com.example.hotelbookingv2.cache;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class LfuCacheBase<T> {

    private final int capacity;
    private final Map<String, Entry<T>> cache = new HashMap<>();

    protected static class Entry<T> {
        T value;
        int frequency;

        Entry(T value) {
            this.value = value;
            this.frequency = 1;
        }
    }

    protected LfuCacheBase(int capacity) {
        this.capacity = capacity;
    }

    public T get(String id) {
        Entry<T> entry = cache.get(id);
        if (entry == null) {
            log.info("❌ LFU Cache: Item NOT found in cache. ID: {}", id);
            return null;
        }

        entry.frequency++;

        log.info("✅ LFU Cache: Item found and retrieved from cache. ID: {},"
                + " Access frequency: {}", id, entry.frequency);

        return entry.value;
    }

    public void put(String id, T value) {
        if (cache.containsKey(id)) {
            Entry<T> entry = cache.get(id);

            entry.value = value;
            entry.frequency++;

            log.info("🔥 LFU Cache: Item updated in cache. ID: {}, New frequency: {}",
                    id, entry.frequency);
        } else {
            if (cache.size() >= capacity) {
                evictLeastFrequentlyUsed();
            }
            cache.put(id, new Entry<>(value));
            log.info("🔥 LFU Cache: New item added to cache. ID: {}", id);
        }
    }

    private void evictLeastFrequentlyUsed() {
        String lfuKey = null;
        int minFrequency = Integer.MAX_VALUE;

        for (Map.Entry<String, Entry<T>> entry : cache.entrySet()) {
            if (entry.getValue().frequency < minFrequency) {
                minFrequency = entry.getValue().frequency;
                lfuKey = entry.getKey();
            }
        }

        if (lfuKey != null) {
            cache.remove(lfuKey);
            log.info("Evicted item from cache. ID: {},"
                    + " Frequency at removal: {}", lfuKey, minFrequency);
        }
    }

    public void remove(String id) {
        if (cache.remove(id) != null) {
            log.info("Item successfully removed from cache. ID: {}", id);
        }
    }

    public void clear() {
        cache.clear();
        log.info("All items have been successfully cleared from the cache.");
    }
}
