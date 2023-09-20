package org.example.exception.enrichment;

public class InvalidEnrichmentException extends RuntimeException{

    public InvalidEnrichmentException() {
    }

    public InvalidEnrichmentException(String message) {
        super(message);
    }
}
