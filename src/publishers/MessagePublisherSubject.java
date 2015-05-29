/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package publishers;

import client.User;
import price.Price;
import publishers.exceptions.PublisherExceptions;
import publishers.message.MarketDataDTO;
import publishers.message.CancelMessage;
import publishers.message.FillMessage;
import publishers.message.MarketMessage;
/**
 *
 * @author Daryl's
 */
public interface MessagePublisherSubject {
    public void subscribe(User u, String product) throws PublisherExceptions;
    
    public void unSubscribe(User u, String product) throws PublisherExceptions;
    
    public void publishCurrentMarket(MarketDataDTO m);
    
    public void publishLastSale(String product, Price p, int v);
    
    public void publishTicker(String product, Price p);
    
    public void publishCancel(CancelMessage cm);
    
    public void publishFill(FillMessage fm);
    
    public void publishMarketMessage(MarketMessage mm);
}
