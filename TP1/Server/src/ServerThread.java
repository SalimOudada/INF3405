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
        String username = null; // Déplacer la déclaration et l'initialisation de la variable username ici

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

            // Authentification de l'utilisateur
            while (true) {
                username = reader.readLine();
                String password = reader.readLine();

                Boolean isAccepted = Verification.verifyCredentials(username, password);

                if (isAccepted) {
                    writer.write("true\n");
                } else {
                    writer.write("false\n");
                }
                writer.flush();

                String clientResponse = reader.readLine();
                if (clientResponse == null || clientResponse.equals("verification fini")) {
                    break;
                }
            }

            // Réception de l'image du client
            InputStream is = socket.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            baos.close();

            // Sauvegarde de l'image reçue sur le disque
            byte[] imageData = baos.toByteArray();
            try (FileOutputStream fos = new FileOutputStream("image_recue.jpg")) {
                fos.write(imageData);
            }

            // Affichage des informations sur l'image reçue
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd@HH:mm:ss");
            String date = dateFormat.format(new Date());
            String clientAddress = socket.getInetAddress().toString();
            int clientPort = socket.getPort();
            String imageName = "photo De " + username + ".jpg";
            System.out.println("[" + username + " - " + clientAddress + ":" + clientPort + " - " + date + "] : " + "Image " + imageName + " reçue pour traitement.");

            // Traitement de l'image
            BufferedImage image = ImageIO.read(new File("image_recue.jpg"));
            File outputFile = new File("image_traitee.jpg");
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
