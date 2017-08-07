package appserver.client;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Properties;
import utils.PropertyHandler;
import appserver.comm.Message;
import appserver.comm.MessageTypes;
import appserver.job.Job;

public class FibClient extends Thread implements MessageTypes{
    string host = null;
    int port;
    Properties properties;
    Integer num;
    public FibClient(String serverPropertiesFile, Integer num) {
        try {
            properties = new PropertyHandler(serverPropertiesFile);
            host = properties.getProperty("HOST");
            System.out.println("[FibClient.FibClient] Host: " + host);
            port = Integer.parseInt(properties.getProperty("PORT"));
            System.out.println("[FibClient.FibClient] Port: " + port);
        } catch (Exception error) {
            System.err.println("[FibClient] Error: " + error);
            error.printStackTrace();
        }
        this.num = num;
    }
    @Override
    public void run() {
        try {

            // connect to application server
            Socket server = new Socket(host, port);

            // hard-coded string of class, aka tool name ... plus one argument
            String classString = "appserver.job.impl.Fib";
            Integer number = new Integer(this.num);

            // create job and job request message
            Job job = new Job(classString, num);
            Message message = new Message(JOB_REQUEST, job);

            // sending job out to the application server in a message
            ObjectOutputStream writeToNet = new ObjectOutputStream(server.getOutputStream());
            writeToNet.writeObject(message);

            // reading result back in from application server
            // for simplicity, the result is not encapsulated in a message
            ObjectInputStream readFromNet = new ObjectInputStream(server.getInputStream());
            Integer result = (Integer) readFromNet.readObject();
            System.out.println("RESULT: " + result);

        } catch (Exception error) {
            System.err.println("[PlusOneClient.run] Error: " + error);
            error.printStackTrace();
        }
    }

    public static void main(String[] args) {

        for (int i=46;i>0;i--){
            (new FibClient("../../config/Server.properties",i)).start();
        }


    }

}

