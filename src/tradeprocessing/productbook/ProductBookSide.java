
package tradeprocessing.productbook;

/**
 *
 * @author Daryl's
 */

import constants.GlobalConstants.BookSide;

import java.util.*;

import price.Price;
import price.PriceFactory;
import price.exceptions.InvalidPriceOperation;
import tradable.Tradable;
import tradable.TradableDTO;
import tradable.exceptions.InvalidVolumeException;
import publishers.MessagePublisher;
import publishers.message.CancelMessage;
import publishers.message.FillMessage;
import publishers.message.exceptions.InvalidMessageException;
import publishers.message.exceptions.InvalidPublisherOperation;
import tradeprocessing.tradeprocessor.TradeProcessor;
import tradeprocessing.tradeprocessor.TradeProcessorFactory;
import tradeprocessing.productbook.exceptions.ProductBookSideException;
import tradeprocessing.productbook.exceptions.InvalidProductBookSideValueException;
import tradeprocessing.productbook.exceptions.OrderNotFoundException;


public class ProductBookSide {
    
    ProductBookSide self = this;
    private BookSide side;
    private Map<Price, ArrayList<Tradable>> bookEntries;
    private ArrayList<Price> removeBookEntryKeys = new ArrayList<>();
    private TradeProcessor processor;
    private ProductBook parent;
    
    public ProductBookSide(ProductBook p, BookSide s)
          throws ProductBookSideException, InvalidProductBookSideValueException, tradeprocessing.tradeprocessor.exceptions.InvalidProductBookSideValueException {
      bookEntries = new HashMap<>();
      setBookSide(s);
      setParentProductBook(p);
      processor = TradeProcessorFactory.createTradeProcessor("price-time", self);
    }
    
    private void setBookSide(BookSide s) throws ProductBookSideException {
        if (!(s.equals(BookSide.BUY) || s.equals(BookSide.SELL))) {
          throw new ProductBookSideException("BookSide: " + s + " is invalid.");
        }
        side = s;
    }
    
    private void setParentProductBook(ProductBook p) throws ProductBookSideException {
        if (p == null) {
          throw new ProductBookSideException("Parent ProductBook cannot be null.");
        }
        parent = p;
    }
    
    public synchronized ArrayList<TradableDTO>
          getOrdersWithRemainingQty(String userName) {
        ArrayList<TradableDTO> a = new ArrayList<>();
        for (Map.Entry<Price, ArrayList<Tradable>> row : bookEntries.entrySet()) {
          for (Tradable t : row.getValue()) {
            if (t.getUser().equals(userName) &&
                    t.getRemainingVolume() > 0) {
              a.add(new TradableDTO(t.getProduct(), t.getPrice(), t.getOriginalVolume(),
                      t.getRemainingVolume(), t.getCancelledVolume(), t.getUser(),
                      t.getSide(), false, t.getId()));
            }
          }
        }
        return a;
    }
    
          
    public synchronized ArrayList<Tradable> getEntriesAtTopOfBook() {
        if (bookEntries.isEmpty()) { 
            return null; }
        ArrayList<Price> sorted = sortPrices();
        return bookEntries.get(sorted.get(0));
    }
    
    public synchronized String[] getBookDepth() {
        if (bookEntries.isEmpty()) {
          return new String[]{ "<Empty>"};
        }
        ArrayList<String> str = new ArrayList<>();
        String[] s = new String[bookEntries.size()];
        ArrayList<Price> sorted = sortPrices();
        for (Price p : sorted) {
          ArrayList<Tradable> tradeable = bookEntries.get(p);
          int sum = 0;
          for (Tradable t : tradeable) {
            sum += t.getRemainingVolume();
          }
          str.add(p + " x " + sum);
        }
        return str.toArray(s);
    }
    
    synchronized ArrayList<Tradable> getEntriesAtPrice(Price price) {
        if (!bookEntries.containsKey(price)) { return null; }
        return bookEntries.get(price);
    }
    
    public synchronized boolean hasMarketPrice(){
        return bookEntries.containsKey(PriceFactory.makeMarketPrice());
    }
    
