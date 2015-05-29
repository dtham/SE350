package publishers;

import client.User;
import price.Price;
import publishers.exceptions.PublisherExceptions;
import publishers.message.CancelMessage;
import publishers.message.FillMessage;
import publishers.message.MarketDataDTO;
import publishers.message.MarketMessage;


public class CurrentMarketPublisher implements MessagePublisherSubject {

  private volatile static CurrentMarketPublisher instance;
  private MessagePublisherSubject messagePublisherSubjectImpl;

  public static CurrentMarketPublisher getInstance() {
    if (instance == null) {
      synchronized (CurrentMarketPublisher.class) {
        if (instance == null) {
          instance = MessagePublisherSubjectFactory
                  .createCurrentMarketPublisher();
        }
      }
    }
    return instance;
  }

  protected CurrentMarketPublisher(MessagePublisherSubject impl) {
    messagePublisherSubjectImpl = impl;
  }

  @Override
  public synchronized void subscribe(User u, String product)
          throws PublisherExceptions {
    messagePublisherSubjectImpl.subscribe(u, product);
  }

  @Override
  public synchronized void unSubscribe(User u, String product) throws
          PublisherExceptions {
    messagePublisherSubjectImpl.unSubscribe(u, product);
  }

  @Override
  public synchronized void publishCurrentMarket(MarketDataDTO m) {
    messagePublisherSubjectImpl.publishCurrentMarket(m);
  }

  @Override
  public synchronized void publishLastSale(String product, Price p, int v) {}

  @Override
  public synchronized void publishTicker(String product, Price p) {}

  @Override
  public synchronized void publishCancel(CancelMessage cm) {}

  @Override
  public synchronized void publishFill(FillMessage fm) {}

  @Override
  public synchronized void publishMarketMessage(MarketMessage mm) {}
}