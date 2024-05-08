import java.util.regex.Matcher;
import java.util.regex.Pattern;


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

}