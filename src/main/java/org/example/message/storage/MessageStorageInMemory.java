package org.example.message.storage;

import org.example.message.Message;

import java.util.List;

public class MessageStorageInMemory implements MessageStorage {
    private List<Message> messages;

    public MessageStorageInMemory(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public void save(Message message) {
        messages.add(message);
    }

    @Override
    public List<Message> get() {
        return messages;
    }
}
