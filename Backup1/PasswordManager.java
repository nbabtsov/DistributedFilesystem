import java.io.*;
import java.security.MessageDigest;
import java.util.Hashtable;
import java.util.Scanner;

public class PasswordManager{

    File users;
    File passwords;
    Hashtable<String,String> dictionary;

    public PasswordManager() throws FileNotFoundException
    {

        users = new File("users.txt");
        passwords = new File("security.txt");
        dictionary = getDictionary();
    }

    public boolean authenticate(String username, String password)
    {
        if(dictionary.containsKey(username))
        {
            String hashedPassword = hasher(password);
            if (dictionary.get(username).equals(hashedPassword))
                return true;
            return false;
        }
        return false;
    }

    public boolean addUser(String user, String password) throws IOException {
        if(dictionary.containsKey(user))
            return false;
        BufferedWriter uwriter = new BufferedWriter(new FileWriter("users.txt",true));
        BufferedWriter pwriter = new BufferedWriter(new FileWriter("security.txt",true));
        String hashedPassword = hasher(password);
        uwriter.write(user);
        pwriter.write(hashedPassword);
        uwriter.newLine();
        pwriter.newLine();
        dictionary.put(user,hashedPassword);
        uwriter.flush();
        uwriter.close();
        pwriter.flush();
        pwriter.close();
        return true;
    }

    public Hashtable<String,String> getDictionary() throws FileNotFoundException {
        Hashtable<String,String> result = new Hashtable<String,String>();
        Scanner ufile = new Scanner(users);
        Scanner pfile = new Scanner(passwords);
        String u,p;
        while(ufile.hasNextLine() && pfile.hasNextLine())
        {
            u = ufile.nextLine();
            p = pfile.nextLine();
            result.put(u,p);
        }
        return result;
    }

    private String hasher(String password){
        try
        {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] hashedPassword = md.digest(password.getBytes());
            return new String(hashedPassword);
        }
        catch (Exception E) { }
        return null;
    }

}
