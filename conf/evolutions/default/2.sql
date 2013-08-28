# Tag/Product mapping

# --- !Ups

CREATE TABLE ProductTags
(
  ProductId integer NOT NULL,
  TagId integer NOT NULL,
  FOREIGN KEY (ProductId) REFERENCES Products (Id) ON DELETE CASCADE,
  FOREIGN KEY (TagId) REFERENCES Tags (Id) ON DELETE CASCADE
);

# --- !Downs

DROP TABLE ProductTags;
