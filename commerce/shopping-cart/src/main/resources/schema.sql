CREATE TABLE IF NOT EXISTS shopping_cart
(
    shopping_cart_id UUID PRIMARY KEY,
    username         VARCHAR(255) NOT NULL,
    state            VARCHAR(12) CHECK (state IN ('ACTIVE', 'DEACTIVATED'))
);

CREATE TABLE IF NOT EXISTS cart_products
(
    shopping_cart_shopping_cart_id UUID NOT NULL,
    product_id                     UUID NOT NULL,
    quantity                       BIGINT,
    PRIMARY KEY (shopping_cart_shopping_cart_id, product_id),
    CONSTRAINT fk_cart_products_cart FOREIGN KEY (shopping_cart_shopping_cart_id) REFERENCES shopping_cart (shopping_cart_id)
);