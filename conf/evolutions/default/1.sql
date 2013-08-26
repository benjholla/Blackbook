# Original schema

# --- !Ups

CREATE TABLE Products
(
  Id serial NOT NULL, 
  Name text NOT NULL, 
  Description text, 
  CreatedAt timestamp with time zone DEFAULT now(), 
  LastModified timestamp with time zone DEFAULT now(), 
  CONSTRAINT ProductPrimaryKey PRIMARY KEY (Id), 
  CONSTRAINT ProductNameUnique UNIQUE (Name)
);

CREATE TABLE Tags
(
  Id serial NOT NULL,
  Name text NOT NULL,
  CreatedAt timestamp with time zone DEFAULT now(),
  LastModified timestamp with time zone DEFAULT now(),
  CONSTRAINT TagPrimaryKey PRIMARY KEY (Id),
  CONSTRAINT TagNameUnique UNIQUE (Name)
);

# --- !Downs 

DROP TABLE Products;
DROP TABLE Tags;

