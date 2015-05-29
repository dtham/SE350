
package tradable;
import price.Price;
import tradable.exceptions.InvalidVolumeException;
import constants.GlobalConstants.BookSide;
/**
 *
 * @author Daryl
 */
public interface Tradable {
    
   // public static enum BookSide{BUY, SELL};
    
    public String getProduct();
    
    public Price getPrice();
    
    public int getOriginalVolume();
    
    public int getRemainingVolume();
    
    public int getCancelledVolume();
    
    public void setCancelledVolume(int newCancelledVolume) throws InvalidVolumeException;
    
    void setRemainingVolume(int newRemainingVolume) throws InvalidVolumeException;
            
    String getUser();
    
    BookSide getSide();
    
    boolean isQuote();
    
    String getId();
}