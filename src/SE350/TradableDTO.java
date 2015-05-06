
package SE350;

import SE350.GlobalConstants.BookSide;
import java.util.Locale;


public class TradableDTO {
    public String product; 
    public Price price;
    public int originalVolume;
    public int remainingVolume;
    public int cancelledVolume;
    public String user;
    public Tradable.BookSide side;
    public boolean isQuote;
    public String id;
    
    public TradableDTO(String theProduct, Price thePrice, int theOriginalVolume,
            int theRemainingVolume, int theCancelledVolume, String theUser, Tradable.BookSide theSide, 
            boolean ifIsQuote, String theId){
        product = theProduct;
        price = thePrice; 
        originalVolume = theOriginalVolume;
        remainingVolume = theRemainingVolume;
        cancelledVolume = theCancelledVolume;
        user = theUser;
        side = theSide;
        isQuote = ifIsQuote;
        id = theId;
    }
    
    @Override
    public String toString(){
        return String.format("Product: %s, Price: %s, OriginalVolume: %s, RemainingVolume: %s,"
                + "CancelledVolume: %s, User: %s, Side: %s, isQuote: %s, id:%s", product, price,
                originalVolume, remainingVolume, cancelledVolume, user, side, isQuote, id );
    }
}
