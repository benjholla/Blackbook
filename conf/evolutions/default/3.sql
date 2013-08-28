# Update timestamp triggers

# --- !Ups

CREATE FUNCTION UpdateLastModified() RETURNS TRIGGER AS 
  $BODY$ BEGIN NEW.LastModified = now();; return NEW;; END;; $BODY$ 
  LANGUAGE plpgsql;

CREATE TRIGGER UpdateProductTimestamp BEFORE UPDATE ON Products 
  FOR EACH ROW EXECUTE PROCEDURE UpdateLastModified();

CREATE TRIGGER UpdateTagTimestamp BEFORE UPDATE ON Tags
  FOR EACH ROW EXECUTE PROCEDURE UpdateLastModified();

# --- !Downs

DROP TRIGGER UpdateProductTimestamp ON ProductTags;
DROP TRIGGER UpdateTagTimestamp ON ProductTags;
DROP FUNCTION UpdateLastModified();
