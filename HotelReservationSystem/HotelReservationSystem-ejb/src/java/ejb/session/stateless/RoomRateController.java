/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomRate;
import entity.RoomType;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.GeneralException;
import util.exception.RoomRateExistException;
import util.exception.RoomTypeNotFoundException;

/**
 *
 * @author Lawrence
 */
@Stateless
@Local(RoomRateControllerLocal.class)
@Remote(RoomRateControllerRemote.class)
public class RoomRateController implements RoomRateControllerLocal, RoomRateControllerRemote {

    @EJB
    private RoomTypeControllerLocal roomTypeControllerLocal;

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    public RoomRateController() {
    }
    
    @Override
    public List<RoomRate> retrieveAllRoomRates() {
        Query query = em.createQuery("SELECT rr FROM RoomRate rr");
        
        return query.getResultList();
    }
    
    public RoomRate createNewRoomRate(RoomRate newRoomRate, Long roomTypeId) throws RoomRateExistException, RoomTypeNotFoundException, GeneralException{
        try {
            RoomType roomType = roomTypeControllerLocal.retrieveRoomTypeByRoomTypeId(roomTypeId);
            em.persist(newRoomRate);
            
            newRoomRate.setRoomType(roomType);
            roomType.getRoomRates().add(newRoomRate);
            
            em.flush();
            em.refresh(newRoomRate);
            
            return newRoomRate;
        } catch (RoomTypeNotFoundException ex) {
            
            throw new RoomTypeNotFoundException("Unable to create new Room Rate as the Room Type record does not exist!");
        
        } catch (PersistenceException ex) {
            if (ex.getCause() != null && ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getSimpleName().equals("SQLIntegrityConstraintViolationException")) {
                throw new RoomRateExistException("A room rate with the provided information already exists!");
            } else {
                throw new GeneralException("An unexpected error has occurred: " + ex.getMessage());
            }
        }
    }
}
