/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tradeprocessing.productservice;

/**
 *
 * @author Daryl's
 */

import constants.GlobalConstants.BookSide;
import constants.GlobalConstants.MarketState;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import publishers.MessagePublisher;
import publishers.message.MarketDataDTO;
import publishers.message.MarketMessage;
import publishers.message.exceptions.InvalidMessageException;
import tradable.Order;
import tradable.Quote;
import tradable.TradableDTO;
import tradable.exceptions.InvalidVolumeException;
import tradeprocessing.productbook.ProductBook;
import tradeprocessing.productbook.exceptions.DataValidationException;
import tradeprocessing.productbook.exceptions.NoSuchProductException;
import tradeprocessing.productbook.exceptions.OrderNotFoundException;
import tradeprocessing.productbook.exceptions.ProductAlreadyExistsException;
import tradeprocessing.productbook.exceptions.ProductBookException;
import tradeprocessing.productbook.exceptions.ProductBookSideException;
import tradeprocessing.productservice.exceptions.InvalidMarketStateException;
import tradeprocessing.productservice.exceptions.InvalidMarketStateTransitionException;
import tradeprocessing.tradeprocessor.exceptions.InvalidProductBookSideValueException;

public class ProductService {
    private volatile static ProductService instance;
    private HashMap<String, ProductBook> allBooks = new HashMap<>();
    private MarketState state = MarketState.CLOSED;
    
    public static ProductService getInstance() {
        if (instance == null) {
          synchronized (ProductService.class) {
            if (instance == null) {
              instance = new ProductService();
            }
          }
        }
        return instance;
    }
    
    public synchronized ArrayList<TradableDTO> getOrdersWithRemainingQty(
          String userName, String product) {
        return allBooks.get(product).getOrdersWithRemainingQty(userName);
    }
    
    public synchronized MarketDataDTO getMarketData(String product) {
        return allBooks.get(product).getMarketData();
    }
    
    public synchronized MarketState getMarketState() {
        return state;
    }
    
    public synchronized String[][] getBookDepth(String product)
          throws NoSuchProductException {
        if (!allBooks.containsKey(product)) {
           throw new NoSuchProductException("The product: " + product +
                   "; does not exist in the product book.");
        }
        return allBooks.get(product).getBookDepth();
    }
    
    public synchronized ArrayList<String> getProductList() {
        return new ArrayList<>(allBooks.keySet());
    }

    private synchronized boolean isValidTransition(MarketState ms) {
        ArrayList<MarketState> trans = new ArrayList<>(Arrays.asList(
                MarketState.CLOSED, MarketState.PREOPEN, MarketState.OPEN ));
        int msPass = trans.indexOf(ms);
        int msCurrent = trans.indexOf(state);
        int diff = msPass - msCurrent;
        if (msCurrent == 2 && msPass == 0) { return true; }
        if (msCurrent < msPass && diff == 1) { return true; }
        return false;
    }
    
    public synchronized void setMarketState(MarketState ms)
          throws InvalidMarketStateTransitionException, InvalidMessageException,
            OrderNotFoundException, InvalidVolumeException {
      if (!isValidTransition(ms)) {
        throw new InvalidMarketStateTransitionException("The market state transition: " +
                ms + "; is invalid, current market state is: " + state);
      }
      state = ms;
      MessagePublisher.getInstance().publishMarketMessage(
              new MarketMessage(state));
      if (state.equals(MarketState.OPEN)) {
        for (Entry<String, ProductBook> row : allBooks.entrySet()) {
          row.getValue().openMarket();
        }
      }
      if (state.equals(MarketState.CLOSED)) {
        for (Entry<String, ProductBook> row : allBooks.entrySet()) {
          row.getValue().closeMarket();
        }
      }
    }
    
    public synchronized void createProduct(String product)
          throws DataValidationException, ProductAlreadyExistsException,
            ProductBookException, ProductBookSideException,
            InvalidProductBookSideValueException, tradeprocessing.productbook.exceptions.InvalidProductBookSideValueException {
      if (product == null || product.isEmpty()) {
        throw new DataValidationException("Product symbol cannot be "
                + "null or empty.");
      }
      if (allBooks.containsKey(product)) {
        throw new ProductAlreadyExistsException("Product " + product +
                " already exists in the ProductBook.");
      }
      allBooks.put(product, new ProductBook(product));
    }
    
    public synchronized void submitQuote(Quote q)
          throws InvalidMarketStateException, NoSuchProductException,
          InvalidVolumeException, DataValidationException,
          InvalidMessageException {
        if (state.equals(MarketState.CLOSED)) {
          throw new InvalidMarketStateException("Marekt is closed!");
        }
        if (!allBooks.containsKey(q.getProduct())) {
          throw new NoSuchProductException("Product does not exist in any book.");
        }
        allBooks.get(q.getProduct()).addToBook(q);
    }
     
    public synchronized String submitOrder(Order o)
          throws InvalidMarketStateException, NoSuchProductException,
          InvalidMessageException, InvalidVolumeException {
        if (state.equals(MarketState.CLOSED)) {
          throw new InvalidMarketStateException("Marekt is closed!");
        }
        if (state.equals(MarketState.PREOPEN) && o.getPrice().isMarket()) {
          throw new InvalidMarketStateException("Marekt is pre-open, cannot submit"
                  + " MKT orders at this time.");
        }
        if (!allBooks.containsKey(o.getProduct())) {
          throw new NoSuchProductException("Product does not exist in any book.");
        }
        allBooks.get(o.getProduct()).addToBook(o);
        return o.getId();
    }
    
    public synchronized void submitOrderCancel(String product, BookSide side,
          String orderId) throws InvalidMarketStateException,
          NoSuchProductException, InvalidMessageException,
          OrderNotFoundException, InvalidVolumeException {
        if (state.equals(MarketState.CLOSED)) {
          throw new InvalidMarketStateException("Marekt is closed!");
        }
        if (!allBooks.containsKey(product)) {
          throw new NoSuchProductException("Product does not exist in any book.");
        }
        allBooks.get(product).cancelOrder(side, orderId);
    }
    
    public synchronized void submitQuoteCancel(String userName, String product)
          throws InvalidMarketStateException, NoSuchProductException,
          InvalidMessageException {
        if (state.equals(MarketState.CLOSED)) {
          throw new InvalidMarketStateException("Marekt is closed!");
        }
        if (!allBooks.containsKey(product)) {
          throw new NoSuchProductException("Product does not exist in any book.");
        }
        allBooks.get(product).cancelQuote(userName);
    }
}
