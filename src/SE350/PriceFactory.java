
package SE350;


public class PriceFactory {
    public static long limit;
    public static Price Factory = new Price();
    
    public static Price makeLimitPrice(String value){
        value = value.replaceAll("[$,]","");
        double temp = Double.parseDouble(value);
        temp = temp*100.0; 
        limit = (long)temp;
        return null;
    }
    
    public static Price makeLimitPrice(long value){
        //not sure if we need to convert this value into Dollars
        value = value*100;
        Factory.Price(value);
        return null; 
    }
    
    public static Price makeMarketPrice(){
        Factory.Price();
        return null; 
    }
    
    
}
