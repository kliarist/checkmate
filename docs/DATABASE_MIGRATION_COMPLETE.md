# Database Migration Complete

## Summary
Created missing Liquibase migration for `game_clocks` and `matchmaking_queue` tables. The `game_invitations` table migration was already present in the initial migration file.

## Changes Made

### 1. Created New Migration File
**File**: `backend/src/main/resources/db/changelog/changes/20260127-0001.sql`

Added two new changesets:
- **Changeset 5**: `game_clocks` table
- **Changeset 6**: `matchmaking_queue` table

### 2. Updated Application Configuration
**File**: `backend/src/main/resources/application.yml`

Added Liquibase configuration:
```yaml
liquibase:
  change-log: classpath:db/changelog/db.changelog-master.yaml
  enabled: true
```

### 3. Table Schemas

#### game_clocks
- `id` (UUID, PRIMARY KEY)
- `game_id` (UUID, UNIQUE, FK to games)
- `white_time_ms` (BIGINT)
- `black_time_ms` (BIGINT)
- `increment_ms` (BIGINT, DEFAULT 0)
- `delay_ms` (BIGINT, DEFAULT 0)
- `current_turn` (VARCHAR(10), DEFAULT 'white')
- `is_paused` (BOOLEAN, DEFAULT FALSE)
- `last_move_time` (TIMESTAMP)
- `created_at` (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)

Indexes:
- `idx_game_clocks_game_id` on `game_id`

#### matchmaking_queue
- `id` (UUID, PRIMARY KEY)
- `user_id` (UUID, FK to users)
- `rating` (INTEGER)
- `time_control` (VARCHAR(50))
- `created_at` (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)

Indexes:
- `idx_matchmaking_user_id` on `user_id`
- `idx_matchmaking_time_control` on `time_control`
- `idx_matchmaking_rating` on `rating`

#### game_invitations (Already Existed)
- `id` (UUID, PRIMARY KEY)
- `creator_id` (UUID, FK to users)
- `invitation_code` (VARCHAR(100), UNIQUE)
- `time_control` (VARCHAR(50))
- `game_type` (VARCHAR(50))
- `status` (VARCHAR(50), DEFAULT 'PENDING')
- `created_at` (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)
- `expires_at` (TIMESTAMP)

Indexes:
- `idx_invitations_code` on `invitation_code`
- `idx_invitations_creator` on `creator_id`
- `idx_invitations_status` on `status`

## Build Status
✅ Build successful with `./gradlew clean build -x test`
✅ Application started successfully
✅ Liquibase migrations applied automatically on startup

## Next Steps
The database tables are now created and the application should be fully functional. You can verify the tables exist by connecting to your PostgreSQL database:

```sql
\dt  -- List all tables
\d game_clocks  -- Describe game_clocks table
\d matchmaking_queue  -- Describe matchmaking_queue table
\d game_invitations  -- Describe game_invitations table
```

## Files Modified
1. `backend/src/main/resources/db/changelog/changes/20260127-0001.sql` (created)
2. `backend/src/main/resources/application.yml` (updated)
