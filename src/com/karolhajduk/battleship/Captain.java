package com.karolhajduk.battleship;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Captain {

    static final int DEFAULT_GRID_SIZE = 32;
    static final int MAX_NUMBER_OF_HITS = 20;

    private List<Ship> ships;
    private List<Integer> myScoreDisplay;
    private List<Integer> enemyScoreDisplay;

    private static int[] startShipCoordinatesX = {600,
            600, 600 + 3 * DEFAULT_GRID_SIZE + 10,
            600, 600 + 2 * DEFAULT_GRID_SIZE + 10, 600 + (2 * DEFAULT_GRID_SIZE + 10) * 2,
            600, 600 + (DEFAULT_GRID_SIZE + 10), 600 + 2 * (DEFAULT_GRID_SIZE + 10), 600 + 3 * (DEFAULT_GRID_SIZE + 10)};
    private static int[] startShipCoordinatesY = {100,
            150, 150,
            200, 200, 200,
            250, 250, 250, 250};

    private HitOrMiss[][] myBoard;
    private HitOrMiss[][] enemyBoard;
    private int hitsTaken;
    private int hitsGiven;
    private GameState ready;
    private boolean myTurn;

    Captain(){
        ships = new ArrayList<>();
        initializeShips();
        initializeScoreDisplays();
        myBoard = new HitOrMiss[10][10];
        enemyBoard = new HitOrMiss[10][10];
        hitsTaken = 0;
        hitsGiven = 0;
        ready = GameState.PREPARE;
        myTurn = false;

        for (HitOrMiss[] boxes: myBoard) {
            Arrays.fill(boxes, HitOrMiss.COVERED_MISS);
        }
    }

    public void getSunkShip(int shipSize, boolean enemy){
        if(shipSize == 0)
            return;

        if(enemy) {
            enemyScoreDisplay.remove(Integer.valueOf(shipSize));
            enemyScoreDisplay.add(0);
        }
        else {
            myScoreDisplay.remove(Integer.valueOf(shipSize));
            myScoreDisplay.add(0);
        }
    }

    public List<Integer> getMyScoreDisplay() {
        return myScoreDisplay;
    }

    public List<Integer> getEnemyScoreDisplay() {
        return enemyScoreDisplay;
    }

    public void initializeScoreDisplays() {
        myScoreDisplay = new ArrayList<>(Arrays.asList(new Integer[]{4, 3, 3, 2, 2, 2, 1, 1, 1, 1}));
        enemyScoreDisplay = new ArrayList<>(Arrays.asList(new Integer[]{4, 3, 3, 2, 2, 2, 1, 1, 1, 1}));
    }

    public int getHitsTaken() {
        return hitsTaken;
    }

    public void setHitsTaken(int hitsTaken) {
        this.hitsTaken = hitsTaken;
    }

    public int getHitsGiven() {
        return hitsGiven;
    }

    public void setHitsGiven(int hitsGiven) {
        this.hitsGiven = hitsGiven;
    }

    public boolean isMyTurn() {
        return myTurn;
    }

    public void setMyTurn(boolean myTurn) {
        this.myTurn = myTurn;
    }

    public GameState getReady() {
        return ready;
    }
    public void setReady(GameState ready) {
        this.ready = ready;
    }
    public void initializeShips(){
        //create Ships inside ships list
        //Starting coordinates
        int k = 0;
        for (int i = 4; i > 0; i--) {
            for (int j = i; j < 5; j++) {
                ships.add(new Ship(i, startShipCoordinatesX[k], startShipCoordinatesY[k]));
                k++;
            }
        }
    }


    public HitOrMiss[][] getMyBoard() {
        return myBoard;
    }
    public void setMyBoard(Ship ship) {

        int logicalX, logicalY;
        logicalX = (ship.getCoordinates().getX() - BattleShipGame.MY_BOARD_START_POSITION_X)/DEFAULT_GRID_SIZE;
        logicalY = (ship.getCoordinates().getY() - BattleShipGame.BOTH_BOARD_START_POSITION_Y)/DEFAULT_GRID_SIZE;
        for (int i = 0; i < ship.getSize(); i++) {
            if(ship.isHorizontal())
                myBoard[logicalX+i][logicalY] = HitOrMiss.COVERED_HIT;
            else
                myBoard[logicalX][logicalY+i] = HitOrMiss.COVERED_HIT;
        }
    }
    public List<Ship> getShips() {
        return ships;
    }

    public HitOrMiss[][] getEnemyBoard() {
        return enemyBoard;
    }

    public void setEnemyBoard(String enemyBoardString) {
        int k = 0;
        for(int i = 0; i < enemyBoard.length; i++) {
            for (int j = 0; j < enemyBoard[i].length; j++) {
                if (enemyBoardString.charAt(k) == '1')
                    enemyBoard[j][i] = HitOrMiss.COVERED_HIT;
                else
                    enemyBoard[j][i] = HitOrMiss.COVERED_MISS;
                k++;
            }
        }
    }

    public void resetPositions(){
        int k = 0;
        for (int i = 4; i > 0; i--) {
            for (int j = i; j < 5; j++) {
                ships.get(k).getCoordinates().setX(startShipCoordinatesX[k]);
                ships.get(k).getCoordinates().setY(startShipCoordinatesY[k]);
                ships.get(k).setHorizontal(true);
                k++;
            }
        }
        for (HitOrMiss[] boxes: myBoard) {
            Arrays.fill(boxes, HitOrMiss.COVERED_MISS);
        }
    }
}
