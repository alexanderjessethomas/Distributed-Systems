package transactionserver.client;

import java.util.Properties;
import java.util.Random;
import utils.PropertyHandler;

/**
 *
 * Class [Client] : Instances of client are threaded in main. The thread uses the TransServerProxy class to transform
 * this classes high level calls to low level network calls.
 *
 */
public class TransClient extends Thread{

    private Properties properties;
    private String host;
    private int port;
    private int numberOfAccounts;

    /**
     * ClassConstructor - Grab all necessary server information for later connection attempts
     * @param serverPropertiesFile - String to the path for the property file
     */
    public TransClient(String serverPropertiesFile) {
        try {
            properties = new PropertyHandler(serverPropertiesFile);
            host = properties.getProperty("HOST");
            System.out.println("[Client] Host: " + host);
            port = Integer.parseInt(properties.getProperty("PORT"));
            System.out.println("[Client] Port: " + port);
            numberOfAccounts = Integer.parseInt(properties.getProperty("NUMBER_OF_ACCOUNT"));

        } catch (Exception e) {
            System.err.println("[Client] Error: " + e);
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        try {
            // Create an instance of the server proxy to handle transactions
            TransServerProxy transServerProxy = new TransServerProxy(host,port);
            
            // Tell the transaction server that we want to open a transaction
            int transID = transServerProxy.openTrans();

            // Grab the initial sum of all currently active accounts
            //transServerProxy.sumAccounts();
            
            // print to console
            System.out.println("[Client].run() Transaction #" + transID + " opened.");
            
            Random rand = new Random();
            
            // randomly selected account number for random account access
            int withdrawnAccount = (int) rand.nextInt((numberOfAccounts - 1) + 1) + 1;;
            int depositedAccount = (int) rand.nextInt((numberOfAccounts - 1) + 1) + 1;;

            // randomly select amount to transfer
            int transferAmount = (int) rand.nextInt((10 - 1) + 1) + 1;;

            // reads from account which is to be withdrawn from
            int withdrawnAccountBal = transServerProxy.read(withdrawnAccount);
            
            // update the balance of the account which we withdrew money from 
            int withdrawnAccountNewBal = transServerProxy.write(withdrawnAccount, withdrawnAccountBal - transferAmount);

            // reads from account where we are going to make the deposit
            int depositedAccountBal = transServerProxy.read(depositedAccount);
            
            // update the balance of the account which is receiving the deposit
            int depositedAccountNewBal = transServerProxy.write(depositedAccount, depositedAccountBal + transferAmount );


            // print to console
            System.out.println("[Client].run() Account #" + withdrawnAccount + " deposited $" + transferAmount + " to account #" + depositedAccount );
            System.out.println("[Client].run() NEW BALANCES:\n Withdrawn Account: " + withdrawnAccount + " = $" + withdrawnAccountNewBal + "\n Deposited Account: " + depositedAccount + " = $" + depositedAccountNewBal);

            // grab the new sum total from all active accounts
            //transServerProxy.sumAccounts();

            // close the transaction which notifies the transaction manager to release all locks
            transServerProxy.closeTrans();

        }catch (Exception e) {
            System.err.println("[Client].run() Error: " + e);
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        // Create multiple thread clients
        for (int i=0;i<5;i++){
            TransClient client = new TransClient("../../config/Server.properties");
            client.start();
        }
        
    }  
}