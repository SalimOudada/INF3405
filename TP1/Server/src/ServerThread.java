import java.io.IOException;
import java.net.Socket;


public class ServerThread extends Thread {
    private Socket socket;
    private int clientNumber;

    public ServerThread(Socket socket, int clientNumber) {
        this.socket = socket;
        this.clientNumber = clientNumber;
        System.out.println("Nouvelle connexion client #" + clientNumber + " de " + socket);
    }

    public void run() {
        try {
        	// mettre la partie du traitement de l'image ici

		} finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Impossible de fermer la connexion client #" + clientNumber);
            }
            System.out.println("Connexion client #" + clientNumber + " fermée");
        }
    }
}
