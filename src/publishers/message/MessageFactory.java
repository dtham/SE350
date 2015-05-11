/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package publishers.message;
import constants.GlobalConstants.BookSide;
import constants.GlobalConstants.MarketState;
import price.Price;
import publishers.message.exceptions.InvalidMessageException;


/**
 *
 * @author Daryl
 */
public class MessageFactory {
    
public static CancelMessageImpl createCancelMessageImpl(String user, String product, Price price,
        int vol, String details, BookSide side, String id) throws InvalidMessageException{
    return new CancelMessageImpl(user, product, price, vol, details, side, id);
}

public static FillMessageImpl createFillMessageImpl(String user, String product, Price price,
        int vol, String details, BookSide side, String id) throws InvalidMessageException{
    return new FillMessageImpl(user, product, price, vol, details, side, id);
    
}

 public static MarketMessageImpl createMarketMessageImpl(MarketState state)
          throws InvalidMessageException {
    return new MarketMessageImpl(state);
  }

protected static GeneralMarketMessage createFillMessageImpl(String user, String product, 
        Price price, int vol, String details, BookSide side, String id) throws InvalidMessageException{
    return new GeneralMarketMessageImpl(user, product, price, vol, details, side, id);
}
}

