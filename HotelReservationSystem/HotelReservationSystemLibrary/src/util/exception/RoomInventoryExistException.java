/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.exception;

/**
 *
 * @author Lawrence
 */
public class RoomInventoryExistException extends Exception {
    
    public RoomInventoryExistException(){
    }
    
    public RoomInventoryExistException(String msg){
        super(msg);
    }
    
}
