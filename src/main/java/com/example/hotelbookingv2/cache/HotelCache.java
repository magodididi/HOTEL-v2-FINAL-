package com.example.hotelbookingv2.cache;

import com.example.hotelbookingv2.model.Hotel;
import org.springframework.stereotype.Component;

@Component
public class HotelCache extends LfuCacheList<Hotel> {
    public HotelCache() {
        super(3);
    }
}