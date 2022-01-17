package uk.ac.cam.ahk44.chess;

import java.util.ArrayList;
import java.util.List;

@Component
public class King extends Piece{

    protected static int KING_VALUE = 10000;

    public King(char name, Position piecePosition, PieceColor pieceColor, Board board) {
        super(name, piecePosition, pieceColor, board);
        this.name = name;
        this.position = piecePosition;
        this.pieceColor = pieceColor;
        this.board = board;
    }

    @Override
    List<Position> validNextPositions(){
        List<Position> nextPositions = new ArrayList<>();
        position.getAllDiagonalMoves(1, board(), nextPositions);
        position.getAllStraightMoves(1, board(), nextPositions);
        return nextPositions;
    }

    @Override
    char icon() {
        boolean colourSwitch = pieceColor == PieceColor.BLACK;
        return colourSwitch ? '♚' : '♔';
    }

    @Override
    int value() {
        return KING_VALUE;
    }

    @Override
    char name() {
        return 'K';
    }
}
