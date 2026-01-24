# Quick Start Guide

## Current Status

✅ **Frontend is running** on `http://localhost:5173`  
❌ **Backend is NOT running** - needs to be started

## Why Nothing is Displaying

The frontend is running, but it needs the backend to be running first. The page appears blank because:
1. Frontend tries to connect to backend API (`http://localhost:8080`)
2. Backend is not running, so connection fails
3. WebSocket connection fails
4. React app waits for backend connection

## How to Start the Application

### Option 1: Start Backend in Terminal

```bash
# Terminal 1: Start Backend
cd backend
./gradlew bootRun

# Wait for "Started ChessApplication" message
# Then open http://localhost:5173 in browser
```

### Option 2: Start Backend in IntelliJ IDEA

1. Open `backend/src/main/java/com/checkmate/chess/ChessApplication.java`
2. Click the green play button next to `main()` method
3. Wait for application to start
4. Open `http://localhost:5173` in browser

### Option 3: Use Docker Compose

```bash
# Start backend + database + Redis
docker-compose up postgres backend

# Wait for services to be healthy
# Then open http://localhost:5173 in browser
```

## Verify Backend is Running

```bash
# Should return: {"status":"UP"}
curl http://localhost:8080/actuator/health
```

## What You Should See

Once backend is running:

1. **Homepage**: `http://localhost:5173`
   - "Welcome to Checkmate Chess"
   - "Play as Guest" button
   - Username input (optional)

2. **Click "Play as Guest"**:
   - Loading spinner appears
   - Navigates to game board
   - Chess board displays
   - Can make moves

## Troubleshooting

### Frontend shows blank page
- ✅ Frontend is running (port 5173)
- ❌ Backend needs to be started (port 8080)
- **Solution**: Start backend (see instructions above)

### Backend fails to start
- Check PostgreSQL is running: `psql -U postgres -d checkmate_dev`
- Start with Docker: `docker-compose up postgres`
- Check logs for errors

### Port already in use
```bash
# Kill process on port 5173 (frontend)
lsof -ti:5173 | xargs kill -9

# Kill process on port 8080 (backend)
lsof -ti:8080 | xargs kill -9
```

## Complete Startup Sequence

```bash
# Terminal 1: Start PostgreSQL
docker-compose up postgres

# Terminal 2: Start Backend
cd backend
./gradlew bootRun

# Terminal 3: Frontend (already running)
# Go to http://localhost:5173
```

## Frontend is Already Running

✅ Frontend dev server is active on port 5173  
✅ Vite is serving the application  
✅ Hot reload is enabled  

**Next Step**: Start the backend!

---

**Current Issue**: Backend not running  
**Solution**: Follow "How to Start the Application" above

