package client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import constants.GlobalConstants.BookSide;
import price.Price;
import price.exceptions.InvalidPriceOperation;
import publishers.CurrentMarketPublisher;
import publishers.LastSalePublisher;
import publishers.MessagePublisher;
import publishers.TickerPublisher;
import publishers.exceptions.AlreadySubscribedException;
import publishers.exceptions.InvalidPublisherOperation;
import publishers.exceptions.NotSubscribedException;
import tradable.Order;
import tradable.Quote;
import tradable.TradableDTO;
import tradable.exceptions.InvalidVolumeException;
import book.ProductService;
import book.exceptions.DataValidationException;
import book.exceptions.InvalidBookException;
import book.exceptions.InvalidMarketStateException;
import book.exceptions.NoSuchProductException;
import client.exceptions.EmptyParameterException;
import client.exceptions.InvalidConnectionIdException;
import client.exceptions.UserNotConnectedException;
import client.exceptions.AlreadyConnectedException;


public class UserCommandService
{
	private HashMap<String, Long> connectedUserIds = new HashMap<String, Long>();
	private HashMap<String, User> connectedUsers = new HashMap<String, User>();
	private HashMap<String, Long> connectedTime = new HashMap<String, Long>();
	private static UserCommandService UserCommandServiceInstance = null;
	private UserCommandService() {};
	
	
	public static UserCommandService getInstance()
	{
		if (UserCommandServiceInstance == null)
		{
			UserCommandServiceInstance = new UserCommandService();
		}
		return UserCommandServiceInstance;
	}
	

	private void verifyUser(String userName, long connectionId) throws EmptyParameterException, UserNotConnectedException, InvalidConnectionIdException
	{
		if ((userName == null) || (userName.length() < 1))
		{
			throw new EmptyParameterException("User name cannot be null or empty.");
		}
		else
		{
			if (!connectedUserIds.containsKey(userName))
			{
				throw new UserNotConnectedException("User is not connected.");
			}
			if (connectionId != connectedUserIds.get(userName))
			{
				throw new InvalidConnectionIdException("Invalid connection.");
			}
		}
	}
	
	
	public synchronized long connect(User user) throws EmptyParameterException, AlreadyConnectedException
	{
		if (user == null)
		{
			throw new EmptyParameterException("The User cannot be null.");
		}
		else
		{
			if (connectedUserIds.containsKey(user))
			{
				throw new AlreadyConnectedException("The User is already connected.");
			}
			connectedUsers.put(user.getUserName(), user);
                        connectedUserIds.put(user.getUserName(), System.nanoTime());
			connectedTime.put(user.getUserName(), System.currentTimeMillis());
			return connectedUserIds.get(user.getUserName());
		}
	}
	
	
	public synchronized void disConnect(String userName, long connectionId) throws EmptyParameterException, UserNotConnectedException, InvalidConnectionIdException
	{
		verifyUser(userName, connectionId);
		connectedUserIds.remove(userName);
		connectedUsers.remove(userName);
		connectedTime.remove(userName);
	}
	
	
	public String[][] getBookDepth(String userName, long connectionId, String product) throws EmptyParameterException, UserNotConnectedException, InvalidConnectionIdException, NoSuchProductException, tradable.exceptions.EmptyParameterException
	{
		verifyUser(userName, connectionId);
		return ProductService.getInstance().getBookDepth(product);
	}
	

	public String getMarketState(String userName, long connectionId) throws EmptyParameterException, UserNotConnectedException, InvalidConnectionIdException
	{
		verifyUser(userName, connectionId);
		return ProductService.getInstance().getMarketState().toString();
	}
	

	public synchronized ArrayList<TradableDTO> getOrdersWithRemainingQty(String userName, long connectionId, String product) throws EmptyParameterException, UserNotConnectedException, InvalidConnectionIdException, InvalidPriceOperation, InvalidVolumeException, tradable.exceptions.EmptyParameterException
	{
		verifyUser(userName, connectionId);
		return ProductService.getInstance().getOrdersWithRemainingQty(userName, product);
	}
	

	public ArrayList<String> getProducts(String userName, long connectionId) throws EmptyParameterException, UserNotConnectedException, InvalidConnectionIdException
	{
		verifyUser(userName, connectionId);
		ArrayList<String> productList = ProductService.getInstance().getProductList();
		Collections.sort(productList);
		return productList;
	}
	

	public String submitOrder(String userName, long connectionId, String product, Price price, int volume, BookSide side) throws EmptyParameterException, UserNotConnectedException, InvalidConnectionIdException, InvalidPriceOperation, InvalidVolumeException, tradable.exceptions.EmptyParameterException, InvalidMarketStateException, NoSuchProductException, InvalidPublisherOperation, InvalidBookException
	{
		verifyUser(userName, connectionId);
		Order order = new Order(userName, product, price, volume, side);
		String orderId = ProductService.getInstance().submitOrder(order);
		return orderId;
	}
	

