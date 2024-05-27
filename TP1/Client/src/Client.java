import java.net.Socket;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Entrez l'adresse IP du serveur : ");
        String ipAddress = scanner.nextLine();
        
        // Validation de l'adresse IP
        try {
            Verification.verifyIP(ipAddress);
        } catch (IllegalArgumentException e) {
            System.out.println("Adresse IP invalide  ");
            return;
        }

        System.out.println("Entrez le port du serveur : ");
        int port = scanner.nextInt();
        scanner.nextLine(); // Consume newline left-over

        // Validation du port
        try {
            Verification.verifyPort(port);
        } catch (IllegalArgumentException e) {
            System.out.println("Port invalide : " + e.getMessage());
            return;
        }
        
      
        try (Socket socket = new Socket(ipAddress, port);
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
             System.out.println("Connecté au serveur " + ipAddress + " sur le port " + port);
            
            while(true) {
            	System.out.println("Nom d'utilisateur: ");
	            String username = scanner.nextLine();
	            
	            System.out.println("Mot de passe: ");
	            String password = scanner.nextLine();
	            
	            bufferedWriter.write(username+"\n"+password+"\n");
	            bufferedWriter.flush();
	            
	            String verifyCredentials = bufferedReader.readLine();
	        
	            if(verifyCredentials.equals("true")){
	            	System.out.println("Connexion acceptée !");
	            	bufferedWriter.write("verification done\n");
	            	bufferedWriter.flush();
	            	break;
	            }
	            else if (verifyCredentials.equals("false")) {
	            	System.out.println("Erreur dans la saisie du mot de passe.");
	            	bufferedWriter.write("verification not done\n");
	            	bufferedWriter.flush();
	            }
	        }
            
            // Code pour envoyer l'image ici

        } catch (IOException e) {
            System.out.println("Erreur lors de la communication avec le serveur : " + e.getMessage());
        }
		finally {
			scanner.close();
		}

        
        
    }
}
