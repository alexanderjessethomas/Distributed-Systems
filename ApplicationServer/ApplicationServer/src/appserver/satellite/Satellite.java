package appserver.satellite;

import appserver.job.Job;
import appserver.comm.ConnectivityInfo;
import appserver.job.UnknownToolException;
import appserver.comm.Message;
import static appserver.comm.MessageTypes.JOB_REQUEST;
import static appserver.comm.MessageTypes.REGISTER_SATELLITE;
import appserver.job.Tool;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.PropertyHandler;

/**
 * Class [Satellite] Instances of this class represent computing nodes that execute jobs by
 * calling the callback method of tool implementation, loading the tools code dynamically over a network
 * or locally, if a tool got executed before.
 *
 * @author Dr.-Ing. Wolf-Dieter Otte
 */
public class Satellite extends Thread {

    private ConnectivityInfo satelliteInfo = new ConnectivityInfo();
    private ConnectivityInfo serverInfo = new ConnectivityInfo();
    private HTTPClassLoader classLoader = null;
    private Hashtable toolsCache = null;

    private PropertyHandler satelliteProperties;
    private PropertyHandler classLoaderProperties;
    private PropertyHandler ServerProperties;

    public Satellite(String satellitePropertiesFile, String classLoaderPropertiesFile, String serverPropertiesFile) {

        // read the configuration information from the file name passed in
        // ---------------------------------------------------------------
        // ...
        try {
            satelliteProperties = new PropertyHandler(satellitePropertiesFile);
            classLoaderProperties = new PropertyHandler(classLoaderPropertiesFile);
            serverProperties = new PropertyHandler(serverPropertiesFile);

        } catch (Exception error) {
            System.err.println("[Satellite] Error: " + error);
        }
        
        
        // create a socket info object that will be sent to the server
        // ...
        satelliteInfo.setHost(satelliteProperties.getProperty("HOST"));
        satelliteInfo.setHost(satelliteProperties.getProperty("PORT"));
        
        
        // get connectivity information of the server
        // ...
        serverInfo.setHost(serverProperties.getProperty("HOST"));
        serverInfo.setHost(serverProperties.getProperty("PORT"));


        // create class loader
        // -------------------
        // ...
        initClassLoader();


        // read class loader config
        // ...
        
        
        // get class loader connectivity properties and create class loader
        // ...
        
        
        // create tools cache
        // -------------------
        // ...
        toolsCache = new Hashtable();
    }

    @Override
    public void run() {

        // register this satellite with the SatelliteManager on the server
        // ---------------------------------------------------------------
        // ...
        try{
            Message message = new Message(3, satelliteInfo);
            Socket socket = new Socket(serverProperties.getProperty("HOST"), Integer.parseInt(serverProperties.getProperty("PORT"));
            ObjectOutputStream writeToNet = new ObjectOutputStream(socket.getOutputStream()));
            writeToNet.writeObject(message);
        }catch(IOException error){
            System.err.println("server Error: " + error);
        }
        
        // create server socket
        // ---------------------------------------------------------------
        // ...
        ServerSocket serverSocket;
        String portString = satelliteProperties.getProperty("PORT");



        // start taking job requests in a server loop
        // ---------------------------------------------------------------
        // ...
        try{
            serverSocket = new ServerSocket(Integer.parseInt(portString));

            while(true){
                (new Thread(new(SatelliteThread(serverSocket.accept(),)this))).start();
            }
        }catch (IOException error){
            System.err.println("Server Error: " + error)
        }
    }

    // inner helper class that is instanciated in above server loop and processes job requests
    private class SatelliteThread extends Thread {

        Satellite satellite = null;
        Socket jobRequest = null;
        ObjectInputStream readFromNet = null;
        ObjectOutputStream writeToNet = null;
        Message message = null;

        SatelliteThread(Socket jobRequest, Satellite satellite) {
            this.jobRequest = jobRequest;
            this.satellite = satellite;
        }

        @Override
        public void run() {
            // setting up object streams
            // ...
            try{
                readFromNet = new ObjectInputStream(jobRequest.getInputStream());
                writeToNet = new ObjectOutputStream(jobRequest.getOutputStream());
                // reading message
                // ...
                message = (Message) readFromNet.readObject();
                // processing message
                switch (message.getType()) {
                    case JOB_REQUEST:
                        // ...
                        Job jobRequested = (Job) message.getContent();
                        Tool tool = getToolObject(jobRequested.getToolName());
                        Object result = tool.go(job.getParameters());
                        writeToNet.writeObject(result);
                        break;

                    default:
                        System.err.println("[SatelliteThread.run] Warning: Message type not implemented");
                }
            }catch(Exception error){
                System.err.println("Server Error: " + error)
            }

        }
    }

    /**
     * Aux method to get a tool object, given the fully qualified class string
     *
     */
    public Tool getToolObject(String toolClassString) throws UnknownToolException, ClassNotFoundException, InstantiationException, IllegalAccessException {

        Tool toolObject = null;

        // ...
        //get tool if available
        if ((toolObject = (Tool) toolsCache.get(toolClassString)) == null) {
            //load tool
            Class toolClass = classLoader.loadClass(toolClassString);
            //create new instance of the tool and make it a tool by casting
            toolObject = (Tool) toolClass.newInstance();
            toolsCache.put(toolClassString, toolObject);
        }
        return toolObject;
    }

    private void initClassLoader() {

        String host = classLoaderProperties.getProperty("HOST");
        String portString = classLoaderProperties.getProperty("PORT");

        if ((host != null) && (portString != null)) {
            try {
                classLoader = new HTTPClassLoader(host, Integer.parseInt(portString));
            } catch (NumberFormatException error) {
                System.err.println("Error: " + error);
            }
        } else {
            System.err.println("configuration data incomplete, using Defaults");
        }

        if (classLoader == null) {
            System.err.println("Could not create HTTPClassLoader, exiting ...");
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        // start a satellite
        Satellite satellite = new Satellite(args[0], args[1], args[2]);
        satellite.run();
        
        //(new Satellite("Satellite.Earth.properties", "WebServer.properties")).start();
        //(new Satellite("Satellite.Venus.properties", "WebServer.properties")).start();
        //(new Satellite("Satellite.Mercury.properties", "WebServer.properties")).start();
    }
}
