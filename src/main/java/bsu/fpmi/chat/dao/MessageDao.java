package bsu.fpmi.chat.dao;

import bsu.fpmi.chat.model.Message;

import java.util.List;

public interface MessageDao {
    void add(Message task);

    void update(String id, String text);

    boolean isUserExist(String name, String password);

    void delete(int id);

    String getUserId(String login, String password);

    void loadHistory();

    Message selectById(Message task);

    List<Message> selectAll();

    void addDeletedUser();

    String getUserIdWhereId(Message message);
}
