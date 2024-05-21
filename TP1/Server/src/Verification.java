import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class Verification{

    public static boolean verifyIP(String ip) {
    // Expression régulière pour vérifier une adresse IP valide
    String format = "^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$";
    return ip.matches(format);
    }

	    public static void verifyPort(int port){
	        if (port>=5050 || port<5000){
	            throw new IllegalArgumentException("pas un port valide");
	        }
	    }	



	public static boolean verifyCredentials(String username, String password) {
		//test push
	    String localDir = System.getProperty("user.dir");
	    File userDatas = new File(localDir + "/Server/src/dataBase.txt");
	    
	    try (BufferedReader reader = new BufferedReader(new FileReader(userDatas))) {
	        String line;
	        while ((line = reader.readLine()) != null) {
	            String[] parts = line.split(":", 2);
	            if (parts.length == 2) {
	                String username_db = parts[0].trim(); 
	                String password_db = parts[1].trim();
	                if(username_db.equals(username)) {
	                    if(password_db.equals(password)) {
	                        return true;
	                    }
	                    else {
	                        return false;
	                    }
	                }
	            }
	        }
	        try (BufferedWriter writer = new BufferedWriter(new FileWriter(userDatas, true))) {
	            writer.newLine();
	            writer.write(username+":"+password);
	            System.out.println("Un compte a été créé avec succès.");
	            return true;
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return false;
	}}