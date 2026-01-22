# Move Rendering Fix

## Problem
The user reported that moves were not being rendered on the chessboard, even though the logic seemed correct.

## Root Cause
The `react-chessboard` component (v5.8.6) was not automatically updating the board visual state when the `position` in the `options` prop changed. This can happen if the component performs a shallow comparison or if the internal state management of the library doesn't react to deep changes in the `options` object immediately.

## Fix
Added a `key={fen}` prop to the `Chessboard` component.
```tsx
<Chessboard
  key={fen}
  options={{
    position: fen,
    // ...
  }}
/>
```

## Effect
This forces the `Chessboard` component to re-mount whenever the FEN string (board position) changes. This guarantees that the board will always display the current state of the game, resolving the "move not rendered" issue.

## Note
This might disable smooth piece sliding animations for moves, but it ensures correctness and reliability of the board state.
