ALTER TABLE users
    ADD COLUMN IF NOT EXISTS display_name VARCHAR(50) DEFAULT 'Анонимный пользователь',
    ADD COLUMN IF NOT EXISTS messenger_links JSON DEFAULT '{}'; 

COMMENT ON COLUMN users.messenger_links IS 'JSON-объект: { "VK.COM": "@username", "MAX": "88005553535" }';