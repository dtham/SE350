
package SE350;


public class Quote {
    String UserName;
    String StockSymbol;
    String current_time;
	int OriQuantity; 
	int CanQuantity; 
	String ID; 
    QuoteSide Buy = new QuoteSide();
    QuoteSide Sell = new QuoteSide();
    Price PriceOrder = new Price();
    Bookside side = new Bookside();
   
    
    public void Quote(String userName, String productSymbol, Price buyPrice,
            int buyVolume, Price sellPrice, int sellVolume){
        UserName = userName; 
        StockSymbol = productSymbol;
        PriceOrder = buyPrice; 
        Buy(UserName, StockSymbol, PriceOrder, buyVolume, bookSide); 
        current_time = Long.toString(System.nanoTime());
    	ID = UserName + StockSymbol + current_time;
    }
    
    public String getUserName(){
        return UserName;
    }
    
    public String getProduct(){
        return StockSymbol;
    }
    
    public QuoteSide getQuoteSide(Bookside sideIn){
        side.setSide(sideIn);
    }
    
    public String toString(){
        String sum_string;
        sum_string = StockSymbol + "$" + PriceOrder + "( Original Vol: " + OriQuantity + ", CXL'd Vol: " + CanQuantity + "), ID: (" + ID + ")";
        return sum_string;
    } 
}
