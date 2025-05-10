-- Create database (for PostgreSQL the syntax is different)
-- PostgreSQL doesn't support "IF NOT EXISTS" for CREATE DATABASE
-- We need to check if the database exists first

CREATE DATABASE currency_db;
\c currency_db;

-- Create conversions table for main service
CREATE TABLE IF NOT EXISTS conversions (
    id SERIAL PRIMARY KEY,
    from_currency VARCHAR(3) NOT NULL,
    to_currency VARCHAR(3) NOT NULL,
    amount DECIMAL(19, 4) NOT NULL,
    converted_amount DECIMAL(19, 4) NOT NULL,
    exchange_rate DECIMAL(19, 6) NOT NULL, 
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

