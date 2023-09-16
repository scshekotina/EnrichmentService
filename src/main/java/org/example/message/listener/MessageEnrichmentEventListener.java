package org.example.message.listener;

import org.example.message.Message;
import org.example.message.storage.MessageStorage;

import java.util.concurrent.BlockingQueue;

public class MessageEnrichmentEventListener implements Runnable{
    private MessageStorage messageStorage;
    private BlockingQueue<Message> queue;

    public MessageEnrichmentEventListener(MessageStorage messageStorage, BlockingQueue<Message> queue) {
        this.messageStorage = messageStorage;
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Message message = queue.take();
                messageStorage.save(message);
            }
        }catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
