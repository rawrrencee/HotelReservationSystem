/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Guest;
import entity.RegisteredGuest;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.GeneralException;
import util.exception.GuestExistException;
import util.exception.GuestNotFoundException;
import util.exception.InvalidLoginCredentialException;
import util.exception.RegisteredGuestExistException;
import util.exception.RegisteredGuestNotFoundException;

/**
 *
 * @author Lawrence
 */
@Stateless
@Local(GuestControllerLocal.class)
@Remote(GuestControllerRemote.class)
public class GuestController implements GuestControllerRemote, GuestControllerLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    Guest currentGuest;

    public GuestController() {
    }
    
    @Override
    public Guest retrieveGuestByPassportNumber(String passportNum) throws GuestNotFoundException {
        Query query = em.createQuery("SELECT g FROM Guest g WHERE g.passportNum = :inPassportNum");
        query.setParameter("inPassportNum", passportNum);
        
        try {
            return (Guest)query.getSingleResult();
        } catch (NoResultException ex) {
            throw new GuestNotFoundException("Guest with Passport Number " + passportNum + " does not exist!");
        }
    }
    
    @Override
    public Boolean checkGuestExistsByPassportNum(String passportNum) throws GuestNotFoundException {
        Query query = em.createQuery("SELECT g FROM Guest g WHERE g.passportNum = :inPassportNum");
        query.setParameter("inPassportNum", passportNum);
        
        try {
            query.getSingleResult();
            return true;
        } catch (NoResultException ex) {
            throw new GuestNotFoundException("Guest with Passport Number " + passportNum + " does not exist!");
        }
    }
    
    @Override
    public Guest createNewGuest(Guest newGuest) throws GuestExistException, GeneralException {
        try {
            retrieveGuestByPassportNumber(newGuest.getPassportNum());
        } catch (GuestNotFoundException ex) {
            try {
                em.persist(newGuest);
                em.flush();
                em.refresh(newGuest);
            } catch (PersistenceException e) {
                if (ex.getCause() != null && ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getSimpleName().equals("SQLIntegrityConstraintViolationException")) {
                    throw new GuestExistException("A guest with the same passport number exists!");
                } else {
                    throw new GeneralException("An unexpected error has occurred: " + e.getMessage());
                }
            }
        }
        return newGuest;
    }

    @Override
    public Guest createNewRegisteredGuest(RegisteredGuest newRegisteredGuest) throws RegisteredGuestExistException, GeneralException {
            try {
                em.persist(newRegisteredGuest);
                em.flush();
                em.refresh(newRegisteredGuest);
            } catch (PersistenceException ex) {
                if (ex.getCause() != null && ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getSimpleName().equals("SQLIntegrityConstraintViolationException")) {
                    throw new RegisteredGuestExistException("A registered guest with the same passport number exists!");
                } else {
                    throw new GeneralException("An unexpected error has occurred: " + ex.getMessage());
                }
            }
        return newRegisteredGuest;
    }
    
    @Override
    public RegisteredGuest retrieveRegisteredGuestByUsername(String username) throws RegisteredGuestNotFoundException {
        Query query = em.createQuery("SELECT rg FROM RegisteredGuest rg WHERE rg.username = :inUsername");
        query.setParameter("inUsername", username);
        
        try {
            return (RegisteredGuest)query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new RegisteredGuestNotFoundException("Employee username " + username + " does not exist!");
        }
    }
    
        
    @Override
    public Guest retrieveRegisteredGuestByPassportNumber(String passportNum) throws RegisteredGuestNotFoundException {
        Query query = em.createQuery("SELECT rg FROM RegisteredGuest rg WHERE rg.passportNum = :inPassportNum");
        query.setParameter("inPassportNum", passportNum);
        
        try {
            return (RegisteredGuest)query.getSingleResult();
        } catch (NoResultException ex) {
            throw new RegisteredGuestNotFoundException("Registered Guest with Passport Number " + passportNum + " does not exist!");
        }
    }
    
    @Override
    public RegisteredGuest registeredGuestLogin(String username, String password) throws InvalidLoginCredentialException {
        try {
            RegisteredGuest registeredGuest = retrieveRegisteredGuestByUsername(username);

            if (registeredGuest.getPassword().equals(password)) {

                return registeredGuest;
            } else {
                throw new InvalidLoginCredentialException("Username does not exist or invalid password!");
            }
        } catch (RegisteredGuestNotFoundException ex) {
            throw new InvalidLoginCredentialException("Username does not exist or invalid password!");
        }
    }
    
}
