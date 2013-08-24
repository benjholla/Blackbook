# adding columns to products and categories

# --- !Ups

ALTER TABLE Products DROP COLUMN Icon;

# --- !Downs

ALTER TABLE Products ADD COLUMN Icon BLOB;
