
package SE350;

/**
 *
 * @author Daryl
 */
public class Tradable {
    Order TradableO = new Order();
    //Price TradableP = new Price();
    Bookside b_side = new Booksides();
    
    String getProduct(){
        return TradableO.product; 
    }
    
    long getPrice(){
        return TradableO.Tradable.P; 
    } //I used long instead of Price because of the long return method
    
    int getOriginalVolume(){
        return TradableO.OriQuantity;
    }
    
    int getRemainingVolume(){
        return TradableO.RemQuantity;
    }
    
    int getCancelledVolume(){
        return TradableO.CanQuantity;
    }
    
    void setCancelledVolume(int newCancelledVolume){
        int check; 
        check = newCancelledVolume + TradableO.RemQuantity; 
        
        if (newCancelledVolume <0 || check < TradableO.OriQuantity)
        {
            System.out.println("Value is invalid");
        }
        else
        {
            TradableO.CanQuantity = newCancelledVolume; 
        }
    }
    
    void setRemainingVolume(int newRemainingVolume){
        int check;
        check = newRemainingVolume + TradableO.CanQuantity; 
        
        if(newRemainingVolume < 0 || check < TradableO.OriQuantity){
            System.out.println("Value is invalid");
        }
        else
        {
            TradableO.RemQuantity = newRemainingVolume;
        }
    }
    
    String getUser(){
        return TradableO.UserID; 
    }
    
    String getSide(){
        return b_side.getSide();
    }
    
    boolean isQuote(){
        return TradableO.quote;
        //not sure about this. check.
    }
    
    String getId(){
        return TradableO.ID; 
    }
}

