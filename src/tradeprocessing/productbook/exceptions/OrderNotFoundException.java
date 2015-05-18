/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tradeprocessing.productbook.exceptions;

/**
 *
 * @author Daryl's
 */
public class OrderNotFoundException extends Exception{
     public OrderNotFoundException(String msg) {
        super(msg);
  }
}
