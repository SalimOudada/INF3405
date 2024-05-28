import java.net.Socket;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.Scanner;
import java.io.File;
import java.io.FileInputStream;

public class Client {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String ipAddress = "";
        int port = 0;

        while (true) {
            System.out.println("Entrez l'adresse IP du serveur : ");
            ipAddress = scanner.nextLine();

            try {
                Verification.verifyIP(ipAddress);
                break; 
            } catch (IllegalArgumentException e) {
                System.out.println("Adresse IP invalide. Veuillez réessayer.");
            }
        }

    
        while (true) {
            System.out.println("Entrez le port du serveur (entre 5000 et 5050) : ");
            port = scanner.nextInt();
            scanner.nextLine(); 

            try {
                Verification.verifyPort(port);
                break; 
            } catch (IllegalArgumentException e) {
                System.out.println("Port invalide : " + e.getMessage() + ". Veuillez réessayer.");
            }
        }

        try (Socket socket = new Socket(ipAddress, port);
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
            System.out.println("Connecté au serveur " + ipAddress + " sur le port " + port);

            while (true) {
                System.out.println("Nom d'utilisateur: ");
                String username = scanner.nextLine();

                System.out.println("Mot de passe: ");
                String password = scanner.nextLine();

                bufferedWriter.write(username + "\n" + password + "\n");
                bufferedWriter.flush();

                String verifyCredentials = bufferedReader.readLine();

                if (verifyCredentials.equals("true")) {
                    System.out.println("Connexion acceptée !");
                    bufferedWriter.write("verification done\n");
                    bufferedWriter.flush();
                    break;
                } else if (verifyCredentials.equals("false")) {
                    System.out.println("Erreur dans la saisie du mot de passe.");
                    bufferedWriter.write("verification not done\n");
                    bufferedWriter.flush();
                }
            }
            // demande au client d'entrer le nom de l'image qui se retriuve dans le repertoire
            System.out.println("Entrez le nom de l'image : ");
            String imageName = scanner.nextLine();


            
            // Code pour envoyer l'image ici

            // mettre le code d'envoi de l'image avec ce print
            System.out.println("Image envoyée pour traitement.");
            
            
            // mettre le code pour recevoir l'image traitée ici et mettre le path de l'image traitée dans processedImageName
            // je commente le code pour le moment, decommenter le print apres avoir mis le code
            //System.out.println("Image traitée reçue et sauvegardée dans : " + processedImageName);


        } catch (IOException e) {
            System.out.println("Erreur lors de la communication avec le serveur : " + e.getMessage());
        }
		finally {
			scanner.close();
		}

        
        
    }
}
