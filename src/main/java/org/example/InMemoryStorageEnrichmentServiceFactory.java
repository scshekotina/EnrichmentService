package org.example;

import org.example.message.Message;
import org.example.message.listener.MessageEnrichmentEventListener;
import org.example.message.storage.MessageStorageInMemory;
import org.example.user.UserStorageInMemory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryStorageEnrichmentServiceFactory implements EnrichmentServiceFactory {

    @Override
    public EnrichmentService createEnrichmentService() {
        BlockingQueue<Message> enrichedMessagesQueue = new ArrayBlockingQueue<>(100);
        BlockingQueue<Message> notEnrichedMessagesQueue = new ArrayBlockingQueue<>(100);

        MessageStorageInMemory enrichedMessageStorageInMemory =
                new MessageStorageInMemory(Collections.synchronizedList(new ArrayList<>()));

        MessageStorageInMemory notEnrichedMessageStorageInMemory =
                new MessageStorageInMemory(Collections.synchronizedList(new ArrayList<>()));

        MessageEnrichmentEventListener enrichmentEventListener = new MessageEnrichmentEventListener(
                enrichedMessageStorageInMemory,
                enrichedMessagesQueue);

        MessageEnrichmentEventListener notEnrichmentEventListener = new MessageEnrichmentEventListener(
                notEnrichedMessageStorageInMemory,
                notEnrichedMessagesQueue);

        Thread enrichmentEventListenerThread = new Thread(enrichmentEventListener);
        enrichmentEventListenerThread.start();

        Thread notEnrichmentEventListenerThread = new Thread(notEnrichmentEventListener);
        notEnrichmentEventListenerThread.start();

        return new EnrichmentService(
                new Enricher(new UserStorageInMemory(new ConcurrentHashMap<>())),
                enrichedMessagesQueue,
                notEnrichedMessagesQueue,
                enrichedMessageStorageInMemory,
                notEnrichedMessageStorageInMemory);
    }

}
