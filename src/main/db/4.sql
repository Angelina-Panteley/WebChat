use chat;
select * from messages, users
where users.id = '9'
and messages.text like '%hello%'
and messages.user_id = users.id;