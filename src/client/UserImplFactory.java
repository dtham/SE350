/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import client.exceptions.UserException;
import client.exceptions.UserImplFactoryException;
import price.exceptions.InvalidPriceOperation;

/**
 *
 * @author Daryl's
 */
public class UserImplFactory {
    
    public static UserImpl createUser(String userName)
          throws UserImplFactoryException, UserException, InvalidPriceOperation {
        if (userName == null || userName.isEmpty()) {
          throw new UserImplFactoryException("Username cannot be null or empty.");
        }
        return new UserImpl(userName);
    }
}
