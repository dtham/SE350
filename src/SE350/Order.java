
package SE350;


public class Order implements Tradable{
    
    private Tradable order;
    
    public Order(String userName, String productSymbols, Price orderPrice, int originalVolume, Tradable.BookSide side)
                throws InvalidVolumeException{
        order = new TradableImpl(userName, productSymbols, orderPrice, originalVolume,
                false, side, userName + productSymbols + orderPrice + System.nanoTime());
    }
    
    @Override
    public String getProduct() {
        return order.getProduct();
    }

    @Override
    public Price getPrice() {
        return order.getPrice();
    }

    @Override
    public int getOriginalVolume() {
        return order.getOriginalVolume();
    }

    @Override
    public int getRemainingVolume() {
        return order.getRemainingVolume();
    }

    @Override
    public int getCancelledVolume() {
        return order.getCancelledVolume();
    }

    @Override
    public void setCancelledVolume(int newCancelledVolume) throws InvalidVolumeException {
        order.setCancelledVolume(newCancelledVolume);
    }

    @Override
    public void setRemainingVolume(int newRemainingVolume) throws InvalidVolumeException {
        order.setRemainingVolume(newRemainingVolume);
    }

    @Override
    public String getUser() {
        return order.getUser();
    }

    @Override
    public BookSide getSide() {
        return order.getSide();
    }

    @Override
    public boolean isQuote() {
        return order.isQuote();
    }

    @Override
    public String getId() {
        return order.getId();
    }
    
    @Override
    public String toString(){
        return String.format("%s order: %s %s %s at %s (Original Vol: %s, CXL'd Vol: %s), ID: %s",
                order.getUser(), order.getSide(), order.getRemainingVolume(), order.getProduct(),
                order.getPrice(), order.getOriginalVolume(), order.getCancelledVolume(), order.getId());
    }
}
