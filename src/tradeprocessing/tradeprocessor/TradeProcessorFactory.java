package tradeprocessing.tradeprocessor;

import tradeprocessing.productbook.ProductBookSide;
import tradeprocessing.tradeprocessor.exceptions.InvalidProductBookSideValueException;


public class TradeProcessorFactory {
    private synchronized static TradeProcessor
          createTradeProcessorPriceTimeImpl(ProductBookSide pbs)
          throws InvalidProductBookSideValueException {
            return new TradeProcessorPriceTimeImpl(pbs);
    }
          
    public synchronized static TradeProcessor
          createTradeProcessor(String type, ProductBookSide pbs)
          throws InvalidProductBookSideValueException {
        TradeProcessor processor;
        switch(type) {
          case "price-time":
          default:
            processor = createTradeProcessorPriceTimeImpl(pbs);
        }
        return processor;
    }
}