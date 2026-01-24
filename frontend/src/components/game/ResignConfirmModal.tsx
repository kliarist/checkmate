import { Modal, ModalButton } from '../common/Modal';

interface ResignConfirmModalProps {
  isOpen: boolean;
  onConfirm: () => void;
  onCancel: () => void;
}

export const ResignConfirmModal = ({ isOpen, onConfirm, onCancel }: ResignConfirmModalProps) => {
  return (
    <Modal
      isOpen={isOpen}
      onClose={onCancel}
      title="Resign Game"
      actions={
        <>
          <ModalButton onClick={onCancel} variant="secondary">
            Cancel
          </ModalButton>
          <ModalButton onClick={onConfirm} variant="danger">
            Resign
          </ModalButton>
        </>
      }
    >
      <div style={{ fontSize: '1rem', lineHeight: '1.6' }}>
        <p style={{ margin: '0 0 1rem 0' }}>
          Are you sure you want to resign this game?
        </p>
        <p style={{ margin: 0, color: '#999' }}>
          This action cannot be undone and you will lose the game.
        </p>
      </div>
    </Modal>
  );
};

