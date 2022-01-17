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

import static uk.ac.cam.ahk44.chess.PieceColor.BLACK;
import static uk.ac.cam.ahk44.chess.PieceColor.WHITE;
import static uk.ac.cam.ahk44.chess.Position.Rank.SEVEN;
import static uk.ac.cam.ahk44.chess.Position.Rank.TWO;

import java.util.ArrayList;
import java.util.List;

public abstract class Piece {

  protected static int KING_VALUE = 10000;

  protected Position position;
  protected PieceColor pieceColor;
  protected Board board;
  protected char name;

  public Piece(char name, Position piecePosition, PieceColor pieceColor, Board board) {
    this.name = name;
    this.position = piecePosition;
    this.pieceColor = pieceColor;
    this.board = board;
  }


  /* naming convention:
     K -> king
     P -> pawn
     B -> bishop
     N -> knight
     R -> rook
     Q -> queen
  */

  abstract List<Position> validNextPositions();

  abstract char icon();

  abstract int value();

  protected Position position() {
    return position;
  }

  protected char name() {
    return name;
  }

  protected void moveTo(Position newPosition) {
    position = newPosition;
  }

  protected PieceColor colour() {
    return pieceColor;
  }

  public String toString() {
    return name() + " " + position.toString();
  }

  protected Board board() {
    return board;
  }
}
