-- Character table with new structure
CREATE TABLE IF NOT EXISTS characters (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    level INT NOT NULL DEFAULT 1,
    
    -- Experience fields
    current_experience BIGINT NOT NULL DEFAULT 0,
    max_experience BIGINT NOT NULL DEFAULT 100,
    experience_for_next_level BIGINT NOT NULL DEFAULT 100,
    
    -- Health fields
    current_health INT NOT NULL,
    max_health INT NOT NULL,
    
    -- Stats fields
    strength INT NOT NULL DEFAULT 10,
    dexterity INT NOT NULL DEFAULT 10,
    intelligence INT NOT NULL DEFAULT 10,
    luck INT NOT NULL DEFAULT 5,
    
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    
    INDEX idx_name (name),
    INDEX idx_level (level),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Character Status table (for status effects)
CREATE TABLE IF NOT EXISTS character_statuses (
    id VARCHAR(36) PRIMARY KEY,
    character_id VARCHAR(36) NOT NULL,
    status_type VARCHAR(20) NOT NULL,
    intensity INT NOT NULL DEFAULT 1,
    duration INT NOT NULL,
    max_duration INT NOT NULL,
    stackable BOOLEAN NOT NULL DEFAULT FALSE,
    applied_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (character_id) REFERENCES characters(id) ON DELETE CASCADE,
    INDEX idx_character_status (character_id, status_type),
    INDEX idx_duration (duration)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Skills table
CREATE TABLE IF NOT EXISTS skills (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description TEXT,
    type VARCHAR(20) NOT NULL,
    damage_multiplier DECIMAL(5,2) DEFAULT 1.0,
    mana_cost INT DEFAULT 0,
    cooldown_seconds INT DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_type (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Character skills relationship
CREATE TABLE IF NOT EXISTS character_skills (
    character_id VARCHAR(36) NOT NULL,
    skill_id VARCHAR(36) NOT NULL,
    skill_level INT NOT NULL DEFAULT 1,
    last_used_at TIMESTAMP NULL,
    PRIMARY KEY (character_id, skill_id),
    FOREIGN KEY (character_id) REFERENCES characters(id) ON DELETE CASCADE,
    FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Story chapters
CREATE TABLE IF NOT EXISTS story_chapters (
    id VARCHAR(36) PRIMARY KEY,
    chapter_number INT NOT NULL UNIQUE,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    requirements JSON,
    rewards JSON,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_chapter_number (chapter_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Character story progress
CREATE TABLE IF NOT EXISTS character_story_progress (
    character_id VARCHAR(36) NOT NULL,
    chapter_id VARCHAR(36) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'NOT_STARTED',
    started_at TIMESTAMP NULL,
    completed_at TIMESTAMP NULL,
    PRIMARY KEY (character_id, chapter_id),
    FOREIGN KEY (character_id) REFERENCES characters(id) ON DELETE CASCADE,
    FOREIGN KEY (chapter_id) REFERENCES story_chapters(id) ON DELETE CASCADE,
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Game sessions
CREATE TABLE IF NOT EXISTS game_sessions (
    id VARCHAR(36) PRIMARY KEY,
    character_id VARCHAR(36) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    started_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ended_at TIMESTAMP NULL,
    metadata JSON,
    FOREIGN KEY (character_id) REFERENCES characters(id) ON DELETE CASCADE,
    INDEX idx_character_status (character_id, status),
    INDEX idx_started_at (started_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
