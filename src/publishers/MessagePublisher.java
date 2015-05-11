package publishers;

import java.util.ArrayList;
import java.util.HashMap;

import price.exceptions.InvalidPriceOperation;
import publishers.exceptions.InvalidPublisherOperation;
import publishers.exceptions.AlreadySubscribedException;
import publishers.exceptions.NotSubscribedException;
import client.User;
import client.exceptions.EmptyParameterException;

public class MessagePublisher implements Publisher 
{
	private SubscriptionHandler subscriptionHandler;
	private HashMap<String, ArrayList<User>> subscriptions = new HashMap<String, ArrayList<User>>();
	private static MessagePublisher MessagePublisherInstance = null;	
	private MessagePublisher() throws InvalidPublisherOperation { subscriptionHandler = SubscriptionHandlerFactory.makeSubscriptionHandler(new SubscriptionHandler()); };
	
	public static MessagePublisher getInstance() throws InvalidPublisherOperation
	{
		if (MessagePublisherInstance == null)
		{
			MessagePublisherInstance = new MessagePublisher();
		}
		return MessagePublisherInstance;
	}
	
	
	public synchronized void publishCancel(CancelMessage cm) throws InvalidPublisherOperation
	{
		if (cm == null)
		{
			throw new InvalidPublisherOperation("CancelMessage cannot be null");
		}
		else
		{
			User u = findUserHelper(cm.getProduct(), cm.getUser());
			if (u != null)
			{
				u.acceptMessage(cm);
			}
		}
	}
	
	
	public synchronized void publishFill(FillMessage fm) throws InvalidPublisherOperation, InvalidPriceOperation, EmptyParameterException
	{
		if (fm == null)
		{
			throw new InvalidPublisherOperation("FillMessage cannot be null");
		}
		else
		{
			User u = findUserHelper(fm.getProduct(), fm.getUser());
			if (u != null)
			{
				u.acceptMessage(fm);
			}
		}
	}
	
	
	public synchronized void publishMarketMessage(MarketMessage mm) throws InvalidPublisherOperation
	{
		if (mm == null)
		{
			throw new InvalidPublisherOperation("MarketMessage cannot be null");
		}
		else
		{
			ArrayList<User> allUsers = new ArrayList<User>();
			
			//Get the list of all users 
			for (ArrayList<User> userGroup : subscriptions.values())
			{
				for (User user : userGroup)
				{
					if (!allUsers.contains(user))
					{
						allUsers.add(user);
					}
				}
			}
			
			//Send the message to all users 
			for (User user : allUsers)
			{
				user.acceptMarketMessage(mm.toString());
			}
		}
	}
	
	
	private User findUserHelper(String product, String user) throws InvalidPublisherOperation
	{
		if (product == null || user == null)
		{
			throw new InvalidPublisherOperation("Product or user cannot be null");
		}
		else
		{
			// find matching user 
			ArrayList<User> usersInCurrentProduct = subscriptions.get(product);
			if (usersInCurrentProduct != null)
			{
				for (User u : usersInCurrentProduct)
				{
					if (u.getUserName().equals(user))
					{
						return u;
					}
				}
			}
			return null;
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