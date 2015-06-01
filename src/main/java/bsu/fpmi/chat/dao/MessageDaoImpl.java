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

    public String getUserId(String login, String password)
    {
        String user_table = "SELECT id FROM `users` where name = ? and password = ?";

        Connection connection = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        String id = null;
        try {
            connection = ConnectionManager.getConnection();
            st = connection.prepareStatement(user_table);
            st.setString(1, login);
            st.setString(2, password);
            rs = st.executeQuery();
            while (rs.next()) {
                id = rs.getString("id");
            }
        }
        catch(Exception e) {
            logger.error("An error occurred in function getUserId: " + e);
        }
        return id;
    }

    public boolean isUserExist(String name, String password)
    {
        String id = getUserId(name, password);
        if(id == null)
            return false;
        return true;
    }

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
                while (rs1.next()) {
                    name = rs1.getString("name");
                }
                //1if(Integer.parseInt(id)>0)
                    MessageStorage.addMessage(new Message(id, name, text,user_id, date));
            }
        } catch (SQLException e) {
            logger.error("SQLException occurred while loading history: "+e);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    logger.error("SQLException while closing statement: "+ e);
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.error("SQLException while closing connection: "+ e);
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
            logger.error("SQLException occurred while adding message: " + e);
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    logger.error("SQLException while closing statement: "+ e);
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.error("SQLException while closing connection: "+ e);
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
            logger.error("SQLException occurred while updating message: " + e);
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    logger.error("SQLException while closing statement: "+ e);
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.error("SQLException while closing connection: "+ e);
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
            logger.error("SQLException occurred while selecting messages: " + e);
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    logger.error("SQLException while closing resultSet: "+e);
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    logger.error("SQLException while closing statement: "+ e);
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.error("SQLException while closing connection: "+ e);
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
            statement = connection.prepareStatement("Update Messages SET user_id = ? WHERE id = ?");
            statement.setString(1, "0");
            statement.setString(2, Integer.toString(id));
            statement.executeUpdate();
            //statement = connection.prepareStatement("delete FROM `messages` where messages.id = ?");
            //statement.setInt(1, id);
            //statement.execute();
        } catch (SQLException e) {
            logger.error(e);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    logger.error("SQLException while closing statement: "+ e);
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.error("SQLException while closing connection: "+ e);
                }
            }
        }
    }

   public  void addDeletedUser()
   {
       Connection connection = null;
       PreparedStatement preparedStatement = null;
       String id = null;
       if(!isUserExist("","1")) {
           try {
               connection = ConnectionManager.getConnection();
               preparedStatement = connection.prepareStatement("INSERT INTO `users` VALUES (?, ?, ?)");
               id = Integer.toString(0);
               preparedStatement.setString(1, id);
               preparedStatement.setString(2, "");
               preparedStatement.setString(3, "1");
               preparedStatement.executeUpdate();
           } catch (SQLException e) {
               logger.error("SQLException occurred while changing message(delete): " + e);
           } finally {
               if (preparedStatement != null) {
                   try {
                       preparedStatement.close();
                   } catch (SQLException e) {
                       logger.error("SQLException while closing statement: "+ e);
                   }
               }
               if (connection != null) {
                   try {
                       connection.close();
                   } catch (SQLException e) {
                       logger.error("SQLException while closing connection: "+ e);
                   }
               }
           }
       }
   }

    public String getUserIdWhereId(Message message)
    {
        String s = "Select user_id from `messages` where id=?";
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        String uid = null;
        try {
            connection = ConnectionManager.getConnection();
            preparedStatement = connection.prepareStatement(s);
            preparedStatement.setString(1,message.getId());
            rs = preparedStatement.executeQuery();
            while(rs.next())
            {
                uid = rs.getString("user_id");
            }
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("SQLException occurred in function getUserIdWhereId: " + e);
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    logger.error("SQLException while closing statement: "+ e);
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.error("SQLException while closing connection: "+ e);
                }
            }
            return uid;
        }
    }
}
