package bsu.fpmi.chat.dao;

import bsu.fpmi.chat.model.MessageStorage;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import bsu.fpmi.chat.db.ConnectionManager;
import bsu.fpmi.chat.model.Message;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDaoImpl implements MessageDao {
    private static Logger logger = Logger.getLogger(MessageDaoImpl.class.getName());

    public void loadHistory()
    {
        String deleteSQL = "SELECT * FROM `messages`";
        String user_table = "SELECT * FROM `users` where users.id = ?";

        Connection connection = null;
        Statement statement = null;
        PreparedStatement st = null;
        ResultSet resultSet = null, rs1 = null;
        try {
            String name = null;
            connection = ConnectionManager.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(deleteSQL);
            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String text = resultSet.getString("text");
                String user_id = resultSet.getString("user_id");
                String date = resultSet.getString("date");

                st = connection.prepareStatement(user_table);
                st.setString(1, user_id);

                rs1 = st.executeQuery();
                while (rs1.next()){
                    name = rs1.getString("name");
                    System.out.println(name);
                }
                MessageStorage.addMessage(new Message(id, name, text,user_id, date));
            }
        } catch (SQLException e) {
            logger.error(e);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    logger.error(e);
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.error(e);
                }
            }
        }
    }

    public void add(Message Message) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionManager.getConnection();
            preparedStatement = connection.prepareStatement("INSERT INTO messages (id, user_id, text, date) VALUES (?, ?, ?, ?)");
            preparedStatement.setString(1, Message.getId());
            preparedStatement.setString(2, Message.getUser_id());
            preparedStatement.setString(3, Message.getDescription());
            preparedStatement.setString(4, Message.getDate());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error(e);
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    logger.error(e);
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.error(e);
                }
            }
        }
    }

    public void update(String id, String text) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionManager.getConnection();
            preparedStatement = connection.prepareStatement("Update Messages SET text = ? WHERE id = ?");
            preparedStatement.setString(1, text);
            preparedStatement.setString(2, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error(e);
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    logger.error(e);
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.error(e);
                }
            }
        }
    }


    public Message selectById(Message Message) {
        throw new UnsupportedOperationException();
    }

    public List<Message> selectAll() {
        List<Message> Messages = new ArrayList<Message>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = ConnectionManager.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM Messages");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String user = resultSet.getString("user");
                String text = resultSet.getString("text");
                int user_id = resultSet.getInt("user_id");
                String date = resultSet.getString("date");
                Messages.add(new Message(Integer.toString(id), user, text,Integer.toString(user_id), date));
            }
        } catch (SQLException e) {
            logger.error(e);
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    logger.error(e);
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    logger.error(e);
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.error(e);
                }
            }
        }
        return Messages;
    }

    public void delete(int id) {

        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = ConnectionManager.getConnection();
            statement = connection.prepareStatement("delete FROM `messages` where messages.id = ?");
            statement.setInt(1, id);
            statement.execute();
        } catch (SQLException e) {
            logger.error(e);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    logger.error(e);
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.error(e);
                }
            }
        }
    }

}
