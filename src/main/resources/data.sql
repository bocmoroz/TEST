CREATE TABLE IF NOT EXISTS public.product
(
    id bigint NOT NULL DEFAULT nextval('product_id_seq'::regclass),
    articul character varying(255) COLLATE pg_catalog."default",
    deleted boolean NOT NULL,
    last_change_date timestamp without time zone,
    last_income_price bigint,
    last_sale_price bigint,
    name character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT product_pkey PRIMARY KEY (id),
    CONSTRAINT uk_d2en73jto4x7mryn4actf91sk UNIQUE (articul)
);

CREATE TABLE IF NOT EXISTS public.warehouse
(
    id bigint NOT NULL DEFAULT nextval('warehouse_id_seq'::regclass),
    deleted boolean NOT NULL,
    last_change_date timestamp without time zone,
    name character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT warehouse_pkey PRIMARY KEY (id),
    CONSTRAINT uk_dbmkeyi4co3vmnwnjxoocd4nh UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS public.warehouse_products_count
(
    warehouse_id bigint NOT NULL,
    count integer,
    product_id bigint NOT NULL,
    CONSTRAINT fk483fdyvkfqw6hpngvh965bpbu FOREIGN KEY (warehouse_id)
        REFERENCES public.warehouse (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fkc203lyujxb74fo4y6gncj74gt FOREIGN KEY (product_id)
        REFERENCES public.product (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

CREATE TABLE IF NOT EXISTS public.warehouse_income
(
    id bigint NOT NULL DEFAULT nextval('warehouse_income_id_seq'::regclass),
    create_date timestamp without time zone,
    warehouse_id bigint,
    CONSTRAINT warehouse_income_pkey PRIMARY KEY (id),
    CONSTRAINT fkktlb6jkbqf5jpf0yb7rqx0f01 FOREIGN KEY (warehouse_id)
        REFERENCES public.warehouse (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

CREATE TABLE IF NOT EXISTS public.income_products_count
(
    product_income_id bigint NOT NULL,
    count integer,
    product_id bigint NOT NULL,
    CONSTRAINT fk2bnof4jebfd1rdky7gn8478sf FOREIGN KEY (product_id)
        REFERENCES public.product (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fkhx3dcid8q0g14555ffhfyg1ca FOREIGN KEY (product_income_id)
        REFERENCES public.warehouse_income (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

CREATE TABLE IF NOT EXISTS public.warehouse_sale
(
    id bigint NOT NULL DEFAULT nextval('warehouse_sale_id_seq'::regclass),
    create_date timestamp without time zone,
    warehouse_id bigint,
    CONSTRAINT warehouse_sale_pkey PRIMARY KEY (id),
    CONSTRAINT fkgdvrrdsa42iqrriwudbqs16tg FOREIGN KEY (warehouse_id)
        REFERENCES public.warehouse (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

CREATE TABLE IF NOT EXISTS public.sale_products_count
(
    product_sale_id bigint NOT NULL,
    count integer,
    product_id bigint NOT NULL,
    CONSTRAINT fk7hrfc0jaenaf9ls229s50semv FOREIGN KEY (product_sale_id)
        REFERENCES public.warehouse_sale (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fkn520q9xjo1nbdwyl3fib3q5an FOREIGN KEY (product_id)
        REFERENCES public.product (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

CREATE TABLE IF NOT EXISTS public.warehouse_transportation
(
    id bigint NOT NULL DEFAULT nextval('warehouse_transportation_id_seq'::regclass),
    create_date timestamp without time zone,
    warehouse_from_id bigint,
    warehouse_to_id bigint,
    CONSTRAINT warehouse_transportation_pkey PRIMARY KEY (id),
    CONSTRAINT fkcuhvjonh2cjtt12kjkmpfvl00 FOREIGN KEY (warehouse_from_id)
        REFERENCES public.warehouse (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fkrgipjn1qoxjaw8gveb69lphy9 FOREIGN KEY (warehouse_to_id)
        REFERENCES public.warehouse (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

CREATE TABLE IF NOT EXISTS public.transportation_products_count
(
    product_transportation_id bigint NOT NULL,
    count integer,
    product_id bigint NOT NULL,
    CONSTRAINT fk11fy8n9wxcitjukqu02b324he FOREIGN KEY (product_transportation_id)
        REFERENCES public.warehouse_transportation (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk18bqk926pw7o6te0tixp28sl7 FOREIGN KEY (product_id)
        REFERENCES public.product (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

