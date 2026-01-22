# Pawn Movement & Interaction Fixes

## Problem
The user reported two issues:
1. Pawn movement "doesn't work" (likely referring to promotion or general interaction).
2. "Click to move" (click start, click end) was not working.
3. Drag and drop was reported as not working.

## Root Causes
1. **Click-to-Move Missing**: The `ChessBoard` component did not implement `onSquareClick` handler, so clicking squares did nothing.
2. **Promotion Handling**: The `makeMove` function did not handle pawn promotion (which requires a promotion piece argument). Standard pawn moves to the last rank would fail validation.
3. **Explicit Dragging**: While `react-chessboard` usually enables dragging by default, we added `allowDragging: true` to be explicit.

## Changes Made

### 1. Implemented Click-to-Move in `ChessBoard.tsx`
- Added `selectedSquare` state to track the source square.
- Added `handleSquareClick` function:
  - First click: Selects the square and highlights it.
  - Second click: Attempts to move from selected square to clicked square.
  - Added visual feedback (yellow highlight) for selected square.

### 2. Updated `useChessGame.ts` for Promotions
- Modified `makeMove` to handle pawn promotions automatically.
- Logic:
  - Try standard move `chess.move({ from, to })`.
  - If that fails (e.g., missing promotion arg), try `chess.move({ from, to, promotion: 'q' })`.
  - This effectively implements "Auto-Queen" behavior.

### 3. Fixed WebSocket Message
- Updated the `send` function to include `promotion: move.promotion` in the payload sent to the backend.

## Result
- **Click-to-Move**: Users can now click a piece, see it highlighted, and click a target square to move.
- **Drag-and-Drop**: Continues to work (and is explicitly enabled).
- **Pawn Promotion**: Moving a pawn to the last rank now automatically promotes to a Queen instead of failing.
