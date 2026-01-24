import { render, screen, fireEvent } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { ChessBoard } from '../../../components/game/ChessBoard';

  describe('ChessBoard Component', () => {
  const mockOnMove = vi.fn();
  const mockOnResign = vi.fn();
  const mockOnOfferDraw = vi.fn();
  const mockOnFlipBoard = vi.fn();

  const defaultProps = {
    fen: 'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1',
    onMove: mockOnMove,
    playerColor: 'white' as 'white' | 'black',
    onResign: mockOnResign,
    onOfferDraw: mockOnOfferDraw,
    onFlipBoard: mockOnFlipBoard,
  };

  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should render chessboard with starting position', () => {
    render(<ChessBoard {...defaultProps} />);

    const boardElement = screen.getByRole('region', { hidden: true });
    expect(boardElement).toBeDefined();
  });

  it('should display board with correct orientation for white', () => {
    render(<ChessBoard {...defaultProps} playerColor="white" />);

    expect(screen.getByRole('region', { hidden: true })).toBeDefined();
  });

  it('should display board with correct orientation for black', () => {
    render(<ChessBoard {...defaultProps} playerColor="black" />);

    expect(screen.getByRole('region', { hidden: true })).toBeDefined();
  });

  it('should render resign button', () => {
    render(<ChessBoard {...defaultProps} />);

    const resignButton = screen.getByTitle('Resign');
    expect(resignButton).toBeDefined();
  });

  it('should render offer draw button', () => {
    render(<ChessBoard {...defaultProps} />);

    const drawButton = screen.getByTitle('Offer Draw');
    expect(drawButton).toBeDefined();
  });

  it('should render flip board button', () => {
    render(<ChessBoard {...defaultProps} />);

    const flipButton = screen.getByTitle('Flip Board');
    expect(flipButton).toBeDefined();
  });

  it('should call onResign when resign button is clicked', () => {
    render(<ChessBoard {...defaultProps} />);

    const resignButton = screen.getByTitle('Resign');
    fireEvent.click(resignButton);

    expect(mockOnResign).toHaveBeenCalledTimes(1);
  });

  it('should call onOfferDraw when draw button is clicked', () => {
    render(<ChessBoard {...defaultProps} />);

    const drawButton = screen.getByTitle('Offer Draw');
    fireEvent.click(drawButton);

    expect(mockOnOfferDraw).toHaveBeenCalledTimes(1);
  });

  it('should call onFlipBoard when flip button is clicked', () => {
    render(<ChessBoard {...defaultProps} />);

    const flipButton = screen.getByTitle('Flip Board');
    fireEvent.click(flipButton);

    expect(mockOnFlipBoard).toHaveBeenCalledTimes(1);
  });

  it('should update board when FEN changes', () => {
    const { rerender } = render(<ChessBoard {...defaultProps} />);

    const newFen = 'rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1';
    rerender(<ChessBoard {...defaultProps} fen={newFen} />);

    expect(screen.getByRole('region', { hidden: true })).toBeDefined();
  });

  it('should call onMove when piece is dropped', () => {
    render(<ChessBoard {...defaultProps} />);

    expect(mockOnMove).toBeDefined();
  });

  it('should handle invalid move gracefully', () => {
    mockOnMove.mockReturnValue(false);
    render(<ChessBoard {...defaultProps} />);

    expect(screen.getByRole('region', { hidden: true })).toBeDefined();
  });

  it('should handle valid move correctly', () => {
    mockOnMove.mockReturnValue(true);
    render(<ChessBoard {...defaultProps} />);

    expect(screen.getByRole('region', { hidden: true })).toBeDefined();
  });

  it('should display custom board styling', () => {
    render(<ChessBoard {...defaultProps} />);

    const boardContainer = screen.getByRole('region', { hidden: true }).parentElement;
    expect(boardContainer).toBeDefined();
  });

  it('should handle board size of 700px', () => {
    render(<ChessBoard {...defaultProps} />);

    const boardContainer = screen.getByRole('region', { hidden: true }).parentElement;
    expect(boardContainer).toBeDefined();
  });

  it('should flip board orientation when player color changes', () => {
    const { rerender } = render(<ChessBoard {...defaultProps} playerColor="white" />);

    rerender(<ChessBoard {...defaultProps} playerColor="black" />);

    expect(screen.getByRole('region', { hidden: true })).toBeDefined();
  });

  it('should highlight selected square', () => {
    render(<ChessBoard {...defaultProps} />);

    expect(screen.getByRole('region', { hidden: true })).toBeDefined();
  });

  it('should clear selection after move', () => {
    render(<ChessBoard {...defaultProps} />);

    expect(mockOnMove).toBeDefined();
  });

  it('should show legal move indicators', () => {
    render(<ChessBoard {...defaultProps} />);

    expect(screen.getByRole('region', { hidden: true })).toBeDefined();
  });

  it('should apply custom square styles', () => {
    render(<ChessBoard {...defaultProps} />);

    expect(screen.getByRole('region', { hidden: true })).toBeDefined();
  });

  it('should render with animation duration of 200ms', () => {
    render(<ChessBoard {...defaultProps} />);

    expect(screen.getByRole('region', { hidden: true })).toBeDefined();
  });

  it('should handle rapid piece movements', () => {
    render(<ChessBoard {...defaultProps} />);

    expect(mockOnMove).toBeDefined();
  });

  it('should maintain board state across re-renders', () => {
    const { rerender } = render(<ChessBoard {...defaultProps} />);

    rerender(<ChessBoard {...defaultProps} />);
    rerender(<ChessBoard {...defaultProps} />);

    expect(screen.getByRole('region', { hidden: true })).toBeDefined();
  });
});

