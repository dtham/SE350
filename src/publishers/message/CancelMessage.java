package publishers.message;

import price.Price;
import price.exceptions.InvalidPriceOperation;
import publishers.exceptions.InvalidPublisherOperation;
import constants.GlobalConstants.BookSide;


 
public class CancelMessage implements GeneralMarketMessage, Comparable<CancelMessage>
{

    protected GeneralMarketMessage cancelMessageImpl;
    
	public CancelMessage(String userName, String productName, Price p, int vol, 
                String details, BookSide side, String id) throws InvalidPriceOperation, InvalidPublisherOperation
	{
            cancelMessageImpl = MessageFactory.createCancelMessageImpl(userName,
                productName, p, vol, details, side, id);
	}
	
        @Override
	public String getUser()
	{
		return cancelMessageImpl.getUser();
	}
	
	
	public String getProduct()
	{
		return cancelMessageImpl.getProduct();
	}
	
	
	public Price getPrice()
	{
		return cancelMessageImpl.getPrice();
	}
	

	public int getVolume()
	{
		return cancelMessageImpl.getVolume();
	}
	

	public String getDetails()
	{
		return cancelMessageImpl.getDetails();
	}
	

	public BookSide getSide()
	{
		return cancelMessageImpl.getSide();
	}
	

	public String getId()
	{
		return cancelMessageImpl.getId();
	}


	public int compareTo(CancelMessage cm)
	{
		return cancelMessageImpl.getPrice().compareTo(cm.getPrice());
	}
	

	public String toString()
	{
		return cancelMessageImpl.toString();
	}
}