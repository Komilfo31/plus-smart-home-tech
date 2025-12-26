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

CREATE TABLE IF NOT EXISTS order_bookings (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    delivery_id UUID
);

CREATE TABLE IF NOT EXISTS booked_product_quantities (
    booking_id UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity BIGINT NOT NULL,

    PRIMARY KEY (booking_id, product_id),

    CONSTRAINT fk_booked_product_quantities_booking_id FOREIGN KEY (booking_id) REFERENCES order_bookings (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);