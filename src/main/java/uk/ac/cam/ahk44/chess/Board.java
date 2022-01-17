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

import static uk.ac.cam.ahk44.chess.Position.getPosAt;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import uk.ac.cam.ahk44.chess.Position.File;
import uk.ac.cam.ahk44.chess.Position.Rank;

public class Board {

  private Map<Position, Piece> piecesMap;
  private Player player1;
  private Player player2;
  private boolean checkMate;

  public Board() {
    // init state
    piecesMap = new HashMap<>();
    checkMate = false;

    // setup the board
    setupBoard();

    // setup the players
    player1 = new Player(this, PieceColor.BLACK);
    player2 = new Player(this, PieceColor.WHITE);
  }

  /**
   * Methods in this section are purely for initial board setup
   * ------------------------------------------------------
   */
  private void setupBoard() {
    setupPawns();
    setupRooks();
    setupKnights();
    setupBishops();
    setupQueensAndKings();
  }

  private void setupPawns() {
    // setup pawns
    for (File file : File.values()) {
      Position positionBlack = getPosAt(Rank.SEVEN, file);
      piecesMap.put(positionBlack, (Piece) new Pawn('P', positionBlack, PieceColor.BLACK, this));

      Position positionWhite = getPosAt(Rank.TWO, file);
      piecesMap.put(positionWhite, new Pawn('P', positionWhite, PieceColor.WHITE, this));
    }
  }

  private void setupRooks() {
    // setup rook
    Position positionRookBlackLeft = getPosAt(Rank.EIGHT, File.A);
    Position positionRookBlackRight = getPosAt(Rank.EIGHT, File.H);
    Position positionRookWhiteLeft = getPosAt(Rank.ONE, File.A);
    Position positionRookWhiteRight = getPosAt(Rank.ONE, File.H);
    piecesMap.put(
        positionRookBlackLeft, new Rook('R', positionRookBlackLeft, PieceColor.BLACK, this));
    piecesMap.put(
        positionRookBlackRight, new Rook('R', positionRookBlackRight, PieceColor.BLACK, this));
    piecesMap.put(
        positionRookWhiteLeft, new Rook('R', positionRookWhiteLeft, PieceColor.WHITE, this));
    piecesMap.put(
        positionRookWhiteRight, new Rook('R', positionRookWhiteRight, PieceColor.WHITE, this));
  }

  private void setupKnights() {
    // setup knight
    Position positionKnightBlackLeft = getPosAt(Rank.EIGHT, File.B);
    Position positionKnightBlackRight = getPosAt(Rank.EIGHT, File.G);
    Position positionKnightWhiteLeft = getPosAt(Rank.ONE, File.B);
    Position positionKnightWhiteRight = getPosAt(Rank.ONE, File.G);
    piecesMap.put(
        positionKnightBlackLeft, new Knight('N', positionKnightBlackLeft, PieceColor.BLACK, this));
    piecesMap.put(
        positionKnightBlackRight, new Knight('N', positionKnightBlackRight, PieceColor.BLACK, this));
    piecesMap.put(
        positionKnightWhiteLeft, new Knight('N', positionKnightWhiteLeft, PieceColor.WHITE, this));
    piecesMap.put(
        positionKnightWhiteRight, new Knight('N', positionKnightWhiteRight, PieceColor.WHITE, this));
  }

  private void setupBishops() {
    // setup bishop
    Position positionBishopBlackLeft = getPosAt(Rank.EIGHT, File.C);
    Position positionBishopBlackRight = getPosAt(Rank.EIGHT, File.F);
    Position positionBishopWhiteLeft = getPosAt(Rank.ONE, File.C);
    Position positionBishopWhiteRight = getPosAt(Rank.ONE, File.F);
    piecesMap.put(
        positionBishopBlackLeft, new Bishop('B', positionBishopBlackLeft, PieceColor.BLACK, this));
    piecesMap.put(
        positionBishopBlackRight, new Bishop('B', positionBishopBlackRight, PieceColor.BLACK, this));
    piecesMap.put(
        positionBishopWhiteLeft, new Bishop('B', positionBishopWhiteLeft, PieceColor.WHITE, this));
    piecesMap.put(
        positionBishopWhiteRight, new Bishop('B', positionBishopWhiteRight, PieceColor.WHITE, this));
  }

