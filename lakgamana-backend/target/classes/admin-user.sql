-- Admin User Creation Script for Lakgamana
-- Run this script to create an admin user for accessing the admin dashboard

-- First, let's check if the users table exists and create it if needed
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(255) UNIQUE NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    last_login DATETIME,
    total_bookings INT DEFAULT 0,
    preferred_seat_class VARCHAR(50),
    preferred_route VARCHAR(100),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Insert admin user
-- Password: Admin123! (encrypted with BCrypt)
INSERT INTO users (
    user_id,
    first_name,
    last_name,
    email,
    phone,
    password,
    role,
    status,
    total_bookings,
    created_at,
    updated_at
) VALUES (
    'ADM001',
    'Admin',
    'User',
    'admin@lakgamana.com',
    '+94 77 123 4567',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', -- Admin123!
    'ADMIN',
    'ACTIVE',
    0,
    NOW(),
    NOW()
) ON DUPLICATE KEY UPDATE
    password = VALUES(password),
    updated_at = NOW();

-- Verify the admin user was created
SELECT 
    user_id,
    first_name,
    last_name,
    email,
    role,
    status,
    created_at
FROM users 
WHERE email = 'admin@lakgamana.com';
