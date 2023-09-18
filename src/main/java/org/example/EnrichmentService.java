package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.example.exception.user.UserNotFoundException;
import org.example.message.Message;
import org.example.exception.InvalidEnrichmentException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

@Getter
public class EnrichmentService {

    private Enricher enricher;
    private BlockingQueue<Message> enrichedMessagesQueue;
    private BlockingQueue<Message> notEnrichedMessagesQueue;

    private ObjectMapper objectMapper = new ObjectMapper();

    private static final String NOT_SUPPORTED_ENRICHMENT_TYPE = "Тип обогащения не поддерживается";

    public EnrichmentService(Enricher enricher,
                             BlockingQueue<Message> enrichedMessagesQueue,
                             BlockingQueue<Message> notEnrichedMessagesQueue) {
        this.enricher = enricher;
        this.enrichedMessagesQueue = enrichedMessagesQueue;
        this.notEnrichedMessagesQueue = notEnrichedMessagesQueue;
    }



    public String enrich(Message message) {
        try {
            validate(message);
            Message enriched = addUserData(message);
            enrichedMessagesQueue.add(enriched);
            return convertToString(enriched);
        } catch (InvalidEnrichmentException e) {
            notEnrichedMessagesQueue.add(message);
            return convertToString(message);
        }
    }

    private void validate(Message message) {
    }

    private Message addUserData(Message message) {

        if (message.getEnrichmentType() != Message.EnrichmentType.MSISDN) {
            throw new UserNotFoundException(NOT_SUPPORTED_ENRICHMENT_TYPE);
        }
        Map<String, String> contentMap;
        try {
            contentMap = objectMapper.readValue(message.getContent(), HashMap.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        enricher.enrich(contentMap, message.getEnrichmentType());
        message.setContent(contentMap.toString());
        return message;
    }

    private String convertToString(Message message) {return message.toString();}

}
