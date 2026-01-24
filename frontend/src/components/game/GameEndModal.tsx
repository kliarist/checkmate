import { Modal, ModalButton } from '../common/Modal';

interface GameEndModalProps {
  result: string;
  onClose: () => void;
}

export const GameEndModal = ({ result, onClose }: GameEndModalProps) => {
  return (
    <Modal
      isOpen={true}
      onClose={onClose}
      title="Game Over"
      actions={
        <ModalButton onClick={onClose} variant="primary">
          Play Again
        </ModalButton>
      }
    >
      <div style={{ textAlign: 'center', fontSize: '1.25rem', padding: '1rem 0' }}>
        {result}
      </div>
    </Modal>
  );
};
