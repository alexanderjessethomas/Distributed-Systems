package transactionserver.data;

import transactionserver.lock.LockManager;
import transactionserver.lock.LockType;
import java.util.ArrayList;

//This is the data manager class
    //This is the class that handles all of the account manipulation that occurs with each transaction
    //It makes it so that no account can be accessed if it is allready being accessed by another transaction
public class DataManager implements LockType {

    static private ArrayList<Account> accountList;
    static private LockManager lockManager;


    public DataManager() {
        accountList = new ArrayList<>();
        lockManager = new LockManager();

        // initialize 10 accounts to $10 and add them to the account list
        for (int i = 0; i<10; i++) {
            Account tempAccount = new Account();
            tempAccount.setAmount(10);
            tempAccount.setId(i);
            accountList.add(tempAccount);
        }
        
        System.out.println("[DataManager].constructor()  all accounts initialized to $10.");

    }


    //This will read the balance of the account requested by the client
    //It does this by taking in the account ID to get which account
    //The Trnascation ID to see which transaction is trying to read the account information
    //Then if all is well and it orking it should return the account balance
    public int read (int accountID,int transID)
    {
            //setting a lock so no other transcaction can manipulate while in use
        lockManager.setLock(accountList.get(accountID),transID,READ_LOCK);
        

        Account account = accountList.get(accountID);
        int balance = account.getAmount();
        
        // unlock the read lock
        lockManager.unLock(transID);
        
        System.out.println("[DataManager].read()  TransactionID  #" + transID + " read from account " + account.getId());
        
        return balance;
    }

    /**
     * write - updates the balance from the requested account
     *
     * @param accountID - ID of the account to read
     * @param transID - ID of the transaction that wants to write to the account
     * @param amount - Amount to write to the account
     * @return balance - returns new balance on account
     *
     */
    //This is pretty much like the read function above accept it uses it for writing
    //it also uses locks to manage the accounts manipulation
    public int write (int accountID,int transID, int amount){

        lockManager.setLock(accountList.get(accountID),transID,WRITE_LOCK);
        Account account = accountList.get(accountID);
        account.setAmount(amount);
        lockManager.unLock(transID);
        System.out.println("[DataManager].write()  TransactionID #" + transID + " wrote $" + amount + " to account #" + account.getId());
        return account.getAmount();
    }

        //This removes all the locks that each transaction is currently holding
    public void removeLocks(int transID)
    {
        lockManager.unLock(transID);
    }

tAllAccountTotal(){
        int total = 0;
        for (int i=0; i<accountList.size(); i++){
            total += accountList.get(i).getAmount();
        }
        return total;
    }

}
