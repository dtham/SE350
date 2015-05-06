
package tradable;
import price.Price;
import SE350.InvalidVolumeException;
import SE350.GlobalConstants.BookSide;



public class TradableImpl implements Tradable {

 
  private String product;
  private Price price;
  private int originalVolume;
  private int remainingVolume;
  private int cancelledVolume;
  private String user;
  private BookSide side;
  private boolean isQuote;
  private String id;

    TradableImpl(String userName, String productSymbol, Price sidePrice, int originalVolume, 
            boolean isQuote, BookSide side, String theId) throws InvalidVolumeException{
        user = userName;
        product = productSymbol;
        price = sidePrice;
        this.originalVolume = originalVolume;
        this.isQuote = isQuote;
        this.side = side;
        id = theId;
        setOriginalVolume(originalVolume);
        setRemainingVolume(originalVolume);
        setCancelledVolume(0);
    }

    @Override
    public final String getProduct() {
        return product;
    }

    @Override
    public final Price getPrice() {
        return price;
    }

    @Override
    public final int getOriginalVolume() {
        return originalVolume;
    }

    @Override
    public final int getRemainingVolume() {
        return remainingVolume;
    }

    @Override
    public final int getCancelledVolume() {
        return cancelledVolume;
    }

    @Override
    public final void setCancelledVolume(int newCancelledVolume) throws InvalidVolumeException {
        if (newCancelledVolume < 0 || newCancelledVolume > originalVolume){
            throw new InvalidVolumeException("Invalid cancel volume: " + newCancelledVolume);
        }
        
        cancelledVolume = newCancelledVolume;
    }

    @Override
    public final void setRemainingVolume(int newRemainingVolume) throws InvalidVolumeException {
        if (newRemainingVolume<0 || newRemainingVolume > originalVolume){
               throw new InvalidVolumeException("Invalid remaning volume: " + newRemainingVolume); 
                }
        
        remainingVolume = newRemainingVolume;
    }
    
    private void setOriginalVolume(int newOriginalVolume)
        throws InvalidVolumeException {
        if (newOriginalVolume < 1) {
        throw new InvalidVolumeException("Invalid original volume " +
              "is being set: " + newOriginalVolume);
    }
    originalVolume = newOriginalVolume;
  }
      
    @Override
    public final String getUser() {
        return user;
    }

    @Override
    public final BookSide getSide() {
        return side;
    }

    @Override
    public boolean isQuote() {
        return isQuote;
    }

    @Override
    public String getId() {
        return id; 
    }

}