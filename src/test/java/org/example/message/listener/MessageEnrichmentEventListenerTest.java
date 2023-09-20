package org.example.message.listener;

import org.example.TestData;
import org.example.message.Message;
import org.example.message.storage.MessageStorage;
import org.example.message.storage.MessageStorageInMemory;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.assertThat;

public class MessageEnrichmentEventListenerTest {

    private BlockingQueue<Message> queue;
    private MessageStorage messageStorage;

    @Before
    public void setUp(){
        queue = new ArrayBlockingQueue<>(100);
        messageStorage = new MessageStorageInMemory(Collections.synchronizedList(new ArrayList<>()));
        MessageEnrichmentEventListener enrichmentEventListener = new MessageEnrichmentEventListener(messageStorage, queue);
        Thread enrichmentEventListenerThread = new Thread(enrichmentEventListener);
        enrichmentEventListenerThread.start();
    }

    @Test
    public void listen() {
        queue.add(TestData.CORRECT_VASYA_ENRICHED_MESSAGE);
        assertThat(messageStorage.get()).containsExactly(TestData.CORRECT_VASYA_ENRICHED_MESSAGE);
        assertEquals(0, queue.size());
    }

    @Test
    public void listenTwoElements() {
        queue.add(TestData.CORRECT_VASYA_ENRICHED_MESSAGE);
        queue.add(TestData.CORRECT_VASYA_MESSAGE);
        assertThat(messageStorage.get()).containsExactly(TestData.CORRECT_VASYA_ENRICHED_MESSAGE, TestData.CORRECT_VASYA_MESSAGE);
        assertEquals(0, queue.size());
    }
}