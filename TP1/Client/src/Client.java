import java.net.Socket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.util.Scanner;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.PrintWriter;

public class Client {
    private static Socket socket;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String adresseServeur = "";
        int portServeur = 0;

        while (true) {
            System.out.println("Entrez l'adresse IP du serveur : ");
            adresseServeur = scanner.nextLine();

            try {
            	Verification.verifyIP(adresseServeur);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("Adresse IP invalide. Veuillez réessayer.");
            }
        }

        while (true) {
            System.out.println("Entrez le port du serveur (entre 5000 et 5050) : ");
            portServeur = scanner.nextInt();
            scanner.nextLine();

            try {
                Verification.verifyPort(portServeur);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("Port invalide : " + e.getMessage() + ". Veuillez réessayer.");
            }
        }

        try {
            socket = new Socket(adresseServeur, portServeur);
            BufferedReader lecteur = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter ecrivain = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            System.out.println("Connecté au serveur " + adresseServeur + " sur le port " + portServeur);

            while (true) {
                System.out.println("Nom d'utilisateur : ");
                String nomUtilisateur = scanner.nextLine();

                System.out.println("Mot de passe : ");
                String motDePasse = scanner.nextLine();

                ecrivain.write(nomUtilisateur + "\n" + motDePasse + "\n");
                ecrivain.flush();

                String verificationCredentials = lecteur.readLine();

                if (verificationCredentials.equals("true")) {
                    System.out.println("Connexion acceptée !");
                    ecrivain.write("verification done\n");
                    ecrivain.flush();
                    break;
                } else if (verificationCredentials.equals("false")) {
                    System.out.println("Erreur dans la saisie du mot de passe.");
                    ecrivain.write("verification not done\n");
                    ecrivain.flush();
                }
            }

            String cheminImage = "";
            while (true) {
                System.out.println("Entrez le chemin de l'image à traiter : ");
                cheminImage = scanner.nextLine();
                if (estCheminImageValide(cheminImage)) {
                    break;
                } else {
                    System.out.println("Chemin d'image invalide. Veuillez réessayer.");
                }
            }

            // Envoyer l'image au serveur
            envoyerImage(cheminImage, socket.getOutputStream());

            // Recevoir l'image traitée du serveur et l'enregistrer sur disque
            recevoirEtSauvegarderImagesTraitees(socket.getInputStream());
            System.out.println("Test.");

        } catch (IOException e) {
            System.out.println("Erreur lors de la communication avec le serveur : " + e.getMessage());
        } finally {
            try {
                socket.close();
                scanner.close();
            } catch (IOException e) {
                System.out.println("Erreur lors de la fermeture du socket : " + e.getMessage());
            }
        }
    }

    private static void envoyerImage(String cheminImage, OutputStream outputStream) throws IOException {
        File fichierImage = new File(cheminImage);
        FileInputStream fis = new FileInputStream(fichierImage);
        byte[] donneesImage = new byte[(int) fichierImage.length()];
        fis.read(donneesImage);
        fis.close();

        // Envoyer l'image par le socket
        outputStream.write(donneesImage);
        outputStream.flush();
        socket.shutdownOutput();
    }

    private static void recevoirEtSauvegarderImagesTraitees(InputStream inputStream) throws IOException {
        BufferedImage imageTraitee = ImageIO.read(inputStream);
        File fichierImageSortie = new File("image_traitee.jpg");
        ImageIO.write(imageTraitee, "JPG", fichierImageSortie);
        System.out.println("Image traitée reçue et sauvegardée dans : " + fichierImageSortie.getAbsolutePath());
    }

    private static boolean estCheminImageValide(String cheminImage) {
        File fichier = new File(cheminImage);
        if (!fichier.exists()) {
            return false;
        }
        try {
            BufferedImage image = ImageIO.read(fichier);
            return image != null;
        } catch (IOException e) {
            return false;
        }
    }

}
