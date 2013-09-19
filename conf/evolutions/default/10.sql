# Adding salted passwords and removing plaintext passwords

# --- !Ups

CREATE EXTENSION pgcrypto; # enabled crypto modules on postgres database

ALTER TABLE Users ADD COLUMN HashedPassword text;
UPDATE Users SET HashedPassword = crypt(Password, gen_salt('md5'));
ALTER TABLE Users DROP COLUMN Password;

# --- !Downs

DROP EXTENSION pgcrypto; # disable crypto modules on postgres database
ALTER TABLE Users DROP COLUMN HashedPassword;
