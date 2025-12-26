CREATE TABLE IF NOT EXISTS orders (
    order_id UUID PRIMARY KEY,
    state VARCHAR(50) NOT NULL,
    shopping_cart_id UUID,
    delivery_id UUID,
    payment_id UUID,
    delivery_volume DOUBLE PRECISION,
    delivery_weight DOUBLE PRECISION,
    fragile BOOLEAN,
    total_price DOUBLE PRECISION,
    product_price DOUBLE PRECISION,
    delivery_price DOUBLE PRECISION
);

CREATE TABLE IF NOT EXISTS order_products (
    order_id UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity BIGINT,
    PRIMARY KEY (order_id, product_id),
    CONSTRAINT fk_order_products FOREIGN KEY (order_id) REFERENCES orders (order_id)
);