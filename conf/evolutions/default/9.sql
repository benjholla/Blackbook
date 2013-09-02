# System-wide announcements

# --- !Ups

CREATE TABLE Announcements
(
  Id serial NOT NULL, 
  Message text NOT NULL,
  Enabled boolean DEFAULT 'yes',
  CreatedAt timestamp with time zone DEFAULT now(), 
  LastModified timestamp with time zone DEFAULT now()
);

CREATE TRIGGER UpdateAnnouncementTimestamp BEFORE UPDATE ON Announcements 
  FOR EACH ROW EXECUTE PROCEDURE UpdateLastModified();

# --- !Downs
DROP TRIGGER UpdateAnnouncementTimestamp ON Transactions;
DROP TABLE Announcements;