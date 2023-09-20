package org.example;

import lombok.Getter;
import org.example.message.Message;
import org.example.exception.enrichment.InvalidEnrichmentException;
import org.example.message.storage.MessageStorageInMemory;

import java.util.concurrent.BlockingQueue;

@Getter
public class EnrichmentService {

    private Enricher msisdnEnricher;
    private BlockingQueue<Message> enrichedMessagesQueue;
    private BlockingQueue<Message> notEnrichedMessagesQueue;
    private MessageStorageInMemory enrichedMessageStorage;
    private MessageStorageInMemory notEnrichedMessageStorage;

    public EnrichmentService(Enricher msisdnEnricher,
                             BlockingQueue<Message> enrichedMessagesQueue,
                             BlockingQueue<Message> notEnrichedMessagesQueue,
                             MessageStorageInMemory enrichedMessageStorage,
                             MessageStorageInMemory notEnrichedMessageStorage) {
        this.msisdnEnricher = msisdnEnricher;
        this.enrichedMessagesQueue = enrichedMessagesQueue;
        this.notEnrichedMessagesQueue = notEnrichedMessagesQueue;
        this.enrichedMessageStorage = enrichedMessageStorage;
        this.notEnrichedMessageStorage = notEnrichedMessageStorage;
    }



    public String enrich(Message message) {
        try {
            Message enriched = msisdnEnricher.enrich(message);
            enrichedMessagesQueue.add(enriched);
            return (enriched.getContent());
        } catch (InvalidEnrichmentException e) {
            notEnrichedMessagesQueue.add(message);
            return String.valueOf(message.getContent());
        }
    }
}
