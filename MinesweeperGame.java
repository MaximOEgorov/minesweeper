package com.javarush.games.minesweeper;

import com.javarush.engine.cell.Color;
import com.javarush.engine.cell.Game;

import java.util.ArrayList;
import java.util.List;

public class MinesweeperGame extends Game {
    private static final int SIDE = 9;
    private GameObject[][] gameField = new GameObject[SIDE][SIDE];
    private int countMinesOnField;
    private int countFlags;
    private int countClosedTiles = SIDE * SIDE;
    private int score;
    private boolean isGameStopped;
    private static final String MINE = "\uD83D\uDCA3";
    private static final String FLAG = "\uD83D\uDEA9";

    @Override
    public void initialize() {
        setScreenSize(SIDE, SIDE);
        createGame();
    }

    private void createGame() {
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                boolean isMine = getRandomNumber(10) < 1;
                if (isMine) {
                    countMinesOnField++;
                }
                gameField[y][x] = new GameObject(x, y, isMine);
                setCellColor(x, y, Color.ORANGE);
                setCellValue(x, y, "");
            }
        }
        countMineNeighbors();
        countFlags = countMinesOnField;
    }

    private void restart() {
        countClosedTiles = SIDE * SIDE;
        score = 0;
        setScore(score);
        countMinesOnField = 0;
        isGameStopped = false;
        createGame();
    }

    private List<GameObject> getNeighbors(GameObject gameObject) {
        List<GameObject> result = new ArrayList<>();
        for (int y = gameObject.y - 1; y <= gameObject.y + 1; y++) {
            for (int x = gameObject.x - 1; x <= gameObject.x + 1; x++) {
                if (y < 0 || y >= SIDE) {
                    continue;
                }
                if (x < 0 || x >= SIDE) {
                    continue;
                }
                if (gameField[y][x] == gameObject) {
                    continue;
                }
                result.add(gameField[y][x]);
            }
        }
        return result;
    }

    private void countMineNeighbors() {
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                GameObject gameObject = gameField[y][x];
                if (!gameObject.isMine) {
                    for (GameObject cellObject : getNeighbors(gameObject)) {
                        if (cellObject.isMine)
                            gameObject.countMineNeighbors++;
                    }
                }
            }
        }

    }

    @Override
    public void onMouseLeftClick(int x, int y) {
        if (!isGameStopped) {
            super.onMouseLeftClick(x, y);
            openTile(x, y);
        } else {
            restart();
        }
    }

    @Override
    public void onMouseRightClick(int x, int y) {
        super.onMouseRightClick(x, y);
        markTile(x, y);
    }

    private void openTile(int x, int y) {
        GameObject curField = gameField[y][x];
        if (curField.isOpen || curField.isFlag || isGameStopped)
            return;
        if (curField.isMine) {
            setCellValueEx(curField.x, curField.y, Color.RED, MINE);
            gameOver();
        } else if (curField.countMineNeighbors > 0 && !curField.isMine) {
            setCellNumber(curField.x, curField.y, curField.countMineNeighbors);
            score += 5;
            setScore(score);
        }
        curField.isOpen = true;
        countClosedTiles--;
        setCellColor(x, y, Color.GREEN);
        if (curField.countMineNeighbors == 0 && !curField.isMine) {
            setCellValue(curField.x, curField.y, "");
            for (GameObject cellObject : getNeighbors(curField)) {
                if (!cellObject.isOpen)
                    openTile(cellObject.x, cellObject.y);
            }
        }
        if (countClosedTiles == countMinesOnField && !curField.isMine)
            win();

    }

    private void markTile(int x, int y) {
        if (!isGameStopped) {
            GameObject curField = gameField[y][x];
            if (curField.isOpen || (!curField.isFlag && countFlags == 0))
                return;
            if (!curField.isFlag) {
                curField.isFlag = true;
                countFlags--;
                setCellValue(curField.x, curField.y, FLAG);
                setCellColor(curField.x, curField.y, Color.YELLOW);
            } else {
                curField.isFlag = false;
                countFlags++;
                setCellValue(curField.x, curField.y, "");
                setCellColor(curField.x, curField.y, Color.ORANGE);
            }
        }
    }

    private void gameOver() {
        isGameStopped = true;
        showMessageDialog(Color.ALICEBLUE, "Проиграл!!", Color.BLACK, 50);
    }

    private void win() {
        isGameStopped = true;
        showMessageDialog(Color.ALICEBLUE, "Выиграл!!", Color.BLACK, 50);
    }

}