	public void submitOrderCancel(String userName, long connectionId, String product, BookSide side, String orderId) throws EmptyParameterException, UserNotConnectedException, InvalidConnectionIdException, InvalidMarketStateException, NoSuchProductException, InvalidPublisherOperation, InvalidPriceOperation, InvalidBookException, InvalidVolumeException, tradable.exceptions.EmptyParameterException
	{
		verifyUser(userName, connectionId);
		ProductService.getInstance().submitOrderCancel(product, side, orderId);
	}
	
	
	public void submitQuote(String userName, long connectionId, String product, Price bPrice, int bVolume, Price sPrice, int sVolume) throws EmptyParameterException, UserNotConnectedException, InvalidConnectionIdException, InvalidPriceOperation, InvalidVolumeException, tradable.exceptions.EmptyParameterException, InvalidMarketStateException, NoSuchProductException, DataValidationException, InvalidPublisherOperation, InvalidBookException
	{
		verifyUser(userName, connectionId);
		Quote quote = new Quote(userName, product, bPrice, bVolume, sPrice, sVolume);
		ProductService.getInstance().submitQuote(quote);
	}
	
	
	public void submitQuoteCancel(String userName, long connectionId, String product) throws InvalidMarketStateException, NoSuchProductException, InvalidPriceOperation, InvalidVolumeException, InvalidPublisherOperation, tradable.exceptions.EmptyParameterException, EmptyParameterException, UserNotConnectedException, InvalidConnectionIdException
	{
		verifyUser(userName, connectionId);
		ProductService.getInstance().submitQuoteCancel(userName, product);
	}
	
	
	public void subscribeCurrentMarket(String userName, long connectionId, String product) throws EmptyParameterException, UserNotConnectedException, InvalidConnectionIdException, AlreadySubscribedException, InvalidPublisherOperation
	{
		verifyUser(userName, connectionId);
		CurrentMarketPublisher.getInstance().subscribe(connectedUsers.get(userName), product);
	}
	
	
	public void subscribeLastSale(String userName, long connectionId, String product) throws EmptyParameterException, UserNotConnectedException, InvalidConnectionIdException, AlreadySubscribedException, InvalidPublisherOperation
	{
		verifyUser(userName, connectionId);
		LastSalePublisher.getInstance().subscribe(connectedUsers.get(userName), product);
	}
	
	
	public void subscribeMessages(String userName, long connectionId, String product) throws EmptyParameterException, UserNotConnectedException, InvalidConnectionIdException, AlreadySubscribedException, InvalidPublisherOperation
	{
		verifyUser(userName, connectionId);
		MessagePublisher.getInstance().subscribe(connectedUsers.get(userName), product);
	}
	
	
	public void subscribeTicker(String userName, long connectionId, String product) throws EmptyParameterException, UserNotConnectedException, InvalidConnectionIdException, AlreadySubscribedException, InvalidPublisherOperation
	{
		verifyUser(userName, connectionId);
		TickerPublisher.getInstance().subscribe(connectedUsers.get(userName), product);
	}
	
	
	public void unSubscribeCurrentMarket(String userName, long connectionId, String product) throws EmptyParameterException, UserNotConnectedException, InvalidConnectionIdException, NotSubscribedException, InvalidPublisherOperation
	{
		verifyUser(userName, connectionId);
		CurrentMarketPublisher.getInstance().unSubscribe(connectedUsers.get(userName), product);
	}
	
	
	public void unSubscribeLastSale(String userName, long connectionId, String product) throws EmptyParameterException, UserNotConnectedException, InvalidConnectionIdException, NotSubscribedException, InvalidPublisherOperation
	{
		verifyUser(userName, connectionId);
		LastSalePublisher.getInstance().unSubscribe(connectedUsers.get(userName), product);
	}
	
	
	public void unSubscribeTicker(String userName, long connectionId, String product) throws EmptyParameterException, UserNotConnectedException, InvalidConnectionIdException, NotSubscribedException, InvalidPublisherOperation
	{
		verifyUser(userName, connectionId);
		TickerPublisher.getInstance().unSubscribe(connectedUsers.get(userName), product);
	}
	
	
	public void unSubscribeMessages(String userName, long connectionId, String product) throws EmptyParameterException, UserNotConnectedException, InvalidConnectionIdException, NotSubscribedException, InvalidPublisherOperation
	{
		verifyUser(userName, connectionId);
		MessagePublisher.getInstance().unSubscribe(connectedUsers.get(userName), product);
	}
}