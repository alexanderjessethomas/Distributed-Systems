package transactionserver.comm;

/**
 * Modified from Dr. Ottes original version to account for transaction types
 * Interface [MessageTypes] Defines the different message types used in the application.
 */
public interface MessageTypes {
    int OPEN_TRANS = 1;
    int CLOSE_TRANS = 2;
    int READ_REQUEST = 3;
    int WRITE_REQUEST = 4;
    int ACCOUNT_TOTAL_REQUEST = 5;
}
