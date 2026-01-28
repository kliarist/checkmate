import { Modal } from '../common/Modal';
import './TimeoutModal.css';

interface TimeoutModalProps {
  winner: 'white' | 'black';
  playerColor: 'white' | 'black';
  onClose: () => void;
}

/**
 * TimeoutModal - Displays when a player runs out of time
 */
const TimeoutModal = ({ winner, playerColor, onClose }: TimeoutModalProps) => {
  const isWinner = winner === playerColor;

  return (
    <Modal isOpen={true} onClose={onClose} title={isWinner ? "Time's Up!" : "Opponent Out of Time"}>
      <div className="timeout-modal">
        <div className="timeout-icon">⏱️</div>
        <h2>{isWinner ? "Time's Up!" : "Opponent Out of Time"}</h2>
        <p className="timeout-message">
          {isWinner 
            ? "Your opponent ran out of time. You win!" 
            : "You ran out of time. You lose."}
        </p>
        <div className="timeout-result">
          <span className={`result-label ${isWinner ? 'win' : 'loss'}`}>
            {isWinner ? 'Victory' : 'Defeat'}
          </span>
          <span className="result-reason">by timeout</span>
        </div>
        <button className="close-button" onClick={onClose}>
          Close
        </button>
      </div>
    </Modal>
  );
};

export default TimeoutModal;
