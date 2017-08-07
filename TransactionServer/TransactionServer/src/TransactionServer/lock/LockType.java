package transactionserver.lock;

//Use integer values to determine the type of lock being used
    //This is based off the class textbook
public interface LockType {
    int EMPTY_LOCK = 0;
    int READ_LOCK = 1;
    int WRITE_LOCK = 2;
}
