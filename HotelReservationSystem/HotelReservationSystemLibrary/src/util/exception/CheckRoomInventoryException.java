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
public class CheckRoomInventoryException extends Exception {

    public CheckRoomInventoryException() {
    }
    
    public CheckRoomInventoryException(String msg) {
        super(msg);
    }
}
