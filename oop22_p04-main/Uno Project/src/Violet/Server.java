package Violet;

import javax.swing.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;
import java.awt.Color;

/**
 * Server class creates a locally hosted server at the given port.
 * Once started, will continously try to accept clients.
 */
public class Server {
    private int port;
    private ArrayList<ClientThread> clients;
    private Table game;
    private ServerGUI GUI;

    /**
     * main constructor. Initializes the global variables and starts the UNO Game.
     * 
     * @param port
     */
    Server(int port) {
        this.port = port;
        // Make a new list of all the connected clients
        this.clients = new ArrayList<ClientThread>();
        GUI = new ServerGUI();
        GUI.setVisible(true);
        GUI.setTitle("User List");
        GUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Make a new table
        game = new Table(this);
        // Should be a thread so that it runs concurrently to the server
        Thread theGame = new Thread(game);
        theGame.start();

        ServerSocket server_socket = null;

        try {
            server_socket = new ServerSocket(port);
            acceptClients(server_socket);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a user to the list of connected users
     * 
     * @param name
     */
    public void addUser(String name) {
        GUI.addUser(name);
    }

    /**
     * Removes a user from the list of connected users
     * 
     * @Bug does not update when a player leaves mid game?
     * @param name
     */
    public void removeUser(String name) {
        GUI.removeUser(name);
    }

    /**
     * acceptClients is a dead loop that continously tries to accept clients. Once
     * the connection is forged
     * it creates a client_thread for them.
     * 
     * @param server_socket
     */
    private void acceptClients(ServerSocket server_socket) {
        // int i = 0;
        while (true) {
            Socket client = null;
            try {
                // If a client connects we accept them
                client = server_socket.accept();
                System.out.println("Connection from: " + client.getRemoteSocketAddress());
                // Then we make a new thread and start the thread for the client
                ClientThread client_thread = new ClientThread(this, client, game);
                Thread new_client = new Thread(client_thread);

                new_client.start();

                // We add them to the list of currently connected users
                clients.add(client_thread);

            } catch (IOException e) {
                // Just ignore it
            }

        }
    }

    /**
     * Returns the list of connected users
     * 
     * @return the list of all the users
     */
    public ArrayList<ClientThread> getClients() {
        return clients;
    }

    public static void main(String[] args) {
        Server server = new Server(8010);
    }
}
