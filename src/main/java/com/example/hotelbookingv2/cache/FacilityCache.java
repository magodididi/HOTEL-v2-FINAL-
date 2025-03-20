package com.example.hotelbookingv2.cache;

import com.example.hotelbookingv2.model.Facility;
import org.springframework.stereotype.Component;

@Component
public class FacilityCache extends LfuCacheList<Facility> {
    public FacilityCache() {
        super(6);
    }
}
