
package SE350;

import java.util.*;

public class PriceFactory {
    
    private static Map<String, Price> flyweights = new HashMap<>();
    
    
    public static Price makeLimitPrice(String value){
       String parsedStringValue = value.replaceAll("[$,]", "");
       long temp = Long.parseLong(parsedStringValue);
       return PriceFactory.makeLimitPrice(temp);
    }
    
    public static Price makeLimitPrice(long value){
        String temp = value + ""; 
        
        Price p = PriceFactory.flyweights.get(temp);
        if(p == null){
            p = new Price(value);
            PriceFactory.flyweights.put(temp, p);
        }
            return p;  
    }
    
    
    public static Price makeMarketPrice(){
        Price p = PriceFactory.flyweights.get("MKT");
        if(p == null){
            p = new Price();
            PriceFactory.flyweights.put("MKT", p);
        }
            return p;
    }
    
    
}
