package com.karolhajduk.battleship;

import java.io.*;
import java.net.Socket;

public class Client {

    Socket socket;
    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;
    BufferedReader bufferedReader;

    String start = "", message1 = "";

    public Client(String host, Captain player) throws IOException {

        System.out.println("CLIENT");
        socket = new Socket(host, 6666);
        player.setReady(2);

        BattleShipGame.connected = socket.isConnected();

        dataInputStream = new DataInputStream(socket.getInputStream());
        dataOutputStream = new DataOutputStream(socket.getOutputStream());
        bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        //send my logic table at the beginning of the game
        for (boolean[] row: player.getMyBoard()){
            for(boolean value: row){
                if(value)
                    start += "1";
                else
                    start += "0";
            }
        }
        dataOutputStream.writeUTF(start);
        System.out.println(start);
        System.out.println("CLIENT: BEFORE LOOP");
        while(!message1.equals("WIN") || !message1.equals("LOOSE")){
            /*BattleShipGame.coordinatesInput = dataInputStream.readUTF();
            System.out.println(BattleShipGame.coordinatesInput);
            if(BattleShipGame.moveDone) {

                dataOutputStream.writeUTF(BattleShipGame.coordinatesOutput);
                BattleShipGame.coordinatesOutput = "";
            }*/
            dataOutputStream.flush();
        }

        dataInputStream.close();
        socket.close();
    }
}