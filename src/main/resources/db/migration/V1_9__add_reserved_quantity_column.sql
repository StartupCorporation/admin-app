ALTER TABLE product
    ADD COLUMN reserved_quantity INT NOT NULL DEFAULT 0,
    ADD CONSTRAINT reserved_quantity_check CHECK (reserved_quantity >= 0);

ALTER TABLE product
    ADD CONSTRAINT stock_quantity_check CHECK (stock_quantity >= 0);