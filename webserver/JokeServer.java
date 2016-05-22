package joke_server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.Vector;

/**
 * Created by mathiasluo on 16-5-3.
 */
public class JokeServer {

    public static final int PORT = 12345;

    private ServerSocket server;
    private Vector<Joke> jokes = new Vector();

    // Prepare to accept clients
    public JokeServer() {
        try {
            server = new ServerSocket(PORT);
        }

        // Couldn't start the server
        catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    // Accept client connections
    public void start() {

        while (true) {
            try {
                Socket socket = server.accept();
                new Thread(new Client(socket)).start();
                System.out.println("Accepted a client.");
            }

            // Survive problems with new clients
            catch (IOException e) {
                System.out.print("Error accepting a client. ");
                System.out.println(e.getMessage());
            }
        }
    }

    // Represents a joke
    private class Joke {

        private String setup;
        private String punchline;

        public Joke(String setup, String punchline) {
            this.setup = setup;
            this.punchline = punchline;
        }
    }

    // Represents a client connection
    private class Client implements Runnable {

        // Request codes
        public static final int SEND_SETUPS = 1;
        public static final int SEND_JOKE = 2;
        public static final int RECEIVE_JOKE = 3;

        private Socket socket;
        private Scanner in;
        private PrintWriter out;

        public Client(Socket socket) {
            this.socket = socket;
        }

        // Respond to this client's request
        public void run() {
            try {
                in = new Scanner(socket.getInputStream());
                out = new PrintWriter(socket.getOutputStream(), true);

                // Receive a request code
                int request = Integer.parseInt(in.nextLine());
                System.out.println("   Received request code: " + request);

                // Handle the request
                if (request == SEND_SETUPS)
                    sendSetups();
                else if (request == SEND_JOKE)
                    sendJoke();
                else if (request == RECEIVE_JOKE)
                    receiveJoke();
                else
                    throw new RuntimeException("invalid request code " + request);
            }

            // Survive problems with this client
            catch (Exception e) {
                System.out.print("   Error handling request. ");
                System.out.println(e.getClass() + ": " + e.getMessage());
            }

            // Make sure we release resources
            finally {
                try {
                    out.close();
                } catch (Exception e) {
                }
                try {
                    in.close();
                } catch (Exception e) {
                }
            }
        }

        // Send a list of setups to the client
        private void sendSetups() {

            int size = jokes.size();

            // Send how many to expect
            out.println(size);
            System.out.println("   Sent list size: " + size);

            // Send the setups (oldest first)
            for (int i = 0; i < size; i++) {
                out.println(jokes.get(i).setup);
                System.out.println("   Sent setup: " + jokes.get(i).setup);
            }
        }

        // Send a requested joke to the client
        private void sendJoke() {

            // Receive the joke index
            int index = Integer.parseInt(in.nextLine());
            System.out.println("   Received joke index: " + index);

            // Send the setup
            out.println(jokes.get(index).setup);
            System.out.println("   Sent setup: " + jokes.get(index).setup);

            // Send the punchline
            out.println(jokes.get(index).punchline);
            System.out.println("   Sent punchline: " + jokes.get(index).punchline);
        }

        // Receive a new joke from the client
        private void receiveJoke() {

            // Receive the setup
            String setup = in.nextLine();
            System.out.println("   Received setup: " + setup);

            // Receive the punchline
            String punchline = in.nextLine();
            System.out.println("   Received punchline: " + punchline);

            // Add the new joke to the list
            Joke joke = new Joke(setup, punchline);
            jokes.add(joke);

            // Send a blank line
            out.println();
            System.out.println("   Sent blank line");
        }
    }

    // Start the server
    public static void main(String[] args) {
        new JokeServer().start();
    }
}