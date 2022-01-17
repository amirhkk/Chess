package uk.ac.cam.ahk44.chess;

import java.util.ArrayList;
import java.util.List;

@Component
public class Knight extends Piece{

    public Knight(char name, Position piecePosition, PieceColor pieceColor, Board board) {
        super(name, piecePosition, pieceColor, board);
        this.name = name;
        this.position = piecePosition;
        this.pieceColor = pieceColor;
        this.board = board;
    }

    @Override
    List<Position> validNextPositions(){
        List<Position> nextPositions = new ArrayList<>();
        computeKnightNextPositions(nextPositions);
        return nextPositions;
    }

    @Override
    char icon() {
        boolean colourSwitch = pieceColor == PieceColor.BLACK;
        return colourSwitch ? '♞' : '♘';
    }

    @Override
    int value() {
        return 3;
    }

    public void computeKnightNextPositions(List<Position> nextPositions) {
        // directions a knight can travel in.
        final int[][] nextPosDeltas =
                new int[][] {
                        {1, 2}, {1, -2}, {-1, 2}, {-1, -2},
                        {2, 1}, {-2, 1}, {2, -1}, {-2, -1}
                };

        // iterate through all possible positions, getting any valid next positions
        for (int[] posDeltaPair : nextPosDeltas) {
            position.addPosAtDelta(posDeltaPair[0], posDeltaPair[1], board(), nextPositions);
        }
    }
}
