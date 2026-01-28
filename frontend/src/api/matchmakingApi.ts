import apiClient from './client';

/**
 * Join matchmaking queue for ranked games
 */
export const joinMatchmakingQueue = async (
  userId: string,
  timeControl: string
): Promise<void> => {
  await apiClient.post('/api/matchmaking/queue', {
    userId,
    timeControl,
  });
};

/**
 * Leave matchmaking queue
 */
export const leaveMatchmakingQueue = async (userId: string): Promise<void> => {
  await apiClient.delete(`/api/matchmaking/queue/${userId}`);
};

/**
 * Get current queue status (optional - for future use)
 */
export const getQueueStatus = async (userId: string): Promise<any> => {
  const response = await apiClient.get(`/api/matchmaking/queue/${userId}`);
  return response.data;
};
