package publishers;


import client.User;
import price.Price;
import publishers.exceptions.PublisherExceptions;
import publishers.message.CancelMessage;
import publishers.message.FillMessage;
import publishers.message.MarketDataDTO;
import publishers.message.MarketMessage;


public class TickerPublisher implements MessagePublisherSubject 
{
    private volatile static TickerPublisher instance;
    private MessagePublisherSubject messagePublisherSubjectImpl;

    public static TickerPublisher getInstance() {
       if (instance == null) {
         synchronized (TickerPublisher.class) {
           if (instance == null) {
             instance = MessagePublisherSubjectFactory.createTickerPublisher();
           }
         }
       }
       return instance;
     }
    
    protected TickerPublisher(MessagePublisherSubject impl) {
        messagePublisherSubjectImpl = impl;
    }
     
    @Override
    public void subscribe(User u, String product) throws PublisherExceptions {
         messagePublisherSubjectImpl.subscribe(u, product);
    }

    @Override
    public void unSubscribe(User u, String product) throws PublisherExceptions {
         messagePublisherSubjectImpl.unSubscribe(u, product);
    }

    @Override
    public void publishCurrentMarket(MarketDataDTO m) {
        
    }

    @Override
    public void publishLastSale(String product, Price p, int v) {
        
    }

    @Override
    public void publishTicker(String product, Price p) {
        messagePublisherSubjectImpl.publishTicker(product, p);
    }

    @Override
    public void publishCancel(CancelMessage cm) {
        
    }

    @Override
    public void publishFill(FillMessage fm) {
        
    }

    @Override
    public void publishMarketMessage(MarketMessage mm) {
        
    }
	
}