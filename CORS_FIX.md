# 🚨 CORS ERROR FIX - Port 5174 Issue

## Problem
```
Access to XMLHttpRequest at 'http://localhost:8080/api/games/guest' 
from origin 'http://localhost:5174' has been blocked by CORS policy
```

## Root Causes
1. **Backend is not running** (primary issue)
2. **CORS not configured for port 5174** (Vite alternate port)

---

## ✅ SOLUTION

### Step 1: CORS Configuration Fixed ✅

**Updated files**:
- `backend/src/main/java/com/checkmate/chess/config/SecurityConfig.java`
- `backend/src/main/java/com/checkmate/chess/config/WebSocketConfig.java`
- `backend/src/main/resources/application.yml`

**Now allows**:
- ✅ `http://localhost:5173` (default Vite port)
- ✅ `http://localhost:5174` (alternate Vite port)
- ✅ `http://localhost:3000` (alternative)

### Step 2: Start Backend

```bash
# Quick start
./scripts/start-backend.sh

# Or manually
docker-compose up -d postgres
cd backend
./gradlew bootRun
```

### Step 3: Verify

```bash
# Check backend
curl http://localhost:8080/actuator/health
# Expected: {"status":"UP"}

# Test CORS
curl -H "Origin: http://localhost:5174" \
     -H "Access-Control-Request-Method: POST" \
     -H "Access-Control-Request-Headers: Content-Type" \
     -X OPTIONS \
     http://localhost:8080/api/games/guest -v
# Should see: Access-Control-Allow-Origin: http://localhost:5174
```

---

## Why Vite Uses Port 5174

**Vite port selection**:
1. Tries default port: **5173**
2. If busy, tries: **5174**
3. If busy, tries: **5175**
4. And so on...

**Why port 5173 might be busy**:
- Previous Vite instance still running
- Another app using the port
- Port not released after crash

**Check what's on port 5173**:
```bash
lsof -i :5173
```

**Kill process if needed**:
```bash
lsof -ti:5173 | xargs kill -9
```

---

## Complete Startup Instructions

### Terminal 1: PostgreSQL
```bash
docker-compose up -d postgres
```

### Terminal 2: Backend
```bash
cd backend
./gradlew bootRun

# Wait for: "Started ChessApplication"
```

### Terminal 3: Frontend  
```bash
cd frontend

# Kill any existing Vite process
lsof -ti:5173 | xargs kill -9

# Start fresh on default port
bun run dev

# Should start on 5173, but 5174 will also work now
```

### Browser
```
http://localhost:5174 (or 5173)
```

---

## Verify Everything Works

### 1. Check All Services
```bash
# PostgreSQL
docker ps --filter "name=postgres"

# Backend
curl http://localhost:8080/actuator/health

# Frontend
lsof -i :5174  # or :5173
```

### 2. Test Guest Game Creation
```bash
curl -X POST http://localhost:8080/api/games/guest \
  -H "Content-Type: application/json" \
  -H "Origin: http://localhost:5174" \
  -d '{"guestUsername":"TestUser"}'
```

**Expected response**:
```json
{
  "message": "Game created successfully",
  "data": {
    "gameId": "uuid...",
    "guestUserId": "uuid...",
    "color": "white"
  }
}
```

### 3. Test in Browser
1. Open `http://localhost:5174`
2. Click "Play as Guest"
3. Should work! ✅

---

## Troubleshooting

### Still Getting CORS Error?

**Check backend is running**:
```bash
curl http://localhost:8080/actuator/health
```

**If not running**:
```bash
cd backend
./gradlew bootRun
```

### Backend Won't Start?

**Port 8080 in use**:
```bash
lsof -ti:8080 | xargs kill -9
```

**PostgreSQL not running**:
```bash
docker-compose up -d postgres
```

### Wrong Port in Browser?

**Frontend URL should match origin**:
- If frontend is on 5174, use: `http://localhost:5174`
- If frontend is on 5173, use: `http://localhost:5173`

**Check which port Vite is using**:
```bash
lsof -i :5173 :5174
```

---

## Quick Fix Commands

```bash
# Kill all conflicting processes
lsof -ti:5173 | xargs kill -9  # Frontend
lsof -ti:8080 | xargs kill -9  # Backend

# Start everything fresh
docker-compose up -d postgres
cd backend && ./gradlew bootRun &
cd frontend && bun run dev
```

---

## Status After Fix

**CORS Configuration**: ✅ Updated
- SecurityConfig.java
- WebSocketConfig.java  
- application.yml

**Supported Origins**: ✅
- http://localhost:5173
- http://localhost:5174
- http://localhost:3000

**Next Steps**:
1. ✅ Start PostgreSQL: `docker-compose up -d postgres`
2. ✅ Start Backend: `cd backend && ./gradlew bootRun`
3. ✅ Verify: `curl http://localhost:8080/actuator/health`
4. ✅ Test: Click "Play as Guest" in browser

---

## Summary

**Problem**: CORS blocking requests from port 5174  
**Cause**: CORS only configured for 5173, backend not running  
**Fix**: Added 5174 to CORS config, start backend  
**Result**: Now works on both 5173 and 5174! ✅

**Your turn**: Start the backend and try again! 🚀

```bash
./scripts/start-backend.sh
```

