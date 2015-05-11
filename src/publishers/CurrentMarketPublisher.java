package publishers;

import java.util.ArrayList;
import java.util.HashMap;
import price.Price;
import price.PriceFactory;
import price.exceptions.InvalidPriceOperation;
import publishers.exceptions.AlreadySubscribedException;
import publishers.exceptions.InvalidPublisherOperation;
import publishers.exceptions.NotSubscribedException;
import client.User;

public class CurrentMarketPublisher implements Publisher 
{
	private SubscriptionHandler subscriptionHandler;
	private HashMap<String, ArrayList<User>> 
	subscriptions = new HashMap<String, ArrayList<User>>();
	private static CurrentMarketPublisher CurrentMarketPublisherInstance = null;
	private CurrentMarketPublisher() throws InvalidPublisherOperation { subscriptionHandler = SubscriptionHandlerFactory.makeSubscriptionHandler(new SubscriptionHandler()); };
	
	
	public static CurrentMarketPublisher getInstance() throws InvalidPublisherOperation
	{
		if (CurrentMarketPublisherInstance == null)
		{
			CurrentMarketPublisherInstance = new CurrentMarketPublisher();
		}
		return CurrentMarketPublisherInstance;
	}
	
	
	public synchronized void publishCurrentMarket(MarketDataDTO md) throws InvalidPriceOperation
	{
		String marketProduct = md.product;
		Price marketBuyPrice = md.buyPrice;
		int marketBuyVolume = md.buyVolume;
		Price marketSellPrice = md.sellPrice;
		int marketSellVolume = md.sellVolume;
		if (marketBuyPrice == null)
		{
			marketBuyPrice = PriceFactory.makeLimitPrice(0);
		}
		if (marketSellPrice == null)
		{
			marketSellPrice = PriceFactory.makeLimitPrice(0);
		}
		if (subscriptions.get(marketProduct) != null)
		{
			for (User u : subscriptions.get(marketProduct))
			{
				u.acceptCurrentMarket(marketProduct, marketBuyPrice, marketBuyVolume, marketSellPrice, marketSellVolume);			
			}
		}
	}
	
	
	public synchronized void subscribe(User u, String product) throws AlreadySubscribedException
	{
		subscriptions.put(product, subscriptionHandler.subscribe(u, product, subscriptions.get(product)));
	}
	
	
	public synchronized void unSubscribe(User u, String product) throws NotSubscribedException
	{
		subscriptions.put(product, subscriptionHandler.unSubscribe(u, product, subscriptions.get(product)));
	}
}