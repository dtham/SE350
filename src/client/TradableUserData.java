package client;

import client.exceptions.TradableUserDataException;
import constants.GlobalConstants.BookSide;
/**
 *
 * @author Daryl's
 */
public class TradableUserData {
    private String userName;
    private String product;
    private BookSide side;
    private  TradableUserData self = this;
    String id;
    
    public TradableUserData(String theName, String theProduct, BookSide sideIn,
            String IdIn) throws TradableUserDataException{
        setUserName(theName);
        setProduct(theProduct);
        setSide(sideIn);
        setID(IdIn);
    }
    
    public String getUsername(){
        return userName;
    }
    
    public BookSide getSide(){
        return side;
    }
    
    public String getProduct(){
        return product;
    }
    
    public String getID(){
        return id;
    }
    
    private void setUserName(String user) throws TradableUserDataException{
        if (user == null || user.isEmpty()){
            throw new TradableUserDataException("User name cannot be null.");
        }
        userName = user;
    }
    
    private void setProduct(String prod) throws TradableUserDataException {
        if (prod == null || prod.isEmpty()) {
            throw new TradableUserDataException("Stock symbol cannot be null.");
        }
        product = prod;
    }
    
    private void setSide(BookSide theSide) throws TradableUserDataException {
        if (theSide == null || !(theSide instanceof BookSide)) {
          throw new TradableUserDataException("The BookSide cannot be null.");
        }
        side = theSide;
    }

    private void setID(String theID) throws TradableUserDataException {
        if (theID == null || theID.isEmpty()) {
          throw new TradableUserDataException("Order ID cannot be null or empty.");
        }
        id = theID;
    }
    
    @Override
    public String toString() {
      return "User " + self.getUsername()+ ", " + self.getSide() + " " +
              self.getProduct() + " (" + self.getID() + ")";
    }
}

