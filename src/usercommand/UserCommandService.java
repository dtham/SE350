package usercommand;

import client.User;
import constants.GlobalConstants.BookSide;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import price.Price;
import publishers.CurrentMarketPublisher;
import publishers.LastSalePublisher;
import publishers.MessagePublisher;
import publishers.TickerPublisher;
import publishers.exceptions.MessagePublisherException;
import publishers.exceptions.PublisherExceptions;
import publishers.message.exceptions.InvalidMessageException;
import tradable.Order;
import tradable.Quote;
import tradable.TradableDTO;
import tradable.exceptions.InvalidVolumeException;
import tradable.exceptions.TradableException;
import tradeprocessing.productbook.exceptions.DataValidationException;
import tradeprocessing.productbook.exceptions.NoSuchProductException;
import tradeprocessing.productbook.exceptions.OrderNotFoundException;
import tradeprocessing.productbook.exceptions.ProductBookException;
import tradeprocessing.productbook.exceptions.ProductBookSideException;
import tradeprocessing.productservice.ProductService;
import tradeprocessing.productservice.exceptions.InvalidMarketStateException;
import tradeprocessing.productservice.exceptions.ProductServiceException;
import tradeprocessing.tradeprocessor.exceptions.TradeProcessorPriceTimeImplException;
import usercommand.exceptions.AlreadyConnectedException;
import usercommand.exceptions.InvalidConnectionIdException;
import usercommand.exceptions.UserNotConnectedException;
import usercommand.exceptions.EmptyParameterException;


public class UserCommandService {

  private volatile static UserCommandService instance;

  HashMap<String, Long> connectedUserIds;
  HashMap<String, User> connectedUsers;
  HashMap<String, Long> connectedTime;

  private UserCommandService() {
    connectedUserIds = new HashMap<>();
    connectedUsers = new HashMap<>();
    connectedTime = new HashMap<>();
  }

  public static UserCommandService getInstance() {
    if (instance == null) {
      synchronized(UserCommandService.class) {
        if (instance == null) {
          instance = new UserCommandService();
        }
      }
    }
    return instance;
  }

  private void verifyUser(String userName, long connId)
          throws UserNotConnectedException, InvalidConnectionIdException {
    if (!connectedUserIds.containsKey(userName)) {
      throw new UserNotConnectedException("User not connected to the system");
    }
    if (!connectedUserIds.get(userName).equals((Long) connId)) {
      throw new InvalidConnectionIdException("Connection ID is not valid");
    }
  }

  public synchronized long connect(User user) throws AlreadyConnectedException {
    if (connectedUserIds.containsKey(user.getUserName())) {
      throw new AlreadyConnectedException("User already connected to the"
              + " system.");
    }
    connectedUserIds.put(user.getUserName(), System.nanoTime());
    connectedUsers.put(user.getUserName(), user);
    connectedTime.put(user.getUserName(), System.currentTimeMillis());
    return connectedUserIds.get(user.getUserName());
  }

  public synchronized void disconnect(String userName, long connId)
          throws UserNotConnectedException, InvalidConnectionIdException {
    verifyUser(userName, connId);
    connectedUserIds.remove(userName);
    connectedUsers.remove(userName);
    connectedTime.remove(userName);
  }

  public String[][] getBookDepth(String userName, long connId, String product)
          throws UserNotConnectedException, InvalidConnectionIdException,
          NoSuchProductException, ProductServiceException {
    verifyUser(userName, connId);
    return ProductService.getInstance().getBookDepth(product);
  }

  public String getMarketState(String userName, long connId)
          throws UserNotConnectedException, InvalidConnectionIdException {
    verifyUser(userName, connId);
    return ProductService.getInstance().getMarketState().toString();
  }

  public synchronized ArrayList<TradableDTO> getOrdersWithRemainingQty(
          String userName, long connId, String product)
          throws UserNotConnectedException, InvalidConnectionIdException,
          ProductBookSideException, ProductBookException,
          ProductServiceException {
    verifyUser(userName, connId);
    return ProductService.getInstance().getOrdersWithRemainingQty(userName,
            product);
  }

  public ArrayList<String> getProducts(String userName, long connId)
          throws UserNotConnectedException, InvalidConnectionIdException {
    verifyUser(userName, connId);
    ArrayList<String> list = ProductService.getInstance().getProductList();
    Collections.sort(list);
    return list;
  }

