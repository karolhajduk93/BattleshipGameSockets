package com.karolhajduk.battleship;

import java.io.*;
import java.net.Socket;

public class Client {

    Socket socket;
    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;
    BufferedReader bufferedReader;

    String sendMessage = "", message1 = "", receivedMessage = "";

    public Client(String host, Captain player)  {

        System.out.println("CLIENT");
        try {
            socket = new Socket(host, 6666);
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.setReady(2);

        BattleShipGame.connected = socket.isConnected();

        try {
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        //send logic table at the beginning of the game
        for(int i1 = 0; i1 < player.getMyBoard().length; i1++){
            for (int j = 0; j < player.getMyBoard()[i1].length; j++){
                if(player.getMyBoard()[j][i1] == 1)
                    sendMessage += "1";
                else
                    sendMessage += "0";
            }
        }
        try {
            dataOutputStream.writeUTF(sendMessage);
            receivedMessage = dataInputStream.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }
        sendMessage = "";
        /*System.out.println("Client get from Server: " + dataInputStream.readUTF());
        System.out.println("Client sends: " + start);*/

        //receive String and convert it to enemy logic table

        player.setEnemyBoard(receivedMessage);
        receivedMessage = "";
        player.setMyTurn(false);

        while(!message1.equals("WIN") || !message1.equals("LOOSE")){

            if(!player.isMyTurn()) {
                try {
                    receivedMessage = dataInputStream.readUTF();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                if(!receivedMessage.isEmpty()) {
                    BattleShipGame.coordinatesInput = receivedMessage;
                    receivedMessage = "";
                }
            } else {
                sendMessage = BattleShipGame.coordinatesOutput;


                if(!sendMessage.isEmpty()) {
                    try {
                        dataOutputStream.writeUTF(sendMessage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    sendMessage = "";
                }
            }


            try {
                dataOutputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            dataInputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}