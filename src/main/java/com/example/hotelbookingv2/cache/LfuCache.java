package com.example.hotelbookingv2.cache;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class LfuCache<T> extends LfuCacheBase<T> {

    public LfuCache(int capacity) {
        super(capacity);
    }

    @Override
    public T get(String id) {
        return super.get(id);
    }

    @Override
    public void put(String id, T value) {
        super.put(id, value);
    }

    @Override
    public void remove(String id) {
        super.remove(id);
    }

    @Override
    public void clear() {
        super.clear();
    }
}
