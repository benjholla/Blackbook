# Tag/Product mapping

# --- !Ups

CREATE TABLE Transactions
(
  Id serial NOT NULL, 
  ProductId integer NOT NULL,
  UserName text NOT NULL,
  Quantity integer NOT NULL,
  Amount numeric NOT NULL,
  Notes text,
  FOREIGN KEY (ProductId) REFERENCES Products (Id) ON DELETE CASCADE,
  FOREIGN KEY (UserName) REFERENCES Users (Name) ON DELETE CASCADE,
  Cancelled boolean DEFAULT 'no',
  CreatedAt timestamp with time zone DEFAULT now(), 
  LastModified timestamp with time zone DEFAULT now(), 
  CONSTRAINT TransactionPrimaryKey PRIMARY KEY (Id)
);

CREATE TRIGGER UpdateTransactionTimestamp BEFORE UPDATE ON Transactions 
  FOR EACH ROW EXECUTE PROCEDURE UpdateLastModified();

# --- !Downs
DROP TRIGGER UpdateTransactionTimestamp ON Transactions;
DROP TABLE Transactions;