  public String submitOrder(String userName, long connId, String product,
          Price price, int volume, BookSide side)
          throws UserNotConnectedException, InvalidConnectionIdException,
          InvalidVolumeException, TradableException,
          InvalidMarketStateException, NoSuchProductException,
          InvalidMessageException, ProductBookSideException,
          ProductBookException, ProductServiceException,
          TradeProcessorPriceTimeImplException {
    verifyUser(userName, connId);
    Order o = new Order(userName, product, price, volume, side);
    return ProductService.getInstance().submitOrder(o);
  }

  public void submitOrderCancel(String userName, long connId, String product,
          BookSide side, String orderId) throws UserNotConnectedException,
          InvalidConnectionIdException, InvalidMarketStateException,
          NoSuchProductException, InvalidMessageException,
          OrderNotFoundException, InvalidVolumeException,
          ProductBookSideException, ProductServiceException,
          ProductBookException {
    verifyUser(userName, connId);
    ProductService.getInstance().submitOrderCancel(product, side, orderId);
  }

  public void submitQuote(String userName, long connId, String product,
          Price bPrice, int bVolume, Price sPrice, int sVolume)
          throws UserNotConnectedException, InvalidConnectionIdException,
          InvalidVolumeException, TradableException,
          InvalidMarketStateException, NoSuchProductException,
          DataValidationException, InvalidMessageException,
          ProductBookSideException, ProductBookException,
          ProductServiceException, TradeProcessorPriceTimeImplException {
    verifyUser(userName, connId);
    Quote q = new Quote(userName, product, bPrice, bVolume, sPrice, sVolume);
    ProductService.getInstance().submitQuote(q);
  }

  public void submitQuoteCancel(String userName, long connId, String product)
          throws UserNotConnectedException, InvalidConnectionIdException,
          InvalidMarketStateException, NoSuchProductException,
          InvalidMessageException, ProductBookSideException,
          ProductBookException, ProductServiceException {
    verifyUser(userName, connId);
    ProductService.getInstance().submitQuoteCancel(userName, product);
  }

  public void subscribeCurrentMarket(String userName, long connId,
          String product) throws UserNotConnectedException,
          InvalidConnectionIdException, MessagePublisherException, PublisherExceptions {
    verifyUser(userName, connId);
    CurrentMarketPublisher.getInstance().subscribe(connectedUsers.get(userName),
            product);
  }

  public void subscribeLastSale(String userName, long connId, String product)
          throws UserNotConnectedException, InvalidConnectionIdException,
          MessagePublisherException, PublisherExceptions {
    verifyUser(userName, connId);
    LastSalePublisher.getInstance().subscribe(connectedUsers.get(userName),
            product);
  }

  public void subscribeMessages(String userName, long connId, String product)
          throws UserNotConnectedException, InvalidConnectionIdException,
          MessagePublisherException, PublisherExceptions {
    verifyUser(userName, connId);
    MessagePublisher.getInstance().subscribe(connectedUsers.get(userName),
            product);
  }

  public void subscribeTicker(String userName, long connId, String product)
          throws UserNotConnectedException, InvalidConnectionIdException,
          MessagePublisherException, PublisherExceptions {
    verifyUser(userName, connId);
    TickerPublisher.getInstance().subscribe(connectedUsers.get(userName),
            product);
  }

  public void unSubscribeCurrentMarket(String userName, long connId,
          String product) throws UserNotConnectedException,
          InvalidConnectionIdException, MessagePublisherException, PublisherExceptions {
    verifyUser(userName, connId);
    CurrentMarketPublisher.getInstance().unSubscribe(connectedUsers.get(userName),
            product);
  }

  public void unSubscribeLastSale(String userName, long connId,
          String product) throws UserNotConnectedException,
          InvalidConnectionIdException, MessagePublisherException, PublisherExceptions {
    verifyUser(userName, connId);
    LastSalePublisher.getInstance().unSubscribe(connectedUsers.get(userName),
            product);
  }

  public void unSubscribeTicker(String userName, long connId, String product)
          throws UserNotConnectedException, InvalidConnectionIdException,
          MessagePublisherException, PublisherExceptions {
    verifyUser(userName, connId);
    TickerPublisher.getInstance().unSubscribe(connectedUsers.get(userName),
            product);
  }

  public void unSubscribeMessages(String userName, long connId,
          String product) throws UserNotConnectedException,
          InvalidConnectionIdException, MessagePublisherException, PublisherExceptions {
    verifyUser(userName, connId);
    MessagePublisher.getInstance().unSubscribe(connectedUsers.get(userName),
            product);
  }
}