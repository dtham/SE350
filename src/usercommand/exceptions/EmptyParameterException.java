/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package usercommand.exceptions;

/**
 *
 * @author Daryl's
 */
public class EmptyParameterException extends Exception{
    public EmptyParameterException(String msg){
        super(msg);
    }
}