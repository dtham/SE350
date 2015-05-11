/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package publishers.message.exceptions;

/**
 *
 * @author Daryl
 */
public class InvalidPublisherOperation extends Exception{
    
    public InvalidPublisherOperation(String msg){
        super(msg);
    }
}
