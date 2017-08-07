package transactionserver.server;

import transactionserver.data.DataManager;
import transactionserver.lock.LockManager;
import utils.PropertyHandler;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;


/**
 *  Class [TransServer] : Server that waits for connections  from the possible clients and
 *  then it creates threads to transaction manager which takes over from there
 *
 */
public class TransServer extends Thread{

    // Singleton objects
    public static TransManager transManager;
    public static DataManager dataManager;
    public static LockManager lockManager;

    // creating a server socket variable to be used later to create a serversocket
    static ServerSocket serverSocket;
    
    // creating variables to hold server things like the host, port and properties of the
    //server
    private String host;
    private int port;
    private Properties properties;

    //This is the server constructor
    //It takes in a server properties file like previous assignments
    //This file is then read and the server properties are imported into their proper place for the server
    //to function correctly
    public TransServer(String serverPropertiesFile) {
        
        // This is where the server properties are read in.
        //This is also where the serversocket will be created.
        try {
            // properties is set to the properties in the serverProtperties file
            properties = new PropertyHandler(serverPropertiesFile);
            //setting host to what is is in the file
            host = properties.getProperty("HOST");
            System.out.println("[TransServer] Host: " + host);
            //setting port to what is it in the file
            port = Integer.parseInt(properties.getProperty("PORT"));
            System.out.println("[TransServer] Port: " + port);
            
            // after all properties are read in from the file we create a serversocket with the port from the properties file
            serverSocket = new ServerSocket(port);
        
        } catch (Exception e) {
            System.err.println("[TransServer] Error: " + e);
            e.printStackTrace();
        }

        // This creates a new transaction manager which is required for the assignment to work
        transManager = new TransManager();

        // creates a datamanger to help keep track of data for the accounts
        dataManager = new DataManager();

        // creates the lock manager, This is were issues are occuring for the acquire method. We talked to Dr. Otte about it during the demo
        lockManager = new LockManager();

    }

    public void run() {
    // During this loop is where the connection to the clients are made. This is what will allow the server to keep talking to clients
        while (true) {
            System.out.println("[TransServer].run() Waiting to accept a client on port " + port + "... ");
            
            try{
                // This accepts the clients
                transManager.runTrans(serverSocket.accept());
                System.out.println("[TransServer].run() Socket accepted.");

            }catch (IOException e) {
                //Print outs the error if one occurs
                System.err.println("[TransServer].run() Error: " + e);
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        // have to run the application somewhere hahaha.
        TransServer transServer;
        if(args.length == 1) {
            transServer = new TransServer(args[0]);
        } else {
            transServer = new TransServer("../../config/Server.properties");
        }
        transServer.start();
    }
}
