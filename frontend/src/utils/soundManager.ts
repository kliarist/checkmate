type SoundType = 'move' | 'capture' | 'check' | 'checkmate' | 'gameStart' | 'gameEnd';

class SoundManager {
  private sounds: Map<SoundType, HTMLAudioElement> = new Map();
  private enabled: boolean = true;

  constructor() {
    this.loadSounds();
  }

  private loadSounds(): void {
    const soundFiles: Record<SoundType, string> = {
      move: '/sounds/move.mp3',
      capture: '/sounds/capture.mp3',
      check: '/sounds/check.mp3',
      checkmate: '/sounds/checkmate.mp3',
      gameStart: '/sounds/game-start.mp3',
      gameEnd: '/sounds/game-end.mp3',
    };

    Object.entries(soundFiles).forEach(([type, path]) => {
      const audio = new Audio(path);
      audio.volume = 0.5;
      audio.preload = 'auto';
      this.sounds.set(type as SoundType, audio);
    });
  }

  play(type: SoundType): void {
    if (!this.enabled) return;

    const sound = this.sounds.get(type);
    if (sound) {
      sound.currentTime = 0;
      sound.play().catch((error) => {
        console.warn('Failed to play sound:', type, error);
      });
    }
  }

  setEnabled(enabled: boolean): void {
    this.enabled = enabled;
  }

  setVolume(volume: number): void {
    const clampedVolume = Math.max(0, Math.min(1, volume));
    this.sounds.forEach((audio) => {
      audio.volume = clampedVolume;
    });
  }
}

export const soundManager = new SoundManager();

