package com.karolhajduk.battleship;

import javax.swing.*;
import java.awt.*;

public class DrawBoardAndShips extends JComponent {

    private static final int MISS_MARK_SIZE = 4;
    private Captain player;
    private static int wait = 0;
    DrawBoardAndShips(Captain player) {
        this.player = player;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2D = (Graphics2D) g;

        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);


        if (player.getReady().equals(GameState.PREPARE)) {
            paintStart(g2D);
        }
        else if(player.getReady().equals(GameState.WAITING)) {
            paintWait(g2D);
        }
        else if(player.getReady().equals(GameState.PLAYING)){
            paintBoards(g2D);
            paintMoves(g2D);
        }

    }

    public void paintStart(Graphics2D g){
        g.drawString("A        B        C        D        E        F        G        H        I        J", 165, 45);
        for (int x = BattleShipGame.MY_BOARD_START_POSITION_X; x < BattleShipGame.MY_BOARD_END_POSITION_X; x = x + Captain.DEFAULT_GRID_SIZE) {
            if (((x - BattleShipGame.MY_BOARD_START_POSITION_X) / Captain.DEFAULT_GRID_SIZE) + 1 != 10)
                g.drawString(Integer.toString(((x - BattleShipGame.MY_BOARD_START_POSITION_X) / Captain.DEFAULT_GRID_SIZE) + 1), 135, 75 + (x - BattleShipGame.MY_BOARD_START_POSITION_X));
            else
                g.drawString("10", 130, 75 + (x - BattleShipGame.MY_BOARD_START_POSITION_X));
            for (int y = BattleShipGame.BOTH_BOARD_START_POSITION_Y; y < BattleShipGame.BOTH_BOARD_END_POSITION_Y; y = y + Captain.DEFAULT_GRID_SIZE) {
                g.drawRect(x, y, Captain.DEFAULT_GRID_SIZE, Captain.DEFAULT_GRID_SIZE);
            }
        }

        g.drawString("Drag the ships to the grid then click to  rotate:", 600, 60);

        //Draw coordinates
        g.setStroke(new BasicStroke(2));

        g.setColor(new Color(0, 0, 1, (float) 0.7));
        player.getShips().stream().filter(ship -> ship.isHorizontal()).forEach(ship -> g.drawRect(ship.getCoordinates().getX(), ship.getCoordinates().getY(),
                ship.getSize()*Captain.DEFAULT_GRID_SIZE, Captain.DEFAULT_GRID_SIZE));
        player.getShips().stream().filter(ship -> !ship.isHorizontal()).forEach(ship -> g.drawRect(ship.getCoordinates().getX(), ship.getCoordinates().getY(),
                Captain.DEFAULT_GRID_SIZE, ship.getSize()*Captain.DEFAULT_GRID_SIZE));

        g.setColor(new Color(0, 0, 1, (float) 0.1));
        player.getShips().stream().filter(ship -> ship.isHorizontal()).forEach(ship -> g.fillRect(ship.getCoordinates().getX(), ship.getCoordinates().getY(),
                ship.getSize()*Captain.DEFAULT_GRID_SIZE, Captain.DEFAULT_GRID_SIZE));
        player.getShips().stream().filter(ship -> !ship.isHorizontal()).forEach(ship -> g.fillRect(ship.getCoordinates().getX(), ship.getCoordinates().getY(),
                Captain.DEFAULT_GRID_SIZE, ship.getSize()*32));
    }


    private void paintWait(Graphics2D g) {
        wait++;
        g.drawString("Waiting for player",480, 230);
        if( wait / 20 == 0) {
            g.fillOval(500, 250, 8, 8);
        }
        else if( wait / 20 == 1) {
            g.fillOval(500, 250, 8, 8);
            g.fillOval(520, 250, 8, 8);
        }
        else if( wait / 20 == 2){
            g.fillOval(500, 250, 8, 8);
            g.fillOval(520, 250, 8, 8);
            g.fillOval(540, 250, 8, 8);
        }
        else if( wait / 20 == 3){
            wait = 0;
        }
    }

    private void paintBoards(Graphics2D g) {
        paintStart(g);

        g.setColor(UIManager.getColor("Panel.background"));
        g.fillRect(550, 40, 300, 30);

        g.setColor(Color.DARK_GRAY);

        g.drawString("Your grid", 290, 390);
        g.drawString("Enemy  grid",730, 390 );

        g.setStroke(new BasicStroke(1));


        g.drawString("A        B        C        D        E        F        G        H        I        J", 615, 45);
        for (int x = BattleShipGame.ENEMY_BOARD_START_POSITION_X; x < BattleShipGame.ENEMY_BOARD_END_POSITION_X; x = x + Captain.DEFAULT_GRID_SIZE) {
            if (((x - BattleShipGame.ENEMY_BOARD_START_POSITION_X) / Captain.DEFAULT_GRID_SIZE) + 1 != 10)
                g.drawString(Integer.toString(((x - BattleShipGame.ENEMY_BOARD_START_POSITION_X) / Captain.DEFAULT_GRID_SIZE) + 1), 585, 75 + (x - BattleShipGame.ENEMY_BOARD_START_POSITION_X));
            else
                g.drawString("10", 580, 75 + (x - BattleShipGame.ENEMY_BOARD_START_POSITION_X));
            for (int y = BattleShipGame.BOTH_BOARD_START_POSITION_Y; y < BattleShipGame.BOTH_BOARD_END_POSITION_Y; y = y + Captain.DEFAULT_GRID_SIZE) {
                g.drawRect(x, y, Captain.DEFAULT_GRID_SIZE, Captain.DEFAULT_GRID_SIZE);
            }
        }
    }

    private void paintMoves(Graphics2D g) {
        g.setFont(new Font("Serif", Font.BOLD, 20));
        if(player.isMyTurn()) {
            g.setColor(Color.BLUE);
            g.drawString("Your turn", 500, 420);
        }
        else {
            g.setColor(Color.RED);
            g.drawString("Opponent's turn", 470, 420);
        }

        g.setStroke(new BasicStroke(2));
        g.setColor(Color.RED);

        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                if(player.getMyBoard()[x][y] == HitOrMiss.UNCOVERED_MISS) { // miss
                    g.fillOval(x * Captain.DEFAULT_GRID_SIZE + BattleShipGame.MY_BOARD_START_POSITION_X + (Captain.DEFAULT_GRID_SIZE-MISS_MARK_SIZE)/2, y * Captain.DEFAULT_GRID_SIZE + BattleShipGame.BOTH_BOARD_START_POSITION_Y + (Captain.DEFAULT_GRID_SIZE-MISS_MARK_SIZE)/2, MISS_MARK_SIZE, MISS_MARK_SIZE);
                }
                else if(player.getMyBoard()[x][y] == HitOrMiss.UNCOVERED_HIT || player.getMyBoard()[x][y] == HitOrMiss.SUNK) { // hit
                    g.drawLine(x*Captain.DEFAULT_GRID_SIZE + BattleShipGame.MY_BOARD_START_POSITION_X, y*Captain.DEFAULT_GRID_SIZE + BattleShipGame.BOTH_BOARD_START_POSITION_Y, x*Captain.DEFAULT_GRID_SIZE + BattleShipGame.MY_BOARD_START_POSITION_X + Captain.DEFAULT_GRID_SIZE, y*Captain.DEFAULT_GRID_SIZE + BattleShipGame.BOTH_BOARD_START_POSITION_Y + Captain.DEFAULT_GRID_SIZE);
                    g.drawLine(x*Captain.DEFAULT_GRID_SIZE + BattleShipGame.MY_BOARD_START_POSITION_X + Captain.DEFAULT_GRID_SIZE, y*Captain.DEFAULT_GRID_SIZE + BattleShipGame.BOTH_BOARD_START_POSITION_Y, x*Captain.DEFAULT_GRID_SIZE + BattleShipGame.MY_BOARD_START_POSITION_X, y*Captain.DEFAULT_GRID_SIZE + BattleShipGame.BOTH_BOARD_START_POSITION_Y + Captain.DEFAULT_GRID_SIZE);
                }
                if(player.getMyBoard()[x][y] == HitOrMiss.SUNK) {//sunk
                    g.drawRect(x*Captain.DEFAULT_GRID_SIZE + BattleShipGame.MY_BOARD_START_POSITION_X, y*Captain.DEFAULT_GRID_SIZE + BattleShipGame.BOTH_BOARD_START_POSITION_Y, Captain.DEFAULT_GRID_SIZE, Captain.DEFAULT_GRID_SIZE);
                }

                //Enemy Board
                if(player.getEnemyBoard()[x][y] == HitOrMiss.UNCOVERED_MISS) { // miss
                    g.fillOval(x * Captain.DEFAULT_GRID_SIZE + BattleShipGame.ENEMY_BOARD_START_POSITION_X + (Captain.DEFAULT_GRID_SIZE-MISS_MARK_SIZE)/2, y * Captain.DEFAULT_GRID_SIZE + BattleShipGame.BOTH_BOARD_START_POSITION_Y + (Captain.DEFAULT_GRID_SIZE-MISS_MARK_SIZE)/2, MISS_MARK_SIZE, MISS_MARK_SIZE);
                }
                else if(player.getEnemyBoard()[x][y] == HitOrMiss.UNCOVERED_HIT || player.getEnemyBoard()[x][y] == HitOrMiss.SUNK) { // hit
                    g.drawLine(x*Captain.DEFAULT_GRID_SIZE + BattleShipGame.ENEMY_BOARD_START_POSITION_X, y*Captain.DEFAULT_GRID_SIZE + BattleShipGame.BOTH_BOARD_START_POSITION_Y, x*Captain.DEFAULT_GRID_SIZE + BattleShipGame.ENEMY_BOARD_START_POSITION_X + Captain.DEFAULT_GRID_SIZE, y*Captain.DEFAULT_GRID_SIZE + BattleShipGame.BOTH_BOARD_START_POSITION_Y + Captain.DEFAULT_GRID_SIZE);
                    g.drawLine(x*Captain.DEFAULT_GRID_SIZE + BattleShipGame.ENEMY_BOARD_START_POSITION_X + Captain.DEFAULT_GRID_SIZE, y*Captain.DEFAULT_GRID_SIZE + BattleShipGame.BOTH_BOARD_START_POSITION_Y, x*Captain.DEFAULT_GRID_SIZE + BattleShipGame.ENEMY_BOARD_START_POSITION_X, y*Captain.DEFAULT_GRID_SIZE + BattleShipGame.BOTH_BOARD_START_POSITION_Y + Captain.DEFAULT_GRID_SIZE);
                }
                if(player.getEnemyBoard()[x][y] == HitOrMiss.SUNK) { //sunk
                    g.drawRect(x*Captain.DEFAULT_GRID_SIZE + BattleShipGame.ENEMY_BOARD_START_POSITION_X, y*Captain.DEFAULT_GRID_SIZE + BattleShipGame.BOTH_BOARD_START_POSITION_Y, Captain.DEFAULT_GRID_SIZE, Captain.DEFAULT_GRID_SIZE);
                }
            }
        }
    }
}

