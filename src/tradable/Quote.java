
package tradable;
import SE350.InvalidVolumeException;
import price.Price;
import constants.GlobalConstants.BookSide;





public class Quote {
    
    private String userName;
    private String stockSymbol;
    private Tradable buyQuoteSide;
    private Tradable sellQuoteSide;

   
    public Quote(String userName, String productSymbol, Price buyPrice,
            int buyVolume, Price sellPrice, int sellVolume)throws InvalidVolumeException{
        this.userName = userName; 
        stockSymbol = productSymbol;
        buyQuoteSide = new QuoteSide(userName, productSymbol, buyPrice, buyVolume, Tradable.BookSide.BUY);
        sellQuoteSide = new QuoteSide(userName, productSymbol, sellPrice, sellVolume, Tradable.BookSide.SELL);
    }
    
    public String getUserName(){
        return userName;
    }
    
    public String getProduct(){
        return stockSymbol;
    }
    
    public QuoteSide getQuoteSide(String sideIn) throws InvalidVolumeException{
        if(sideIn.equals(BookSide.BUY)){
            return (QuoteSide) buyQuoteSide;
        }
        else{
            return (QuoteSide)sellQuoteSide;
        }
    }
    
    @Override
    public String toString(){
        return String.format("%s quote: %s - %s", userName, buyQuoteSide, sellQuoteSide);
    } 
}
