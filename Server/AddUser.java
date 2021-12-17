import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class AddUser {

    public static void main(String[] args) throws IOException {
        PasswordManager pm = new PasswordManager();
        if(pm.authenticate("admin",args[0]))
        {
            Scanner sc = new Scanner(System.in);
            System.out.println("New username:");
            String user = sc.nextLine();
            System.out.println("Password:");
            String pass1 = sc.nextLine();
            System.out.println("Confirm Password");
            String pass2 = sc.nextLine();
            if(!pass1.equals(pass2))
            {
                System.out.println("Passwords don't match");
                return;
            }
            if(pm.addUser(user,pass1))
                System.out.println("Added");
            else
                System.out.println("User Exists Already");
        }
        else
        {
            System.out.println("Incorrect Admin password");
        }
    }

}
