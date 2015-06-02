/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import client.exceptions.EmptyParameterException;
import client.exceptions.PositionException;
import client.exceptions.TradableUserDataException;
import client.exceptions.UserException;
import constants.GlobalConstants.BookSide;
import gui.UserDisplayManager;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import price.Price;
import price.exceptions.InvalidPriceOperation;
import publishers.exceptions.MessagePublisherException;
import publishers.exceptions.PublisherExceptions;
import publishers.message.CancelMessage;
import publishers.message.FillMessage;
import publishers.message.exceptions.InvalidMessageException;
import tradable.TradableDTO;
import tradable.exceptions.InvalidVolumeException;
import tradable.exceptions.TradableException;
import tradeprocessing.productbook.exceptions.DataValidationException;
import tradeprocessing.productbook.exceptions.NoSuchProductException;
import tradeprocessing.productbook.exceptions.OrderNotFoundException;
import tradeprocessing.productbook.exceptions.ProductBookException;
import tradeprocessing.productbook.exceptions.ProductBookSideException;
import tradeprocessing.productservice.exceptions.InvalidMarketStateException;
import tradeprocessing.productservice.exceptions.ProductServiceException;
import tradeprocessing.tradeprocessor.exceptions.TradeProcessorPriceTimeImplException;
import usercommand.UserCommandService;
import usercommand.exceptions.AlreadyConnectedException;
import usercommand.exceptions.InvalidConnectionIdException;
import usercommand.exceptions.UserNotConnectedException;

public class UserImpl implements User {

  private String userName;
  private long connectionId;
  ArrayList<String> stocks = null;
  ArrayList<TradableUserData> trades;
  Position position;
  private static final Logger log = Logger.getLogger(UserImpl.class.getName());
  UserDisplayManager udm;

  public UserImpl(String userName) throws UserException, InvalidPriceOperation {
    setUserName(userName);
    position = new Position();
    trades = new ArrayList<>();
  }

  private void setUserName(String name) throws UserException {
    if (name == null || name.isEmpty()) {
      throw new UserException("User name cannot be null or empty.");
    }
    userName = name;
  }
    
    
    @Override
    public String getUserName() {
        return userName;
    }

    @Override
    public void acceptLastSale(String product, Price p, int v) {
        udm.updateLastSale(product, p, v);
      try { 
          position.updateLastSale(product, p);
      } catch (PositionException ex) {
          Logger.getLogger(UserImpl.class.getName()).log(Level.SEVERE, null, ex);
      }
    }

    @Override
    public void acceptMessage(FillMessage fm) {
        try {
          Timestamp timestamp = new Timestamp(System.currentTimeMillis());
          String msg = "{" + timestamp.toString() + "} Fill Message: " +
                  fm.getSide() + " "+ fm.getVolume() + " " + fm.getProduct()
                  + " at " + fm.getPrice() + ", " + fm.getDetails()
                  + " [Tradeable Id: " + fm.getID() + "]";
          udm.updateMarketActivity(msg);
          position.updatePosition(fm.getProduct(), fm.getPrice(), fm.getSide(),
                  fm.getVolume());
        } catch (InvalidPriceOperation ex) {   
          Logger.getLogger(UserImpl.class.getName()).log(Level.SEVERE, null, ex);
      } catch (PositionException ex) {
          Logger.getLogger(UserImpl.class.getName()).log(Level.SEVERE, null, ex);
      }   
    }

    @Override
    public final void acceptMessage(CancelMessage cm) {
      try {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String msg = "{" + timestamp.toString() + "} Cancel Message: " +
                cm.getSide() + " " + cm.getVolume() + " " + cm.getProduct()
                + " at " + cm.getPrice() + ", " +  cm.getDetails() +
                " [Tradeable Id: " + cm.getID() + "]";
        udm.updateMarketActivity(msg);
      } catch(Exception e) {
        log.log(Level.SEVERE, null, e);
      }
    }    
    
    @Override
    public void acceptMarketMessage(String message) {
        try {
          udm.updateMarketState(message);
        } catch(Exception e) {
          log.log(Level.SEVERE, null, e);
        }      
    }

