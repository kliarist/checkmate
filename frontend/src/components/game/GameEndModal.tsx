import { Modal, ModalButton } from '../common/Modal';
import './GameEndModal.css';

interface GameEndModalProps {
  result: string;
  ratingChange?: number;
  isRanked?: boolean;
  onPlayAgain: () => void;
  onGoToMenu: () => void;
}

export const GameEndModal = ({ 
  result, 
  ratingChange, 
  isRanked = false, 
  onPlayAgain,
  onGoToMenu 
}: GameEndModalProps) => {
  return (
    <Modal
      isOpen={true}
      onClose={onGoToMenu}
      title="Game Over"
      actions={
        <>
          <ModalButton onClick={onGoToMenu} variant="secondary">
            Go to Menu
          </ModalButton>
          <ModalButton onClick={onPlayAgain} variant="primary">
            Play Again
          </ModalButton>
        </>
      }
    >
      <div className="game-end-content">
        <div className="game-result">{result}</div>
        
        {isRanked && ratingChange !== undefined && (
          <div className="rating-change-container">
            <div className="rating-change-label">Rating Change</div>
            <div className={`rating-change ${ratingChange >= 0 ? 'positive' : 'negative'}`}>
              {ratingChange >= 0 ? '+' : ''}{ratingChange}
            </div>
          </div>
        )}
      </div>
    </Modal>
  );
};
