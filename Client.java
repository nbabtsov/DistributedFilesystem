import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {


    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("127.0.0.1", Integer.parseInt(args[0]));
        DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        System.out.println("Welcome user");
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.print(">");
            out.writeUTF(sc.nextLine());
            String line = in.readUTF();
            System.out.println(line);

        }
    }
}
