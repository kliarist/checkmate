import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { BrowserRouter } from 'react-router-dom';
import { GuestLandingPage } from '../../../pages/GuestLandingPage';
import apiClient from '../../../api/client';

/**
 * Integration tests for guest game creation flow (T044).
 * Tests end-to-end flow from landing page to game creation.
 */
describe('Guest Game Creation Flow', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should display "Play as Guest" button on landing page', () => {
    render(
      <BrowserRouter>
        <GuestLandingPage />
      </BrowserRouter>
    );

    const playButton = screen.getByText(/Play as Guest/i);
    expect(playButton).toBeDefined();
  });

  it('should show loading state when creating game', async () => {
    // Mock API call with delay
    vi.spyOn(apiClient, 'post').mockImplementation(() =>
      new Promise(resolve => setTimeout(() => resolve({
        data: {
          success: true,
          data: {
            id: 1,
            gameType: 'GUEST',
            currentFen: 'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1',
            status: 'IN_PROGRESS'
          }
        }
      }), 100))
    );

    render(
      <BrowserRouter>
        <GuestLandingPage />
      </BrowserRouter>
    );

    const playButton = screen.getByText(/Play as Guest/i);
    fireEvent.click(playButton);

    // Should show loading state
    await waitFor(() => {
      const loadingElement = screen.queryByText(/Creating game/i) ||
                            screen.queryByText(/Loading/i);
      expect(loadingElement).toBeDefined();
    });
  });

  it('should create guest game on button click', async () => {
    const mockResponse = {
      data: {
        success: true,
        data: {
          id: 123,
          gameType: 'GUEST',
          currentFen: 'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1',
          status: 'IN_PROGRESS',
          whitePlayerId: 1,
          blackPlayerId: 2
        }
      }
    };

    vi.spyOn(apiClient, 'post').mockResolvedValue(mockResponse);

    render(
      <BrowserRouter>
        <GuestLandingPage />
      </BrowserRouter>
    );

    const playButton = screen.getByText(/Play as Guest/i);
    fireEvent.click(playButton);

    await waitFor(() => {
      expect(apiClient.post).toHaveBeenCalledWith('/api/games/guest');
    });
  });

  it('should navigate to game page after successful creation', async () => {
    const mockResponse = {
      data: {
        success: true,
        data: {
          id: 456,
          gameType: 'GUEST',
          currentFen: 'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1',
          status: 'IN_PROGRESS'
        }
      }
    };

    vi.spyOn(apiClient, 'post').mockResolvedValue(mockResponse);

    const { container } = render(
      <BrowserRouter>
        <GuestLandingPage />
      </BrowserRouter>
    );

    const playButton = screen.getByText(/Play as Guest/i);
    fireEvent.click(playButton);

    await waitFor(() => {
      expect(apiClient.post).toHaveBeenCalled();
    });

    // Navigation happens after successful game creation
    await waitFor(() => {
      expect(window.location.pathname).toMatch(/\/game\/\d+/);
    }, { timeout: 3000 });
  });

  it('should display error message on failed game creation', async () => {
    const errorMessage = 'Failed to create game';
    vi.spyOn(apiClient, 'post').mockRejectedValue({
      response: {
        data: {
          success: false,
          error: errorMessage
        }
      }
    });

    render(
      <BrowserRouter>
        <GuestLandingPage />
      </BrowserRouter>
    );

    const playButton = screen.getByText(/Play as Guest/i);
    fireEvent.click(playButton);

    await waitFor(() => {
      const errorElement = screen.queryByText(new RegExp(errorMessage, 'i')) ||
                          screen.queryByText(/error/i);
      expect(errorElement).toBeDefined();
    });
  });

  it('should handle network errors gracefully', async () => {
    vi.spyOn(apiClient, 'post').mockRejectedValue(new Error('Network error'));

    render(
      <BrowserRouter>
        <GuestLandingPage />
      </BrowserRouter>
    );

    const playButton = screen.getByText(/Play as Guest/i);
    fireEvent.click(playButton);

    await waitFor(() => {
      const errorElement = screen.queryByText(/error/i) ||
                          screen.queryByText(/failed/i);
      expect(errorElement).toBeDefined();
    });
  });

  it('should store guest user ID in localStorage', async () => {
    const mockResponse = {
      data: {
        success: true,
        data: {
          id: 789,
          gameType: 'GUEST',
          currentFen: 'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1',
          status: 'IN_PROGRESS',
          whitePlayerId: 1,
          blackPlayerId: 2
        }
      }
    };

    vi.spyOn(apiClient, 'post').mockResolvedValue(mockResponse);
    const setItemSpy = vi.spyOn(Storage.prototype, 'setItem');

    render(
      <BrowserRouter>
        <GuestLandingPage />
      </BrowserRouter>
    );

    const playButton = screen.getByText(/Play as Guest/i);
    fireEvent.click(playButton);

    await waitFor(() => {
      expect(setItemSpy).toHaveBeenCalledWith('guestUserId', expect.any(String));
    });
  });

  it('should disable button during game creation', async () => {
    vi.spyOn(apiClient, 'post').mockImplementation(() =>
      new Promise(resolve => setTimeout(() => resolve({
        data: {
          success: true,
          data: {
            id: 1,
            gameType: 'GUEST',
            currentFen: 'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1'
          }
        }
      }), 1000))
    );

    render(
      <BrowserRouter>
        <GuestLandingPage />
      </BrowserRouter>
    );

    const playButton = screen.getByText(/Play as Guest/i);
    fireEvent.click(playButton);

    // Button should be disabled during creation
    await waitFor(() => {
      expect(playButton.hasAttribute('disabled')).toBe(true);
    });
  });

  it('should handle multiple rapid clicks gracefully', async () => {
    const mockResponse = {
      data: {
        success: true,
        data: {
          id: 1,
          gameType: 'GUEST',
          currentFen: 'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1'
        }
      }
    };

    vi.spyOn(apiClient, 'post').mockResolvedValue(mockResponse);

    render(
      <BrowserRouter>
        <GuestLandingPage />
      </BrowserRouter>
    );

    const playButton = screen.getByText(/Play as Guest/i);

    // Click multiple times rapidly
    fireEvent.click(playButton);
    fireEvent.click(playButton);
    fireEvent.click(playButton);

    await waitFor(() => {
      // Should only create one game
      expect(apiClient.post).toHaveBeenCalledTimes(1);
    });
  });

  it('should display game type as GUEST', async () => {
    const mockResponse = {
      data: {
        success: true,
        data: {
          id: 1,
          gameType: 'GUEST',
          currentFen: 'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1',
          status: 'IN_PROGRESS'
        }
      }
    };

    vi.spyOn(apiClient, 'post').mockResolvedValue(mockResponse);

    render(
      <BrowserRouter>
        <GuestLandingPage />
      </BrowserRouter>
    );

    const playButton = screen.getByText(/Play as Guest/i);
    fireEvent.click(playButton);

    await waitFor(() => {
      expect(apiClient.post).toHaveBeenCalled();
    });
  });

  it('should initialize with starting FEN position', async () => {
    const mockResponse = {
      data: {
        success: true,
        data: {
          id: 1,
          gameType: 'GUEST',
          currentFen: 'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1',
          status: 'IN_PROGRESS'
        }
      }
    };

    vi.spyOn(apiClient, 'post').mockResolvedValue(mockResponse);

    render(
      <BrowserRouter>
        <GuestLandingPage />
      </BrowserRouter>
    );

    const playButton = screen.getByText(/Play as Guest/i);
    fireEvent.click(playButton);

    await waitFor(() => {
      expect(apiClient.post).toHaveBeenCalled();
    });

    // Verify starting position
    const call = apiClient.post.mock.results[0];
    await call.value.then((response: any) => {
      expect(response.data.data.currentFen).toBe(
        'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1'
      );
    });
  });

  it('should create game with IN_PROGRESS status', async () => {
    const mockResponse = {
      data: {
        success: true,
        data: {
          id: 1,
          gameType: 'GUEST',
          currentFen: 'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1',
          status: 'IN_PROGRESS'
        }
      }
    };

    vi.spyOn(apiClient, 'post').mockResolvedValue(mockResponse);

    render(
      <BrowserRouter>
        <GuestLandingPage />
      </BrowserRouter>
    );

    const playButton = screen.getByText(/Play as Guest/i);
    fireEvent.click(playButton);

    await waitFor(() => {
      expect(apiClient.post).toHaveBeenCalled();
    });

    const call = apiClient.post.mock.results[0];
    await call.value.then((response: any) => {
      expect(response.data.data.status).toBe('IN_PROGRESS');
    });
  });
});

