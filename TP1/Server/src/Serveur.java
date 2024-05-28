import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.Scanner;

public class Serveur {
    private static ServerSocket Listener;

    public static void main(String[] args) throws Exception {
        System.out.println("Serveur");
        int clientNumber = 0;

        Scanner userInput = new Scanner(System.in);
        String ipAddress = "";
        int port = 0;

        while (true) {
            System.out.println("Entrez l'adresse IP du serveur : ");
            ipAddress = userInput.nextLine();

            try {
                Verification.verifyIP(ipAddress);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("Adresse IP invalide. Veuillez réessayer.");
            }
        }

        while (true) {
            System.out.println("Entrez le port d'écoute (entre 5000 et 5050) : ");
            port = userInput.nextInt();
            userInput.nextLine();

            try {
                Verification.verifyPort(port);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("Port invalide : " + e.getMessage() + ". Veuillez réessayer.");
            }
        }

        Listener = new ServerSocket();
        Listener.setReuseAddress(true);
        InetAddress serverIP = InetAddress.getByName(ipAddress);
        Listener.bind(new InetSocketAddress(serverIP, port));
        System.out.println("Le serveur est à l'écoute sur le port " + port + " et l'adresse IP " + serverIP);
        userInput.close();

        try {
            while (true) {
                new ServerThread(Listener.accept(), clientNumber++).start();
            }
        } finally {
            if (Listener != null && !Listener.isClosed()) {
                Listener.close();
            }
        }
    }
}
