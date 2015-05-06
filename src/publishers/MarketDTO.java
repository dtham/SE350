package SE350;

public class MarketDTO {
	public String product;
	public Price buyPrice;
	public int buyVolume;
	public Price sellPrice;
	public int sellVolume;
	
	public MarketDTO (String theProduct, Price theBuyPrice, int theBuyVolume, Price theSellPrice, int theSellVolume ){
		product = theProduct;
		buyPrice = theBuyPrice;
		buyVolume = theBuyVolume;
		sellPrice = theSellPrice;
		sellVolume = theSellVolume;
	}
	
	public String toString(){
        return String.format("Product: %s, Buy Price: %s, Buy Volume: %s,"
                + "Sell Price: %s, Sell Volume: %s", product, 
                buyPrice, buyVolume, sellPrice, sellVolume );
    }
}
