package SE350;

public interface User {


  public String getUserName();

  
  public void acceptLastSale(String product, Price p, int v);

 
  public void acceptMessage(FillMessage fm);

  
  public void acceptMessage(CancelMessage cm);

  
  public void acceptMarketMessage(String message);

  
  public void acceptTicker(String product, Price p, char direction);

  
  public void acceptCurrentMarket(String product, Price bp, int bv, Price sp,
          int sv);
}