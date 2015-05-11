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


public class TickerPublisher implements Publisher 
{
	private SubscriptionHandler subscriptionHandler;
	private HashMap<String, ArrayList<User>> subscriptions = new HashMap<String, ArrayList<User>>();
	private HashMap<String, Price> lastTicker = new HashMap<String, Price>();
	private static TickerPublisher TickerPublisherInstance = null;
	private TickerPublisher() throws InvalidPublisherOperation { subscriptionHandler = SubscriptionHandlerFactory.makeSubscriptionHandler(new SubscriptionHandler()); };
	
	
	public static TickerPublisher getInstance() throws InvalidPublisherOperation
	{
		if (TickerPublisherInstance == null)
		{
			TickerPublisherInstance = new TickerPublisher();
		}
		return TickerPublisherInstance;
	}
	
	
	public synchronized void publishTicker(String product, Price p) throws InvalidPriceOperation
	{
		char direction = ' ';
		if (lastTicker.containsKey(product))
		{
			if (p.greaterThan(lastTicker.get(product)))
			{
				direction = '↑';
			}
			else if (p.lessThan(lastTicker.get(product)))
			{
				direction = '↓';
			}
			else 
			{
				direction = '=';
			}
		}
		else
		{
			lastTicker.put(product, p);
			
		}
		if (p == null)
		{
			p = PriceFactory.makeLimitPrice(0);
		}
		if (subscriptions.get(product) != null)
		{
			for (User u : subscriptions.get(product))
			{
				u.acceptTicker(product, p, direction);			
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