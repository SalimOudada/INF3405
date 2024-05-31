import java.io.*;
import java.net.Socket;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

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
            while (true) {
                String username = bufferedReader.readLine();
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
            try (FileOutputStream fos = new FileOutputStream("received_image.jpg")) {
                fos.write(imageData);
            }

            // Traitement de l'image
            BufferedImage image = ImageIO.read(new File("received_image.jpg"));
            File outputFile = new File("image_traite.jpg");
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
