import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.Scanner;

public class Serveur {
    private static ServerSocket Listener;

    public static void main(String[] args) throws Exception {
        System.out.println("serveur");
        int clientNumber = 0;

        // Demande l'adresse et le port à l'utilisateur + les vérifie
        Scanner userInput = new Scanner(System.in);
        System.out.println("Entrez l'adresse IP du serveur : ");
        String ipAdress = userInput.nextLine();
        try {
        	Verification.verifyIP(ipAdress);
        } catch (IllegalArgumentException e) {
            System.out.println("Adresse IP invalide  ");
            return;
        }

        System.out.println("Entrez le port d'écoute : ");
        int port = userInput.nextInt();
        try {
        	Verification.verifyPort(port);
        } catch (IllegalArgumentException e) {
            System.out.println("Port invalide : " + e.getMessage());
            return;
        }

        Listener = new ServerSocket();
        // Permet de réutiliser le port
        Listener.setReuseAddress(true);
        // Permet d'obtenir l'adresse IP du serveur
        InetAddress serverIP = InetAddress.getByName(ipAdress);

        // On bind le serveur
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