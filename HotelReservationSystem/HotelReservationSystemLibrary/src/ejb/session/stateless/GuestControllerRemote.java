/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Guest;
import entity.RegisteredGuest;
import util.exception.GeneralException;
import util.exception.GuestExistException;
import util.exception.GuestNotFoundException;
import util.exception.InvalidLoginCredentialException;
import util.exception.RegisteredGuestExistException;
import util.exception.RegisteredGuestNotFoundException;

public interface GuestControllerRemote {

    public Guest createNewGuest(Guest newGuest) throws GuestExistException, GeneralException;

    public Guest retrieveGuestByPassportNumber(String passportNum) throws GuestNotFoundException;

    public Boolean checkGuestExistsByPassportNum(String passportNum) throws GuestNotFoundException;

    public RegisteredGuest registeredGuestLogin(String username, String password) throws InvalidLoginCredentialException;

    public RegisteredGuest retrieveRegisteredGuestByUsername(String username) throws RegisteredGuestNotFoundException;

    public Guest createNewRegisteredGuest(RegisteredGuest newRegisteredGuest) throws RegisteredGuestExistException, GeneralException;

    public Guest retrieveRegisteredGuestByPassportNumber(String passportNum) throws RegisteredGuestNotFoundException;

}
