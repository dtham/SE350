package client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import client.exceptions.EmptyParameterException;
import constants.GlobalConstants.BookSide;
import price.Price;
import price.PriceFactory;
import price.exceptions.InvalidPriceOperation;


public class Position 
{
	HashMap<String, Integer> holdings = new HashMap<String, Integer>();
	Price accountCosts;
	HashMap<String, Price> lastSales = new HashMap<String, Price>();
	
	
	public Position() throws InvalidPriceOperation
	{
		accountCosts = PriceFactory.makeLimitPrice(0);
	}
	
	
	public void updatePosition(String product, Price price, BookSide side, int volume) throws InvalidPriceOperation, EmptyParameterException
	{
		if ((product == null) || (product.length() < 1) || (price == null) || (side == null))
		{
			throw new EmptyParameterException("Values cannot be null or empty.");
		}
		else
		{
			int adjustedVolume;
			if (side == BookSide.BUY)
			{
				adjustedVolume = volume;
			}
			else 
			{
				adjustedVolume = volume * (-1);
			}
			if (!holdings.containsKey(product))
			{
				holdings.put(product, adjustedVolume);			
			}
			else 
			{
				int stockOwn = holdings.get(product) + adjustedVolume;
				if (stockOwn == 0)
				{
					holdings.remove(product);
				}
				else
				{
					holdings.put(product, stockOwn);
				}
			}
			Price totalPrice = price.multiply(volume);
			if (side == BookSide.BUY)
			{
				accountCosts = accountCosts.subtract(totalPrice);
			}
			else 
			{
				accountCosts = accountCosts.add(totalPrice);
			}
		}
	}
	
	
	public void updateLastSale(String product, Price price) throws EmptyParameterException
	{
		if ((product == null) || (product.length() < 1) || (price == null))
		{
			throw new EmptyParameterException("Values cannot be null or empty.");
		}
		else
		{
			lastSales.put(product, price);
		}
	}
	

	public int getStockPositionVolume(String product) throws EmptyParameterException
	{
		if ((product == null) || (product.length() < 1))
		{
			throw new EmptyParameterException("Product cannot be null or empty.");
		}
		else
		{
			if (holdings.containsKey(product))
			{
				return holdings.get(product);
			}
			else
			{
				return 0;
			}
		}
	}
	

	public ArrayList<String> getHoldings()
	{
		ArrayList<String> h = new ArrayList<String>(holdings.keySet());
		Collections.sort(h);
		return h;
	}
	

	public Price getStockPositionValue(String product) throws InvalidPriceOperation, EmptyParameterException
	{
		if ((product == null) || (product.length() < 1))
		{
			throw new EmptyParameterException("Product cannot be null or empty.");
		}
		else
		{
			if (!holdings.containsKey(product))
			{
				return PriceFactory.makeLimitPrice(0);
			}
			else
			{
				Price lastSalePrice = lastSales.get(product);
				if (lastSalePrice == null)
				{
					return PriceFactory.makeLimitPrice(0);
				}
				Price positionValue = lastSalePrice.multiply(holdings.get(product));
				return positionValue;
			}
		}
	}
	

	public Price getAccountCosts()
	{
		return accountCosts;
	}
	

	public Price getAllStockValue() throws InvalidPriceOperation, EmptyParameterException
	{
		Price result = PriceFactory.makeLimitPrice(0);
		for (String holdingProduct : holdings.keySet())
		{
			result = result.add(getStockPositionValue(holdingProduct));
		}
		return result;
	}
	
	public Price getNetAccountValue() throws InvalidPriceOperation, EmptyParameterException
	{
		return getAllStockValue().add(accountCosts);
	}
}