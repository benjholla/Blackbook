# renaming product/tag labels to names

# --- !Ups

ALTER TABLE Products ALTER COLUMN label RENAME TO name;
ALTER TABLE Tags ALTER COLUMN label RENAME TO name;

# --- !Downs

ALTER TABLE Products ALTER COLUMN name RENAME TO label;
ALTER TABLE Tags ALTER COLUMN name RENAME TO label;

