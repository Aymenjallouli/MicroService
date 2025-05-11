-- Alter the file_data column to LONGBLOB to store large files
ALTER TABLE depense MODIFY COLUMN file_data LONGBLOB;
