DROP TABLE IF EXISTS order_products CASCADE;

CREATE TABLE IF NOT EXISTS orders
(
    order_id         UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    shopping_cart_id UUID,
    payment_id       UUID,
    delivery_id      UUID,
    state            VARCHAR(16),
    delivery_weight  DOUBLE PRECISION,
    delivery_volume  DOUBLE PRECISION,
    fragile          BOOLEAN,
    total_price      DOUBLE PRECISION,
    delivery_price   DOUBLE PRECISION,
    product_price    REAL
);

CREATE TABLE IF NOT EXISTS order_products
(
    order_id   UUID  NOT NULL,
    product_id UUID,
    quantity   BIGINT,
    CONSTRAINT pk_order_products PRIMARY KEY(order_id, product_id),
    CONSTRAINT fk_order_to_orders FOREIGN KEY(order_id) REFERENCES orders(order_id) ON DELETE CASCADE
);