# Products schema
 
# --- !Ups

CREATE SEQUENCE product_id_seq;
CREATE TABLE product (
    id integer NOT NULL DEFAULT nextval('product_id_seq'),
    label varchar(255)
);
 
# --- !Downs
 
DROP TABLE product;
DROP SEQUENCE product_id_seq;
