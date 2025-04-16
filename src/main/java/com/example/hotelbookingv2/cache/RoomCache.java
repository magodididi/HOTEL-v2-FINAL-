package com.example.hotelbookingv2.cache;

import com.example.hotelbookingv2.model.Room;
import org.springframework.stereotype.Component;

@Component
public class RoomCache extends LfuCache<Room> {
    public RoomCache() {
        super(3);
    }
}
