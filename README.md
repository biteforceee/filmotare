# java-filmorate
Template repository for Filmorate project.

https://cloud.unn.ru/s/yDWNrY29JyJAG6k

```sql
-- Обновление пользователя
UPDATE users 
SET name = ?, login = ?, email = ?, birthday_date = ? 
WHERE id = ?;

-- Удаление пользователя
DELETE FROM users WHERE id = ?;

-- Получение всех пользователей
SELECT * FROM users;

-- Получение пользователя по ID
SELECT * FROM users WHERE id = ?;

-- Добавление в друзья
INSERT INTO friends (user_id, friend_id, status_id, created_at) 
VALUES (?, ?, ?, CURRENT_TIMESTAMP);

-- Удаление из друзей
DELETE FROM friends 
WHERE (user_id = ? AND friend_id = ?) 
   OR (user_id = ? AND friend_id = ?);

-- Получение друзей пользователя
SELECT u.* 
FROM users u
JOIN friends f ON u.id = f.friend_id 
WHERE f.user_id = ? AND f.status_id = ?; -- status_id для подтвержденной дружбы

-- Получение общих друзей
SELECT u.* 
FROM users u
JOIN friends f1 ON u.id = f1.friend_id
JOIN friends f2 ON u.id = f2.friend_id
WHERE f1.user_id = ? AND f2.user_id = ? 
  AND f1.status_id = ? AND f2.status_id = ?; -- status_id для подтвержденной дружбы
```
