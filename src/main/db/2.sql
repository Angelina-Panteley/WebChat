use chat;
select * from users, messages 
where users.name = 'Alice' 
and users.id = messages.user_id;