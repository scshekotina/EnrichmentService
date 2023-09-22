package org.example.message;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Message {
    private String content;
    private EnrichmentType enrichmentType;

    public enum EnrichmentType {
        MSISDN
    }
}
