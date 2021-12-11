import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Scanner;

public class Client {


    public static void main(String[] args) {
        Socket socket;
        DataInputStream in;
        DataOutputStream out;

        System.out.println("Welcome user");
        System.out.println("Type OPEN <filename> to retrieve file");
        Scanner sc = new Scanner(System.in);

        while (true) {
            try {
                socket = new Socket("127.0.0.1", Integer.parseInt(args[0]));
                in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                out = new DataOutputStream(socket.getOutputStream());
                System.out.print(">");
                String line = sc.nextLine();
                if (line.split("\\s+")[0].equals("OPEN")) {
                    retrieve(line, out, in);
                } else {
                    out.writeUTF(line);
                    line = in.readUTF();
                    System.out.println(line);
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
}
