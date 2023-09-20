package org.example.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.exception.enrichment.IncorrectEnrichmentDataException;
import org.example.exception.enrichment.UnsupportedEnrichmentTypeException;

import java.util.Map;

public class MessageConverter {
    private static final String NOT_SUPPORTED_ENRICHMENT_TYPE = "Тип обогащения не поддерживается";
    private static final String INVALID_JSON = "Некорректный json!";

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private MessageConverter() {
    }

    public static Map<String, Object> getValidMessageContentMap(Message message) {
        validateMessageType(message);
        try {
            Map<String, Object> content = objectMapper.readValue(message.getContent(), new TypeReference<>() {});
            validateByEnrichmentType(message.getEnrichmentType(), content);
            return content;
        } catch (JsonProcessingException e) {
            throw new IncorrectEnrichmentDataException(INVALID_JSON);
        }
    }

    private static void validateMessageType(Message message) {
        if (message.getEnrichmentType() != Message.EnrichmentType.MSISDN) {
            throw new UnsupportedEnrichmentTypeException(NOT_SUPPORTED_ENRICHMENT_TYPE);
        }
    }

    private static void validateByEnrichmentType(Message.EnrichmentType enrichmentType, Map<String, Object> content) {
        if (enrichmentType == Message.EnrichmentType.MSISDN) {
            Object msisdn = content.get("msisdn");
            if (msisdn == null || msisdn.toString().isBlank()) {
                throw new IncorrectEnrichmentDataException(INVALID_JSON);
            }
        }
    }

    public static String getContentString(Map<String, Object> contentMap) {
        try {
            return objectMapper.writeValueAsString(contentMap);
        } catch (JsonProcessingException e) {
            throw new IncorrectEnrichmentDataException(INVALID_JSON);
        }
    }

}
