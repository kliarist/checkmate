import { describe, it, expect } from 'vitest';
import { Chess } from 'chess.js';

describe('Chess Validation Utilities', () => {
  describe('Basic Move Validation', () => {
    it('should validate legal pawn move', () => {
      const chess = new Chess();
      const move = chess.move({ from: 'e2', to: 'e4' });

      expect(move).not.toBeNull();
      expect(move.san).toBe('e4');
    });

    it('should reject illegal pawn move', () => {
      const chess = new Chess();

      expect(() => chess.move({ from: 'e2', to: 'e5' })).toThrow();
    });

    it('should validate knight move', () => {
      const chess = new Chess();
      const move = chess.move({ from: 'g1', to: 'f3' });

      expect(move).not.toBeNull();
      expect(move.san).toBe('Nf3');
    });

    it('should reject move to occupied square', () => {
      const chess = new Chess();

      expect(() => chess.move({ from: 'e1', to: 'e2' })).toThrow();
    });

    it('should validate pawn capture', () => {
      const chess = new Chess();
      chess.move({ from: 'e2', to: 'e4' });
      chess.move({ from: 'e7', to: 'e5' });
      chess.move({ from: 'd2', to: 'd4' });
      const capture = chess.move({ from: 'e5', to: 'd4' });

      expect(capture).not.toBeNull();
      expect(capture.captured).toBe('p');
    });
  });

  describe('Special Moves', () => {
    it('should validate kingside castling', () => {
      const chess = new Chess();
      chess.load('r1bqkbnr/pppppppp/2n5/8/8/5N2/PPPPPPPP/RNBQK2R w KQkq - 0 1');
      const castle = chess.move({ from: 'e1', to: 'g1' });

      expect(castle).not.toBeNull();
      expect(castle.san).toBe('O-O');
    });

    it('should validate queenside castling', () => {
      const chess = new Chess();
      chess.load('r1bqkbnr/pppppppp/2n5/8/8/2N5/PPPPPPPP/R3KBNR w KQkq - 0 1');
      const castle = chess.move({ from: 'e1', to: 'c1' });

      expect(castle).not.toBeNull();
      expect(castle.san).toBe('O-O-O');
    });

    it('should validate en passant capture', () => {
      const chess = new Chess();
      chess.load('rnbqkbnr/ppp1pppp/8/3pP3/8/8/PPPP1PPP/RNBQKBNR w KQkq d6 0 2');
      const enPassant = chess.move({ from: 'e5', to: 'd6' });

      expect(enPassant).not.toBeNull();
      expect(enPassant.flags).toContain('e');
    });

    it('should validate pawn promotion', () => {
      const chess = new Chess();
      chess.load('4k3/P7/8/8/8/8/8/4K3 w - - 0 1');
      const promotion = chess.move({ from: 'a7', to: 'a8', promotion: 'q' });

      expect(promotion).not.toBeNull();
      expect(promotion.promotion).toBe('q');
      expect(promotion.san).toBe('a8=Q');
    });

    it('should require promotion piece for pawn reaching 8th rank', () => {
      const chess = new Chess();
      chess.load('4k3/P7/8/8/8/8/8/4K3 w - - 0 1');

      expect(() => chess.move({ from: 'a7', to: 'a8' })).toThrow();
    });
  });

  describe('Game State Detection', () => {
    it('should detect check', () => {
      const chess = new Chess();
      chess.load('rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w KQkq e6 0 2');
      chess.move({ from: 'f1', to: 'b5' });

      expect(chess.isCheck()).toBe(true);
    });

    it('should not detect check in normal position', () => {
      const chess = new Chess();

      expect(chess.isCheck()).toBe(false);
    });

    it('should detect checkmate', () => {
      const chess = new Chess();
      chess.load('r1bqkb1r/pppp1Qpp/2n2n2/4p3/2B1P3/8/PPPP1PPP/RNB1K1NR b KQkq - 0 4');

      expect(chess.isCheckmate()).toBe(true);
      expect(chess.isGameOver()).toBe(true);
    });

    it('should detect stalemate', () => {
      const chess = new Chess();
      chess.load('7k/5Q2/6K1/8/8/8/8/8 b - - 0 1');

      expect(chess.isStalemate()).toBe(true);
      expect(chess.isGameOver()).toBe(true);
    });

    it('should detect draw by insufficient material', () => {
      const chess = new Chess();
      chess.load('4k3/8/8/8/8/8/8/4K3 w - - 0 1');

      expect(chess.isInsufficientMaterial()).toBe(true);
      expect(chess.isGameOver()).toBe(true);
    });

    it('should detect threefold repetition', () => {
      const chess = new Chess();
      chess.move('Nf3');
      chess.move('Nf6');
      chess.move('Ng1');
      chess.move('Ng8');
      chess.move('Nf3');
      chess.move('Nf6');
      chess.move('Ng1');
      chess.move('Ng8');
      chess.move('Nf3');
      chess.move('Nf6');

      expect(chess.isThreefoldRepetition()).toBe(true);
    });
  });

  describe('Move Generation', () => {
    it('should get legal moves for a piece', () => {
      const chess = new Chess();
      const moves = chess.moves({ square: 'e2' });

      expect(moves).toContain('e3');
      expect(moves).toContain('e4');
      expect(moves.length).toBe(2);
    });

    it('should return empty array for piece with no legal moves', () => {
      const chess = new Chess();
      const moves = chess.moves({ square: 'a1' });

      expect(moves.length).toBe(0);
    });

    it('should get all legal moves', () => {
      const chess = new Chess();
      const moves = chess.moves();

      expect(moves.length).toBe(20); // 20 possible opening moves
    });

    it('should get moves in verbose format', () => {
      const chess = new Chess();
      const moves = chess.moves({ verbose: true, square: 'e2' });

      expect(moves.length).toBe(2);
      expect(moves[0]).toHaveProperty('from');
      expect(moves[0]).toHaveProperty('to');
      expect(moves[0]).toHaveProperty('san');
    });
  });

  describe('FEN Manipulation', () => {
    it('should load FEN string', () => {
      const chess = new Chess();
      const fen = 'rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1';
      chess.load(fen);

      expect(chess.fen()).toBe(fen);
    });

    it('should return current FEN', () => {
      const chess = new Chess();
      chess.move('e4');

      expect(chess.fen()).toBe('rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1');
    });

    it('should validate FEN string', () => {
      const chess = new Chess();
      const validFen = 'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1';

      expect(chess.validateFen(validFen).ok).toBe(true);
    });

    it('should reject invalid FEN string', () => {
      const chess = new Chess();
      const invalidFen = 'invalid-fen';

      expect(chess.validateFen(invalidFen).ok).toBe(false);
    });
  });

  describe('Turn Management', () => {
    it('should track whose turn it is', () => {
      const chess = new Chess();

      expect(chess.turn()).toBe('w');

      chess.move('e4');
      expect(chess.turn()).toBe('b');

      chess.move('e5');
      expect(chess.turn()).toBe('w');
    });

    it('should prevent moving opponents pieces', () => {
      const chess = new Chess();

      expect(() => chess.move({ from: 'e7', to: 'e5' })).toThrow();
    });
  });

  describe('Move History', () => {
    it('should track move history', () => {
      const chess = new Chess();
      chess.move('e4');
      chess.move('e5');
      chess.move('Nf3');

      const history = chess.history();
      expect(history).toEqual(['e4', 'e5', 'Nf3']);
    });

    it('should track move history in verbose format', () => {
      const chess = new Chess();
      chess.move('e4');

      const history = chess.history({ verbose: true });
      expect(history[0]).toHaveProperty('from', 'e2');
      expect(history[0]).toHaveProperty('to', 'e4');
      expect(history[0]).toHaveProperty('san', 'e4');
    });

    it('should undo move', () => {
      const chess = new Chess();
      const initialFen = chess.fen();

      chess.move('e4');
      chess.undo();

      expect(chess.fen()).toBe(initialFen);
    });

    it('should undo multiple moves', () => {
      const chess = new Chess();
      const initialFen = chess.fen();

      chess.move('e4');
      chess.move('e5');
      chess.move('Nf3');

      chess.undo();
      chess.undo();
      chess.undo();

      expect(chess.fen()).toBe(initialFen);
    });
  });

  describe('PGN Support', () => {
    it('should generate PGN notation', () => {
      const chess = new Chess();
      chess.move('e4');
      chess.move('e5');
      chess.move('Nf3');

      const pgn = chess.pgn();
      expect(pgn).toContain('1. e4 e5');
      expect(pgn).toContain('2. Nf3');
    });

    it('should load PGN notation', () => {
      const chess = new Chess();
      const pgn = '1. e4 e5 2. Nf3 Nc6';
      chess.loadPgn(pgn);

      const history = chess.history();
      expect(history).toEqual(['e4', 'e5', 'Nf3', 'Nc6']);
    });
  });

  describe('Board State', () => {
    it('should get piece at square', () => {
      const chess = new Chess();
      const piece = chess.get('e2');

      expect(piece).not.toBeNull();
      expect(piece?.type).toBe('p');
      expect(piece?.color).toBe('w');
    });

    it('should return null for empty square', () => {
      const chess = new Chess();
      const piece = chess.get('e4');

      expect(piece).toBeNull();
    });

    it('should get board representation', () => {
      const chess = new Chess();
      const board = chess.board();

      expect(board).toHaveLength(8);
      expect(board[0]).toHaveLength(8);
    });
  });

  describe('Edge Cases', () => {
    it('should handle rapid move sequence', () => {
      const chess = new Chess();

      expect(() => {
        chess.move('e4');
        chess.move('e5');
        chess.move('Nf3');
        chess.move('Nc6');
        chess.move('Bc4');
      }).not.toThrow();
    });

    it('should reset to starting position', () => {
      const chess = new Chess();
      chess.move('e4');
      chess.move('e5');

      chess.reset();

      expect(chess.fen()).toBe('rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1');
    });

    it('should handle clear board', () => {
      const chess = new Chess();
      chess.clear();

      const board = chess.board();
      board.forEach(row => {
        row.forEach(square => {
          expect(square).toBeNull();
        });
      });
    });
  });
});

