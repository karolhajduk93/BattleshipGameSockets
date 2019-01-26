package com.karolhajduk.battleship;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Captain {

    private List<Ship> ships = new ArrayList<>();

    private static int[] startCoordinatesX = {600,
            600, 600 + 3 * 32 + 10,
            600, 600 + 2 * 32 + 10, 600 + (2 * 32 + 10) * 2,
            600, 600 + (32 + 10), 600 + 2 * (32 + 10), 600 + 3 * (32 + 10)};
    private static int[] startCoordinatesY = {100,
            150, 150,
            200, 200, 200,
            250, 250, 250, 250};

    private int[][] myBoard = new int[10][10];
    private int[][] enemyBoard = new int[10][10];
    private boolean winLoseState = false;
    private int ready = 0;

    public boolean isMyTurn() {
        return myTurn;
    }

    public void setMyTurn(boolean myTurn) {
        this.myTurn = myTurn;
    }

    private boolean myTurn = false; ////////////////////////////////

    public int getReady() {
        return ready;
    }
    public void setReady(int ready) {
        this.ready = ready;
    }
    public void initializeShips(){
        //create Ships inside ships list
        //Starting coordinates
        int k = 0;
        for (int i = 4; i > 0; i--) {
            for (int j = i; j < 5; j++) {
                ships.add(new Ship(i, startCoordinatesX[k], startCoordinatesY[k]));
                k++;
            }
        }
    }


    public int[][] getMyBoard() {
        return myBoard;
    }
    public void setMyBoard(Ship ship) {
        int logicalX, logicalY;
        logicalX = (ship.getCoordinates().getX() - 150)/32;
        logicalY = (ship.getCoordinates().getY() - 50)/32;
        for (int i = 0; i < ship.getSize(); i++) {
            if(ship.isHorizontal())
                myBoard[logicalX+i][logicalY] = 1;
            else
                myBoard[logicalX][logicalY+i] = 1;
        }
    }
    public List<Ship> getShips() {
        return ships;
    }

    public int[][] getEnemyBoard() {
        return enemyBoard;
    }

    public void setEnemyBoard(String enemyBoardString) {
        int k = 0;
        for(int i = 0; i < enemyBoard.length; i++) {
            for (int j = 0; j < enemyBoard[i].length; j++) {
                if (enemyBoardString.charAt(k) == '1')
                    enemyBoard[j][i] = 1;
                else
                    enemyBoard[j][i] = 0;
                k++;
            }
        }
    }

    public void resetPositions(){
        int k = 0;
        for (int i = 4; i > 0; i--) {
            for (int j = i; j < 5; j++) {
                ships.get(k).getCoordinates().setX(startCoordinatesX[k]);
                ships.get(k).getCoordinates().setY(startCoordinatesY[k]);
                ships.get(k).setHorizontal(true);
                k++;
            }
        }
        for (int[] ints: myBoard) {
            Arrays.fill(ints, 0);
        }
    }
}
