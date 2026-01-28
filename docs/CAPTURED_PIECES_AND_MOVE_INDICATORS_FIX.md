# Captured Pieces and Move Indicators Fix

## Issues Fixed

### 1. Captured Pieces Display
**Problem**: Captured pieces were not grouped and the wrong pieces were being shown for each player.

**Root Cause**: The logic in `GamePage.tsx` was inverted. When displaying the top player (opponent), it was showing the current player's captured pieces instead of the opponent's captured pieces.

**Solution**:
- Fixed the logic in `GamePage.tsx` to correctly assign captured pieces:
  - Top player (opponent): Shows pieces THEY captured
  - Bottom player (you): Shows pieces YOU captured
- Updated `PlayerInfo.tsx` to use the `groupedPieces` array
- Fixed the `getPieceSymbol` function to use the `capturedPieceColor` parameter
- Pieces are now displayed grouped by type (e.g., ♟×3 instead of ♟♟♟)
- Pieces now show the correct color (opposite of the capturing player)

**Example**:
- If you're playing as White (bottom):
  - Top player (Black opponent) shows `capturedByBlack` (pieces Black captured from you)
  - Bottom player (You) shows `capturedByWhite` (pieces you captured from Black)

**Files Modified**:
- `frontend/src/components/game/PlayerInfo.tsx`
- `frontend/src/pages/GamePage.tsx`

### 2. Legal Move Indicators
**Problem**: No visual indication of possible allowed moves when selecting a piece.

**Solution**:
- Added Chess.js instance to ChessBoard component to calculate legal moves
- When a piece is selected (by click or drag), the component now:
  - Highlights the selected square in yellow
  - Shows small dots on empty squares where the piece can move
  - Shows larger circles on squares where the piece can capture
- Legal moves are calculated using `chess.moves({ square, verbose: true })`

**Files Modified**:
- `frontend/src/components/game/ChessBoard.tsx`

### 3. Code Cleanup
**Problem**: TypeScript build errors due to unused imports and props.

**Solution**:
- Removed unused props from ChessBoard component interface
- Simplified ChessBoard to only accept essential props
- Updated GamePage and PrivateGamePage to pass only required props
- Removed unused imports and variables

**Files Modified**:
- `frontend/src/components/game/ChessBoard.tsx`
- `frontend/src/pages/GamePage.tsx`
- `frontend/src/pages/PrivateGamePage.tsx`

## Implementation Details

### Captured Pieces Grouping
```typescript
// Group pieces by type and count
const groupPieces = (pieces: string[]) => {
  const counts: Record<string, number> = {};
  pieces.forEach(piece => {
    const key = piece.toLowerCase();
    counts[key] = (counts[key] || 0) + 1;
  });
  
  // Sort by piece value (queen, rook, bishop, knight, pawn)
  const order = ['q', 'r', 'b', 'n', 'p'];
  return order
    .filter(piece => counts[piece] > 0)
    .map(piece => ({ piece, count: counts[piece] }));
};

// Display grouped pieces
{groupedPieces.map(({ piece, count }, index) => (
  <span key={index}>
    {getPieceSymbol(piece, capturedPieceColor)}
    {count > 1 && <span>×{count}</span>}
  </span>
))}
```

### Legal Move Indicators
```typescript
// Get legal moves for selected square
const moves = chessRef.current.moves({ square: square as any, verbose: true });
const newSquares: Record<string, React.CSSProperties> = {};

// Highlight selected square
newSquares[square] = { backgroundColor: 'rgba(255, 255, 0, 0.4)' };

// Highlight legal move destinations
moves.forEach((move: any) => {
  newSquares[move.to] = {
    background: move.captured
      ? 'radial-gradient(circle, rgba(0,0,0,.1) 85%, transparent 85%)'
      : 'radial-gradient(circle, rgba(0,0,0,.1) 25%, transparent 25%)',
    borderRadius: '50%',
  };
});
```

## Remaining Issues

### Material Score Calculation
The material score calculation in `useChessGame.ts` appears to be correct. It calculates:
- Value of pieces captured by white
- Value of pieces captured by black
- Material advantage = white captures - black captures

If there are still issues with the score, they may be related to:
1. How pieces are being tracked during move replay
2. Edge cases with promotions or special moves

### Game End Modal
The `GameEndModal` component is already integrated in `GamePage.tsx` and should display when `isGameOver` is true. If it's not appearing on checkmate, the issue may be:
1. The `isGameOver` state not being set properly
2. The modal being rendered but not visible due to z-index or styling issues
3. The checkmate detection in the backend or frontend not working correctly

## Testing Recommendations

1. **Captured Pieces**: Play a game and capture pieces of different types. Verify:
   - Pieces are grouped correctly (e.g., ♟×3)
   - Pieces show the correct color
   - Material advantage score is accurate

2. **Move Indicators**: Select different pieces and verify:
   - Legal moves are highlighted with dots
   - Capture moves are highlighted with larger circles
   - Indicators disappear after making a move

3. **Game End**: Play a game to checkmate and verify:
   - The GameEndModal appears
   - The "Play Again" button works
   - The "Go to Menu" button works

## Build Status
✅ TypeScript compilation successful
✅ Vite build successful
✅ No errors or warnings
