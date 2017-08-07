package transactionserver.data;


//This is the account class
public class Account {

    private int id;
    private int amount;

    public Account(){}
    
    public int getId(){
        return this.id;
    }
    
    public void setId(int id){
        this.id = id;
    }
    
    public int getAmount(){
        return this.amount;
    }
    
    public void setAmount(int amount)
    { 
        this.amount = amount;
    }

}
