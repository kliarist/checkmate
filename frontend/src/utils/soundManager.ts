type SoundType = 'move' | 'capture' | 'check' | 'checkmate' | 'castle' | 'promote' | 'gameStart' | 'gameEnd';

class SoundManager {
  private audioContext: AudioContext | null = null;
  private enabled: boolean = true;
  private volume: number = 0.3;
  private audioCache: Map<SoundType, AudioBuffer> = new Map();
  private soundUrls: Record<SoundType, string> = {
    move: 'https://images.chesscomfiles.com/chess-themes/sounds/_MP3_/default/move-self.mp3',
    capture: 'https://images.chesscomfiles.com/chess-themes/sounds/_MP3_/default/capture.mp3',
    check: 'https://images.chesscomfiles.com/chess-themes/sounds/_MP3_/default/move-check.mp3',
    castle: 'https://images.chesscomfiles.com/chess-themes/sounds/_MP3_/default/castle.mp3',
    promote: 'https://images.chesscomfiles.com/chess-themes/sounds/_MP3_/default/promote.mp3',
    checkmate: 'https://images.chesscomfiles.com/chess-themes/sounds/_MP3_/default/notify.mp3',
    gameStart: 'https://images.chesscomfiles.com/chess-themes/sounds/_MP3_/default/move-self.mp3',
    gameEnd: 'https://images.chesscomfiles.com/chess-themes/sounds/_MP3_/default/notify.mp3',
  };

  constructor() {
    // Initialize AudioContext lazily on first use
  }

  private getAudioContext(): AudioContext {
    if (!this.audioContext) {
      this.audioContext = new (window.AudioContext || (window as any).webkitAudioContext)();
    }
    return this.audioContext;
  }

  private async loadSound(type: SoundType): Promise<AudioBuffer | null> {
    if (this.audioCache.has(type)) {
      return this.audioCache.get(type)!;
    }

    try {
      const ctx = this.getAudioContext();
      const response = await fetch(this.soundUrls[type]);
      const arrayBuffer = await response.arrayBuffer();
      const audioBuffer = await ctx.decodeAudioData(arrayBuffer);
      this.audioCache.set(type, audioBuffer);
      return audioBuffer;
    } catch (error) {
      console.warn('Failed to load sound:', type, error);
      return null;
    }
  }

  async play(type: SoundType): Promise<void> {
    if (!this.enabled) return;

    try {
      const ctx = this.getAudioContext();
      const buffer = await this.loadSound(type);
      
      if (!buffer) return;

      const source = ctx.createBufferSource();
      const gainNode = ctx.createGain();

      source.buffer = buffer;
      source.connect(gainNode);
      gainNode.connect(ctx.destination);
      gainNode.gain.value = this.volume;

      source.start(0);
    } catch (error) {
      console.warn('Failed to play sound:', error);
    }
  }

  setEnabled(enabled: boolean): void {
    this.enabled = enabled;
  }

  setVolume(volume: number): void {
    this.volume = Math.max(0, Math.min(1, volume));
  }
}

export const soundManager = new SoundManager();