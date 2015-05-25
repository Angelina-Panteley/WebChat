package bsu.fpmi.chat.dao;

import bsu.fpmi.chat.model.Message;

import java.util.List;

public interface MessageDao {
    void add(Message task);

    void update(String id, String text);

    void delete(int id);

    void loadHistory();

    Message selectById(Message task);

    List<Message> selectAll();
}
