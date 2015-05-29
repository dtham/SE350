package publishers.message;

import constants.GlobalConstants.MarketState;
import publishers.message.exceptions.InvalidMessageException;

public class MarketMessage implements StateOfMarket{
    
    private StateOfMarket marketMessageImpl;
    
    public MarketMessage(MarketState state) throws InvalidMessageException{
        marketMessageImpl = MessageFactory.createMarketMessageImpl(state);
    }
    
    @Override
    public MarketState getState() {
        return marketMessageImpl.getState();
    }
	
	
}
