package org.example.message;

import lombok.Data;
import lombok.Getter;

@Data
public class Message {
    private String content;
    private EnrichmentType enrichmentType;

    public enum EnrichmentType {
        MSISDN
    }
}
