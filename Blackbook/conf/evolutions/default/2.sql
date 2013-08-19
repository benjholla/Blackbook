# Tags schema

# --- !Ups

ALTER TABLE product 
    ALTER COLUMN id integer NOT NULL AUTO_INCREMENT;

ALTER TABLE product
    ADD PRIMARY KEY (id);

DROP SEQUENCE product_id_seq;

CREATE TABLE tags (
    id integer NOT NULL AUTO_INCREMENT,
    label varchar(255) UNIQUE,
    PRIMARY KEY(id)
);

CREATE TABLE product_tags (
    ProductId integer,
    TagId integer,
    FOREIGN KEY (ProductId) REFERENCES Product(ID) ON DELETE CASCADE,
    FOREIGN KEY (TagId) REFERENCES Tags(ID) ON DELETE CASCADE,
    PRIMARY KEY (ProductId, TagId)
);

# -- !Downs

ADD SEQUENCE product_id_seq;

ALTER TABLE product
    ALTER COLUMN id integer NOT NULL DEFAULT nextval('product_id_seq'),
    DROP PRIMARY KEY;

DROP TABLE tags
