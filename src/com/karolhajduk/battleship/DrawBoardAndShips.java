package com.karolhajduk.battleship;

import javax.swing.*;
import java.awt.*;

public class DrawBoardAndShips extends JComponent {

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


        if (player.getReady() == 0) {
            paintStart(g2D);
        }
        else if(player.getReady() == 1) {
            paintWait(g2D);
        }
        else if(player.getReady() == 2){
            paintBoards(g2D);
            paintMoves(g2D);
        }

    }

    public void paintStart(Graphics2D g){
        //320x320 [10x10] |  (150,50)-(x = 470, y = 370)
        g.drawString("A        B        C        D        E        F        G        H        I        J", 165, 45);
        for (int x = 150; x < 470; x = x + 32) {
            if (((x - 150) / 32) + 1 != 10)
                g.drawString(Integer.toString(((x - 150) / 32) + 1), 135, 75 + (x - 150));
            else
                g.drawString("10", 130, 75 + (x - 150));
            for (int y = 50; y < 370; y = y + 32) {
                g.drawRect(x, y, 32, 32);
            }
        }

        g.drawString("Drag the ships to the grid then click to  rotate:", 600, 60);

        //Draw coordinates
        g.setStroke(new BasicStroke(2));

        g.setColor(new Color(0, 0, 1, (float) 0.7));
        player.getShips().stream().filter(ship -> ship.isHorizontal()).forEach(ship -> g.drawRect(ship.getCoordinates().getX(), ship.getCoordinates().getY(),
                ship.getSize()*32, 32));
        player.getShips().stream().filter(ship -> !ship.isHorizontal()).forEach(ship -> g.drawRect(ship.getCoordinates().getX(), ship.getCoordinates().getY(),
                32, ship.getSize()*32));

        g.setColor(new Color(0, 0, 1, (float) 0.1));
        player.getShips().stream().filter(ship -> ship.isHorizontal()).forEach(ship -> g.fillRect(ship.getCoordinates().getX(), ship.getCoordinates().getY(),
                ship.getSize()*32, 32));
        player.getShips().stream().filter(ship -> !ship.isHorizontal()).forEach(ship -> g.fillRect(ship.getCoordinates().getX(), ship.getCoordinates().getY(),
                32, ship.getSize()*32));
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

        //draw enemy table
        g.drawString("A        B        C        D        E        F        G        H        I        J", 615, 45);
        for (int x = 600; x < 920; x = x + 32) {
            if (((x - 600) / 32) + 1 != 10)
                g.drawString(Integer.toString(((x - 600) / 32) + 1), 585, 75 + (x - 600));
            else
                g.drawString("10", 580, 75 + (x - 600));
            for (int y = 50; y < 370; y = y + 32) {
                g.drawRect(x, y, 32, 32);
            }
        }
    }

    private void paintMoves(Graphics2D g) {

        //320x320 [10x10]
        //(150, 50)-(470, 370) - MY BOARD
        //(600, 50)-(920, 370) - ENEMY BOARD

        g.setStroke(new BasicStroke(2));
        g.setColor(Color.RED);

        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                //My Board
                if(player.getMyBoard()[x][y] == -1) { // miss
                    g.fillOval(x * 32 + 150 + 14, y * 32 + 50 + 14, 4, 4);
                }
                else if(player.getMyBoard()[x][y] == 2 || player.getMyBoard()[x][y] == 3) { // hit
                    g.drawLine(x*32 + 150, y*32 + 50, x*32 + 150 + 32, y*32 + 50 + 32);
                    g.drawLine(x*32 + 150 + 32, y*32 + 50, x*32 + 150, y*32 + 50 + 32);
                }
                else if(player.getMyBoard()[x][y] == 3) {//sunk
                    g.drawRect(x*32 + 150, y*32 + 50, 32, 32);
                }

                //Enemy Board
                if(player.getEnemyBoard()[x][y] == -1) { // miss
                    System.out.println(player.getEnemyBoard()[x][y] + "  " + x + "-" + y);//////////////
                    g.fillOval(x * 32 + 600 + 14, y * 32 + 50 + 14, 4, 4);
                }
                else if(player.getEnemyBoard()[x][y] == 2 || player.getEnemyBoard()[x][y] == 3) { // hit
                    g.drawLine(x*32 + 600, y*32 + 50, x*32 + 600 + 32, y*32 + 50 + 32);
                    g.drawLine(x*32 + 600 + 32, y*32 + 50, x*32 + 600, y*32 + 50 + 32);
                }
                else if(player.getEnemyBoard()[x][y] == 3) {//sunk
                    g.drawRect(x*32 + 600, y*32 + 50, 32, 32);
                }
            }
        }
    }
}

