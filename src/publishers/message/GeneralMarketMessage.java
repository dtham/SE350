/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package publishers.message;
import price.Price;
import constants.GlobalConstants.BookSide;



/**
 *
 * @author Daryl
 */
public interface GeneralMarketMessage {
    
    public String getUser();
    
    public String getProduct();
    
    public Price getPrice();
    
    public int getVolume();
    
    public String getDetails();
    
    public BookSide getSide();
    
}
