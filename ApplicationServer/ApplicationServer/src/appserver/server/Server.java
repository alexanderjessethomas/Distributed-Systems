package appserver.server;

import appserver.comm.Message;
import static appserver.comm.MessageTypes.JOB_REQUEST;
import static appserver.comm.MessageTypes.REGISTER_SATELLITE;
import appserver.comm.ConnectivityInfo;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import utils.PropertyHandler;

/**
 *
 * @author Dr.-Ing. Wolf-Dieter Otte
 */
public class Server {

    // Singleton objects - there is only one of them. For simplicity, this is not enforced though ...
    static SatelliteManager satelliteManager = new SatelliteManager();
    static LoadManager loadManager = new LoadManager();
    static ServerSocket serverSocket = null;

    string host = null;
    int port;
    Properties properties;

    public Server(String serverPropertiesFile) {

        // create satellite and load managers
        // ...
        try{
            properties = new PropertyHandler(serverPropertiesFile);
            host = properties.getProperty("HOST");
            System.out.println("[Server] Host: " + host);
            port = Integer.parseInt(Properties.getProperty("PORT"));
            System.out.println("[Server] Port: " + port);
            serverSocket = new ServerSocket(port);
        }catch (Exception error){
            System.err.println("[Server] Error: " + error);
        }
        // read server port from server properties file
        int serverPort = 0;
        // ...
        
        // create server socket
        // ...
    }

    public void run() {
    // start serving clients in server loop ...
        while(true){
            System.out.println("[Server] Waiting for port request to accept");
            try {
                new(Thread(new ServerThread(serverSocket.accept()))).start();

            }catch(IOException error){
                System.err.println("[Server] Error: " + error);
            }
        }
    }

    // objects of this helper class communicate with clients
    private class ServerThread extends Thread {

        Socket client = null;
        ObjectInputStream readFromNet = null;
        ObjectOutputStream writeToNet = null;
        Message message = null;

        private ServerThread(Socket client) {

            this.client = client;
        }

        @Override
        public void run() {

            // ...
            
            

            try {
                // setting up object streams
                readFromNet = new ObjectInputStream(client.getInputStream());
                writeToNet = new ObjectOutputStream(client.getOutputStream());
                // reading message
                message = (Message) readFromNet.readObject();
            } catch (Exception e) {
                System.err.println("[ServerThread.run] Message could not be read from object stream.");
                e.printStackTrace();
                System.exit(1);
            }

            // processing message
            ConnectivityInfo satelliteInfo = null;
            switch (message.getType()) {
                case REGISTER_SATELLITE:
                    // read satellite info
                    satelliteInfo = (ConnectivityInfo) message.getContent();
                    System.out.println("[ServerThread] Name: " + satelliteInfo.getName() );
                    // ...
                    
                    // register satellite
                    synchronized (Server.satelliteManager) {
                        satelliteManager.satelliteAdded(satelliteInfo);
                        // ...
                    }

                    // add satellite to loadManager
                    synchronized (Server.loadManager) {
                        loadManager.satelliteAdded(satelliteInfo.getName());
                        // ...
                    }

                    break;

                case JOB_REQUEST:
                    System.err.println("\n[ServerThread.run] Received job request");

                    String satelliteName = null;
                    synchronized (Server.loadManager) {
                        // get next satellite from load manager
                        // ...
                        try{
                            satelliteName = loadManager.nextSatellite();


                            satelliteInfo = satelliteManager.getSatelliteForName(satelliteName);

                        }catch(Exception error){
                            System.err.println("[Server] Error: " + error);
                            System.exit(1);
                        }
                        
                        // get connectivity info for next satellite from satellite manager
                        // ...
                    }

                    Socket satelliteSocket = null;
                    ObjectInputStream satelliteReadFromNet;
                    ObjectOutputStream satelliteWriteFromNet;
                    try{
                        // connect to satellite
                        satelliteSocket = new Socket(satelliteInfo.getHost(), satelliteInfo.getPort());

                        // ...

                        // open object streams,
                        satelliteReadFromNet = new ObjectOutputStream(satelliteSocket.getInputSteam());
                        satelliteWriteFromNet = new ObjectOutputStream(satelliteSocket.getOutputStream())
                        // forward message (as is) to satellite,
                        sattilleteWriteFromNet.writeObject(message);
                        // receive result from satellite and
                        Object result  = sattilleteReadFromNet.readObject();
                        // write result back to client
                        writeToNet.writeObject(result);
                        // ...
                    }catch (Exception error) {
                        System.err.println("[Server] Error: " + error);
                        System.exit(1);
                    }


                    break;

                default:
                    System.err.println("[ServerThread.run] Warning: Message type not implemented");
            }
        }
    }

    // main()
    public static void main(String[] args) {
        // start the application server
        Server server = null;
        if(args.length == 1) {
            server = new Server(args[0]);
        } else {
            server = new Server("../../config/Server.properties");
        }
        server.run();
    }
}
