type SoundType = 'move' | 'capture' | 'check' | 'checkmate' | 'gameStart' | 'gameEnd';

class SoundManager {
  private audioContext: AudioContext | null = null;
  private enabled: boolean = true;
  private volume: number = 0.3;

  constructor() {
    // Initialize AudioContext lazily on first use
  }

  private getAudioContext(): AudioContext {
    if (!this.audioContext) {
      this.audioContext = new (window.AudioContext || (window as any).webkitAudioContext)();
    }
    return this.audioContext;
  }

  private playTone(frequency: number, duration: number, type: OscillatorType = 'sine'): void {
    if (!this.enabled) return;

    try {
      const ctx = this.getAudioContext();
      const oscillator = ctx.createOscillator();
      const gainNode = ctx.createGain();

      oscillator.connect(gainNode);
      gainNode.connect(ctx.destination);

      oscillator.frequency.value = frequency;
      oscillator.type = type;

      gainNode.gain.setValueAtTime(this.volume, ctx.currentTime);
      gainNode.gain.exponentialRampToValueAtTime(0.01, ctx.currentTime + duration);

      oscillator.start(ctx.currentTime);
      oscillator.stop(ctx.currentTime + duration);
    } catch (error) {
      console.warn('Failed to play sound:', error);
    }
  }

  play(type: SoundType): void {
    if (!this.enabled) return;

    switch (type) {
      case 'move':
        // Simple click sound - short high-pitched tone
        this.playTone(800, 0.05, 'sine');
        break;
      case 'capture':
        // Slightly different tone for captures
        this.playTone(600, 0.08, 'sine');
        break;
      case 'check':
        // Higher pitched for check
        this.playTone(1000, 0.1, 'sine');
        break;
      case 'checkmate':
        // Two tones for checkmate
        this.playTone(800, 0.15, 'sine');
        setTimeout(() => this.playTone(600, 0.2, 'sine'), 100);
        break;
      case 'gameStart':
        this.playTone(600, 0.1, 'sine');
        break;
      case 'gameEnd':
        this.playTone(500, 0.2, 'sine');
        break;
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