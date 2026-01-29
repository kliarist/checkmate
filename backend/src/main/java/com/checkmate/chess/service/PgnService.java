package com.checkmate.chess.service;

import com.checkmate.chess.model.Game;
import com.checkmate.chess.model.Move;
import com.checkmate.chess.repository.MoveRepository;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Service for generating PGN (Portable Game Notation) format from games.
 */
@Service
public class PgnService {

    private final MoveRepository moveRepository;

    public PgnService(MoveRepository moveRepository) {
        this.moveRepository = moveRepository;
    }

    /**
     * Generate PGN format for a game.
     *
     * @param game The game to export
     * @return PGN formatted string
     */
    public String generatePgn(Game game) {
        StringBuilder pgn = new StringBuilder();

        // Add PGN headers
        appendHeaders(pgn, game);

        // Add moves
        appendMoves(pgn, game);

        // Add result
        appendResult(pgn, game);

        return pgn.toString();
    }

    private void appendHeaders(StringBuilder pgn, Game game) {
        pgn.append("[Event \"CheckMate Game\"]\n");
        pgn.append("[Site \"CheckMate.com\"]\n");
        
        String date = game.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        pgn.append("[Date \"").append(date).append("\"]\n");
        
        pgn.append("[White \"").append(getPlayerName(game, true)).append("\"]\n");
        pgn.append("[Black \"").append(getPlayerName(game, false)).append("\"]\n");
        
        pgn.append("[Result \"").append(getPgnResult(game)).append("\"]\n");
        
        if (game.getTimeControl() != null) {
            pgn.append("[TimeControl \"").append(game.getTimeControl()).append("\"]\n");
        }
        
        pgn.append("\n");
    }

    private void appendMoves(StringBuilder pgn, Game game) {
        List<Move> moves = moveRepository.findByGameIdOrderByMoveNumberAsc(game.getId());
        
        int moveNumber = 1;
        boolean isWhiteMove = true;
        
        for (Move move : moves) {
            if (isWhiteMove) {
                pgn.append(moveNumber).append(". ");
            }
            
            pgn.append(move.getAlgebraicNotation()).append(" ");
            
            if (!isWhiteMove) {
                moveNumber++;
            }
            isWhiteMove = !isWhiteMove;
        }
        
        pgn.append("\n");
    }

    private void appendResult(StringBuilder pgn, Game game) {
        pgn.append(getPgnResult(game)).append("\n");
    }

    private String getPlayerName(Game game, boolean isWhite) {
        if (isWhite) {
            return game.getWhitePlayer() != null ? 
                game.getWhitePlayer().getUsername() : "Guest";
        } else {
            return game.getBlackPlayer() != null ? 
                game.getBlackPlayer().getUsername() : "Guest";
        }
    }

    private String getPgnResult(Game game) {
        if (game.getResult() == null) {
            return "*";
        }
        
        return switch (game.getResult()) {
            case "WHITE_WINS" -> "1-0";
            case "BLACK_WINS" -> "0-1";
            case "DRAW" -> "1/2-1/2";
            default -> "*";
        };
    }
}
