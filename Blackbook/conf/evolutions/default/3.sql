# Rename some tables.

# --- !Ups

ALTER TABLE Product
    RENAME TO Products;

ALTER TABLE Product_Tags
    RENAME TO ProductTags;

# --- !Downs

ALTER TABLE Products
    RENAME TO Product;

ALTER TABLE ProductTags
    RENAME TO Product_Tags;

