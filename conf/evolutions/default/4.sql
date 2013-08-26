# Add users table

# --- !Ups 

CREATE TABLE Users
(
  Id serial 
    NOT NULL, 

  Name text 
    NOT NULL, 

  Password text 
    NOT NULL,

  Email text 
    NOT NULL,

  CreatedAt timestamp with time zone 
    DEFAULT now(),

  LastModified timestamp with time zone 
    DEFAULT now(),

  LastLogin timestamp with time zone 
    DEFAULT timestamp with time zone '1970-1-1 00:00:00-00',

  Permissions bit(32) 
    DEFAULT 0::bit(32), 

  CONSTRAINT UserPrimaryKey 
    PRIMARY KEY (Id),

  CONSTRAINT UserNameUnique 
    UNIQUE (Name)
);

CREATE TRIGGER UpdateUsersTimestamp BEFORE UPDATE ON Users
  FOR EACH ROW EXECUTE PROCEDURE UpdateLastModified();

# --- !Downs

DROP TABLE Users;
