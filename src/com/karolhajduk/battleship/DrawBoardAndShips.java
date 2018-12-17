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


    private void paintWait(Graphics2D g2D) {
        wait++;
        g2D.drawString("Waiting for player",480, 230);
        if( wait / 20 == 0) {
            g2D.fillOval(500, 250, 8, 8);
        }
        else if( wait / 20 == 1) {
            g2D.fillOval(500, 250, 8, 8);
            g2D.fillOval(520, 250, 8, 8);
        }
        else if( wait / 20 == 2){
            g2D.fillOval(500, 250, 8, 8);
            g2D.fillOval(520, 250, 8, 8);
            g2D.fillOval(540, 250, 8, 8);
        }
        else if( wait / 20 == 3){
            wait = 0;
        }
    }

    private void paintBoards(Graphics2D g2D) {
        paintStart(g2D);

        g2D.setColor(UIManager.getColor("Panel.background"));
        g2D.fillRect(550, 40, 300, 30);
    }
}

