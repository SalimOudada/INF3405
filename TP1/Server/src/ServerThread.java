import java.io.*;
import java.net.Socket;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerThread extends Thread {
    private static final int BUFFER_SIZE = 4096;
    private Socket socket;
    private int clientNumber;

    public ServerThread(Socket socket, int clientNumber) {
        this.socket = socket;
        this.clientNumber = clientNumber;
        System.out.println("Nouvelle connexion client #" + clientNumber + " de " + socket);
    }

    public void run() {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

            // Authentification de l'utilisateur
            String username = "";
            while (true) {
                username = bufferedReader.readLine();
                String password = bufferedReader.readLine();

                Boolean isAccepted = Verification.verifyCredentials(username, password);

                if (isAccepted) {
                    bufferedWriter.write("true\n");
                } else {
                    bufferedWriter.write("false\n");
                }
                bufferedWriter.flush();

                String clientResponse = bufferedReader.readLine();
                if (clientResponse == null || clientResponse.equals("verification done")) {
                    break;
                }
            }

            // Réception du nom de l'image traitée
            String nomImageTraitee = bufferedReader.readLine();
            
            // Réception de l'image du client
            InputStream is = socket.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            baos.close();

            // Sauvegarde de l'image reçue sur le disque
            byte[] imageData = baos.toByteArray();
            try (FileOutputStream fos = new FileOutputStream(nomImageTraitee)) {
                fos.write(imageData);
            }
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd@HH:mm:ss");
            String dateTime = dateFormat.format(new Date());
            System.out.println("[" + socket.getInetAddress().getHostAddress() + ":" + socket.getPort() 
                                  + " - " + dateTime + "] : Image " + nomImageTraitee + " reçue pour traitement.");

            // Affichage des informations de réception de l'image en console
            String clientAddress = socket.getInetAddress().getHostAddress();
            int clientPort = socket.getPort();
            String timestamp = new SimpleDateFormat("yyyy-MM-dd@HH:mm:ss").format(new Date());
            System.out.println("[" + username + " - " + clientAddress + ":" + clientPort + " - " + timestamp + "] "
            		+ "           : Image " + receivedImageName + " reçue pour traitement.");

       
            // Traitement de l'image
            BufferedImage image = ImageIO.read(new File(receivedImageName));
            File outputFile = new File(nomImageTraitee);
            ImageIO.write(Sobel.process(image), "jpg", outputFile);

            // Envoi de l'image traitée au client
            try (FileInputStream fis = new FileInputStream(outputFile);
                 OutputStream os = socket.getOutputStream()) {
                byte[] imageOutputData = new byte[(int) outputFile.length()];
                fis.read(imageOutputData);
                os.write(imageOutputData);
                os.flush();
                socket.shutdownOutput();
            }

            System.out.println("Image traitée envoyée au client #" + clientNumber);

        } catch (IOException e) {
            System.err.println("Erreur lors de la communication avec le client #" + clientNumber + ": " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Impossible de fermer la connexion client #" + clientNumber);
            }
            System.out.println("Connexion client #" + clientNumber + " fermée");
        }
    }
}