    public synchronized boolean hasOnlyMarketPrice(){
        return (bookEntries.size() == 1) && bookEntries.containsKey(PriceFactory.makeMarketPrice());
    }
    
    public synchronized Price topOfBookPrice(){
        if (bookEntries.isEmpty()) {return null;}
        ArrayList<Price> sorted = sortPrices();
        return sorted.get(0);
    }
    
    public synchronized int topOfBookVolume(){
        if(bookEntries.isEmpty()){return 0;}
        ArrayList<Price> sorted = sortPrices();
        ArrayList<Tradable> tradables = bookEntries.get(sorted.get(0));
        int a = 0; 
        for (Tradable t: tradables){
            a += t.getRemainingVolume();
        }
        return a;
    }
    
    public synchronized boolean isEmpty(){
        return bookEntries.isEmpty();
    }
    
    public synchronized void cancelAll()
          throws InvalidMessageException, OrderNotFoundException, InvalidVolumeException {   
        ArrayList<Price> prices = new ArrayList<>(bookEntries.keySet());
        HashMap<Price, ArrayList<Tradable>> tempHash = new HashMap<>(bookEntries);
        for (Price p : prices) {
          ArrayList<Tradable> tempList = new ArrayList<>(tempHash.get(p));
          for (Tradable t: tempList) {
            if (t.isQuote()) {
              submitQuoteCancel(t.getUser());
            } else {
              submitOrderCancel(t.getId());
            }
          }
        }
        removeBookEntryEmptyKeys();
    }
    
    public synchronized TradableDTO removeQuote(String user) {
        TradableDTO quote = null;
        for (Map.Entry<Price, ArrayList<Tradable>> row : bookEntries.entrySet()) {
          ListIterator<Tradable> iterator = row.getValue().listIterator();
          int size = row.getValue().size();
          if (size == 1) {
            removeBookEntryKeys.add(row.getKey());
          }
          while (iterator.hasNext()) {
            Tradable t = iterator.next();
            if (t.isQuote() && t.getUser().equals(user)) {
              quote = new TradableDTO(t.getProduct(), t.getPrice(), t.getOriginalVolume(),
                      t.getRemainingVolume(), t.getCancelledVolume(), t.getUser(),
                      t.getSide(), false, t.getId());
              iterator.remove();
            }
          }
        }
        return quote;
    }
    
