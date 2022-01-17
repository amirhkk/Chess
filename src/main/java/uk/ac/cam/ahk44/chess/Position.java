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

import java.util.List;
import java.util.Objects;

public class Position {

  /** RANK and FILE enums, the inner components of a Position. */
  public enum Rank {
    // row index
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6),
    SEVEN(7),
    EIGHT(8);

    private final int index;

    Rank(int index) {
      this.index = index;
    }

    private Rank movedBy(int delta) {
      int newRank = index + delta;

      if (newRank >= 8 || newRank < 0) {
        return null;

      } else {
        // relies on ordinal
        return values()[newRank];
      }
    }

    public int value() {
      return index;
    }

    @Override
    public String toString() {
      return Integer.toString(index);
    }
  }

  public enum File {
    // column index
    A(1),
    B(2),
    C(3),
    D(4),
    E(5),
    F(6),
    G(7),
    H(8);

    private final int index;

    File(int index) {
      this.index = index;
    }

    private File movedBy(int delta) {
      int newFile = index + delta;

      if (newFile >= 8 || newFile < 0) {
        return null;

      } else {
        // relies on ordinal
        return values()[newFile];
      }
    }

    public int value() {
      return index;
    }
  }

  /** State in a position object, it has a rank, a file, and access to all position objects. */
  private final Rank rank;

  private final File file;
  private static Position[][] allPositions = new Position[8][8];

  /**
   * Create a position object for every position on the board. There will only ever be one object
   * per position.
   */
  static {
    for (Rank rank : Rank.values()) {
      for (File file : File.values()) {
        allPositions[rank.index - 1][file.index - 1] = new Position(rank, file);
      }
    }
  }

  /**
   * Positions can only be constructed from the position class. Use the factory method getPosAt
   * during initial board setup.
   *
   * @param rank of the position
   * @param file of the position
   */
  private Position(Rank rank, File file) {
    this.rank = rank;
    this.file = file;
  }

  /**
   * Factory method for retrieving positions at the start of the game.
   *
   * @param rank of the position
   * @param file of the position
   * @return Position object at the rank and file
   */
  public static Position getPosAt(Rank rank, File file) {
    return allPositions[rank.index - 1][file.index - 1];
  }

  public Rank getRank() {
    return rank;
  }

  public File getFile() {
    return file;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Position position = (Position) o;
    return rank == position.rank && file == position.file;
  }

  @Override
  public int hashCode() {
    return Objects.hash(rank, file);
  }

  public Position getPosAtDelta(int deltaRank, int deltaFile) {
    // calculate new pos index
    int rindex = rank.index + deltaRank;
    int findex = file.index + deltaFile;

    // if valid return new pos, else null
    if (0 < rindex && rindex <= 8 && 0 < findex && findex <= 8) {
      return allPositions[rindex - 1][findex - 1];
    } else {
      // throw new InvalidPositionException(rank.index + deltaRank, rank.index + deltaFile);
      return null;
    }
  }

  public void addPosAtDelta(int deltaRank, int deltaFile, Board board, List<Position> positionList) {
    Position newPos = getPosAtDelta(deltaRank, deltaFile);
    if (newPos == null) return;

    if (!board.positionOccupied(newPos)){
      positionList.add(newPos);
    }

    else if (board.positionOccupied(this)){
      if(board.atPosition(newPos).colour() != board.atPosition(this).colour()) positionList.add(newPos);
    }
  }

  public void getAllDiagonalMoves(int maxDistance, Board board, List<Position> positionList) {
    // up and left diagonals
    getAllMovesOnPath(1, -1, board, maxDistance, positionList);

    // up and left diagonals
    getAllMovesOnPath(1, 1, board, maxDistance, positionList);

    // down and left diagonals
    getAllMovesOnPath(-1, -1, board, maxDistance, positionList);

    // down and right diagonals
    getAllMovesOnPath(-1, 1, board, maxDistance, positionList);
  }

  public void getAllStraightMoves(int maxDistance, Board board, List<Position> positionList) {
    // up
    getAllMovesOnPath(1, 0, board, maxDistance, positionList);

    // up
    getAllMovesOnPath(-1, 0, board, maxDistance, positionList);

    // left
    getAllMovesOnPath(0, -1, board, maxDistance, positionList);

    // right
    getAllMovesOnPath(0, 1, board, maxDistance, positionList);
  }

  private void getAllMovesOnPath(
      int rankDelta, int fileDelta, Board board, int cap, List<Position> positionList) {
    if (cap <= 0) {
      return;
    }

    for (int step = 1; step <= cap; step += 1) {
      Position nextPos = getPosAtDelta(step * rankDelta, step * fileDelta);
      if (nextPos == null) break;

      if (board.positionOccupied(nextPos) && board.positionOccupied(this)) {
        board.atPosition(this).name();
        if (board.atPosition(nextPos).colour() != board.atPosition(this).colour()) {
          positionList.add(nextPos);
        }
        break;
      }

      positionList.add(nextPos);
    }
  }

  @Override
  public String toString() {
    return this.file.toString().toLowerCase() + this.rank.toString();
  }
}
