-- Users table
CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(36) PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    roles JSON NOT NULL DEFAULT ('["USER"]'),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_is_active (is_active),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Add user_id column to characters table
ALTER TABLE characters 
ADD COLUMN user_id VARCHAR(36) AFTER id,
ADD CONSTRAINT fk_characters_user_id 
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

-- Add index for user_id
ALTER TABLE characters 
ADD INDEX idx_user_id (user_id);

-- Insert default admin user (password: admin123)
INSERT INTO users (id, username, email, password_hash, roles, is_active) VALUES 
('00000000-0000-0000-0000-000000000001', 
 'admin', 
 'admin@textrpg.com', 
 '$2a$10$EqKwQKUjQQ7zMwcXCNHyWu.V.VwKSLGrjTlIr/Rvz3Yh4VWL6/QE.', -- admin123
 '["USER", "ADMIN"]', 
 true);
