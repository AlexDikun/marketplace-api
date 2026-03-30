-- На время миграции отключаю проверку на внешние ключи
SET session_replication_role = 'replica';

-- Удаляю тестовые данные, ранее добавленные классом DataInit.java
TRUNCATE TABLE images, comments, adverts, categories, users, roles RESTART IDENTITY CASCADE;

-- Начинаю транзакцию для атомарности операций
BEGIN TRANSACTION;

DROP TABLE IF EXISTS roles;

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS keycloak_id VARCHAR(64),
    ADD COLUMN IF NOT EXISTS email VARCHAR(50);

ALTER TABLE users
    DROP COLUMN IF EXISTS password,
    DROP COLUMN IF EXISTS role_id,
    DROP COLUMN IF EXISTS name;

ALTER TABLE users
    ALTER COLUMN keycloak_id SET NOT NULL,
    ALTER COLUMN email SET NOT NULL;

ALTER TABLE users
    ADD CONSTRAINT users_keycloak_id_unique UNIQUE (keycloak_id),
    ADD CONSTRAINT users_email_unique UNIQUE (email);

-- фиксирую изменения
COMMIT;

-- восстанавливаю настройку
SET session_replication_role = 'origin';
