
package SE350;


public class Price{
    
    public long P; 
    public boolean market_price = false; 
    
    public void Price(long value){
       //price check
        if(value == 0 ) //check marketprice
        {
            System.out.println("Invalid Price Operation");
        }
        else
        {
            P = value;
        }
    }
    
    public void Price(){
        market_price = true; //check
    }
    
    public class InvalidPriceOperation extends Exception{
    	public InvalidPriceOperation(String message) {
    		super(message);
    	}
    	public InvalidPriceOperation(String message, Throwable throwable){
    		super(message, throwable);
    }
    }
           
    public long add(Price p) throws InvalidPriceOperation{
    	if(p == null || market_price == true || p.market_price==true) {
            throw new InvalidPriceOperation("Price either null or market price!");
    	}
    	else{
            long p_val = p.P + this.P;
            return p_val;
            }
    }
    
    public long subtract(Price p) throws InvalidPriceOperation{
    	if(p == null || market_price == true || p.market_price==true) {
            throw new InvalidPriceOperation("Price either null or market price!");
        }
    	else {
            long p_val = this.P - p.P; 
            return p_val;
        }
    }
    
    public long multiply(Price p)throws InvalidPriceOperation{
    	if(p == null || market_price == true || p.market_price==true) {
            throw new InvalidPriceOperation("Price either null or market price!");
        }
    	else {
            long p_val = p.P * this.P;
            return p_val;
        }
    }
    
    public int compareTo(Price p)throws InvalidPriceOperation{
    	if(p == null || market_price == true || p.market_price==true) {
        	throw new InvalidPriceOperation("Price either null or market price!");
        }
    	else{
            int comp_result = 0;
    	if (p.P == P) {
    		comp_result = 0;
    	}
    	if (p.P > P){
    		comp_result = -1;
    	}
    	if (p.P < P){
    		comp_result = 1;
    	}
    		return comp_result;
        
    }
    	}
    public boolean greaterOrEqual(Price p)throws InvalidPriceOperation{
    	if(p == null || market_price == true || p.market_price==true) {
            throw new InvalidPriceOperation("Price either null or market price!");
        }
    	else{
            boolean a = true;
            boolean b = false;
            if (compareTo(p) >= 0) { 
                return a;
            }
    	return b;
        }
    }
    
    
    public boolean greaterThan(Price p)throws InvalidPriceOperation{
    	if(p == null || market_price == true || p.market_price==true) {
        	throw new InvalidPriceOperation("Price either null or market price!");
        }
    	else{
            boolean a = true;
            boolean b = false;
            if (compareTo(p) > 0) { 
            return a;
        }
            return b;
        }
    }

    
    public boolean lessOrEqual(Price p){
    	boolean a = true;
    	boolean b = false;
    	try{
    	if (compareTo(p) <= 0) { 
            return a;
        }
    	}
    	catch(InvalidPriceOperation i){
    		System.out.print(i);
    	}
    	return b;
    }
    
    public boolean lessThan(Price p){
    	boolean a = true;
    	boolean b = false;
    	try{
    	if (compareTo(p) < 0) { 
    	return a;
        }
    	}
    	catch(InvalidPriceOperation x){
    		System.out.print(x);
    	}
    	return b;
    }
   
    public boolean equals(Price p){
    	boolean a = true;
    	boolean b = false;
    	try{
            if (compareTo(p) == 0) { 
            return a;
            }
        }
            catch(InvalidPriceOperation y){
    		System.out.print(y);
    	}
    	return b;
    }
    
    public boolean isMarket() {
    	return this.market_price;
    }
    
    public boolean isNegative() {
    	boolean a = true;
    	boolean b = false;
    	 if (market_price == true) {
    		 return b;
    	 }
    	 else {
    	if (P < 0) {
    		return a;
    	}
    	return b;
    	}
    }
    
    public String toString() {
    	String pos_P = '$'+ Long.toString(P/10);
    	String neg_P = '$' + '-' + Long.toString(P/10);
    	
        if (market_price == true) {
    		return "MKT";
    	}
        else if (P<0) {
    		return neg_P;	
    	}
        else{         //if (P>0) causes an error (replaced)
    		return pos_P;
    	}
    }
        
    
    
    
    }
