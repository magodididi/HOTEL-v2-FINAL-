package com.example.hotelbookingv2.cache;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LfuCache<T> extends LfuCacheBase<T> {

    public LfuCache(int capacity) {
        super(capacity);
    }
}
