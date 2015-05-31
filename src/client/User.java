package client;

import client.exceptions.EmptyParameterException;
import constants.GlobalConstants.BookSide;
import java.util.ArrayList;
import price.Price;
import price.exceptions.InvalidPriceOperation;
import publishers.exceptions.MessagePublisherException;
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
import usercommand.exceptions.AlreadyConnectedException;
import usercommand.exceptions.InvalidConnectionIdException;
import usercommand.exceptions.UserNotConnectedException;
import client.exceptions.PositionException;
import client.exceptions.TradableUserDataException;

public interface User {


  public String getUserName();

  
  public void acceptLastSale(String product, Price p, int v);

 
  public void acceptMessage(FillMessage fm);

  
  public void acceptMessage(CancelMessage cm);

  
  public void acceptMarketMessage(String message);

  
  public void acceptTicker(String product, Price p, char direction);

  
  public void acceptCurrentMarket(String product, Price bp, int bv, Price sp,
          int sv);
  
  public void connect() throws AlreadyConnectedException,
          UserNotConnectedException, InvalidConnectionIdException;
  
  public void disconnect()
          throws UserNotConnectedException, InvalidConnectionIdException;
  
  public void showMarketDisplay() throws UserNotConnectedException;
  
  public String submitOrder(String product, Price price,
          int volume, BookSide side) throws TradableUserDataException,
          UserNotConnectedException, InvalidConnectionIdException,
          InvalidVolumeException, TradableException,
          InvalidMarketStateException, NoSuchProductException,
          InvalidMessageException, ProductBookSideException,
          ProductBookException, ProductServiceException,
          TradeProcessorPriceTimeImplException;
  
  public void submitOrderCancel(String product, BookSide side, String orderId)
          throws UserNotConnectedException, InvalidConnectionIdException,
          InvalidMarketStateException, NoSuchProductException,
          InvalidMessageException, OrderNotFoundException,
          InvalidVolumeException, ProductBookSideException,
          ProductServiceException, ProductBookException;
  
  public void submitQuote(String product, Price buyPrice, int buyVolume,
          Price sellPrice, int sellVolume)
          throws UserNotConnectedException, InvalidConnectionIdException,
          InvalidVolumeException, TradableException,
          InvalidMarketStateException, NoSuchProductException,
          DataValidationException, InvalidMessageException,
          ProductBookSideException, ProductBookException,
          ProductServiceException, TradeProcessorPriceTimeImplException;
  
  public void submitQuoteCancel(String product)
          throws UserNotConnectedException, InvalidConnectionIdException,
          InvalidMarketStateException, NoSuchProductException,
          InvalidMessageException, ProductBookSideException,
          ProductBookException, ProductServiceException;
  
  public void subscribeCurrentMarket(String product)
          throws UserNotConnectedException, InvalidConnectionIdException,
          MessagePublisherException;
  
  public void subscribeLastSale(String product)
          throws UserNotConnectedException, InvalidConnectionIdException,
          MessagePublisherException;
  
  public void subscribeMessages(String product)
          throws UserNotConnectedException, InvalidConnectionIdException,
          MessagePublisherException;
  
  public void subscribeTicker(String product)
          throws UserNotConnectedException, InvalidConnectionIdException,
          MessagePublisherException;
  
  public void unSubscribeCurrentMarket(String product)
          throws UserNotConnectedException, InvalidConnectionIdException,
          MessagePublisherException;
  
  public void unSubscribeLastSale(String product)
          throws UserNotConnectedException, InvalidConnectionIdException,
          MessagePublisherException;  
  
  public void unSubscribeMessages(String product)
          throws UserNotConnectedException, InvalidConnectionIdException,
          MessagePublisherException;
  
  public void unSubscribeTicker(String product)
          throws UserNotConnectedException, InvalidConnectionIdException,
          MessagePublisherException;  
  
  public Price getAllStockValue() throws PositionException,
          InvalidPriceOperation, EmptyParameterException;
  
  public Price getAccountCosts(); 
  
  public Price getNetAccountValue()
          throws PositionException, InvalidPriceOperation, EmptyParameterException;  
  
  public String[][] getBookDepth(String product)
          throws UserNotConnectedException, InvalidConnectionIdException,
          NoSuchProductException, ProductServiceException;  
  
  public String getMarketState()
          throws UserNotConnectedException, InvalidConnectionIdException; 
  
  public ArrayList<TradableUserData> getOrderIds();  
  
  public ArrayList<String> getProductList();
  
  public Price getStockPositionValue(String sym)
          throws PositionException, InvalidPriceOperation,EmptyParameterException; 
  
  public int getStockPositionVolume(String product) throws PositionException,EmptyParameterException;
  
  public ArrayList<String> getHoldings();  
  
  public ArrayList<TradableDTO> getOrdersWithRemainingQty(String product)
          throws UserNotConnectedException, InvalidConnectionIdException,
          ProductBookSideException, ProductBookException,
          ProductServiceException;  
}