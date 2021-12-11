import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.ArrayList;

public class Server {
    private static ArrayList<Integer> ports = new ArrayList<Integer>();

    public static void main(String[] args){
        if (args.length < 1){ //no args
            System.out.println("must enter one port number to run primary server");
        }
        else if (args.length == 1)//start primary
        {
            startPrimaryServer(args[0]);//start backup
        }
        else if (args.length == 2){
            startBackupServer(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        }
        else{
            System.out.println("Wrong input");
        }


    }
    public static void startPrimaryServer(String port){
        System.out.println("Primary Server started");
        int p_port = Integer.parseInt(port);

        try(ServerSocket server = new ServerSocket(p_port)){
            System.out.println("Waiting for a client ...");

        while(true){

            Socket socket = server.accept();
            ClientHandler clientSock = new ClientHandler(socket);
            new Thread(clientSock).start();

        }

        }
        catch(Exception ex){
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        // Constructor
        public ClientHandler(Socket socket)
        {
            this.clientSocket = socket;
        }

        public void run()
        {

            try {
                DataInputStream in = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
                DataOutputStream out =  new DataOutputStream(clientSocket.getOutputStream());
                String line = in.readUTF();
                System.out.println("Got message: " + line);
                if (line.split("\\s+")[0].equals("OPEN")) {
                    System.out.println("Attemtping to sending file...");
                    String name = line.split("\\s+")[1];
                    boolean filesent = sendFile(name, out);
                    if (filesent) {
                        System.out.println("File sent!");
                    }
                    else {
                        //Potential TODO: propagate file to all backups if doesn't exist there?
                        System.out.println("Something went wrong with sending the file, trying backups");
                        for(Integer p:ports)
                        {
                            Socket socket2 = new Socket("127.0.0.1", p);
                            DataInputStream in2 = new DataInputStream(new BufferedInputStream(socket2.getInputStream()));
                            DataOutputStream out2 =  new DataOutputStream(socket2.getOutputStream());
                            out2.writeUTF("OPEN " + name);
                            String res = in2.readUTF();
                            if(res.contains("FILE_EXISTS")) {
                                int c;
                                File f = new File(name);
                                DataOutputStream file2 = new DataOutputStream(new FileOutputStream(f));
                                byte[] b = new byte[8192]; // or 4096, or more
                                while ((c = in2.read(b)) > 0) {
                                    file2.write(b, 0, c);
                                }
                                filesent = true;
                                System.out.println("File sent from backup");

                                file2.close();
                                socket2.close();
                                in2.close();
                                out2.close();
                                sendFile(name, out);

                            }
                        }
                        if (!filesent){
                            System.out.println("all backups unsuccessful");
                        }
                  }
                }
                if (line.split("\\s+")[0].contains("JOIN")){
                    String backup_port = line.split("\\s+")[1];
                    ports.add(Integer.parseInt(backup_port));
                    out.writeUTF("COMPLETE_JOIN");
                    System.out.println("Just had backup server join on port " + backup_port);
                }
                if (line.split("\\s+")[0].equals("UPDATE")){ //client updates file
                    //update ("send?") file and propagate updates to all files in backups
                    //or "append"? use as separate command?
                }
                if (line.split("\\s+")[0].equals("REMOVE")){ //NOT DONE
                    //remove file and propagate remove to all files in backups
                    String name = line.split("\\s+")[1];
                    File file = new File(name);
                    if(file.exists()){
                        file.delete();
                        System.out.println("File "+ name + " deleted from primary");
                        for(Integer p:ports) {
                            Socket socket2 = new Socket("127.0.0.1", p);
                            DataInputStream in2 = new DataInputStream(new BufferedInputStream(socket2.getInputStream()));
                            DataOutputStream out2 = new DataOutputStream(socket2.getOutputStream());
                            out2.writeUTF("REMOVE " + name);
                            String res = in2.readUTF();
                            if(res.contains("FILE_DELETED")){
                                //TODO: verify response? -- # of deletions matches number of ports connected?
                            }
                        }
                        System.out.println("File " + name + " deleted from all backups");

                    }
                }


                out.flush();
                in.close();
                out.close();
                clientSocket.close();
            }
            catch (IOException e) {
                //e.printStackTrace();
                System.out.println("Client disconnected");
            }
        }
    }


    public static synchronized boolean sendFile(String name, DataOutputStream out) {
        try {
            System.out.println(name);
            int count;
            byte[] buffer = new byte[8192]; // or 4096, or more

            File fileSource = new File(name);
            if(fileSource.exists()){
                out.writeUTF("FILE_EXISTS");
                FileInputStream file = new FileInputStream(fileSource);
                while ((count = file.read(buffer)) > 0) {
                    out.write(buffer, 0, count);
                }
                file.close();
                System.out.println("Done!");
            }
            else{
                return false;
            }


        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public static synchronized boolean  sendFileBackup(String name, DataOutputStream out){
        try {
            System.out.println(name);
            int count;
            byte[] buffer = new byte[8192]; // or 4096, or more
            File fileSource = new File(name);
            FileInputStream file = new FileInputStream(fileSource);
            while ((count = file.read(buffer)) > 0) {
                out.write(buffer, 0, count);
            }

            try (BufferedReader br = new BufferedReader(new FileReader(name))) {
                String line;
                while ((line = br.readLine()) != null) {
                    System.out.println(line);
                }
            }

            file.close();
            System.out.println("Done!");
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public static class ClientHandlerBackup implements Runnable {
        private final Socket clientSocket;
        private final int pport;

        // Constructor
        public ClientHandlerBackup(Socket socket, int pport)
        {
            this.clientSocket = socket;
            this.pport = pport;
        }

        public void run()
        {

            try {
                DataInputStream in = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
                DataOutputStream out =  new DataOutputStream(clientSocket.getOutputStream());
                String line = in.readUTF();
                System.out.println("Backup got message: " + line);
                if (line.split("\\s+")[0].contains("OPEN")) {
                    File fileSource = new File(line.split("\\s+")[1]);
                    if(fileSource.exists()){
                        out.writeUTF("FILE_EXISTS");
                        sendFileBackup(line.split("\\s+")[1], out);
                        out.flush();
                        in.close();
                        out.close();
                        clientSocket.close();;
                    }
                    else{
                        out.writeUTF("NO_SUCH_FILE");

                    }
                }
                else if (line.split("\\s+")[0].equals("UPDATE")) {
                    //run update method? (again?)
                }
                else if(line.split("\\s+")[0].equals("REMOVE")){
                    String name = line.split("\\s+")[1];
                    File file = new File(name);
                    if(file.exists()){
                        out.writeUTF("FILE_DELETED " + name  );
                        file.delete();
                    }
                    else{
                        out.writeUTF("NO_SUCH_FILE " + name  );

                    }
                }
                else
                {
                    out.writeUTF("command unrecognized");
                }

                out.flush();
                in.close();
                out.close();
                clientSocket.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void startBackupServer(int secondaryPort, int primaryPort)
    {
        String response = oneTimeCommunicate(primaryPort, "JOIN "+secondaryPort); //request to join primary server
        if(response.contains("COMPLETE_JOIN"))
        {

            try (ServerSocket serverSocket = new ServerSocket(secondaryPort)) {
                System.out.println("Backup with port " + secondaryPort + " is up and listening for primary");

                while (true) {
                    Socket socket = serverSocket.accept();

                    ClientHandlerBackup clientSock = new ClientHandlerBackup(socket, primaryPort);
                    //create new thread per client
                    new Thread(clientSock).start();

                }

            } catch (Exception ex) {
                System.out.println("Server exception: " + ex.getMessage());
                ex.printStackTrace();
            }

        }
        else {
            System.out.println("Something went wrong");
        }

    }



    //for handling communication between the primary and backup servers, acts like "client" code
    public static String oneTimeCommunicate(int port, String message) {
        Socket socket=null;
        try {
            socket = new Socket("127.0.0.1", port);
            DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            DataOutputStream out =  new DataOutputStream(socket.getOutputStream());

            out.writeUTF(message);

           System.out.println("Just send out: " + message + " to port: " + port);

            //get response
            String line = in.readUTF();

            System.out.println("Got response: " + line);

            //close the socket
            socket.close();
            return line;
        } catch (IOException e) {
            e.printStackTrace();
            return "bad";
        }
    }


}
