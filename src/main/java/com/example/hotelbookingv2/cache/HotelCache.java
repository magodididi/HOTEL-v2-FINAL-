package com.example.hotelbookingv2.cache;

import com.example.hotelbookingv2.model.Hotel;

public class HotelCache extends LfuCacheList<Hotel> {
    public HotelCache() {
        super(3);
    }
}