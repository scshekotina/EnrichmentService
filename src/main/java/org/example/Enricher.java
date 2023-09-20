package org.example;

import lombok.Getter;
import org.example.exception.enrichment.IncorrectEnrichmentDataException;
import org.example.exception.enrichment.UnsupportedEnrichmentTypeException;
import org.example.exception.user.UserNotFoundException;
import org.example.message.Message;
import org.example.message.MessageConverter;
import org.example.user.User;
import org.example.user.UserStorage;

import java.util.HashMap;
import java.util.Map;

@Getter
public class Enricher {
    private UserStorage userStorage;
    public Enricher(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Message enrich(Message message) {
        Map<String, Object> contentMap = MessageConverter.getValidMessageContentMap(message);
        Map<String, Object> enriched = enrichByType(contentMap, message.getEnrichmentType());
        message.setContent(MessageConverter.getContentString(enriched));
        return message;
    }

    private Map<String, Object> enrichByType(Map<String, Object> content, Message.EnrichmentType enrichmentType){
        if (enrichmentType == Message.EnrichmentType.MSISDN) {
            return msisdnEnrich(content);
        }
        else {
            throw new UnsupportedEnrichmentTypeException();
        }
    }

    private Map<String, Object> msisdnEnrich(Map<String, Object> content){
        String  msisdn = content.get("msisdn").toString();
        if (msisdn == null || msisdn.isBlank()) {
            throw new IncorrectEnrichmentDataException();
        }
        User user;
        try {
            user = userStorage.get(msisdn);
        } catch (UserNotFoundException e) {
            throw new IncorrectEnrichmentDataException();
        }
        content.remove("enrichment");
        Map<String, Object> enrichmentMap = new HashMap<>();
        enrichmentMap.put("firstname", user.getFirstname());
        enrichmentMap.put("lastname", user.getLastname());
        content.put("enrichment", enrichmentMap);
        return content;
    }
}
