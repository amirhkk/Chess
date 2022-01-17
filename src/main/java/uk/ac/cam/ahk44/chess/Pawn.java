package uk.ac.cam.ahk44.chess;

import java.util.ArrayList;
import java.util.List;

import static uk.ac.cam.ahk44.chess.PieceColor.BLACK;
import static uk.ac.cam.ahk44.chess.PieceColor.WHITE;
import static uk.ac.cam.ahk44.chess.Position.Rank.SEVEN;
import static uk.ac.cam.ahk44.chess.Position.Rank.TWO;

@Component
public class Pawn extends Piece{

    public Pawn(char name, Position piecePosition, PieceColor pieceColor, Board board) {
        super(name, piecePosition, pieceColor, board);
        this.name = name;
        this.position = piecePosition;
        this.pieceColor = pieceColor;
        this.board = board;
    }

    @Override
    List<Position> validNextPositions(){
        List<Position> nextPositions = new ArrayList<>();
        computePawnNextPositions(nextPositions);
        return nextPositions;
    }

    @Override
    char icon() {
        boolean colourSwitch = pieceColor == PieceColor.BLACK;
        return colourSwitch ? '♟' : '♙';
    }

    @Override
    int value() {
        return 1;
    }

    public void computePawnNextPositions(List<Position> nextPositions) {
        // The En passant move is not included.
        // The Promotion is not included.

    /*
    pawns can move up 1 if it is a non occupied square.
    pawns can move (and take) up 1 and left or right 1 if the square is occupied by an opponent
    pawns can move up 2 if they are currently on their home row (pawns cannot go backwards)

     */

        int upDir = (pieceColor == WHITE ? 1 : -1);

        // move up by one
        addPawnPositionIfValid(upDir, 0, false, nextPositions);

        // move up left and right
        addPawnPositionIfValid(upDir, -1, true, nextPositions);
        addPawnPositionIfValid(upDir, 1, true, nextPositions);

        // move up two if on their home row
        if (position().getRank() == (colour() == BLACK ? SEVEN : TWO)) {
            addPawnPositionIfValid(upDir + upDir, 0, false, nextPositions);
        }
    }

    private void addPawnPositionIfValid(
            int deltaRank,
            int deltaFile,
            boolean allowIfOccupiedByOpponent,
            List<Position> nextPositions) {

        Position movePosition = position().getPosAtDelta(deltaRank, deltaFile);

        if (movePosition != null) {
            boolean movePosOccupied = board().positionOccupied(movePosition);
            // the up left and right cases
            if (allowIfOccupiedByOpponent
                    && movePosOccupied
                    && board().atPosition(movePosition).colour() != colour()) {
                nextPositions.add(movePosition);
            }
            // the "up straight ahead" and "up straight ahead two" moves.
            else if (!allowIfOccupiedByOpponent && !movePosOccupied) {
                nextPositions.add(movePosition);
            }
        }
    }
}
