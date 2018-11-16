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
public class RegisteredGuestExistException extends Exception {

    public RegisteredGuestExistException() {
    }
    
    public RegisteredGuestExistException(String msg) {
        super(msg);
    }
    
}
