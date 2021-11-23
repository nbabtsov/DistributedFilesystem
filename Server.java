import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {


    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(Integer.parseInt(args[0]));


        System.out.println("Waiting for a client ...");

        Socket socket = server.accept();
        System.out.println("Client accepted");

        // takes input from the client socket
        DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());


        while (true)
        {
                String line = in.readUTF();
                System.out.print(line);
                out.writeUTF("Received: " + line);
                System.out.print("sent");
        }
    }
}
