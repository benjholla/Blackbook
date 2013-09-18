# Adding salted passwords and removing plaintext passwords

# --- !Ups

ALTER TABLE Users ADD COLUMN HashedPassword text;
UPDATE Users SET HashedPassword = crypt(Password, gen_salt('md5'));
ALTER TABLE Users DROP COLUMN Password;

# --- !Downs

ALTER TABLE Users DROP COLUMN HashedPassword;