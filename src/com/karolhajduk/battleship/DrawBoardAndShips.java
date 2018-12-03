package com.karolhajduk.battleship;

import javax.swing.*;
import java.awt.*;

public class DrawBoardAndShips extends JComponent {

    private Captain player;
    DrawBoardAndShips(Captain player) {
        this.player = player;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2D = (Graphics2D) g;

        if (true)
            paintStart(g2D);
        else
            g2D.drawRect(100,100, 100, 100);
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

        player.getShips().forEach(ship -> g.drawRect(ship.getCoordinates().getX(), ship.getCoordinates().getY(),
                ship.getSize()*32, 32));


        /*g.drawRect(600, 100, 4 * 32, 32); //4

        g.drawRect(600, 150, 3 * 32, 32); //3
        g.drawRect(600 + 3 * 32 + 10, 150, 3 * 32, 32); //3

        g.drawRect(600, 200, 2 * 32, 32); //2
        g.drawRect(600 + 2 * 32 + 10, 200, 2 * 32, 32); //2
        g.drawRect(600 + (2 * 32 + 10) * 2, 200, 2 * 32, 32); //2

        g.drawRect(600, 250, 32, 32); //1
        g.drawRect(600 + (32 + 10), 250, 32, 32); //1
        g.drawRect(600 + 2 * (32 + 10), 250, 32, 32); //1
        g.drawRect(600 + 3 * (32 + 10), 250, 32, 32); //1*/
    }
}
