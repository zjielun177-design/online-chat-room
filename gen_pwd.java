import java.security.MessageDigest;
import java.util.Base64;

public class gen_pwd {
    public static void main(String[] args) throws Exception {
        String password = "123456";
        String salt = "chat-app-salt-key";
        String saltedPassword = password + salt;
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashedBytes = md.digest(saltedPassword.getBytes());
        String encoded = Base64.getEncoder().encodeToString(hashedBytes);
        System.out.println("密码: " + password);
        System.out.println("加密: " + encoded);
        System.out.println("");
        System.out.println("SQL: UPDATE t_user SET password = '" + encoded + "' WHERE 1=1;");
    }
}
