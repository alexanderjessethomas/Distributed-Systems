package transactionserver.lock;

import java.util.ArrayList;


//This is the lock class. This is used each time a lock is created to lock an object.
//It is synchronized to that threads to not interfere with eachother as they are locking and when they are unlocking
//Code is based off of code inside of course textbook
public class Lock implements LockType {

    //variables to use later in the class
    private Object object;
    private ArrayList<Integer> holders;
    private int currentLockType;


    //Creates the Lock
    public Lock(Object object){
        this.object = object;
        holders = new ArrayList<Integer>();
    }


    //This is what synchronizes the threads based off of transaction id and the lock type that is being used
    //This is done to start the lock
    public synchronized void acquire(int transID, int lockType){

        while(isConflict(transID,lockType)){
            try { 
                wait(); 
            }
            catch ( InterruptedException e){
                //stuff
            }
        }
        // if no transaction ids hold lock
        if (holders.isEmpty()) {   
            holders.add(transID);
            currentLockType = lockType;

        } else if (!holders.isEmpty()) {
            if (!holders.contains(transID)){
                holders.add(transID);
            }
            currentLockType = lockType;

        } else if ( !holders.contains(transID) && currentLockType == READ_LOCK && lockType == WRITE_LOCK ){

            currentLockType = lockType;
        }

        System.out.println("[Lock].acquire() transactionID #" + transID + " acquired LockType: " + lockType + ".");

    }


    //Releases the lock based off of the transaction ID the lock has been aquired by
    public synchronized void release(int transID) {

        holders.remove(holders.indexOf(transID));

        if (holders.isEmpty())
        {
            currentLockType = EMPTY_LOCK;
        }
        
        // let waiting transactions know that the lock is now available
        notifyAll();

        // print to console
        System.out.println("[Lock].release() transactionID #" + transID + " released a lock.");
    }


    //Checks for lock conflicts with a lock type and current locks
    public boolean isConflict(int transID, int newLockType){

        // checks to see the conditions when there is no conflicts
        if (holders.isEmpty()){
            // no conflict because no locks in holder
            System.out.println("[Lock].isConflict() holders empty, no conflict.");
            return false;
        }
        // holder list length 1 and lock holder has trans
        else if (holders.size() == 1 && holders.contains(transID)){
            System.out.println("[Lock].isConflict() transactionID only holder, no conflict.");
            return false;
        }
        // current lock type is read and new lock type is read
        else if (currentLockType == READ_LOCK && newLockType == READ_LOCK){
            System.out.println("[Lock].isConflict() current lock READ and new lock READ, no conflict.");
            return false;
        }
        // there is a conflict
        else {
            System.out.println("[Lock].isConflict() Locks conflict.");
            return true;
        }
    }


    public ArrayList<Integer> getTransactionIDHolders(){
        return holders;
    }
}
