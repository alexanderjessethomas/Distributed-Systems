package transactionserver.client;

import transactionserver.comm.Message;
import transactionserver.comm.MessageTypes;
import transactionserver.comm.Parameters;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

/**
 * Based on Dr. Otte's in class example
 * 
 * Class [TransServerProxy] : An instance of this class is created within the client to communicate with the transaction
 * server. This class simply takes the high-level calls such as openTrans(), closeTrans(), read/write and converts them
 * down to lower level network calls. It uses object streams to communicate to the server, these object streams pass a
 * message object to server where the parameters are stored as the content of that message.
 *
 */
public class TransServerProxy implements MessageTypes{

    private String host;
    private int port;
    private Socket socket;

    /** Class constructor
     *
     * @param host - a string that contains the name for the server
     * @param port - a integer that contains the port for the server connection
     */
    public TransServerProxy(String host, int port) throws IOException{
        this.host = host;
        this.port = port;
        this.socket = new Socket(this.host,this.port);
    }

    /** openTrans : Opens the transaction on the server
     * 
     * @return transID - the id for the transaction that is opened
     * @throws IOException - thrown if object streams can't be opened,
     * for basic exception handling
     */
    public int openTrans() throws IOException,ClassNotFoundException{
        int transID;

        // setting up object streams
        ObjectOutputStream writeToNet = new ObjectOutputStream(this.socket.getOutputStream());
        ObjectInputStream readFromNet = new ObjectInputStream(this.socket.getInputStream());

        // create message with message type
        Message message = new Message();
        message.setType(OPEN_TRANS);
        
        // write object to stream
        writeToNet.writeObject(message);
        
        // read the transID back from the server
        transID = (int) readFromNet.readObject(); 

        // print to console
        System.out.println("[TransServerProxy].openTrans() trans # " + transID + ".");

        return transID;
    }

    /** closeTrans : Closes transaction on the server which in turn releases locks
     *
     * @throws IOException - thrown if object streams can't be opened,
     * for basic error handling
     */
    public void closeTrans() throws IOException, ClassNotFoundException{

        // setting up object streams
        ObjectOutputStream writeToNet = new ObjectOutputStream(this.socket.getOutputStream());

        // create message with message type
        Message message = new Message();
        message.setType(CLOSE_TRANS);

        // write object to stream
        writeToNet.writeObject(message);

    }

    /** read: Reads the balance on the account, which in turn tries to establish lock with account
     *
     * @param accountID - the ID of the account to read from
     * @return balance - the balance of the account
     * @throws IOException - thrown if object streams can't be opened,
     * for basic error handling
     */
    public int read(int accountID) throws IOException, ClassNotFoundException{

        // initialize balance
        int balance = 0;

        // setting up object streams
        ObjectInputStream readFromNet = new ObjectInputStream(this.socket.getInputStream());
        ObjectOutputStream writeToNet = new ObjectOutputStream(this.socket.getOutputStream());

        // create message with the accountID and message type
        Message message = new Message();
        message.setType(READ_REQUEST);
        Parameters param = new Parameters();
        param.arg1 = accountID;
        message.setContent(param);

        // write object to stream
        writeToNet.writeObject(message);
        
        // read the returned balance from the transaction server
        balance = (int) readFromNet.readObject();

        // print to console
        System.out.println("[TransServerProxy].read() Account #" + accountID + ": $" + balance + ".");

        return balance;

    }

    /** write : Writes the given amount to the given account, which in turn tries to establish a lock
     *
     * @param accountID - ID of the account to write to
     * @param amount - the amount written to the account
     * @return balance - the balance of the account after writing
     * @throws IOException - thrown if object streams can't be opened,
     * for basic error handling
     */
    public int write (int accountID, int amount) throws IOException, ClassNotFoundException{

        // initialize variables
        int balance;

        // setting up object streams
        ObjectInputStream readFromNet = new ObjectInputStream(this.socket.getInputStream());
        ObjectOutputStream writeToNet = new ObjectOutputStream(this.socket.getOutputStream());

        // create parameters for message passing
        Parameters param = new Parameters();
        param.arg1 = accountID;
        param.arg2 = amount;

        // create message with the content and message type
        Message message = new Message();
        message.setType(WRITE_REQUEST);
        message.setContent(param); // add content to message

        // write object to stream
        writeToNet.writeObject(message);
        
        // read the returned balance from the transaction server
        balance = (int) readFromNet.readObject();

        // print to console
        System.out.println("[TransServerProxy].write() $" + amount + " written to account " + accountID);

        return balance;

    }
    
    /** sumAccounts: Returns the sum of all active accounts
     *
     * @return sumBalance - the sum of all the accounts
     * @throws IOException - thrown if object streams can't be opened,
     * for basic error handling
     */
    public int sumAccounts () throws IOException, ClassNotFoundException{
        // initialize variables
        int sumBalance;

        // setting up object streams
        ObjectInputStream readFromNet = new ObjectInputStream(this.socket.getInputStream());
        ObjectOutputStream writeToNet = new ObjectOutputStream(this.socket.getOutputStream());

        // create message with the content and message type
        Message message = new Message();
        message.setType(ACCOUNT_TOTAL_REQUEST);

        // write object to stream
        writeToNet.writeObject(message);
        
        // reads the sum account balance from all current accounts
        sumBalance = (int) readFromNet.readObject();

        // print to console
        System.out.println("[TransServerProxy].getAllAcountTotal() $" + sumBalance);

        return sumBalance;

    }

}
