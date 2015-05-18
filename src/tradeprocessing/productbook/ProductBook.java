/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tradeprocessing.productbook;

import constants.GlobalConstants.BookSide;
import constants.GlobalConstants.MarketState;
import java.util.*;
import price.Price;
import price.PriceFactory;
import tradable.Tradable;
import tradable.Order;
import tradable.Quote;
import tradable.TradableDTO;
import tradable.exceptions.InvalidVolumeException;
import publishers.CurrentMarketPublisher;
import publishers.LastSalePublisher;
import publishers.MessagePublisher;
import publishers.message.CancelMessage;
import publishers.message.FillMessage;
import publishers.message.MarketDataDTO;
import publishers.message.exceptions.InvalidMessageException;
import tradeprocessing.productbook.exceptions.ProductBookException;
import tradeprocessing.productbook.exceptions.OrderNotFoundException;
import tradeprocessing.productservice.ProductService;
/**
 *
 * @author Daryl's
 */
public class ProductBook {
    
    private String symbol;
    
    private ProductBookSide buySide;
    
    private ProductBookSide sellSide;
    
    private String lastCurrentMarket;
    
    private HashSet<String> userQuotes = new HashSet<>();
    
    private HashMap<Price, ArrayList<Tradable>> oldEntries = new HashMap<>();
    
    public ProductBook(String sym) throws ProductBookException{
        setSymbol(sym);
        buySide = new ProductBookSide(this, BookSide.BUY);
        sellSide = new ProductBookSide(this, BookSide.SELL);
    }
    
    private void setSymbol(String sym) throws ProductBookException{
        if (sym == null){
            throw new ProductBookException("Symbol cannot be null.");
        }
        symbol = sym;
    }
    
    public synchronized final ArrayList<TradableDTO>
            getOrdersWithRemainingQty(String userName){
        ArrayList<TradableDTO> t = new ArrayList<>;
        t.addAll(buySide.getOrdersWithRemainingQty(userName));
        t.addAll(sellSide.getOrdersWithRemainingQty(userName));
        return t;
    }
            
    public synchronized final void checkTooLateToCancel(String orderId)
          throws OrderNotFoundException, InvalidMessageException {
        boolean isFound = false;
        for(Map.Entry<Price, ArrayList<Tradeable>> row : oldEntries.entrySet()) {
          ListIterator<Tradeable> iterator = row.getValue().listIterator();
          while (iterator.hasNext()) {
            Tradeable t = iterator.next();
            if (t.getId().equals(orderId)) {
              isFound = true;
              MessagePublisher.getInstance().publishCancel(new CancelMessage(
                      t.getUser(), t.getProduct(), t.getPrice(),
                      // is this remaining volume or cancelled volume
                      t.getRemainingVolume(), "Too late to cancel order ID: " +
                      t.getId(), t.getSide(), t.getId()));
            }
          }
        }
        if (!isFound) {
          throw new OrderNotFoundException("The order with the"
                  + " specified order id: " + orderId + "; could not be found.");
        }
    }
    
    public synchronized final String[][] getBookDepth() {
        String[][] bd = new String[2][];
        bd[0] = buySide.getBookDepth();
        bd[1] = sellSide.getBookDepth();
        return bd;
    }
    
    public synchronized final MarketDataDTO getMarketData() {
        Price topBuyPrice = buySide.topOfBookPrice();
        Price topSellPrice = sellSide.topOfBookPrice();
        if (topBuyPrice == null) {
          topBuyPrice = PriceFactory.makeLimitPrice("0");
        }
        if (topSellPrice == null) {
          topSellPrice = PriceFactory.makeLimitPrice("0");
        }
        int bestBuySideVolume = buySide.topOfBookVolume();
        int bestSellSideVolume = sellSide.topOfBookVolume();
        return new MarketDataDTO(symbol, topBuyPrice, bestBuySideVolume,
                topSellPrice, bestSellSideVolume);
    }
    
    public synchronized final void addOldEntry(Tradeable t)
          throws InvalidVolumeException {
        if (!oldEntries.containsKey(t.getPrice())) {
          oldEntries.put(t.getPrice(), new ArrayList<Tradeable>());
        }
        t.setCancelledVolume(t.getRemainingVolume());
        t.setRemainingVolume(0);
        oldEntries.get(t.getPrice()).add(t);
    }
    
    public synchronized final void openMarket()
          throws InvalidMessageException, InvalidVolumeException {
        Price buyPrice = buySide.topOfBookPrice();
        Price sellPrice = sellSide.topOfBookPrice();
        if (buyPrice == null || sellPrice == null) { return; }
        while (buyPrice.greaterOrEqual(sellPrice) || buyPrice.isMarket()
                || sellPrice.isMarket()) {
          ArrayList<Tradeable> topOfBuySide = buySide.getEntriesAtPrice(buyPrice);
          HashMap<String, FillMessage> allFills = null;
          ArrayList<Tradeable> toRemove = new ArrayList<>();
          for (Tradeable t : topOfBuySide) {
            allFills = sellSide.tryTrade(t);
            if (t.getRemainingVolume() == 0) {
              toRemove.add(t);
            }
          }
          for (Tradeable t : toRemove) {
            buySide.removeTradeable(t);
          }
          updateCurrentMarket();
          Price lastSalePrice = determineLastSalePrice(allFills);
          int lastSaleVolume = determineLastSaleQuantity(allFills);
          LastSalePublisher.getInstance().publishLastSale(symbol, lastSalePrice,
                  lastSaleVolume);
          buyPrice = buySide.topOfBookPrice();
          sellPrice = sellSide.topOfBookPrice();
          if (buyPrice == null || sellPrice == null) { break; }
        }
    }
    
