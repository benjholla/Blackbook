# Add the disabled flag to some items.

# --- !Ups

ALTER TABLE Products
  ADD COLUMN Enabled boolean DEFAULT 'yes';

ALTER TABLE Users 
  ADD COLUMN Enabled boolean DEFAULT 'yes';

# --- !Downs

ALTER TABLE Products
  DROP COLUMN Enabled;

ALTER TABLE Users
  DROP COLUMN Enabled;
