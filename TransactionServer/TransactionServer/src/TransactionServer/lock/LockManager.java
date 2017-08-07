package transactionserver.lock;

import transactionserver.data.Account;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


//This is the lockmanager class
    //This handles all the locks that are being used
public class LockManager implements LockType {
    private HashMap<Account, Lock> locks;


    public LockManager(){
        locks = new HashMap();
    }

 public void setLock(Account account, int transID, int lockType){

        // foundLock from the text
        Lock lock; 

        synchronized(this){
            // find lock associated with account
            lock = locks.get(account);


            // if there isn't one, create it and add it to the hash table
            if (lock == null){
                lock = new Lock (account);
                locks.put(account,lock);
                System.out.println("[LockManager].setLock on Account #" + account.getId());
            }

            // acquire lock for the transactionID
            lock.acquire(transID,lockType);
            
            // print status to the console
            System.out.println("[LockManager].setLock " + lockType + " set on transactionID #" + transID + ".");
        }
    }

    //used to unlock a locked object
    public synchronized void unLock(int transID) {
        // temp lock for iterations
        Lock tempLock; 
        
        // list of transactionIDs in a lock
        ArrayList<Integer> transactionList; 

        // iterates through all the locks and release the locks that contain transactionID
        Iterator iterator = locks.entrySet().iterator();
        while (iterator.hasNext()){
            // get the locks from the hash map
            tempLock = (Lock) ((HashMap.Entry) iterator.next()).getValue();
            
            // grab transactionID
            transactionList = tempLock.getTransactionIDHolders();
            
            // the transactionID matches
            if (transactionList.contains(transID)){
 
                // release the transactionID
                tempLock.release(transID); 
            }
            iterator.remove();
        }
        // Print to console
        System.out.println("[LockManager].unlock: all locks for transactionID #" + transID + " removed.");
    }

}