    public synchronized final void closeMarket()
          throws InvalidMessageException, OrderNotFoundException,
            InvalidVolumeException {
      buySide.cancelAll();
      sellSide.cancelAll();
      updateCurrentMarket();
    }
    
    public synchronized final void cancelOrder(BookSide side, String orderId)
          throws InvalidMessageException, OrderNotFoundException,
            InvalidVolumeException {
      if (side.equals(BookSide.BUY)) {
        buySide.submitOrderCancel(orderId);
      } else {
        sellSide.submitOrderCancel(orderId);
      }
      updateCurrentMarket();
    }
    
    public synchronized final void cancelQuote(String userName)
          throws InvalidMessageException {
        buySide.submitQuoteCancel(userName);
        sellSide.submitQuoteCancel(userName);
        buySide.removeBookEntryEmptyKeys();
        sellSide.removeBookEntryEmptyKeys();
        updateCurrentMarket();
    }
    
    public synchronized final void addToBook(Quote q)
          throws InvalidVolumeException, DataValidationException,
          InvalidMessageException {
        if (q.getQuoteSide(BookSide.SELL).getPrice().lessOrEqual(
                q.getQuoteSide(BookSide.BUY).getPrice())) {
          throw new DataValidationException("Sell Price is less than or equal to"
                  + " buy price.");
        }
        if (q.getQuoteSide(BookSide.SELL).getPrice().lessOrEqual(
                PriceFactory.makeLimitPrice("0")) ||
                q.getQuoteSide(BookSide.BUY).getPrice().lessOrEqual(
                PriceFactory.makeLimitPrice("0"))) {
          throw new DataValidationException("Buy or Sell Price cannot be less than"
                  + " or equal to zero.");
        }
        if (q.getQuoteSide(BookSide.SELL).getOriginalVolume() <= 0 ||
                q.getQuoteSide(BookSide.BUY).getOriginalVolume() <= 0) {
          throw new DataValidationException("Volume of a Buy or Sell side quote"
                  + " cannot be less than or equal to zero,");
        }
        if (userQuotes.contains(q.getUserName())) {
          buySide.removeQuote(q.getUserName());
          sellSide.removeQuote(q.getUserName());
          updateCurrentMarket();
        }
        addToBook(BookSide.BUY, q.getQuoteSide(BookSide.BUY));
        addToBook(BookSide.SELL, q.getQuoteSide(BookSide.SELL));
        userQuotes.add(q.getUserName());
        updateCurrentMarket();
      }
     
    public synchronized final void addToBook(Order o)
          throws InvalidMessageException, InvalidVolumeException {
        addToBook(o.getSide(), o);
        updateCurrentMarket();
    }
    
    public synchronized final void updateCurrentMarket() {
        String var = buySide.topOfBookPrice() +
                String.valueOf(buySide.topOfBookVolume()) +
                sellSide.topOfBookPrice() +
                String.valueOf(sellSide.topOfBookVolume());
        if (!lastCurrentMarket.equals(var)) {
          MarketDataDTO current = new MarketDataDTO(symbol,
                  (buySide.topOfBookPrice() == null) ?
                  PriceFactory.makeLimitPrice("0")
                  : buySide.topOfBookPrice(),
                  buySide.topOfBookVolume(),
                  (sellSide.topOfBookPrice() == null) ?
                  PriceFactory.makeLimitPrice("0") : sellSide.topOfBookPrice(),
                  sellSide.topOfBookVolume());
          CurrentMarketPublisher.getInstance().publishCurrentMarket(current);
          lastCurrentMarket = var;
        }
      }
    
    private synchronized Price determineLastSalePrice(
          HashMap<String, FillMessage> fills) {
        ArrayList<FillMessage> msgs = new ArrayList<>(fills.values());
        Collections.sort(msgs);
        return msgs.get(0).getPrice();
    }
    
    private synchronized int determineLastSaleQuantity(
          HashMap<String, FillMessage> fills) {
        ArrayList<FillMessage> msgs = new ArrayList<>(fills.values());
        Collections.sort(msgs);
        return msgs.get(0).getVolume();
    }
    
    private synchronized void addToBook(BookSide side, Tradeable trd)
          throws InvalidMessageException, InvalidVolumeException {
        if (ProductService.getInstance().getMarketState().equals(
                MarketState.PREOPEN)) {
          if (side.equals(BookSide.BUY)) {
            buySide.addToBook(trd);
          } else {
            sellSide.addToBook(trd);
          }
          return;
        }
        HashMap<String, FillMessage> allFills = null;
        if (side.equals(BookSide.BUY)) {
          allFills = sellSide.tryTrade(trd);
        } else {
          allFills = buySide.tryTrade(trd);
        }
        if (allFills != null && !allFills.isEmpty()) {
          updateCurrentMarket();
          int diff = trd.getOriginalVolume() - trd.getRemainingVolume();
          Price lastSalePrice = determineLastSalePrice(allFills);
          LastSalePublisher.getInstance().publishLastSale(symbol,
                  lastSalePrice, diff);
        }
        if (trd.getRemainingVolume() > 0) {
          if (trd.getPrice().isMarket()) {
              MessagePublisher.getInstance().publishCancel(new CancelMessage(
                      trd.getUser(), trd.getProduct(), trd.getPrice(),
                      // is this remaining volume or cancelled volume
                      trd.getRemainingVolume(), "Canceling order with order ID: " +
                      trd.getId(), trd.getSide(), trd.getId()));
          } else {
            if (side.equals(BookSide.BUY)) {
              buySide.addToBook(trd);
            } else {
              sellSide.addToBook(trd);
            }
          }
        }
    }
}