  private void setupQueensAndKings() {
    // setup queen
    Position positionQueenBlack = getPosAt(Rank.EIGHT, File.D);
    Position positionQueenWhite = getPosAt(Rank.ONE, File.D);
    piecesMap.put(positionQueenBlack, new Queen('Q', positionQueenBlack, PieceColor.BLACK, this));
    piecesMap.put(positionQueenWhite, new Queen('Q', positionQueenWhite, PieceColor.WHITE, this));

    // setup king
    Position positionKingBlack = getPosAt(Rank.EIGHT, File.E);
    Position positionKingWhite = getPosAt(Rank.ONE, File.E);
    piecesMap.put(positionKingBlack, new King('K', positionKingBlack, PieceColor.BLACK, this));
    piecesMap.put(positionKingWhite, new King('K', positionKingWhite, PieceColor.WHITE, this));
  }

  /*
   * -----------------------------------------------------------------------------------------------------------------
   */

  /**
   * AUX GAME PLAY FUNCTIONS
   * -----------------------------------------------------------------------------------------
   */
  void forEachPiece(Predicate<Piece> pieceFilter, Consumer<Piece> pieceConsumer) {
    piecesMap.values().stream().filter(pieceFilter).forEach(pieceConsumer);
  }

  public Piece atPosition(Position position) {
    // returns null if there is no mapping.
    return piecesMap.get(position);
  }

  // note, not using .containsKey since the map may store some nulls.
  public boolean positionOccupied(Position position) {
    return piecesMap.get(position) != null;
  }

  void performMove(Position before, Position after, Piece piece, Player player, boolean printMove) {

    // update piece
    piece.moveTo(after);

    // if there is a piece at the move position, it must be an opponents, so take it.
    if (piecesMap.containsKey(after)) opponent(player).removePiece(piecesMap.get(after));

    // update board
    piecesMap.remove(before);
    piecesMap.put(after, piece);

    // print out the move, if printing enabled
    if (printMove) printMove(piece, before, after);
  }

  void undoMove(
      Position afterOriginalMove,
      Position beforeOriginalMove,
      Piece playerPiece,
      Piece takenPiece,
      Player player) {

    // update board
    piecesMap.put(beforeOriginalMove, playerPiece);
    if (takenPiece != null) {
      piecesMap.put(afterOriginalMove, takenPiece);
      // note that taken piece might be null
      opponent(player).addPiece(takenPiece);
    } else {
      piecesMap.remove(afterOriginalMove);
    }

    // update original piece
    playerPiece.moveTo(beforeOriginalMove);
  }

  private void printMove(Piece piece, Position before, Position after) {
    System.out.println("");
    System.out.println(
        piece.colour() + " " + piece.name() + " " + before.toString() + " to " + after.toString());
  }

  void completeCheckmate(Player losingPlayer, boolean printing) {
    checkMate = true;
    if (printing) {
      System.out.println(opponent(losingPlayer).colour() + " wins!");
      printBoard();
    }
  }

  Player opponent(Player player) {
    // ternary operator
    return player == player1 ? player2 : player1;
  }

  void addPiece(Piece piece) {
    piecesMap.put(piece.position(), piece);
  }

  /*
   * -----------------------------------------------------------------------------------------------------------------
   */

  /**
   * RUNNING THE GAME
   * ------------------------------------------------------------------------------------------------
   */
  public boolean resultedInCheckmate() {
    return checkMate;
  }

