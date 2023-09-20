package org.example.exception.enrichment;

public class UnsupportedEnrichmentTypeException extends InvalidEnrichmentException{
    public UnsupportedEnrichmentTypeException() {
    }

    public UnsupportedEnrichmentTypeException(String message) {
        super(message);
    }
}
