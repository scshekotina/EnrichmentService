package org.example.message.storage;

import org.example.message.Message;

import java.util.List;

public interface MessageStorage {
    void save(Message message);
    List<Message> get();
}
