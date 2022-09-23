package Violet;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

/**
 * Client class is the class run for client side operation. Takes in a port and
 * a username for the server to receive
 * A socket is created to connect with the server at the given port.
 */
public class Client {
    private int port;
    private String username;

    /**
     * main constructor
     * 
     * @param port (int) port number
     * @param name (str) username
     */
    public Client(int port, String name) {
        this.port = port;
        this.username = name;
        startClient();
    }

    /**
     * Getter for the username
     * 
     * @return (str) username
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * StartClient establishes the connection and sever threads.
     * 
     * @return void
     */
    private void startClient() {

        try {
            // Establish a connection with the server
            Socket client = new Socket("localhost", port);

            // Start a server thread
            ServerThread serverthread = new ServerThread(client, username);
            Thread server = new Thread(serverthread);
            server.start();

            // For messages sent via command line
            // Useless, as we don't want to read from the command line
            Scanner scan = new Scanner(System.in);

            // While the server is still running
            while (server.isAlive()) {
                // If the scanner has something from the command line
                if (scan.hasNextLine()) {
                    // We read the message and send it to the thread.
                    serverthread.newMessage(scan.nextLine());
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        int port;
        String name = null;

        // Read from the command line
        Scanner in = new Scanner(System.in);

        // Get the username of the player
        System.out.print("Enter a username: ");
        name = in.nextLine();

        // Make a new client
        Client client = new Client(8010, name);

    }
}
