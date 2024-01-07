CREATE SEQUENCE IF NOT EXISTS product_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

CREATE SEQUENCE IF NOT EXISTS warehouse_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

CREATE SEQUENCE IF NOT EXISTS warehouse_income_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

CREATE SEQUENCE IF NOT EXISTS warehouse_sale_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

CREATE SEQUENCE IF NOT EXISTS warehouse_transportation_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

CREATE TABLE IF NOT EXISTS product
(
    id bigint NOT NULL DEFAULT nextval('product_id_seq'::regclass),
    articul character varying(255) COLLATE pg_catalog."default",
    name character varying(255) COLLATE pg_catalog."default",
    deleted boolean NOT NULL,
    last_change_date timestamp without time zone,
    last_income_price numeric(12, 2),
    last_sale_price numeric(12, 2),
    CONSTRAINT product_id_pkey PRIMARY KEY (id),
    CONSTRAINT product_articul_key UNIQUE (articul)
);

CREATE TABLE IF NOT EXISTS warehouse
(
    id bigint NOT NULL DEFAULT nextval('warehouse_id_seq'::regclass),
    name character varying(255) COLLATE pg_catalog."default",
    deleted boolean NOT NULL,
    last_change_date timestamp without time zone,
    CONSTRAINT warehouse_id_pkey PRIMARY KEY (id),
    CONSTRAINT warehouse_name_key UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS warehouse_products_count
(
    warehouse_id bigint NOT NULL,
    product_id bigint NOT NULL,
    count integer,
    price numeric(12, 2),
    CONSTRAINT warehouseProductsCount_warehouseId_fkey FOREIGN KEY (warehouse_id)
        REFERENCES warehouse (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT warehouseProductsCount_productId_fkey FOREIGN KEY (product_id)
        REFERENCES product (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

CREATE TABLE IF NOT EXISTS warehouse_income
(
    id bigint NOT NULL DEFAULT nextval('warehouse_income_id_seq'::regclass),
    create_date timestamp without time zone,
    warehouse_id bigint,
    CONSTRAINT warehouseIncome_id_pkey PRIMARY KEY (id),
    CONSTRAINT warehouseIncome_warehouseId_fkey FOREIGN KEY (warehouse_id)
        REFERENCES warehouse (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

CREATE TABLE IF NOT EXISTS income_products_count
(
    warehouse_income_id bigint NOT NULL,
    product_id bigint NOT NULL,
    count integer,
    price numeric(12, 2),
    CONSTRAINT incomeProductsCount_productId_fkey FOREIGN KEY (product_id)
        REFERENCES product (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT incomeProductsCount_productIncomeId_fkey FOREIGN KEY (warehouse_income_id)
        REFERENCES warehouse_income (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

CREATE TABLE IF NOT EXISTS warehouse_sale
(
    id bigint NOT NULL DEFAULT nextval('warehouse_sale_id_seq'::regclass),
    create_date timestamp without time zone,
    warehouse_id bigint,
    CONSTRAINT warehouseSale_id_pkey PRIMARY KEY (id),
    CONSTRAINT warehouseSale_warehouseId_fkey FOREIGN KEY (warehouse_id)
        REFERENCES warehouse (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

CREATE TABLE IF NOT EXISTS sale_products_count
(
    warehouse_sale_id bigint NOT NULL,
    product_id bigint NOT NULL,
    count integer,
    price numeric(12, 2),
    CONSTRAINT saleProductsCount_productSaleId_fkey FOREIGN KEY (warehouse_sale_id)
        REFERENCES warehouse_sale (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT saleProductsCount_productId_fkey FOREIGN KEY (product_id)
        REFERENCES product (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

CREATE TABLE IF NOT EXISTS warehouse_transportation
(
    id bigint NOT NULL DEFAULT nextval('warehouse_transportation_id_seq'::regclass),
    create_date timestamp without time zone,
    warehouse_from_id bigint,
    warehouse_to_id bigint,
    CONSTRAINT warehouseTransportation_id_pkey PRIMARY KEY (id),
    CONSTRAINT warehouseTransportation_warehouseFromId_fkey FOREIGN KEY (warehouse_from_id)
        REFERENCES warehouse (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT warehouseTransportation_warehouseToId_fkey FOREIGN KEY (warehouse_to_id)
        REFERENCES warehouse (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

CREATE TABLE IF NOT EXISTS transportation_products_count
(
    warehouse_transportation_id bigint NOT NULL,
    product_id bigint NOT NULL,
    count integer,
    price numeric(12, 2),
    CONSTRAINT transportationProductsCount_productTransportationId_fkey FOREIGN KEY (warehouse_transportation_id)
        REFERENCES warehouse_transportation (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT transportationProductsCount_productId_fkey FOREIGN KEY (product_id)
        REFERENCES product (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);
