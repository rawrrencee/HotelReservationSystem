/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Guest;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.GeneralException;
import util.exception.GuestExistException;
import util.exception.GuestNotFoundException;

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
    
}
