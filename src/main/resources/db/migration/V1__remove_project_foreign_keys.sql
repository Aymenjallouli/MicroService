-- Drop foreign key constraints that reference Project
ALTER TABLE facture DROP FOREIGN KEY IF EXISTS FK6u1maco09cnvbtcwgnaweocl4;
ALTER TABLE depense DROP FOREIGN KEY IF EXISTS FKq0rx6beo4skeoudjq4eof51kx;

-- Convert project_id column to simple bigint without foreign key constraint
ALTER TABLE facture MODIFY project_id BIGINT NULL;
