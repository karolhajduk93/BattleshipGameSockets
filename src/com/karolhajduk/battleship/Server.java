package com.karolhajduk.battleship;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

class Server extends Thread {
    ServerSocket serverSocket;
    Socket socket;
    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;

    String sendMessage = "", message1 = "", receivedMessage = "";

    public Server(Captain player)  {

        System.out.println("SERVER");

        int asd = 0;

        player.setReady(1);
        try {
            serverSocket = new ServerSocket(6666);
            socket = serverSocket.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }

        player.setReady(2);
        try {
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }

        //send logic table at the beginning of the game
        for(int i1 = 0; i1 < player.getMyBoard().length; i1++){
            for (int j = 0; j < player.getMyBoard()[i1].length; j++){
                if(player.getMyBoard()[j][i1] == 1) /////////////////////
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
        /*System.out.println("Sever get from Client: " + dataInputStream.readUTF());
        System.out.println("Server send: " + start);*/

        //receive String and convert it to enemy logic table

        player.setEnemyBoard(receivedMessage);
        receivedMessage = "";
        player.setMyTurn(true);

        while(!message1.equals("WIN") || !message1.equals("LOOSE")){

            try {
                dataOutputStream.writeUTF(BattleShipGame.coordinatesOutput);
            } catch (IOException e) {
                e.printStackTrace();
            }
            BattleShipGame.coordinatesOutput = "";



            if(!player.isMyTurn()) {
                try {
                    receivedMessage = dataInputStream.readUTF();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                if(!receivedMessage.isEmpty()) {
                    BattleShipGame.coordinatesInput = receivedMessage;
                    receivedMessage = "";
                    //player.setMyTurn(true);
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
                    //player.setMyTurn(false);
                }
            }




            //dataOutputStream.flush();
        }

        try {
            dataInputStream.close();
            socket.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
