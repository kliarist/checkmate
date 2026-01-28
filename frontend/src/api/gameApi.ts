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