    public synchronized final void submitOrderCancel(String orderId)
          throws InvalidMessageException, OrderNotFoundException, InvalidVolumeException {
        boolean isFound = false;
        for (Map.Entry<Price, ArrayList<Tradable>> row : bookEntries.entrySet()) {
          ListIterator<Tradable> iterator = row.getValue().listIterator();
          int size = row.getValue().size();
          if (size == 1) {
            removeBookEntryKeys.add(row.getKey());
          }
          while (iterator.hasNext()) {
            Tradable t = iterator.next();
            if (t.getId().equals(orderId)) {
              isFound = true;
              try {
				MessagePublisher.getInstance().publishCancel(new CancelMessage(
				          t.getUser(), t.getProduct(), t.getPrice(),
				          t.getRemainingVolume(), "Canceling order with order ID: ",
				          t.getSide(), t.getId()));
			} catch (InvalidPriceOperation e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
                      addOldEntry(t);
            }
          }
        }
        if (!isFound) {
          parent.checkTooLateToCancel(orderId);
        }
    }
    
    public synchronized final void submitQuoteCancel(String userName)
          throws InvalidMessageException {
        TradableDTO quote = removeQuote(userName);
        if (quote != null) {
          try {
			MessagePublisher.getInstance().publishCancel(new CancelMessage(
			          quote.user, quote.product, quote.price, quote.remainingVolume,
			          "Quote ",quote.side,
			          quote.id));
		} catch (InvalidPriceOperation e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        }
    }
    
    public synchronized final void removeBookEntryEmptyKeys() {
        for (Price key : removeBookEntryKeys) {
          bookEntries.remove(key);
        }
        removeBookEntryKeys = new ArrayList<>();
    }
    
    public final void addOldEntry(Tradable t) throws InvalidVolumeException {
        parent.addOldEntry(t);
    }
    
    public synchronized final void addToBook(Tradable trd) {
        if (bookEntries.containsKey(trd.getPrice())) {
          bookEntries.get(trd.getPrice()).add(trd);
        } else {
          ArrayList<Tradable> l = new ArrayList<>();
          l.add(trd);
          bookEntries.put(trd.getPrice(), l);
        }
    }
    
    public HashMap<String, FillMessage> tryTrade(Tradable trd)
          throws InvalidMessageException, InvalidVolumeException, InvalidPublisherOperation {
        HashMap<String, FillMessage> allFills;
        if (side.equals(BookSide.BUY)) {
          allFills = trySellAgainstBuySideTrade(trd);
        } else {
          allFills = tryBuyAgainstSellSideTrade(trd);
        }
        for (Map.Entry<String, FillMessage> row : allFills.entrySet()) {
          MessagePublisher.getInstance().publishFill(row.getValue());
        }
        return allFills;
    }
    
    public synchronized HashMap<String, FillMessage>
            trySellAgainstBuySideTrade(Tradable trd)
            throws InvalidMessageException, InvalidVolumeException, InvalidPublisherOperation {
      HashMap<String, FillMessage> allFills = new HashMap<>();
      HashMap<String, FillMessage> fillMsgs = new HashMap<>();
      while((trd.getRemainingVolume() > 0 && !bookEntries.isEmpty()) &&
              (trd.getPrice().isMarket() ||
              trd.getPrice().lessOrEqual(topOfBookPrice()))) {
        HashMap<String, FillMessage> temp = processor.doTrade(trd);
        fillMsgs = mergeFills(fillMsgs, temp);
      }
      allFills.putAll(fillMsgs);
      return allFills;
    }
            
    public synchronized HashMap<String, FillMessage>
          tryBuyAgainstSellSideTrade(Tradable trd)
          throws InvalidMessageException, InvalidVolumeException, InvalidPublisherOperation {
        HashMap<String, FillMessage> allFills = new HashMap<>();
        HashMap<String, FillMessage> fillMsgs = new HashMap<>();
        while((trd.getRemainingVolume() > 0 && !bookEntries.isEmpty()) &&
                (trd.getPrice().isMarket() ||
                trd.getPrice().greaterOrEqual(topOfBookPrice()))) {
          HashMap<String, FillMessage> temp = processor.doTrade(trd);
          fillMsgs = mergeFills(fillMsgs, temp);
        }
        allFills.putAll(fillMsgs);
        return allFills;
    }
          
    private HashMap<String, FillMessage> mergeFills(
          HashMap<String, FillMessage> existing,
          HashMap<String, FillMessage> newOnes) throws InvalidMessageException {
        if (existing.isEmpty()) {
          return new HashMap<>(newOnes);
        }
        HashMap<String, FillMessage> results = new HashMap<>(existing);
        for (String key : newOnes.keySet()) { 
          if (!existing.containsKey(key)) { 
            results.put(key, newOnes.get(key)); 
          } else { 
              FillMessage fm = results.get(key); 
            fm.setVolume(newOnes.get(key).getVolume()); 
            fm.setDetails(newOnes.get(key).getDetails()); 
          }
        }
        return results;
    }
    
     public synchronized void clearIfEmpty(Price p) {
         if (bookEntries.get(p).isEmpty()) {
            bookEntries.remove(p);
        }
    }
    
    public synchronized void removeTradeable(Tradable t) {
        ArrayList<Tradable> entries = bookEntries.get(t.getPrice());
        if (entries == null) { return; }
        boolean removeOp = entries.remove(t);
        if (!removeOp) { return; }
        if (entries.isEmpty()) {
          clearIfEmpty(t.getPrice());
        }
    }
    private synchronized ArrayList<Price> sortPrices() {
        ArrayList<Price> sorted = new ArrayList<>(bookEntries.keySet());
        Collections.sort(sorted);
        if (side.equals(BookSide.BUY)) {
            Collections.reverse(sorted);
        }
        return sorted;
    }      
    
    
}
