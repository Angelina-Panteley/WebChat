use chat;
select * from users, messages 
where messages.date > '2015-05-02 00:00:00' 
and users.id = '3' 
and users.id = messages.user_id;