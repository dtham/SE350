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
public class FillMessage implements GeneralMarketMessage, Comparable<FillMessage>{
    
    protected GeneralMarketMessage fm;
    
    public FillMessage(String user, String product, Price price, int vol, String details, 
    BookSide side, String id) throws InvalidMessageException{
    fm = MessageFactory.createFillMessageImpl(user, product, price, vol, details, side, id);
    }

    @Override
    public String getUser() {
        return fm.getUser();
    }

    @Override
    public String getProduct() {
        return fm.getProduct();
    }

    @Override
    public Price getPrice() {
        return fm.getPrice();
    }

    @Override
    public int getVolume() {
        return fm.getVolume();
    }

    @Override
    public String getDetails() {
        return fm.getDetails();
    }

    @Override
    public BookSide getSide() {
        return fm.getSide();
    }

    @Override
    public int compareTo(FillMessage o) {
        return fm.getPrice().compareTo(o.getPrice());
    }
    
    @Override
    public String toString(){
        return fm.toString();
    }
}


