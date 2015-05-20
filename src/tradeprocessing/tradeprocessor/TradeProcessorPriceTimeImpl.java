/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tradeprocessing.tradeprocessor;


import java.util.ArrayList;
import java.util.HashMap;
import price.Price;
import publishers.message.FillMessage;
import publishers.message.exceptions.InvalidMessageException;
import publishers.message.exceptions.InvalidPublisherOperation;
import tradable.Tradable;
import tradable.exceptions.InvalidVolumeException;
import tradeprocessing.productbook.ProductBookSide;
import tradeprocessing.tradeprocessor.exceptions.InvalidProductBookSideValueException;


public class TradeProcessorPriceTimeImpl implements TradeProcessor {

    private HashMap<String, FillMessage> fillMessages = new HashMap<>();
    ProductBookSide parent;
    
    public TradeProcessorPriceTimeImpl(ProductBookSide pbs)
          throws InvalidProductBookSideValueException {
        setProductBookSide(pbs);
    }
    
    private void setProductBookSide(ProductBookSide pbs)
          throws InvalidProductBookSideValueException {
        if (pbs == null) {
          throw new InvalidProductBookSideValueException("ProductBookSide cannot be"
                  + " null.");
        }
        parent = pbs;
    }
    
    private String makeFillKey(FillMessage fm) {
        return fm.getUser() + fm.getID() + fm.getPrice();
    }
    
    private boolean isNewFill(FillMessage fm) {
        String key = makeFillKey(fm);
        if (fillMessages.containsKey(key)) { return true; }
        FillMessage oldFill = fillMessages.get(key);
        if (oldFill == null) { return true; }
        if (!oldFill.getSide().equals(fm.getSide())) { return true; }
        if (!oldFill.getID().equals(fm.getID())) { return true; }
        return false;
    }
    
    private void addFillMessage(FillMessage fm)
          throws InvalidMessageException {
        if (isNewFill(fm)) {
          String key = makeFillKey(fm);
          fillMessages.put(key, fm);
        } else {
          String key = makeFillKey(fm);
          FillMessage oldFill = fillMessages.get(key);
          oldFill.setVolume(fm.getVolume());
          oldFill.setDetails(fm.getDetails());
        }
    }
    
    @Override
    public HashMap<String, FillMessage> doTrade(Tradable trd) throws InvalidVolumeException, InvalidMessageException, InvalidPublisherOperation {
        fillMessages = new HashMap<>();
        ArrayList<Tradable> tradedOut = new ArrayList<>();
        ArrayList<Tradable> entriesAtPrice = parent.getEntriesAtTopOfBook();
        for (Tradable t : entriesAtPrice) {
          if (trd.getRemainingVolume() != 0) {
            if (trd.getRemainingVolume() >= t.getRemainingVolume()) {
              tradedOut.add(t);
              Price tPrice;
              if (t.getPrice().isMarket()) {
                tPrice = trd.getPrice();
              } else {
                tPrice = t.getPrice();
              }
              FillMessage tFill = new FillMessage(t.getUser(), t.getProduct(),
                      tPrice, t.getRemainingVolume(), "leaving " + 0 , t.getSide(),
                      t.getId());
              addFillMessage(tFill);
              FillMessage trdFill = new FillMessage (trd.getUser(), t.getProduct(),
                      tPrice, t.getRemainingVolume(), "leaving " +
                      (trd.getRemainingVolume() - t.getRemainingVolume()), 
                      trd.getSide(), trd.getId());
              addFillMessage(trdFill);
              trd.setRemainingVolume(trd.getRemainingVolume() - t.getRemainingVolume());
              t.setRemainingVolume(0);
              parent.addOldEntry(t);
            } else {
              int remainder = t.getRemainingVolume() - trd.getRemainingVolume();
              Price tPrice;
              if (t.getPrice().isMarket()) {
                tPrice = trd.getPrice();
              } else {
                tPrice = t.getPrice();
              }
              FillMessage tFill = new FillMessage(t.getUser(), t.getProduct(),
                      tPrice, trd.getRemainingVolume(), "leaving " +
                      remainder, t.getSide(), t.getId());
              addFillMessage(tFill);
              FillMessage trdFill = new FillMessage(trd.getUser(), t.getProduct(),
                      tPrice, trd.getRemainingVolume(),
                      "leaving " + 0, trd.getSide(), trd.getId());
              addFillMessage(trdFill);
              trd.setRemainingVolume(0);
              t.setRemainingVolume(remainder);
              parent.addOldEntry(trd);
            }
          } else {
            break;
          }
        }
        for (Tradable t : tradedOut) {
          entriesAtPrice.remove(t);
        }
        if (entriesAtPrice.isEmpty()) {
          parent.clearIfEmpty(parent.topOfBookPrice());
        }
        return fillMessages;
      }
        }
    
}
