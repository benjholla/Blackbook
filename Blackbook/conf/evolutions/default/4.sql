# renaming product/tag labels to names

# --- !Ups

ALTER TABLE Products DROP label;
ALTER TABLE Products ADD name varchar(255);

ALTER TABLE Tags DROP label;
ALTER TABLE Tags ADD name varchar(255);

# --- !Downs

ALTER TABLE Products DROP name;
ALTER TABLE Tags DROP name;

