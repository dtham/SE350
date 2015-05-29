package tradeprocessing.tradeprocessor;

import java.util.HashMap;
import publishers.message.FillMessage;
import publishers.message.exceptions.InvalidPublisherOperation;
import tradable.Tradable;
import tradable.exceptions.InvalidVolumeException;
import publishers.message.exceptions.InvalidMessageException;


public interface TradeProcessor 
{
	
	public HashMap<String, FillMessage> doTrade(Tradable trd) 
                throws InvalidVolumeException, InvalidMessageException, InvalidPublisherOperation;
}