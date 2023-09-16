package org.example.message;

public class NotEnrichedException extends RuntimeException{

    public NotEnrichedException() {
    }

    public NotEnrichedException(String message) {
        super(message);
    }
}
