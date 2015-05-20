
package tradable;
import tradable.exceptions.InvalidVolumeException;
import price.Price;


public class QuoteSide implements Tradable{
    
    private Tradable quoteSide;

    public QuoteSide(String userName, String productSymbol, Price sidePrice, int originalVolume, BookSide side)
                    throws InvalidVolumeException{
        quoteSide = new TradableImpl(userName, productSymbol, sidePrice, originalVolume,
                    true, side, userName + productSymbol + System.nanoTime());
    }
    
    //copy constructor
    public QuoteSide(QuoteSide qs) throws InvalidVolumeException{
        quoteSide = new TradableImpl(qs.getUser(), qs.getProduct(), qs.getPrice(),qs.getOriginalVolume(),
                        qs.isQuote(), qs.getSide(), qs.getUser() + qs.getProduct() + System.nanoTime());
    }
    
    @Override
    public String getProduct() {
        return quoteSide.getProduct();
    }

    @Override
    public Price getPrice() {
        return quoteSide.getPrice();
    }

    @Override
    public int getOriginalVolume() {
        return quoteSide.getOriginalVolume();
    }

    @Override
    public int getRemainingVolume() {
        return quoteSide.getRemainingVolume();
    }

    @Override
    public int getCancelledVolume() {
        return quoteSide.getCancelledVolume();
    }

    @Override
    public void setCancelledVolume(int newCancelledVolume) throws InvalidVolumeException {
        quoteSide.setCancelledVolume(newCancelledVolume);
    }

    @Override
    public void setRemainingVolume(int newRemainingVolume) throws InvalidVolumeException {
        quoteSide.setRemainingVolume(newRemainingVolume);
    }

    @Override
    public String getUser() {
        return quoteSide.getUser();
    }

    @Override
    public BookSide getSide() {
        return quoteSide.getSide();
    }

    @Override
    public boolean isQuote() {
        return quoteSide.isQuote();
    }

    @Override
    public String getId() {
        return quoteSide.getId();
    }
    
    @Override
    public String toString(){
        return String.format("%s X %s (Original Vol: %s, CXL'd Vol: %s) [%s]",
                quoteSide.getPrice(), quoteSide.getRemainingVolume(), quoteSide.getOriginalVolume(),
                quoteSide.getCancelledVolume(), quoteSide.getId());
    }
}
	

