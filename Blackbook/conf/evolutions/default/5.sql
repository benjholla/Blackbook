# making product names unique

# --- !Ups

ALTER TABLE Products ADD CONSTRAINT UniqueName UNIQUE(Name)

# --- !Downs

ALTER TABLE Products DROP CONSTRAINT UniqueName

