package org.example;

import lombok.Getter;
import org.example.exception.IncorrectEnrichmentDataException;
import org.example.exception.UnsupportedEnrichmentTypeException;
import org.example.exception.user.UserNotFoundException;
import org.example.message.Message;
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

    public Map<String, String> enrich(Map<String, String> content, Message.EnrichmentType enrichmentType) {
        if (enrichmentType == Message.EnrichmentType.MSISDN) {
            return msisdnEnrich(content);
        }
        throw new UnsupportedEnrichmentTypeException();
    }

    private Map<String, String> msisdnEnrich(Map<String, String> content){
        String  msisdn = content.get("msisdn");
        if (msisdn == null || msisdn.isBlank()) {
            throw new IncorrectEnrichmentDataException();
        }
        User user;
        try {
            user = userStorage.get(content.get("msisdn"));
        } catch (UserNotFoundException e) {
            throw new IncorrectEnrichmentDataException();
        }
        content.remove("enrichment");
        Map<String, String> enrichmentMap = new HashMap<>();
        enrichmentMap.put("firstname", user.getFirstname());
        enrichmentMap.put("lastname", user.getLastname());
        content.put("enrichment", enrichmentMap.toString());
        return content;
    }
}
