# 🚨 TROUBLESHOOTING: "Failed to create game" Error

## Problem
When clicking "Play as Guest", you see: **"Failed to create game. Please try again."**

## Root Cause
The **backend is not running**. The frontend tries to call the API at `http://localhost:8080/api/games/guest` but gets no response.

---

## ✅ Solution: Start the Backend

### Quick Fix (Automated)
```bash
# Use the startup script
./scripts/start-backend.sh
```

### Manual Fix (Step by Step)

#### Step 1: Start PostgreSQL
```bash
docker-compose up -d postgres

# Verify it's running
docker ps --filter "name=postgres"
```

#### Step 2: Kill Any Process on Port 8080
```bash
# Find and kill
lsof -ti:8080 | xargs kill -9

# Verify port is free
lsof -i :8080
# Should show: (no output)
```

#### Step 3: Start Backend
```bash
cd backend
./gradlew bootRun

# Or in background:
nohup ./gradlew bootRun > backend.log 2>&1 &
```

#### Step 4: Wait for Backend to Start
```bash
# Watch the logs
tail -f backend/backend.log

# Wait for this message:
# "Started ChessApplication in X.XXX seconds"
```

#### Step 5: Verify Backend is Running
```bash
curl http://localhost:8080/actuator/health
# Expected: {"status":"UP"}
```

---

## 🎯 Complete Startup Checklist

Follow this order:

1. **Start PostgreSQL**
   ```bash
   docker-compose up -d postgres
   ```
   ✅ Verify: `docker ps --filter "name=postgres"`

2. **Start Backend**
   ```bash
   cd backend
   ./gradlew bootRun
   ```
   ✅ Verify: `curl http://localhost:8080/actuator/health`

3. **Start Frontend** (already running)
   ```bash
   cd frontend
   bun run dev
   ```
   ✅ Verify: `lsof -i :5173`

4. **Test the Application**
   - Open: http://localhost:5173
   - Click: "Play as Guest"
   - Should work now! ✅

---

## 🔍 Diagnostic Commands

### Check What's Running
```bash
# PostgreSQL
docker ps --filter "name=postgres"

# Backend (port 8080)
lsof -i :8080

# Frontend (port 5173)
lsof -i :5173
```

### Check Backend Logs
```bash
# If started in background
tail -f backend/backend.log

# If running in terminal
# Logs show directly
```

### Test Backend Endpoints
```bash
# Health check
curl http://localhost:8080/actuator/health

# Create guest game (should work)
curl -X POST http://localhost:8080/api/games/guest \
  -H "Content-Type: application/json" \
  -d '{"guestUsername":"TestUser"}'
```

---

## ❌ Common Errors & Fixes

### Error: "Port 8080 already in use"
**Fix**:
```bash
lsof -ti:8080 | xargs kill -9
# Then restart backend
```

### Error: "Connection refused to PostgreSQL"
**Fix**:
```bash
docker-compose up -d postgres
sleep 5  # Wait for it to start
# Then restart backend
```

### Error: "Liquibase migration failed"
**Fix**:
```bash
# Drop and recreate database
docker-compose down postgres
docker-compose up -d postgres
# Liquibase will run migrations on next backend start
```

### Error: Backend starts then crashes
**Check logs**:
```bash
cd backend
./gradlew bootRun
# Read the error output
```

---

## 🎮 Testing Guest Game Creation

Once backend is running, test from command line:

```bash
# Create a guest game
curl -X POST http://localhost:8080/api/games/guest \
  -H "Content-Type: application/json" \
  -d '{"guestUsername":"Player1"}' | jq

# Expected response:
# {
#   "message": "Game created successfully",
#   "data": {
#     "gameId": "uuid-here",
#     "guestUserId": "uuid-here",
#     "color": "white" (or "black")
#   }
# }
```

---

## 🚀 Automated Startup Script

We created `scripts/start-backend.sh` that:
1. ✅ Checks PostgreSQL status
2. ✅ Starts PostgreSQL if needed
3. ✅ Checks port 8080 availability
4. ✅ Starts backend
5. ✅ Waits for backend to be ready
6. ✅ Verifies health endpoint

**Usage**:
```bash
./scripts/start-backend.sh
```

---

## 📊 Expected Running State

When everything is working:

| Service    | Status | Port | URL |
|------------|--------|------|-----|
| PostgreSQL | ✅ Running | 5432 | N/A |
| Backend    | ✅ Running | 8080 | http://localhost:8080 |
| Frontend   | ✅ Running | 5173 | http://localhost:5173 |

---

## 🎉 Success Indicators

You'll know everything is working when:

1. ✅ `curl http://localhost:8080/actuator/health` returns `{"status":"UP"}`
2. ✅ Frontend loads at `http://localhost:5173`
3. ✅ Clicking "Play as Guest" creates a game
4. ✅ You see the chess board
5. ✅ No error messages in browser console
6. ✅ Can make moves on the board

---

## 🛑 How to Stop Everything

```bash
# Stop backend
pkill -f 'gradle.*bootRun'

# Stop PostgreSQL
docker-compose down

# Stop frontend
# Just Ctrl+C in the terminal where it's running
```

---

## 💡 Pro Tips

1. **Use separate terminals** for backend and frontend so you can see logs
2. **Keep backend.log open** in another terminal: `tail -f backend/backend.log`
3. **Check health endpoint first** before testing frontend
4. **PostgreSQL data persists** between restarts (in Docker volume)
5. **Backend takes 10-15 seconds** to start completely

---

## 📝 Summary

**The error "Failed to create game" means the backend is not running.**

**Fix**: Start the backend with `./scripts/start-backend.sh` or manually with the steps above.

**Verify**: `curl http://localhost:8080/actuator/health` should return `{"status":"UP"}`

**Then**: The frontend will work and you can play chess! ♟️

---

**🎯 Your Next Step: Run `./scripts/start-backend.sh` and wait for it to complete!**

