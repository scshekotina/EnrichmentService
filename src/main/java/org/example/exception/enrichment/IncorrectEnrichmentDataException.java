package org.example.exception.enrichment;

public class IncorrectEnrichmentDataException extends InvalidEnrichmentException{
    public IncorrectEnrichmentDataException() {
    }

    public IncorrectEnrichmentDataException(String message) {
        super(message);
    }
}
