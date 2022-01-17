package uk.ac.cam.ahk44.chess;

import java.util.ArrayList;
import java.util.List;

@Component
public class Q extends Piece{

    public Q(char name, Position piecePosition, PieceColor pieceColor, Board board) {
        super(name, piecePosition, pieceColor, board);
        this.name = name;
        this.position = piecePosition;
        this.pieceColor = pieceColor;
        this.board = board;
    }

    @Override
    List<Position> validNextPositions(){
        List<Position> nextPositions = new ArrayList<>();
        position.getAllDiagonalMoves(8, board(), nextPositions);
        position.getAllStraightMoves(8, board(), nextPositions);
        return nextPositions;
    }

    @Override
    char icon() {
        boolean colourSwitch = pieceColor == PieceColor.BLACK;
        return colourSwitch ? '♛' : '♕';
    }

    @Override
    int value() {
        return 10;
    }
}
