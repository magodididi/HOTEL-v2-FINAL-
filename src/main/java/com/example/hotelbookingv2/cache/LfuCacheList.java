package com.example.hotelbookingv2.cache;

import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LfuCacheList<T> extends LfuCacheBase<List<T>> {

    public LfuCacheList(int capacity) {
        super(capacity);
    }

}
