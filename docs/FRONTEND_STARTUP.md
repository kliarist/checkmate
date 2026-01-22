# Frontend Startup Instructions - COMPLETE GUIDE

## ✅ All Issues Fixed

The following issues have been resolved:
1. ✅ Redis connection failure - Redis removed from dependencies
2. ✅ UserDetailsService bean missing - Implementation added
3. ✅ WebSocket auth dependency - Removed for guest users
4. ✅ STOMP import error - Fixed StompSubscription import

## 🚀 How to Start the Application

### Step 1: Install Frontend Dependencies

```bash
cd frontend
bun install
```

**Expected Output**: Should install ~500 packages

### Step 2: Start PostgreSQL

```bash
# From project root
docker-compose up -d postgres

# Verify it's running
docker ps --filter "name=postgres"
```

**Expected**: `checkmate-postgres` container running on port 5432

### Step 3: Start Backend

```bash
# Option A: Using Gradle
cd backend
./gradlew bootRun

# Option B: Using IntelliJ IDEA
# Open ChessApplication.java and click Run
```

**Wait for**: `Started ChessApplication in X.XXX seconds`

**Verify**:
```bash
curl http://localhost:8080/actuator/health
# Should return: {"status":"UP"}
```

### Step 4: Start Frontend

```bash
# In a new terminal
cd frontend
bun run dev
```

**Expected Output**:
```
VITE v5.x.x  ready in XXX ms

➜  Local:   http://localhost:5173/
➜  Network: use --host to expose
```

### Step 5: Open Browser

```
http://localhost:5173
```

**You should see**:
- "Welcome to Checkmate Chess" heading
- "Play chess instantly without registration" text
- Username input field (optional)
- Green "Play as Guest" button

---

## 🐛 Troubleshooting

### Issue: Frontend shows blank page

**Cause**: Backend not running or WebSocket connection failed

**Solution**:
1. Verify backend is running: `curl http://localhost:8080/actuator/health`
2. Check browser console for errors (F12)
3. Restart both frontend and backend

### Issue: "bun: command not found"

**Solution**: Install Bun
```bash
curl -fsSL https://bun.sh/install | bash
```

### Issue: STOMP import error

**Already Fixed**: Committed in latest version
```bash
git pull origin main  # Get latest fixes
cd frontend
bun install          # Reinstall dependencies
```

### Issue: PostgreSQL connection refused

**Solution**: Start PostgreSQL
```bash
docker-compose up -d postgres
```

### Issue: Port 5173 already in use

**Solution**: Kill existing process
```bash
lsof -ti:5173 | xargs kill -9
```

### Issue: Port 8080 already in use

**Solution**: Kill existing process
```bash
lsof -ti:8080 | xargs kill -9
```

---

## 📋 Complete Startup Checklist

- [ ] Bun installed (`bun --version`)
- [ ] Frontend dependencies installed (`cd frontend && bun install`)
- [ ] PostgreSQL running (`docker ps --filter "name=postgres"`)
- [ ] Backend running (`curl http://localhost:8080/actuator/health`)
- [ ] Frontend running (`lsof -i :5173`)
- [ ] Browser open to `http://localhost:5173`
- [ ] Homepage displays "Welcome to Checkmate Chess"

---

## 🎮 Test the Application

Once everything is running:

1. **Click "Play as Guest"**
   - ✅ Loading spinner appears
   - ✅ Navigates to `/game/{id}`
   - ✅ Chess board displays

2. **Make a Move**
   - ✅ Drag a piece (e.g., e2 pawn to e4)
   - ✅ Piece moves smoothly
   - ✅ Move appears in history

3. **Check Features**
   - ✅ Move history updates
   - ✅ Flip board button works
   - ✅ Resign button available
   - ✅ Error messages show if needed

---

## 🔧 Development Commands

### Frontend
```bash
cd frontend

# Start dev server
bun run dev

# Build for production
bun run build

# Run tests
bun run test

# Run E2E tests
bun run test:e2e

# Lint code
bun run lint

# Format code
bun run format
```

### Backend
```bash
cd backend

# Start application
./gradlew bootRun

# Build
./gradlew build

# Run tests
./gradlew test

# Build Docker image
./gradlew bootBuildImage
```

---

## 📊 Ports Reference

| Service    | Port | URL                          |
|------------|------|------------------------------|
| Frontend   | 5173 | http://localhost:5173        |
| Backend    | 8080 | http://localhost:8080        |
| PostgreSQL | 5432 | jdbc:postgresql://localhost:5432/checkmate_dev |
| Health     | 8080 | http://localhost:8080/actuator/health |
| WebSocket  | 8080 | ws://localhost:8080/ws       |

---

## ✅ Success Criteria

**You'll know it's working when**:

1. ✅ Frontend loads at `http://localhost:5173`
2. ✅ "Welcome to Checkmate Chess" displays
3. ✅ No errors in browser console (F12)
4. ✅ Backend health check returns `{"status":"UP"}`
5. ✅ PostgreSQL container is running
6. ✅ Can click "Play as Guest" and see chess board
7. ✅ Can make moves and see them update

---

## 🎉 All Fixed!

**Changes Committed**:
- ✅ Redis dependency removed
- ✅ UserDetailsService implementation added
- ✅ WebSocket works without authentication
- ✅ STOMP import corrected
- ✅ Docker-compose simplified
- ✅ Documentation complete

**Your application is ready to run!** 🚀

Just follow the 5 steps above and you'll have a working chess application.

---

## 📞 Need Help?

Check these files:
- `QUICKSTART.md` - Quick start guide
- `README.md` - Project overview
- `backend/README.md` - Backend specific docs
- `PHASE3_COMPLETE.md` - Feature completion status

All issues have been resolved and committed to git.

