use chat;
SELECT *
from users, messages 
where users.id=messages.user_id 
order by messages.date ASC;