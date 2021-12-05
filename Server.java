import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {


    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(Integer.parseInt(args[0]));


        System.out.println("Waiting for a client ...");

        Socket socket;

        // takes input from the client socket
        DataInputStream in;
        DataOutputStream out;


        while (true) {
            socket = server.accept();
            System.out.println("ClientSide.Client accepted");
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            out = new DataOutputStream(socket.getOutputStream());
            String line = in.readUTF();
            System.out.println(line);
            if (line.split("\\s+")[0].equals("Open"))
                sendFile(line.split("\\s+")[1], out);
            else {
                out.writeUTF("Not Recognized");
                out.writeUTF("Received: " + line);
            }
            in.close();
            out.close();
            socket.close();
        }
    }

    public static boolean sendFile(String name, DataOutputStream out) {
        try {
            System.out.println(name);
            int count;
            byte[] buffer = new byte[8192]; // or 4096, or more
            File fileSource = new File(name);
            FileInputStream file = new FileInputStream(fileSource);
            while ((count = file.read(buffer)) > 0) {
                out.write(buffer, 0, count);
            }
            file.close();
            System.out.println("Done!");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
