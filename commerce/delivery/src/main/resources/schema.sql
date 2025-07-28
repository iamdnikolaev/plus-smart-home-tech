DROP TABLE IF EXISTS delivery, address CASCADE;

CREATE TABLE IF NOT EXISTS address (
    address_id       UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    country          varchar(100),
    city             varchar(100),
    street           varchar(255),
    house            varchar(20),
    flat             varchar(20)
);

CREATE TABLE IF NOT EXISTS delivery (
    delivery_id      UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    order_id         UUID,
    from_address_id  UUID,
    to_address_id    UUID,
    state            varchar(20),
    delivery_weight  DOUBLE PRECISION,
    delivery_volume  DOUBLE PRECISION,
    fragile          BOOLEAN,

    CONSTRAINT fk_delivery_from_address FOREIGN KEY (from_address_id) REFERENCES address(address_id) ON DELETE CASCADE,
    CONSTRAINT fk_delivery_to_address FOREIGN KEY (to_address_id) REFERENCES address(address_id) ON DELETE CASCADE
);