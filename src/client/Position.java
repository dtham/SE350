package client;

import client.exceptions.PositionException;
import constants.GlobalConstants.BookSide;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import price.Price;
import price.PriceFactory;
import price.exceptions.InvalidPriceOperation;


public class Position 
{
    HashMap<String, Integer> holdings;
    Price accountCosts;
    HashMap<String, Price> lastSales = new HashMap<>();
  
    public Position() {
      holdings = new HashMap<>();
      accountCosts = PriceFactory.makeLimitPrice(0);
    }

    public void updatePosition(String product, Price price,
            BookSide side, int volume) throws PositionException,
            InvalidPriceOperation {
      if (product == null || product.isEmpty()) {
        throw new PositionException("Argument product in updatePosition cannot"
                + " be empty.");
      }
      if (price == null) {
        throw new PositionException("Argument price in updatePosition cannot"
                + " be empty.");
      }
      int adjustedVolume = (side.equals(BookSide.BUY) ? volume : -volume);
      if (!holdings.containsKey(product)) {
        holdings.put(product, adjustedVolume);
      } else {
        int resultingVolume = holdings.get(product) + adjustedVolume;
        if (resultingVolume == 0) {
          holdings.remove(product);
        } else {
          holdings.put(product, resultingVolume);
        }
      }
      Price totalPrice = price.multiply(volume);
      if (side.equals(BookSide.BUY)) {
        accountCosts = accountCosts.subtract(totalPrice);
      } else {
        accountCosts = accountCosts.add(totalPrice);
      }
    }

    public void updateLastSale(String product, Price price)
            throws PositionException {
      if (product == null || product.isEmpty()) {
        throw new PositionException("Argument product in updateLastSale cannot"
                + " be empty.");
      }
      if (price == null) {
        throw new PositionException("Argument price in updateLastSale cannot"
                + " be empty.");
      }
      lastSales.put(product, price);
    }

    public int getStockPositionVolume(String product) throws PositionException {
      if (product == null || product.isEmpty()) {
        throw new PositionException("Argument product in getStockPositionVolume"
                + " cannot be empty.");
      }
      if (!holdings.containsKey(product)) { return 0; }
      return holdings.get(product);
    }

    public ArrayList<String> getHoldings() {
      ArrayList<String> h = new ArrayList<>(holdings.keySet());
      Collections.sort(h);
      return h;
    }

    public Price getStockPositionValue(String product)
            throws PositionException, InvalidPriceOperation {
      if (product == null || product.isEmpty()) {
        throw new PositionException("Argument product in getStockPositionValue"
                + " cannot be empty.");
      }
      if (!holdings.containsKey(product)) {
        return PriceFactory.makeLimitPrice(0);
      }
      Price lastPrice = lastSales.get(product);
      if (lastPrice == null) {
        lastPrice = PriceFactory.makeLimitPrice(0);
      }
      return lastPrice.multiply(holdings.get(product));
    }

    public Price getAccountCosts() {
      return accountCosts;
    }

    public Price getAllStockValue()
            throws InvalidPriceOperation, PositionException {
      Price sum = PriceFactory.makeLimitPrice(0);
      for (String key : holdings.keySet()) {
        sum = sum.add(getStockPositionValue(key));
      }
      return sum;
    }

    public Price getNetAccountValue()
            throws PositionException, InvalidPriceOperation {
      return getAllStockValue().add(getAccountCosts());
    }
  }