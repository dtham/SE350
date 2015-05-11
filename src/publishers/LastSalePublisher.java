package publishers;

import java.util.ArrayList;
import java.util.HashMap;
import price.Price;
import price.PriceFactory;
import price.exceptions.InvalidPriceOperation;
import publishers.exceptions.AlreadySubscribedException;
import publishers.exceptions.InvalidPublisherOperation;
import publishers.exceptions.NotSubscribedException;
import tradable.exceptions.InvalidVolumeException;
import client.User;
import client.exceptions.EmptyParameterException;

public class LastSalePublisher implements Publisher 
{
	private SubscriptionHandler subscriptionHandler;
	private HashMap<String, ArrayList<User>> subscriptions = new HashMap<String, ArrayList<User>>();
	private static LastSalePublisher LastSalePublisherInstance = null;
	private LastSalePublisher() throws InvalidPublisherOperation { subscriptionHandler = SubscriptionHandlerFactory.makeSubscriptionHandler(new SubscriptionHandler()); };
	
	
	public static LastSalePublisher getInstance() throws InvalidPublisherOperation
	{
		if (LastSalePublisherInstance == null)
		{
			LastSalePublisherInstance = new LastSalePublisher();
		}
		return LastSalePublisherInstance;
	}
	
	
	public synchronized void publishLastSale(String product, Price p, int v) throws InvalidPriceOperation, InvalidVolumeException, InvalidPublisherOperation, EmptyParameterException
	{
		if (p == null)
		{
			p = PriceFactory.makeLimitPrice(0);
		}
		if (subscriptions.get(product) != null)
		{
			for (User u : subscriptions.get(product))
			{
				u.acceptLastSale(product, p, v);			
			}
		}
		TickerPublisher.getInstance().publishTicker(product, p);
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