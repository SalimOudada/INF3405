import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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
        	BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
            
            while(true) {
	        	 String username = bufferedReader.readLine();
	             String password = bufferedReader.readLine();
	             
	             Boolean isAccepted = Verification.verifyCredentials(username, password);
	             
	             if(isAccepted) {
	             	bufferedWriter.write("true\n");
	             }
	             else {
	             	bufferedWriter.write("false\n");
	             }
	             bufferedWriter.flush();
	             
	             String clientResponse = bufferedReader.readLine();
	             if (clientResponse == null || clientResponse.equals("verification done")) {
	                 break;
	             }
            }
           
           
         // mettre la partie du traitement de l'image ici
        	
       
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
