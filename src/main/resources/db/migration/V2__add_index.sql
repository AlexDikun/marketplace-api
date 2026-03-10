-- Индексы для внешний ключей
CREATE INDEX idx_adverts_user_id ON adverts(user_id);
CREATE INDEX idx_comments_user_id ON comments(user_id);
CREATE INDEX idx_images_advert_id ON images(advert_id);
CREATE INDEX idx_adverts_category_id ON adverts(category_id);
CREATE INDEX idx_comments_advert_id ON comments(advert_id);
CREATE INDEX idx_comments_parent_id ON comments(parent_id);

-- Индекс для часто фильтруемых полей
CREATE INDEX idx_adverts_title ON adverts(title);
CREATE INDEX idx_users_login ON users(login);
CREATE INDEX idx_adverts_cost ON adverts(cost);
CREATE INDEX idx_comments_created_at ON comments(created_at);
CREATE INDEX idx_adverts_created_at ON adverts(created_at);
