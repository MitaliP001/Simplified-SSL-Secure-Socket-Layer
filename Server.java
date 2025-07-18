import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) {
        // Retrieve system properties
        String privateKeyFile = System.getProperty("server.private_key");
        String usersFile = System.getProperty("server.users");
        String portStr = System.getProperty("server.port");

        System.out.println("Private Key File: " + privateKeyFile);
        System.out.println("Users File: " + usersFile);
        System.out.println("Port: " + portStr);

        int port = 0;
        try {
            port = Integer.parseInt(portStr);
        } catch (NumberFormatException e) {
            System.err.println("Invalid port number: " + portStr);
            System.exit(1);
        }

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started. Listening on port " + port);

            // Listen for incoming connections indefinitely
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connection established with client " + clientSocket.getInetAddress());

                // Handle each client in a new thread
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        ) {
            out.println("Welcome to the server! Type something and press enter. Type 'bye' to disconnect.");

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Received from client: " + inputLine);
                if ("bye".equalsIgnoreCase(inputLine.trim())) {
                    out.println("Goodbye!");
                    break;
                }
                // Echo back the received message
                out.println("Server echoes: " + inputLine);
            }

            System.out.println("Client " + clientSocket.getInetAddress() + " disconnected.");

        } catch (IOException e) {
            System.err.println("Client handling error: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing client socket: " + e.getMessage());
            }
        }
    }
}
