# Change permissions to an integer

# --- !Ups

ALTER TABLE Users
  ALTER COLUMN Permissions DROP DEFAULT,
  ALTER COLUMN Permissions SET DATA TYPE integer USING Permissions::integer,
  ALTER COLUMN Permissions SET DEFAULT 0;

# --- !Downs

ALTER TABLE Users
  ALTER COLUMN Permissions DROP DEFAULT,
  ALTER COLUMN Permissions SET DATA TYPE bit(32) USING Permissions::bit(32),
  ALTER COLUMN Permissions SET DEFAULT 0::bit(32)
