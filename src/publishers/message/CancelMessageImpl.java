/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package publishers.message;
import publishers.message.exceptions.InvalidMessageException;
import price.Price;
import constants.GlobalConstants.BookSide;



/**
 *
 * @author Daryl
 */
public class CancelMessageImpl implements GeneralMarketMessage {
    
    GeneralMarketMessage gm;
    
public CancelMessageImpl(String user, String product, Price price, int vol, String details,
        BookSide side, String id) throws InvalidMessageException{
    gm = MessageFactory.createGeneralMarketMessageImpl(user,
            product, price, vol, details, side, id);
}

    @Override
    public String getUser() {
        return gm.getUser();
    }

    @Override
    public String getProduct() {
        return gm.getProduct();
    }

    @Override
    public Price getPrice() {
        return gm.getPrice();
    }

    @Override
    public int getVolume() {
        return gm.getVolume();
    }

    @Override
    public String getDetails() {
        return gm.getDetails();
    }

    @Override
    public BookSide getSide() {
        return gm.getSide();
    }
}
