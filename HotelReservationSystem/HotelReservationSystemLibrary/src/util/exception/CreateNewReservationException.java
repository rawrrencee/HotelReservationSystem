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
public class CreateNewReservationException extends Exception {

    public CreateNewReservationException() {
    }
    
    public CreateNewReservationException(String msg){
        super(msg);
    }
    
}
