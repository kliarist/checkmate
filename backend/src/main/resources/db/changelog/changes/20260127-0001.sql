--liquibase formatted sql

--changeset checkmate:5
CREATE TABLE game_clocks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    game_id UUID NOT NULL UNIQUE REFERENCES games(id) ON DELETE CASCADE,
    white_time_ms BIGINT NOT NULL,
    black_time_ms BIGINT NOT NULL,
    increment_ms BIGINT NOT NULL DEFAULT 0,
    delay_ms BIGINT NOT NULL DEFAULT 0,
    current_turn VARCHAR(10) NOT NULL DEFAULT 'white',
    is_paused BOOLEAN NOT NULL DEFAULT FALSE,
    last_move_time TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_game_clocks_game_id ON game_clocks(game_id);

--changeset checkmate:6
CREATE TABLE matchmaking_queue (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    rating INTEGER NOT NULL,
    time_control VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_matchmaking_user_id ON matchmaking_queue(user_id);
CREATE INDEX idx_matchmaking_time_control ON matchmaking_queue(time_control);
CREATE INDEX idx_matchmaking_rating ON matchmaking_queue(rating);
