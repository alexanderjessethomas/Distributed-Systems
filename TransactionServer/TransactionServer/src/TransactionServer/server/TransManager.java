package transactionserver.server;

import transactionserver.comm.Message;
import transactionserver.comm.MessageTypes;
import transactionserver.comm.Parameters;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;


//This is the Transaction Manager class. THe point of this class is to manage all of the transactions that the server
//will recieve from clients
public class TransManager {
    // Come up with transaction ids for each transaction. THis done by incrementing the counter
    private static int transCounter;
    private static ArrayList<Transaction> transactions;

    TransManager(){
        transCounter = 0;
        transactions = new ArrayList();
    }


    //THis is where the transactions are created
    //after they are created the transactions are ran as well
    //This needs a conenction to the client to get the transaction information from.
    public void runTrans(Socket client){
        System.out.println("[TransManager].runTrans() called.");

        // This is where the transaction counter comes into play
        //Since the counter is incremented after setting the transaction ID no two transactions will have the same id number
        int transID = transCounter;
        transCounter ++;

        //after transaction id is set we actually create the transaction
        //It gets added to the list of current transactions
        //Then the transaction is started by starting the transaction in a thread
        Transaction transaction = new Transaction(client, transID);
        transactions.add(transaction);
        transaction.start();
    }


    //This is the transaction class
    // This si where the transactions are created when the request from the client comes in.
    //each time a new request comes in a new transaction is start which means a new thread is started as well
    //The thread is done and handled by the transaction manager
    private class Transaction extends Thread implements MessageTypes{

        private int transID;
        private Socket client;
        private ObjectInputStream readFromNet;
        private ObjectOutputStream writeToNet;
        private Message message;

        //takes in the Client socket and the transaction ID
        //THis is done to show the transaction is being processed
        public Transaction(Socket client, int transID){
            System.out.println("[TransManager][Trans].constructor() called.");
            this.transID = transID;
            this.client = client;
        }

        //This is where the transaction is ran
        //reads in the messages from the client associated with the transaction
        //it creates the stream required and the messages to complete the transaction
        //it then processes the messages when they come in from the client.
        @Override
        public void run() {
            System.out.println("[TransManager][Trans].run() called.");

            while(true){

                try {

                    writeToNet = new ObjectOutputStream(client.getOutputStream());
                    readFromNet = new ObjectInputStream(client.getInputStream());


                    message = (Message) readFromNet.readObject();


                    Parameters params = (Parameters) message.getContent();

                    int fromAccountID;
                    int toAccountID;
                    int amount;
                    int balance;


                    switch (message.getType()) {

                        case OPEN_TRANS:
                            writeToNet.writeObject(transID);
                            System.out.println("[TransManager][Trans].run() OPEN_TRANS #"+ transID +".");
                            break;

                        case CLOSE_TRANS:
                            TransServer.dataManager.removeLocks(transID);
                            transactions.remove(this);
                            transCounter--; 
                            System.out.println("[TransManager][Trans].run() CLOSE_TRANS #"+ transID +".");
                            return;
                            
                        case READ_REQUEST:
                            fromAccountID = (int) params.arg1;
                            balance = TransServer.dataManager.read(fromAccountID,transID);
                            writeToNet.writeObject(balance);
                            System.out.println("[TransManager][Trans].run() READ_REQUEST -> account #" + fromAccountID + ": $" + balance + ".");
                            break;

                        case WRITE_REQUEST:
                            toAccountID = (int) params.arg1;
                            amount = (int) params.arg2;
                            balance = TransServer.dataManager.write(toAccountID,transID,amount);
                            writeToNet.writeObject(balance);
                            System.out.println("[TransManager][Trans].run() WRITE_REQUEST to account #" + toAccountID + ": $" + amount + ".");
                            break;
                        
                        default:
                            System.err.println("[TransManager][Trans].run() Warning: Message type not implemented");
                    }
                }
                catch (Exception e) {
                    System.err.println("[TransManager][Trans].run() Message could not be read from object stream.");
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        }
    }


}
