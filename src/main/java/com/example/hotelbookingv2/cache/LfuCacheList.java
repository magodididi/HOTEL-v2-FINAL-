package com.example.hotelbookingv2.cache;

import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LfuCacheList<T> extends LfuCacheBase<List<T>> {

    public LfuCacheList(int capacity) {
        super(capacity);
    }

    @Override
    public List<T> get(String id) {
        return super.get(id);
    }

    @Override
    public void put(String id, List<T> value) {
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
