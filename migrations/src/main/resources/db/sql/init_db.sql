CREATE SEQUENCE IF NOT EXISTS products_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

CREATE SEQUENCE IF NOT EXISTS warehouses_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

CREATE SEQUENCE IF NOT EXISTS warehouse_transportation_documents_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

CREATE TABLE IF NOT EXISTS products
(
    id bigint NOT NULL DEFAULT nextval('product_id_seq'::regclass),
    articul character varying(255) COLLATE pg_catalog."default",
    name character varying(255) COLLATE pg_catalog."default",
    deleted boolean NOT NULL,
    last_change_date timestamp without time zone,
    last_income_price numeric(12, 2),
    last_sale_price numeric(12, 2),
    CONSTRAINT product_id_pk PRIMARY KEY (id),
    CONSTRAINT product_articul_key UNIQUE (articul)
);

CREATE TABLE IF NOT EXISTS warehouses
(
    id bigint NOT NULL DEFAULT nextval('warehouse_id_seq'::regclass),
    name character varying(255) COLLATE pg_catalog."default",
    deleted boolean NOT NULL,
    last_change_date timestamp without time zone,
    CONSTRAINT warehouse_id_pk PRIMARY KEY (id),
    CONSTRAINT warehouse_name_key UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS warehouse_products_count
(
    warehouse_id bigint NOT NULL,
    product_id bigint NOT NULL,
    count integer,
    price numeric(12, 2),
    CONSTRAINT warehouse_products_count_warehouse_id_fk FOREIGN KEY (warehouse_id)
        REFERENCES warehouses (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT warehouse_products_count_product_id_fk FOREIGN KEY (product_id)
        REFERENCES products (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

CREATE TABLE IF NOT EXISTS transportation_documents
(
    id bigint NOT NULL DEFAULT nextval('warehouse_transportation_documents_id_seq'::regclass),
    name character varying(255) COLLATE pg_catalog."default",
    created_date timestamp without time zone,
    warehouse_id_from bigint,
    warehouse_id_to bigint,
    type character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT transportation_documents_id_pk PRIMARY KEY (id),
    CONSTRAINT transportation_documents_name_unique_key UNIQUE (name),
    CONSTRAINT transportation_documents_warehouse_id_from_fk FOREIGN KEY (warehouse_id_from)
        REFERENCES warehouses (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT transportation_documents_warehouse_id_to_fk FOREIGN KEY (warehouse_id_to)
        REFERENCES warehouses (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

CREATE TABLE IF NOT EXISTS transportation_document_products
(
    transportation_document_id bigint NOT NULL,
    product_id bigint NOT NULL,
    count integer,
    price numeric(12, 2),
    CONSTRAINT transportation_document_products_product_id_fk FOREIGN KEY (product_id)
        REFERENCES products (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT transportation_document_products_product_income_id_fk FOREIGN KEY (transportation_document_id)
        REFERENCES transportation_documents (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);