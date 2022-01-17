/*
 * Copyright 2021 Ben Philps <bp413@cam.ac.uk>, Amir Kadkhodaei
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.cam.ahk44.chess;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

public class Player {

  private final PieceColor pieceColor;
  private final ArrayList<Piece> pieces;
  private boolean
      inCheck; // the check behaviour should be the second part of the task maybe...? I think so.
  private final Board board;

  public Player(Board board, PieceColor color) {
    // setup state
    this.pieceColor = color;
    pieces = new ArrayList<>();
    this.board = board;
    inCheck = false;

    // add all pieces
    board.forEachPiece(piece -> piece.colour() == color, pieces::add);
  }

  void removePiece(Piece piece) {
    pieces.remove(piece);
    // the removal from the board takes place as part of the move when the user takes.
  }

  void addPiece(Piece piece) {
    pieces.add(piece);
  }

  /** Private static inner class for holding potential moves. */
  private static class Move {
    Piece pieceToMove;
    Position positionToMoveTo;
    int moveValue;

    public Move(Piece pieceToMove, Position positionToMoveTo, Piece pieceAtMovePosition) {
      this.pieceToMove = pieceToMove;
      this.positionToMoveTo = positionToMoveTo;
      // if there is a piece at the move position (it must be an enemy piece to be a valid position,
      // then get the value of taking it. else get 0. also include which moves get us closer to the
      // king.
      // these moves are more preferable.
      moveValue = pieceAtMovePosition == null ? 0 : pieceAtMovePosition.value();
    }

    private int value() {
      return moveValue;
    }
  }

  void playRandomMove() {
    // new algorithm should be
    // order all moves by value that they can take.
    // modify the move to be able to take king.
    // then see if this move causes check in the enemy on the next move, (so one move lookahead).
    // then try methods one at a time and see if I am in check or not. first try the algorithm
    // without
    // pawn promotion, and then *with* pawn promotion.

    PriorityQueue<Move> movesQueue =
        new PriorityQueue<>(Comparator.comparingInt(Move::value).reversed());

    // get all possible moves for each piece, and put it in the map, sorted by the value of the move
    for (Piece playerPiece : pieces) {
      for (Position movePosition : playerPiece.validNextPositions()) {
        // Piece at position
        Piece atMovePos =
            board.positionOccupied(movePosition) ? board.atPosition(movePosition) : null;
        Position orignalPos = playerPiece.position();

        // create a log of the move
        Move move = new Move(playerPiece, movePosition, atMovePos);

        // simulate the move (don't print out the move, since we are just testing it for check here)
        board.performMove(orignalPos, movePosition, playerPiece, this, false);

        // check if it causes this player to be in check. if so can't use this move
        if (computeInCheck(this)) {
          // undo move
          board.undoMove(movePosition, orignalPos, playerPiece, atMovePos, this);
          // continue to next move in loop iteration.
          continue;
        }

        // check if it causes the opponent to be in check, if so, upgrade the value of the move
        // note that its not possible for the move to already have the value of King, due to the
        // rules of check.
        if (computeInCheck(board.opponent(this))) {
          move.moveValue += King.KING_VALUE;
        }

        // add move to moves tree
        movesQueue.add(move);

        // undo the move
        board.undoMove(movePosition, orignalPos, playerPiece, atMovePos, this);
      }
    }

    // can't move, so its a checkmate.
    if (movesQueue.isEmpty()) {
      System.out.println("pieces at mate: " + pieces);
      board.completeCheckmate(this, true);
      return;
    }

    Move nextMove = movesQueue.peek();

    if (nextMove.value() == 0) {
      for (int i = 0; i < new Random().nextInt(Math.min(movesQueue.size(), 3)); i += 1)
        nextMove = movesQueue.poll();
    } else {
      // get a move based upon the pieceColor's strategy
      nextMove =
          pieceColor == PieceColor.BLACK ? strategyBlack(movesQueue) : strategyWhite(movesQueue);
    }

    // complete the selected move
    board.performMove(
        nextMove.pieceToMove.position(),
        nextMove.positionToMoveTo,
        nextMove.pieceToMove,
        this,
        true);
  }

  private Move strategyWhite(PriorityQueue<Move> movesQueue) {
    // pick from the first half or third (rounded up) of moves from the moves queue.
    double length = movesQueue.size();
    double cap = 0;
    if (length > 15) {
      cap = Math.ceil(length / 3.0);
    } else {
      cap = Math.ceil(length / 2.0);
    }

    int randomIndex = new Random().nextInt((int) cap);
    while (randomIndex > 0) {
      randomIndex--;
      movesQueue.poll();
    }

    return movesQueue.poll();
  }

  private Move strategyBlack(PriorityQueue<Move> movesQueue) {
    // only pick from best moves
    // pick a random move from the list of highest value moves.

    ArrayList<Move> allHighestValueMoves = new ArrayList<>();
    int maxValue = 0;
    // iterate through the tree, from greatest value move though to least value move
    while (!movesQueue.isEmpty()) {
      Move move = movesQueue.poll();

      if (move.moveValue >= maxValue) {
        maxValue = move.moveValue;
        allHighestValueMoves.add(move);

      } else {
        // its a descending set, so we've reached the last of the highest value moves.
        break;
      }
    }

    return allHighestValueMoves.get(new Random().nextInt(allHighestValueMoves.size()));
  }

  /**
   * @param player calculate, if in the current board state, this player is in check.
   * @return true if the player is in check, false otherwise.
   */
  private boolean computeInCheck(Player player) {
    // get the opponent of the player
    Player opponent = board.opponent(player);

    // get all the possible move positions for that player
    for (Piece piece : opponent.pieces) {
      for (Position position : piece.validNextPositions()) {

        if (board.positionOccupied(position)) {
          if (board.atPosition(position).value() == King.KING_VALUE) {
            // opponents piece can move to this players king, this players king is in check!
            return true;
          }
        }
      }
    }
    // no opponent moves can capture this player's king, so not in check!
    return false;
  }

  public List<Piece> getPieces() {
    return pieces;
  }

  PieceColor colour() {
    return pieceColor;
  }
}
