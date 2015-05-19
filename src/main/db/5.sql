use chat;
select user_id, count(*) as count
from messages 
group by user_id 
having count(*) > 3;