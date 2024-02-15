package server;

import java.io.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

public class Server {
    private BigInteger privateKey;
    private Properties usersProfile;

    public Server(String privateKeyFile, String usersProfileFile) throws IOException {
        loadPrivateKey(privateKeyFile);
        loadUsersProfile(usersProfileFile);
    }

    private void loadPrivateKey(String privateKeyFile) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(privateKeyFile))) {
            String key = br.readLine();
            privateKey = new BigInteger(key);
        }
    }

    private void loadUsersProfile(String usersProfileFile) throws IOException {
        usersProfile = new Properties();
        try (InputStream is = new FileInputStream(usersProfileFile)) {
            usersProfile.load(is);
        }
    }

    private void handleClient(Socket clientSocket) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String username = in.readLine();
            String company = in.readLine();
            String proposedKey = in.readLine();

            BigInteger userPublicKey = new BigInteger(usersProfile.getProperty(username));
            BigInteger companyPublicKey = new BigInteger(usersProfile.getProperty(company));

            // Decrypt user's identity and proposed key
            String decryptedUsername = new String(new BigInteger(username).modPow(privateKey, userPublicKey).toByteArray());
            String decryptedProposedKey = new String(new BigInteger(proposedKey).modPow(privateKey, companyPublicKey).toByteArray());

            System.out.println("Client: " + decryptedUsername + ", Proposed Key: " + decryptedProposedKey);

            // Send acknowledgment to client
            out.println("Welcome, " + decryptedUsername);

            // Data transfer
            String clientData;
            while ((clientData = in.readLine()) != null) {
                System.out.println("Received from client: " + clientData);
                // Modify data: uppercase to lowercase, lowercase to uppercase
                String modifiedData = clientData.chars()
                        .map(c -> Character.isUpperCase(c) ? Character.toLowerCase(c) : Character.toUpperCase(c))
                        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                        .toString();
                // Send modified data back to client
                out.println(modifiedData);
            }
        }
    }

    public void start(int port) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started. Listening on port " + port + "...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());
                handleClient(clientSocket);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.out.println("Usage: java server.Server private_key_file users_profile_file port");
            return;
        }

        String privateKeyFile = args[0];
        String usersProfileFile = args[1];
        int port = Integer.parseInt(args[2]);

        Server server = new Server(privateKeyFile, usersProfileFile);
        server.start(port);
    }
}
