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

    public Server(Captain player) throws IOException {

        System.out.println("SERVER");

        int asd = 0;

        player.setReady(1);
        serverSocket = new ServerSocket(6666);
        socket = serverSocket.accept();


        player.setReady(2);

        dataInputStream = new DataInputStream(socket.getInputStream());
        dataOutputStream = new DataOutputStream(socket.getOutputStream());


        //send logic table at the beginning of the game
        for (int i1 = 0; i1 < player.getMyBoard().length; i1++) {
            for (int j = 0; j < player.getMyBoard()[i1].length; j++) {
                if (player.getMyBoard()[j][i1] == 1) /////////////////////
                    sendMessage += "1";
                else
                    sendMessage += "0";
            }
        }

        dataOutputStream.writeUTF(sendMessage);
        dataOutputStream.flush();
        receivedMessage = dataInputStream.readUTF();

        sendMessage = "";
        /*System.out.println("Sever get from Client: " + dataInputStream.readUTF());
        System.out.println("Server send: " + start);*/

        //receive String and convert it to enemy logic table

        player.setEnemyBoard(receivedMessage);
        receivedMessage = "";
        player.setMyTurn(true);

        while (!message1.equals("WIN") || !message1.equals("LOOSE")) {



            /*dataOutputStream.writeUTF(BattleShipGame.coordinatesOutput);

            BattleShipGame.coordinatesOutput = "";*/


            if (!player.isMyTurn()) {

                receivedMessage = dataInputStream.readUTF();


                if (!receivedMessage.isEmpty()) {
                    BattleShipGame.coordinatesInput = receivedMessage;
                    receivedMessage = "";
                    player.setMyTurn(true);
                }
            } else {
                sendMessage = BattleShipGame.coordinatesOutput;


                if (!sendMessage.isEmpty()) {

                    dataOutputStream.writeUTF(sendMessage);

                    sendMessage = "";
                    BattleShipGame.coordinatesOutput = "";
                    player.setMyTurn(false);
                }
            }


            //dataOutputStream.flush();
        }

        dataInputStream.close();
        socket.close();
        serverSocket.close();

    }
}
