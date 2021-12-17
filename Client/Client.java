import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Scanner;

public class Client {


    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);

        System.out.println("Enter username: ");
        String user = sc.nextLine();
        System.out.println("Enter password: ");
        String password = sc.nextLine();

        Socket socket;
        DataInputStream in;
        DataOutputStream out;
        socket = new Socket("127.0.0.1", Integer.parseInt(args[0]));
        in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        out = new DataOutputStream(socket.getOutputStream());
        out.writeUTF("TEST "+ user +" " +password);
        String testResult = in.readUTF();
        if(testResult.equals("REJECT"))
        {
            System.out.println("Incorrect Username or Password");
            return;
        }
        in.close();
        out.close();
        socket.close();

        System.out.println("Welcome user");
        System.out.println("Type OPEN <filename> to retrieve file, ADD <filename> to add new file, REMOVE <filename> to remove it");

        while (true) {
            try {
                socket = new Socket("127.0.0.1", Integer.parseInt(args[0]));
                in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                out = new DataOutputStream(socket.getOutputStream());
                System.out.print(">");
                String line = sc.nextLine();

                if (line.split("\\s+")[0].equals("OPEN")) {
                    retrieve(line, out, in);
                }
                else if(line.split("\\s+")[0].equals("REMOVE")){
                    remove(line, out, in);
                }
                else if(line.split("\\s+")[0].equals("ADD")){
                    add(line, out, in);
                }
                else {
                    System.out.println("Please use a specified command");
                }
                in.close();
                out.close();
                socket.close();
            }
            catch(IOException e){
                if(e instanceof ConnectException ){
                    System.out.println("Invalid connection");
                    return;
                }
                else{
                    e.printStackTrace();
                    return;
                }

            }
        }
    }

    public static boolean add(String line, DataOutputStream out, DataInputStream in) { //need to add check if file doesn't exist
        try {
            out.writeUTF(line);
            String response = in.readUTF();
            if(response.contains("READY")){

                int count;
                File fileSource = new File(line.split("\\s+")[1]);
                byte[] buffer = new byte[8192]; // or 4096, or more
                FileInputStream file = new FileInputStream(fileSource);
                while ((count = file.read(buffer)) > 0) {
                    out.write(buffer, 0, count);
                }
                file.close();
                System.out.println("File sent to server");
            }
            else {
                System.out.println("File does not exist");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean retrieve(String line, DataOutputStream out, DataInputStream in) { //need to add check if file doesn't exist
        try {
            out.writeUTF(line);
            String response = in.readUTF();
            if(response.contains("FILE_EXISTS")){

                int count;
                File fileSource = new File(line.split("\\s+")[1]);
                DataOutputStream file = new DataOutputStream(new FileOutputStream(fileSource));
                byte[] buffer = new byte[8192]; // or 4096, or more
                while ((count = in.read(buffer)) > 0) {
                    file.write(buffer, 0, count);
                }
                System.out.println("File " + line.split("\\s+")[1] +" downloaded!");
                file.close();
            }
            else {
                System.out.println("File does not exist");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean remove(String line, DataOutputStream out, DataInputStream in){

        File file = new File(line.split("\\s+")[1]);
        if(file.exists()){
            try {
                out.writeUTF(line);
                file.delete();
                System.out.println("File " + line.split("\\s+")[1] + " removed locally");
                String response = in.readUTF();
                if (response.contains("FILE_DELETED")){
                    System.out.println("File " + line.split("\\s+")[1]  + " deleted from primary and backups successfully");
                    return true;
                }
            }
            catch(Exception e){
                return false;
            }
            return true;
        }
        else{
            System.out.println("No such file");
            return false;
        }

    }
}
