/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package publishers.message;
import constants.GlobalConstants.BookSide;
import price.Price;
import publishers.message.exceptions.InvalidMessageException;



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
    
    @Override
    public String toString(){
        String str = gm.toString();
        return str.substring(0, str.indexOf("ID") -2);
    }

    @Override
    public String getID() {
        return gm.getID();
    }

    @Override
    public void setVolume(int volume) throws InvalidMessageException {
        gm.setVolume(volume);
    }

    @Override
    public void setDetails(String details) throws InvalidMessageException {
        gm.setDetails(details);
    }
}
