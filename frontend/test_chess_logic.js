import { Chess } from 'chess.js';

const chess = new Chess();
console.log('Initial FEN:', chess.fen());

// 1. Test normal pawn move
try {
    const move = chess.move({ from: 'e2', to: 'e4' });
    console.log('Move e2-e4 success:', move);
} catch (e) {
    console.error('Move e2-e4 failed:', e);
}

// 2. Setup promotion scenario
chess.load('8/P7/8/8/8/8/8/k6K w - - 0 1');
console.log('Promotion setup FEN:', chess.fen());

// 3. Test promotion move (should fail without promotion arg)
let move = null;
const from = 'a7';
const to = 'a8';

try {
    console.log(`Attempting ${from}-${to} without promotion arg...`);
    move = chess.move({ from, to });
} catch (e) {
    console.log('Expected failure caught:', e.message);
    // Retry with promotion
    try {
        console.log(`Retrying ${from}-${to} with promotion: 'q'...`);
        move = chess.move({ from, to, promotion: 'q' });
        console.log('Promotion move success:', move);
    } catch (e2) {
        console.error('Promotion retry failed:', e2);
    }
}

if (move) {
    console.log('Final result: SUCCESS');
} else {
    console.log('Final result: FAILURE');
}
