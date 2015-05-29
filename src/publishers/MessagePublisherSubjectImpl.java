/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package publishers;

import price.Price;
import java.util.*;
import client.User;
import java.util.logging.Level;
import java.util.logging.Logger;
import price.exceptions.InvalidPriceOperation;
import publishers.exceptions.PublisherExceptions;
import publishers.message.CancelMessage;
import publishers.message.FillMessage;
import publishers.message.MarketDataDTO;
import publishers.message.MarketMessage;


public class MessagePublisherSubjectImpl implements MessagePublisherSubject {
    
    private Map<String, Set<User>> subscribers;
    private Map<String, Price> stockValue;
    
    protected MessagePublisherSubjectImpl(){
        subscribers = new HashMap<>();
        stockValue = new HashMap<>();
    }
    
    @Override
    public synchronized final void subscribe(User u, String product)
          throws PublisherExceptions {
    
        createUserSetForProduct(product);
        Set<User> set = subscribers.get(product);
        if (set.contains(u)) {
          throw new PublisherExceptions("The users has already subscribed to "
                  + "receive updates for this stock symbol: " + product);
        }
        set.add(u);
    }

    @Override
    public synchronized final void unSubscribe(User u, String product)
          throws PublisherExceptions {
        Set<User> set = subscribers.get(product);
        if (set == null) {
          throw new PublisherExceptions("No one is registered for this "
                  + "stock symbol: " + product);
        }
        if (!set.contains(u)) {
          throw new PublisherExceptions("The user is not subscribed to "
                  + "receive updates for this stock symbol: " + product);
        }
        set.remove(u);
    }

    private synchronized void createUserSetForProduct(String product) {
      if (!subscribers.containsKey(product)) {
        subscribers.put(product, new HashSet<User>());
      }
    }

    @Override
    public synchronized void publishCurrentMarket(MarketDataDTO m) {
      if (!subscribers.containsKey(m.product)) { return; }
      Set<User> users = subscribers.get(m.product);
      for (User u : users) {
        u.acceptCurrentMarket(m.product, m.buyPrice, m.buyVolume, m.sellPrice,
                m.sellVolume);
      }
    }

    @Override
    public synchronized void publishLastSale(String product, Price p, int v) {
      if (!subscribers.containsKey(product)) { return; }
      Set<User> users = subscribers.get(product);
      for (User u : users) {
        u.acceptLastSale(product, p, v);
      }
      TickerPublisher.getInstance().publishTicker(product, p);
    }

    @Override
    public synchronized void publishTicker(String product, Price p) {
      if (!subscribers.containsKey(product)) { return; }
      char direction = ' ';
      Price val = stockValue.get(product);
      if (val != null) {
        if (p.equals(val)) {
          direction = '=';
        } else try {
            if (p.greaterThan(val)) {
                direction = '\u2191';
            } else if (p.lessThan(val)) {
                direction = '\u2193';
            }
        } catch (InvalidPriceOperation ex) {
            Logger.getLogger(MessagePublisherSubjectImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
      stockValue.put(product, p);
      Set<User> users = subscribers.get(product);
      for (User u : users) {
        u.acceptTicker(product, p, direction);
      }
    }

    @Override
    public synchronized void publishCancel(CancelMessage cm) {
      String p = cm.getProduct();
      if (!subscribers.containsKey(p)) { return; }
      for (User u : subscribers.get(p)) {
        if (u.getUserName().equals(cm.getUser())) {
          u.acceptMessage(cm);
        }
      }
    }

    @Override
    public synchronized void publishFill(FillMessage fm) {
      String p = fm.getProduct();
      if (!subscribers.containsKey(p)) { return; }
      for (User u : subscribers.get(p)) {
        if (u.getUserName().equals(fm.getUser())) {
          u.acceptMessage(fm);
        }
      }
    }

    @Override
    public synchronized void publishMarketMessage(MarketMessage mm) {
      for (Set<User> users : subscribers.values()) {
        for (User u : users) {
          u.acceptMarketMessage(mm.getState().toString());
        }
      }
    }
  }
