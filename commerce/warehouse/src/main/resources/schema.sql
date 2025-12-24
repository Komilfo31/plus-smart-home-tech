CREATE TABLE IF NOT EXISTS warehouse_products
(
    product_id UUID PRIMARY KEY,
    depth      DOUBLE PRECISION,
    height     DOUBLE PRECISION,
    width      DOUBLE PRECISION,
    fragile    BOOLEAN          NOT NULL,
    quantity   BIGINT           NOT NULL,
    weight     DOUBLE PRECISION NOT NULL
);