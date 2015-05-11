/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package publishers.message;
import constants.GlobalConstants;
import price.Price;
import publishers.message.exceptions.InvalidMessageException;

import constants.GlobalConstants.BookSide;

/**
 *
 * @author Daryl
 */
public class GeneralMarketMessageImpl implements GeneralMarketMessage{
    
    private String user;
    private String product;
    private Price price;
    private int volume;
    private String details;
    private BookSide side;
    private String id;
    
    public GeneralMarketMessageImpl(String user, String product, Price price, int volume,
            String details, BookSide side, String id) throws InvalidMessageException{
        setUser(user);
        setProduct(product);
        setPrice(price);
        setVolume(volume);
        setDetails(details);
        setSide(side);
        setId(id);
    }

    @Override
    public String getUser() {
        return user;
    }

    @Override
    public String getProduct() {
        return product;
    }

    @Override
    public Price getPrice() {
        return price;
    }

    @Override
    public int getVolume() {
       return volume;
    }

    @Override
    public String getDetails() {
        return details;
    }

    @Override
    public GlobalConstants.BookSide getSide() {
        return side;
    }
    
    @Override
    public String toString() {
        return ("User: " + user + ", Product: " + product + ", Price: " + price +
            ", Volume: " + volume + ", Details: " + details + ", Side: " +
            side + ", ID: " + id);
    }
    
    private void setUser(String user) throws InvalidMessageException {
    if (user == null || user.isEmpty()) {
      throw new InvalidMessageException("User cannot be null or empty.");
    }
    this.user = user;
  }

  private void setProduct(String product) throws InvalidMessageException {
    if (product == null || product.isEmpty()) {
      throw new InvalidMessageException("Product cannot be null or empty.");
    }
    this.product = product;
  }

  private void setPrice(Price price) throws InvalidMessageException {
    if (price == null) {
      throw new InvalidMessageException("Price cannot be null");
    }
    this.price = price;
  }

  private void setVolume(int volume) throws InvalidMessageException {
    if (volume < 0) {
      throw new InvalidMessageException("Volume cannot be negative.");
    }
    this.volume = volume;
  }

  private void setDetails(String details) throws InvalidMessageException {
    if (details == null || details.isEmpty()) {
      throw new InvalidMessageException("Details cannot be null or empty");
    }
    this.details = details;
    }

  private void setSide(BookSide side) throws InvalidMessageException {
    if (!(side instanceof BookSide)) {
      throw new InvalidMessageException("Side must be a valid Book Side");
    }
    this.side = side;
    }

  private void setId(String id) throws InvalidMessageException {
    if (id == null || id.isEmpty()) {
      throw new InvalidMessageException("ID cannot be null or empty.");
    }
    this.id = id;
    }
}
