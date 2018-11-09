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
public class CheckRoomInventoryAvailabilityException extends Exception {

    public CheckRoomInventoryAvailabilityException() {
    }
    
    public CheckRoomInventoryAvailabilityException(String msg) {
        super(msg);
    }
}
