CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) DEFAULT 'user'
);

CREATE TABLE IF NOT EXISTS prizes (
    id SERIAL PRIMARY KEY,
    prize_id VARCHAR(100) UNIQUE NOT NULL,
    award_year VARCHAR(10) NOT NULL,
    category VARCHAR(50) NOT NULL,
    full_name VARCHAR(255),
    motivation TEXT,
    detail_link VARCHAR(500)
);

CREATE TABLE IF NOT EXISTS laureates (
    id SERIAL PRIMARY KEY,
    prize_id INTEGER REFERENCES prizes(id),
    laureate_id VARCHAR(100) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    portion VARCHAR(10),
    motivation TEXT,
    portrait_url VARCHAR(500)
);

CREATE TABLE IF NOT EXISTS user_prizes (
    user_id INTEGER REFERENCES users(id),
    prize_id INTEGER REFERENCES prizes(id),
    added_at TIMESTAMP NOT NULL,
    PRIMARY KEY (user_id, prize_id)
);