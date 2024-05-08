import java.net.Socket;

public class ServerThread extends Thread {
    private Socket socket;
    private int clientNumber;

    public ServerThread(Socket socket, int clientNumber) {
        this.socket = socket;
        this.clientNumber = clientNumber;
        System.out.println("Nouvelle connection avec le client#" + clientNumber + " at " + socket);
    }
}
