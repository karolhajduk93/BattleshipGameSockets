package com.karolhajduk.battleship;

import java.util.ArrayList;
import java.util.List;

    public class Ship {
    private int size;
    List<Coordinates> coordinates = new ArrayList<>();

        public Ship(int size) {
            this.size = size;
        }

        public void setSize(int size) {
            this.size = size;
        }
        public int getSize() {
            return size;
        }
    }
