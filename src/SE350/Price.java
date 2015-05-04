
package SE350;


public class Price implements Comparable<Price>{
    
    boolean market_price;
    Price self = this; 
    long value; 
    
    private final int BEFORE = -1;
    private final int EQUAL = 0; 
    private final int AFTER = 1; 
    
    Price(long value){
       //price check
        self.value = value; 
        self.market_price = false;
    }
    
    //constructor
    Price(){
        self.value = 0; 
        self.market_price = true; 
    }
           
    public Price add(Price p) throws InvalidPriceOperation{
    	if(self.isMarket()) {
            throw new InvalidPriceOperation("Current Price is a Market Price!");
    	}
    	if(p.isMarket()) {
            throw new InvalidPriceOperation("Price passed in is Market Price!");
        }
        
        return new Price(self.value + p.value);
    }
    
    public Price subtract(Price p) throws InvalidPriceOperation{
    	if(self.isMarket()){
            throw new InvalidPriceOperation("Current Price is a Market Price!");
        }
    	if(p.isMarket()){
            throw new InvalidPriceOperation("Price passed in is Market Price");
        }
        
        return new Price(self.value - p.value);
    }
    
    public Price multiply(int p)throws InvalidPriceOperation{
    	if(self.isMarket()){
            throw new InvalidPriceOperation("Current Price is a Market Price");                  
        }
            return new Price(self.value * p);
    }
    
    public int compareTo(Price p){ 
    	if (self.value == p.value) {
    		return self.EQUAL;
    	}
        if (self.value > p.value){
    		return self.AFTER;
    	}
    	if (self.value < p.value){
    		return self.BEFORE;
    	}
        else{
            return self.EQUAL;
        }
    }
    
    public boolean greaterOrEqual(Price p){
    	if(p.isMarket() || self.isMarket()){
            return false;
        }
        
        int comp = self.compareTo(p);
        
        if(comp == self.AFTER || comp == self.EQUAL){
            return true;
        }
        else{
            return false;
        }
    }
    
    
    public boolean greaterThan(Price p)throws InvalidPriceOperation{
    	if(p.isMarket() || self.isMarket()){
            return false;
        }
        
        int comp = self.compareTo(p);
        
        if(comp == self.AFTER){
            return true;
        }
        else{
            return false;
        }
    }

    
    public boolean lessOrEqual(Price p){
    	if(p.isMarket() || self.isMarket()){
            return false;
        }
        
        int comp = self.compareTo(p);
        
        if(comp == self.EQUAL || comp == self.BEFORE){
            return true;
        }
        else{
            return false;
        }
    }
    
    public boolean lessThan(Price p){
    	if(p.isMarket() || self.isMarket()){
            return false;
        }
        
        int comp = self.compareTo(p);
        
        if(comp == self.BEFORE){
            return true;
        }
        else{
            return false;
        }
    }
   
    public boolean equals(Price p){
    	if(p.isMarket() || self.isMarket()){
            return false;
        }
        
        int comp = self.compareTo(p);
        
        if(comp == self.EQUAL){
            return true;
        }
        else{
            return false;
        }
    }
    
    public boolean isMarket() {
    	return this.market_price;
    }
    
    public boolean isNegative() {
        if(self.isMarket() || self.value >= 0){
            return false;
        }
        else{
            return true;
        }
    }
    
    public String toString() {
    	return (self.isMarket() ? "MKT" : String.format("$%,.2f", self.value/100.0));
    }  
}
