/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import constants.GlobalConstants.BookSide;
import price.Price;
import publishers.exceptions.InvalidMessageException;

package publishers;

/**
 *
 * @author Daryl
 */
public class FillMessageImpl implements GeneralMarketMessage {

    GeneralMarketMessage gm;
    
    public FillMessageImpl(String user, String product, Price price, int vol, String details,
        BookSide side, String id) throws InvalidMessageException{
        gm = MessageFactory.createGeneralMarketMessageImpl(user, product, price, vol, details, side, id);
    }
    
    @Override
    public String getUser() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getProduct() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Price getPrice() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getVolume() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getDetails() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public BookSide getSide() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
