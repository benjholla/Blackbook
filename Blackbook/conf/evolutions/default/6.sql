# Add a primary key to the ProductTags table

# --- !Ups

ALTER TABLE ProductTags
  ADD CONSTRAINT ProductTagsPrimaryKey PRIMARY KEY (ProductId, TagId);

# --- !Downs

ALTER TABLE ProductTags
  DROP CONSTRAINT ProductTagsPrimaryKey;
