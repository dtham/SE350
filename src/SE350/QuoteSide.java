
package SE350;


public class QuoteSide {
	String UserID;
	String current_time;
	String product;
	int OriQuantity; 
	int RemQuantity;
	int CanQuantity; 
	String ID; 
	Price PriceOrder = new Price();
        Bookside b_side = new Bookside();
	
	
	
    public void QuoteSide (String userName, String productSymbol, Price sidePrice, int originalVolume, String side){
    	UserID = userName;
    	product = productSymbol;
    	PriceOrder = sidePrice;
    	OriQuantity = originalVolume;
    	b_side.setSide(side);
    	current_time = Long.toString(System.nanoTime());
    	ID = UserID + product + current_time;
            }
    public String getProduct() {
 	   return this.product;
    }
    
    public int getRemainingVolume(){
 	   return this.RemQuantity;
    }
    
    public void setRemainingVolume(int newRemainingVolume) {
 	   RemQuantity = newRemainingVolume;
    }
    
    public String toString() {
 	   String sum_string;
 	   sum_string = "$" + PriceOrder + "( Original Vol: " + OriQuantity + ", CXL'd Vol: " + CanQuantity + "), ID: (" + ID + ")";
 	   return sum_string;
    }
}
