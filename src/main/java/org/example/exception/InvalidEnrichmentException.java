package org.example.exception;

public class InvalidEnrichmentException extends RuntimeException{

    public InvalidEnrichmentException() {
    }

    public InvalidEnrichmentException(String message) {
        super(message);
    }
}