    @Override
    public void acceptTicker(String product, Price p, char direction) {
        try {
            udm.updateTicker(product, p, direction);
        } catch(Exception e) {
            log.log(Level.SEVERE, null, e);
        }    
    }

    @Override
    public void acceptCurrentMarket(String product, Price bp, int bv, Price sp, int sv) {  
        try {
          udm.updateMarketData(product, bp, bv, sp, sv);
        } catch(Exception e) {
          log.log(Level.SEVERE, null, e);
        }        
    }

    @Override
    public void connect() throws AlreadyConnectedException, UserNotConnectedException, InvalidConnectionIdException {
        connectionId = UserCommandService.getInstance().connect(this);
        stocks = UserCommandService.getInstance()
                .getProducts(userName, connectionId);        
    }

    @Override
    public void disconnect() throws UserNotConnectedException, InvalidConnectionIdException {
        UserCommandService.getInstance().disconnect(userName, connectionId);
    }

    @Override
    public void showMarketDisplay() throws UserNotConnectedException {
        if (stocks == null) {
          throw new UserNotConnectedException("User currently not connected.");
        }
        if (udm == null) {
          udm = new UserDisplayManager(this);
        }
        try {
            udm.showMarketDisplay();
        } catch (Exception ex) {
            Logger.getLogger(UserImpl.class.getName()).log(Level.SEVERE, null, ex);
      }
    }

    @Override
    public final String submitOrder(String product, Price price, int volume,
            BookSide side) throws TradableUserDataException,
            UserNotConnectedException, InvalidConnectionIdException,
            InvalidVolumeException, TradableException,
            InvalidMarketStateException, NoSuchProductException,
            InvalidMessageException, ProductBookSideException,
            ProductBookException, ProductServiceException,
            TradeProcessorPriceTimeImplException {
      String id = UserCommandService.getInstance().submitOrder(userName,
              connectionId, product, price, volume, side);
      trades.add(new TradableUserData(userName, product, side, id));
      return id;
    }
    
    @Override
    public final void submitOrderCancel(String product, BookSide side,
        String orderId) throws UserNotConnectedException,
          InvalidConnectionIdException, InvalidMarketStateException,
          NoSuchProductException, InvalidMessageException,
          OrderNotFoundException, InvalidVolumeException,
          ProductBookSideException, ProductServiceException,
          ProductBookException {
    UserCommandService.getInstance().submitOrderCancel(userName, connectionId,
            product, side, orderId);
  }

    @Override
    public final void submitQuote(String product, Price buyPrice,
                int buyVolume, Price sellPrice, int sellVolume)
          throws UserNotConnectedException, InvalidConnectionIdException,
          InvalidVolumeException, TradableException,
          InvalidMarketStateException, NoSuchProductException,
          DataValidationException, InvalidMessageException,
          ProductBookSideException, ProductBookException,
          ProductServiceException, TradeProcessorPriceTimeImplException {
    UserCommandService.getInstance().submitQuote(userName, connectionId,
            product, buyPrice, buyVolume, sellPrice, sellVolume);
  }
   
  @Override
    public final void submitQuoteCancel(String product)
            throws UserNotConnectedException, InvalidConnectionIdException,
            InvalidMarketStateException, NoSuchProductException,
            InvalidMessageException, ProductBookSideException,
            ProductBookException, ProductServiceException {
      UserCommandService.getInstance().submitQuoteCancel(userName, connectionId,
              product);
    }

    @Override
    public void subscribeCurrentMarket(String product) throws UserNotConnectedException, 
            InvalidConnectionIdException, MessagePublisherException {
      try {
          UserCommandService.getInstance().subscribeCurrentMarket(userName,
                  connectionId, product);
      } catch (PublisherExceptions ex) {
          Logger.getLogger(UserImpl.class.getName()).log(Level.SEVERE, null, ex);
      }
    }

    @Override
    public void subscribeLastSale(String product) throws UserNotConnectedException, 
            InvalidConnectionIdException, MessagePublisherException {
      try {
          UserCommandService.getInstance().subscribeLastSale(userName, connectionId,    
                  product);
      } catch (PublisherExceptions ex) {
          Logger.getLogger(UserImpl.class.getName()).log(Level.SEVERE, null, ex);
      }
    }

