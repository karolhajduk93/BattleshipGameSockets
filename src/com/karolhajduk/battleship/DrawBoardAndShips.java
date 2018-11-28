package com.karolhajduk.battleship;

import javax.swing.*;
import java.awt.*;

public class DrawBoardAndShips extends JComponent {

    int i = 0;
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2D = (Graphics2D) g;
        i++;
        g2D.drawString(Integer.toString(i), 100, 100);
    }
}
