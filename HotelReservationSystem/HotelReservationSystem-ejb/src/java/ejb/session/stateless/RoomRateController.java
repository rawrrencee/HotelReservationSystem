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
import util.exception.RoomRateNotFoundException;
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
    
    @Override
    public RoomRate retrieveRoomRateByRoomRateId(Long roomRateId) throws RoomRateNotFoundException {
        RoomRate roomRate = em.find(RoomRate.class, roomRateId);
        
        if (roomRate != null) {
        return roomRate;
        } else {
            throw new RoomRateNotFoundException("Room Rate ID " + roomRateId + " does not exist!");
        }
    }
    
    @Override
    public void updateRoomRate(RoomRate roomRate, Long newRoomTypeId) throws RoomTypeNotFoundException {
        Long currentRoomTypeId = roomRate.getRoomType().getRoomTypeId();
        RoomType currentRoomType = roomTypeControllerLocal.retrieveRoomTypeByRoomTypeId(currentRoomTypeId);
        
        try {
            if (!currentRoomTypeId.equals(newRoomTypeId)) {
                RoomType newRoomType = roomTypeControllerLocal.retrieveRoomTypeByRoomTypeId(newRoomTypeId);
                roomRate.setRoomType(newRoomType);
                newRoomType.getRoomRates().add(roomRate);
                currentRoomType.getRoomRates().remove(roomRate);
            }
        } catch (RoomTypeNotFoundException ex) {
            System.out.println("Room rate does not exist!");
        }
        em.merge(roomRate);     
    }
    
    @Override
    public Boolean deleteRoomRate(Long roomRateId) throws RoomRateNotFoundException {
        RoomRate roomRateToRemove = retrieveRoomRateByRoomRateId(roomRateId);
        
        try {
            roomRateToRemove.getRoomType();
            roomRateToRemove.setIsEnabled(Boolean.FALSE);
            return false;
        } catch (NullPointerException ex) {
            em.remove(roomRateToRemove);
            return true;
        }
    }
}