    @Override
    public void subscribeMessages(String product) throws UserNotConnectedException, 
            InvalidConnectionIdException, MessagePublisherException {
      try {
          UserCommandService.getInstance().subscribeMessages(userName, connectionId,
                  product);
      } catch (PublisherExceptions ex) {
          Logger.getLogger(UserImpl.class.getName()).log(Level.SEVERE, null, ex);
      }
    }

    @Override
    public void subscribeTicker(String product) throws UserNotConnectedException, 
            InvalidConnectionIdException, MessagePublisherException {
      try {
          UserCommandService.getInstance().subscribeTicker(userName, connectionId,
                  product);
      } catch (PublisherExceptions ex) {
          Logger.getLogger(UserImpl.class.getName()).log(Level.SEVERE, null, ex);
      }
    }

    @Override
    public void unSubscribeCurrentMarket(String product) throws UserNotConnectedException, 
            InvalidConnectionIdException, MessagePublisherException {
      try {
          UserCommandService.getInstance().unSubscribeCurrentMarket(userName,    
                  connectionId, product);
      } catch (PublisherExceptions ex) {
          Logger.getLogger(UserImpl.class.getName()).log(Level.SEVERE, null, ex);
      }
    }

    @Override
    public void unSubscribeLastSale(String product) throws UserNotConnectedException, 
            InvalidConnectionIdException, MessagePublisherException {
      try {
          UserCommandService.getInstance().subscribeLastSale(userName, connectionId,
                  product);
      } catch (PublisherExceptions ex) {
          Logger.getLogger(UserImpl.class.getName()).log(Level.SEVERE, null, ex);
      }
    }

    @Override
    public void unSubscribeMessages(String product) throws UserNotConnectedException, 
            InvalidConnectionIdException, MessagePublisherException {
      try {
          UserCommandService.getInstance().unSubscribeMessages(userName, connectionId,
                  product);
      } catch (PublisherExceptions ex) {
          Logger.getLogger(UserImpl.class.getName()).log(Level.SEVERE, null, ex);
      }
    }

    @Override
    public void unSubscribeTicker(String product) throws UserNotConnectedException, 
            InvalidConnectionIdException, MessagePublisherException {
      try {
          UserCommandService.getInstance().unSubscribeTicker(userName, connectionId,
                  product);
      } catch (PublisherExceptions ex) {
          Logger.getLogger(UserImpl.class.getName()).log(Level.SEVERE, null, ex);
      }
    }

    @Override
    public Price getAllStockValue() throws EmptyParameterException, PositionException, 
            InvalidPriceOperation {
        return position.getAllStockValue();
    }

    @Override
    public Price getAccountCosts() {
        return position.getAccountCosts();
    }

    @Override
    public Price getNetAccountValue() throws PositionException, InvalidPriceOperation,
            EmptyParameterException{
        return position.getNetAccountValue();
    }

    @Override
    public String[][] getBookDepth(String product) throws UserNotConnectedException, 
            InvalidConnectionIdException, NoSuchProductException, ProductServiceException {
        return UserCommandService.getInstance().getBookDepth(userName, connectionId,
            product);
    }

    @Override
    public String getMarketState() throws UserNotConnectedException, InvalidConnectionIdException {
        return UserCommandService.getInstance().getMarketState(userName,
            connectionId);
    }

    @Override
    public ArrayList<TradableUserData> getOrderIds() {
        return trades;
    }

    @Override
    public ArrayList<String> getProductList() {
        return stocks;
    }

    @Override
    public Price getStockPositionValue(String sym) throws PositionException, 
            InvalidPriceOperation, EmptyParameterException {
        return position.getStockPositionValue(sym);
    }

    @Override
    public int getStockPositionVolume(String product) throws PositionException,
            EmptyParameterException{
        return position.getStockPositionVolume(product);
    }

    @Override
    public ArrayList<String> getHoldings() {
        return position.getHoldings();
    }

    @Override
    public ArrayList<tradable.TradableDTO> getOrdersWithRemainingQty(String product) 
            throws UserNotConnectedException, InvalidConnectionIdException, 
            ProductBookSideException, ProductBookException, ProductServiceException {
        return UserCommandService.getInstance().getOrdersWithRemainingQty(userName,
            connectionId, product);
    }
    
}
