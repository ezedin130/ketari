-- V9__drop_refresh_tokens_table.sql
-- Retiring database-backed refresh tokens in favor of secure stateless JWT refresh tokens

DROP TABLE IF EXISTS refresh_tokens;
