
package SE350;


public class Order {
	
	String onePartOfID;
	String time_nano;
    String product;
    int OriQuantity; 
    int RemQuantity;
    int CanQuantity;
    String UserID;
    String ID; 
    Price PriceOrder = new Price();
    Price Tradable = new Price();
    Bookside b_side = new Bookside();

    
    public void Order(String userName, String productSymbol, Price orderPrice, int originalVolume, Bookside side)
    {
        UserID = userName; 
        product = productSymbol;
        OriQuantity = originalVolume; 
        time_nano = Long.toString(System.nanoTime());
        PriceOrder = orderPrice;
        onePartOfID = PriceOrder.toString();
        ID = UserID + product + onePartOfID + time_nano;
        b_side.setSide(side);
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
	   sum_string = UserID + "order:" + b_side + " " + OriQuantity + " " + '$'+ PriceOrder + "( Original Vol: " + OriQuantity + ", CXL'd Vol: " + CanQuantity + "), ID: " + ID;
	   return sum_string;
   }
}
