package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.exception.user.UserNotFoundException;
import org.example.message.Message;
import org.example.message.NotEnrichedException;
import org.example.message.listener.MessageEnrichmentEventListener;
import org.example.user.User;
import org.example.user.UserStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class EnrichmentService {

    private UserStorage userStorage;
    private MessageEnrichmentEventListener enrichmentEventListener;
    private MessageEnrichmentEventListener notEnrichmentEventListener;
    private BlockingQueue<Message> enrichedMessagesQueue;
    private BlockingQueue<Message> notEnrichedMessagesQueue;

    private ObjectMapper objectMapper = new ObjectMapper();

    private static final String NOT_SUPPORTED_ENRICHMENT_TYPE = "Тип обогащения не поддерживается";

    public EnrichmentService(UserStorage userStorage,
                             MessageEnrichmentEventListener enrichmentEventListener,
                             MessageEnrichmentEventListener notEnrichmentEventListener,
                             BlockingQueue<Message> enrichedMessagesQueue,
                             BlockingQueue<Message> notEnrichedMessagesQueue) {
        this.userStorage = userStorage;
        this.enrichmentEventListener = enrichmentEventListener;
        this.notEnrichmentEventListener = notEnrichmentEventListener;
        this.enrichedMessagesQueue = enrichedMessagesQueue;
        this.notEnrichedMessagesQueue = notEnrichedMessagesQueue;

        startListeners();
    }

    private void startListeners() {
        enrichmentEventListener.run();
        notEnrichmentEventListener.run();
    }

    public String enrich(Message message) {
        try {
            Message enriched = addUserData(message);
            enrichedMessagesQueue.add(enriched);
            return convertToString(enriched);
        } catch (NotEnrichedException e) {
            notEnrichedMessagesQueue.add(message);
            return convertToString(message);
        }
    }

    private void validate(Message message) {
    }

    private Message addUserData(Message message) {
        validate(message);
        if (message.getEnrichmentType() != Message.EnrichmentType.MSISDN) {
            throw new UserNotFoundException(NOT_SUPPORTED_ENRICHMENT_TYPE);
        }
        Map<String, String> contentMap;
        try {
            contentMap = objectMapper.readValue(message.getContent(), new TypeReference<>(){});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        String  msidsn = contentMap.get("msidsn");
        User user = userStorage.get(msidsn);
        contentMap.remove("enrichment");
        HashMap<String, String> enrichmentMap = new HashMap<>();
        enrichmentMap.put("firstname", user.getFirstname());
        enrichmentMap.put("lastname", user.getLastname());
        contentMap.put("enrichment", enrichmentMap.toString());
        message.setContent(contentMap.toString());
        return message;
    }

    private String convertToString(Message message) {return message.toString();}

}
