package client;

import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Properties;
import java.util.Scanner;

public class Client {
    private BigInteger privateKey;
    private Properties usersProfile;

    public Client(String privateKeyFile, String usersProfileFile) throws IOException {
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

    private void handshake(Socket socket, String username, String company, String proposedKey) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            out.println(encryptMessage(username, usersProfile.getProperty(company)));
            out.println(encryptMessage(company, usersProfile.getProperty(username)));
            out.println(encryptMessage(proposedKey, usersProfile.getProperty(username)));

            System.out.println("Server response: " + in.readLine());
        }
    }

    private String encryptMessage(String message, String publicKeyString) {
        BigInteger publicKey = new BigInteger(publicKeyString);
        return new BigInteger(message.getBytes()).modPow(publicKey, publicKey).toString();
    }

    public void start(String host, int port, String username, String company, String proposedKey) throws IOException {
        try (Socket socket = new Socket(host, port)) {
            System.out.println("Connected to server: " + host);

            handshake(socket, username, company, proposedKey);

            try (BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                String userInput;
                while ((userInput = stdIn.readLine()) != null) {
                    out.println(userInput);
                    System.out.println("Server response: " + in.readLine());
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 6) {
            System.out.println("Usage: java client.Client private_key_file users_profile_file host port username company");
            return;
        }

        String privateKeyFile = args[0];
        String usersProfileFile = args[1];
        String host = args[2];
        int port = Integer.parseInt(args[3]);
        String username = args[4];
        String company = args[5];
        String proposedKey = "SomeRandomKey"; // Example: proposed one-time key

        Client client = new Client(privateKeyFile, usersProfileFile);
        client.start(host, port, username, company, proposedKey);
    }
}
