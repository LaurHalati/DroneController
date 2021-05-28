package com.example.demo;

class DroneNotFoundException extends RuntimeException {
    DroneNotFoundException(String id) {
        super("Could not find drone plan " + id);

    }
}
