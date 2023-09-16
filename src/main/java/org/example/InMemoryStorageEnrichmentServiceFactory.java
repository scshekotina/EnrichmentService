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
        BlockingQueue<Message> queue = new ArrayBlockingQueue<>(100);
        return new EnrichmentService(
                new UserStorageInMemory(new ConcurrentHashMap<>()),
                new MessageEnrichmentEventListener(new MessageStorageInMemory(Collections.synchronizedList(new ArrayList<>())), queue),
                new MessageEnrichmentEventListener(new MessageStorageInMemory(Collections.synchronizedList(new ArrayList<>())), queue),
                queue, queue);
    }
}