  // just for reference
  private static final String fullBoard =
      "╔═╤═╤═╤═╤═╤═╤═╤═╗\n"
          + "║♜│♞│♝│♛│♚│♝│♞│♜║\n"
          + "╟─┼─┼─┼─┼─┼─┼─┼─╢\n"
          + "║♟│♟│♟│♟│♟│♟│♟│♟║\n"
          + "╟─┼─┼─┼─┼─┼─┼─┼─╢\n"
          + "║ │░│ │░│ │░│ │░║\n"
          + "╟─┼─┼─┼─┼─┼─┼─┼─╢\n"
          + "║░│ │░│ │░│ │░│ ║\n"
          + "╟─┼─┼─┼─┼─┼─┼─┼─╢\n"
          + "║ │░│ │░│ │░│ │░║\n"
          + "╟─┼─┼─┼─┼─┼─┼─┼─╢\n"
          + "║░│ │░│ │░│ │░│ ║\n"
          + "╟─┼─┼─┼─┼─┼─┼─┼─╢\n"
          + "║♙│♙│♙│♙│♙│♙│♙│♙║\n"
          + "╟─┼─┼─┼─┼─┼─┼─┼─╢\n"
          + "║♖│♘│♗│♕│♔│♗│♘│♖║\n"
          + "╚═╧═╧═╧═╧═╧═╧═╧═╝";

  private static final String emptyBoard =
      " ╔═╤═╤═╤═╤═╤═╤═╤═╗\n"
          + "8║ │░│ │░│ │░│ │░║\n"
          + " ╟─┼─┼─┼─┼─┼─┼─┼─╢\n"
          + "7║░│ │░│ │░│ │░│ ║\n"
          + " ╟─┼─┼─┼─┼─┼─┼─┼─╢\n"
          + "6║ │░│ │░│ │░│ │░║\n"
          + " ╟─┼─┼─┼─┼─┼─┼─┼─╢\n"
          + "5║░│ │░│ │░│ │░│ ║\n"
          + " ╟─┼─┼─┼─┼─┼─┼─┼─╢\n"
          + "4║ │░│ │░│ │░│ │░║\n"
          + " ╟─┼─┼─┼─┼─┼─┼─┼─╢\n"
          + "3║░│ │░│ │░│ │░│ ║\n"
          + " ╟─┼─┼─┼─┼─┼─┼─┼─╢\n"
          + "2║ │░│ │░│ │░│ │░║\n"
          + " ╟─┼─┼─┼─┼─┼─┼─┼─╢\n"
          + "1║░│ │░│ │░│ │░│ ║\n"
          + " ╚═╧═╧═╧═╧═╧═╧═╧═╝\n"
          + "  a b c d e f g h";

  private static final int rowDistance = 38;
  private static final int a8Position = 21;
  private static final int boardDimension = 8;

  public void printBoard() {
    char[] boardArray = emptyBoard.toCharArray();

    for (Piece piece : piecesMap.values()) {
      // get indexes of rank and file of the piece
      if (piece == null) {
        continue;
      }
      int rankValue = piece.position().getRank().value() - 1;
      int fileValue = piece.position().getFile().value() - 1;

      // compute the index of the piece on the board string.
      int index =
          a8Position
              + ((boardDimension - rankValue - 1) * rowDistance) // row position
              + (2 * (fileValue)); // col position

      // add icon for piece to board
      boardArray[index] = piece.icon();
    }

    System.out.println(String.valueOf(boardArray));
  }

  void runRandomGame() {
    // white to start - white is player 2.
    // printBoard();

    int count = 0;

    while (true) {
      player2.playRandomMove();
      if (checkMate) break;
      player1.playRandomMove();
      if (checkMate) break;
      //printBoard();

      // if it is a stalemate
      if (piecesMap.size() == 2) {
        printBoard();
        System.out.println("STALEMATE");
        break;
      }

      count += 2;
      if (count == 512) {
        // game has gone on for a while, end it.
        printBoard();
        System.out.println("Game exceeded 512 moves - terminated");
        break;
      }
    }
  }
}
