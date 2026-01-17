--liquibase formatted sql

--changeset checkmate:5
ALTER TABLE users ADD COLUMN is_guest BOOLEAN DEFAULT false;

