package publishers;

import price.Price;
import price.exceptions.InvalidPriceOperation;
import publishers.exceptions.InvalidPublisherOperation;
import constants.GlobalConstants.BookSide;

// Sohaib R. Khan
// Cancel Message 
// 1A

 
public class CancelMessage implements Message, Comparable<CancelMessage>
{
	private String user;
	private String product;
	private Price price;
	private int volume;
	private String details;
	private BookSide side;
	private String id;
	
	
	public CancelMessage(String username, String productname, Price p, int vol, String d, BookSide s, String i) throws InvalidPriceOperation, InvalidPublisherOperation
	{
		setUser(username);
		setProduct(productname);
		setPrice(p);
		setVolume(vol);
		setDetails(d);
		setSide(s);
		setId(i);
	}
	
	/**
	 * Sets the String username of the user whose order or quote-side is being cancelled
	 * 
	 * @param username the user's name
	 * @throws InvalidPublisherOperation if username is null or empty 
	 */
	private void setUser(String username) throws InvalidPublisherOperation
	{
		if (username == null || username.isEmpty())
		{
			throw new InvalidPublisherOperation("User cannot be null or empty.");
		}
		else
		{
			user = username;
		}
	}

	
	private void setProduct(String productname) throws InvalidPublisherOperation
	{
		if (productname == null || productname.isEmpty())
		{
			throw new InvalidPublisherOperation("Product cannot be null or empty.");
		}
		else
		{
			product = productname;
		}
	}
	

	private void setPrice(Price p) throws InvalidPublisherOperation
	{
		if (p == null)
		{
			throw new InvalidPublisherOperation("Price cannot be null.");
		}
		else
		{
			price = p;
		}
	}
	

	private void setVolume(int vol) throws InvalidPublisherOperation
	{
		if (vol < 0)
		{
			throw new InvalidPublisherOperation("Volume cannot be negative.");
		}
		else
		{
			volume = vol;
		}
	}
	

	private void setDetails(String d) throws InvalidPublisherOperation
	{
		if (d == null)
		{
			throw new InvalidPublisherOperation("Details cannot be null.");
		}
		else
		{
			details = d;
		}
	}
	

	private void setSide(BookSide s) throws InvalidPublisherOperation
	{
		if ((s.equals(BookSide.BUY)) || (s.equals(BookSide.SELL)))
		{
			side = s;
		}
		else
		{
			throw new InvalidPublisherOperation("Side must be valid.");
		}
	}
	

	private void setId(String i) throws InvalidPublisherOperation
	{
		if (i == null)
		{
			throw new InvalidPublisherOperation("Id cannot be null.");
		}
		else
		{
			id = i;
		}
	}
	
	
	public String getUser()
	{
		return user;
	}
	
	
	public String getProduct()
	{
		return product;
	}
	
	
	public Price getPrice()
	{
		return price;
	}
	

	public int getVolume()
	{
		return volume;
	}
	

	public String getDetails()
	{
		return details;
	}
	

	public BookSide getSide()
	{
		return side;
	}
	

	public String getId()
	{
		return id;
	}


	public int compareTo(CancelMessage cm)
	{
		return price.compareTo(cm.getPrice());
	}
	

	public String toString()
	{
		return "User: " + user + ", Product: " + product + ", Price: " + price + ", Volume: " + volume + 
		", Details: " + details + ", Side: " + side + ", Id: " + id;
	}
}