import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.Scanner;

public class Serveur {
    Verification verify = new Verification();

    private static ServerSocket Listener;

    public static void main(String[] args) throws Exception {

        System.out.println("serveur");
        int clientNumber = 0;

        // on demande ladress et le port a l<utilisateur + on les verifies
        Scanner userInput = new Scanner(System.in);
        System.out.println("Entrez l'adresse IP du serveur : ");
        String ipAdress = userInput.nextLine();
        Verification.verifyIP(ipAdress);

        System.out.println("Entrez le port d'écoute : ");
        int port = userInput.nextInt();
        Verification.verifyPort(port);

        Listener = new ServerSocket();
        // permet de réutiliser le port
        Listener.setReuseAddress(true);
        // permet d'obtenir l'adresse ip du serveur
        InetAddress serverIP = InetAddress.getByName(ipAdress);

        // on bind le serveur
        Listener.bind(new InetSocketAddress(serverIP, port));
        System.out.println("Le serveur est à l'écoute sur le port " + port + " et l'adresse IP " + serverIP);
        userInput.close();

        try {
            while (true) {
                new ServerThread(Listener.accept(), clientNumber++).start();
            }
        } finally {
            Listener.close();

        }
    }
}