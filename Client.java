import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {


    public static void main(String[] args) throws IOException {
        Socket socket;
        DataInputStream in;
        DataOutputStream out;

        System.out.println("Welcome user");
        Scanner sc = new Scanner(System.in);

        while (true) {
            socket = new Socket("127.0.0.1", Integer.parseInt(args[0]));
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            out = new DataOutputStream(socket.getOutputStream());
            System.out.print(">");
            String line = sc.nextLine();
            if (line.split("\\s+")[0].equals("Open")) {
                receive(line, out, in);
            } else {
                out.writeUTF(line);
                line = in.readUTF();
                System.out.println(line);
            }
            in.close();
            out.close();
            socket.close();
        }
    }

    public static boolean receive(String line, DataOutputStream out, DataInputStream in) {
        try {
            out.writeUTF(line);
            int count;
            File fileSource = new File(line.split("\\s+")[1]);
            DataOutputStream file = new DataOutputStream(new FileOutputStream(fileSource));
            byte[] buffer = new byte[8192]; // or 4096, or more
            while ((count = in.read(buffer)) > 0) {
                file.write(buffer, 0, count);
            }
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
