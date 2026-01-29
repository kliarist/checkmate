import apiClient from './client';

export interface CreateComputerGameRequest {
  difficulty: 'beginner' | 'intermediate' | 'advanced';
  playerColor: 'white' | 'black';
}

export interface CreateComputerGameResponse {
  gameId: string;
  playerId: string;
  color: string;
  difficulty: string;
}

export const createComputerGame = async (
  request: CreateComputerGameRequest
): Promise<CreateComputerGameResponse> => {
  const response = await apiClient.post('/api/games/computer', request);
  return response.data.data;
};

export interface CreateGuestGameRequest {
  guestUsername: string;
}

export interface CreateGuestGameResponse {
  gameId: string;
  guestUserId: string;
  color: string;
  token: string;
}

export const createGuestGame = async (
  request: CreateGuestGameRequest
): Promise<CreateGuestGameResponse> => {
  const response = await apiClient.post('/api/games/guest', request);
  return response.data.data;
};

export const downloadPgn = async (gameId: string): Promise<void> => {
  const response = await apiClient.get(`/api/games/${gameId}/pgn`, {
    responseType: 'blob',
  });
  
  const blob = new Blob([response.data], { type: 'application/x-chess-pgn' });
  const url = window.URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.href = url;
  link.download = `game-${gameId}.pgn`;
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
  window.URL.revokeObjectURL(url);
};
