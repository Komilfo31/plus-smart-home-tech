DROP TABLE IF EXISTS products CASCADE;

CREATE TABLE IF NOT EXISTS products
(
    product_id       UUID PRIMARY KEY,
    product_name     VARCHAR(255)   NOT NULL,
    description      TEXT           NOT NULL,
    image_src        VARCHAR(255),
    quantity_state   VARCHAR(32)    NOT NULL,
    product_state    VARCHAR(32)    NOT NULL,
    product_category VARCHAR(32)    NOT NULL,
    price            NUMERIC(10, 2) NOT NULL
);