/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package publishers.message;


import constants.GlobalConstants.MarketState;
import publishers.message.exceptions.InvalidMessageException;
/**
 *
 * @author Daryl's
 */
public class MarketMessageImpl implements StateOfMarket {
    
    private MarketState state;
    
    public MarketMessageImpl (MarketState state) throws InvalidMessageException{
        setState(state);
    }
    
    private void setState(MarketState state) throws InvalidMessageException{
        if(!(state instanceof MarketState)){
            throw new InvalidMessageException("Market State is is invalid type");
        }
        this.state = state;
    }
    
    @Override
    public final MarketState getState() {
        return state;
    }
    
}
