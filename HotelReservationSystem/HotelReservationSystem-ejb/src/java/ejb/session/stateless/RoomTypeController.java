/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomType;
import java.util.List;
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
import util.exception.RoomTypeExistException;
import util.exception.RoomTypeNotFoundException;

/**
 *
 * @author Lawrence
 */
@Stateless
@Local(RoomTypeControllerLocal.class)
@Remote(RoomTypeControllerRemote.class)
public class RoomTypeController implements RoomTypeControllerLocal, RoomTypeControllerRemote {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    public RoomTypeController() {
    }
    
    @Override
    public List<RoomType> retrieveAllRoomTypes() {
        Query query = em.createQuery("SELECT rt from RoomType rt");
        
        return query.getResultList();
    }
    
    @Override
    public RoomType retrieveRoomTypeByRoomTypeId(Long roomTypeId) throws RoomTypeNotFoundException {
        RoomType roomType = em.find(RoomType.class, roomTypeId);
        if (roomType != null) {
            return roomType;
        } else {
            throw new RoomTypeNotFoundException("Room Type " + roomTypeId + " does not exist!");
        }
    }
    
    @Override
    public RoomType createNewRoomType(RoomType roomType) throws RoomTypeExistException, GeneralException {
        try {
            em.persist(roomType);
            em.flush();
            
            return roomType;
        } catch (PersistenceException ex) {
            if (ex.getCause() != null && ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getSimpleName().equals("SQLIntegrityConstraintViolationException")) {
                throw new RoomTypeExistException("A room type with the provided particulars already exists!");
            } else {
                throw new GeneralException("An unexpected error has occurred: " + ex.getMessage());
            }
        }
    }
    
    @Override
    public Boolean checkRoomTypeExists(String roomTypeName) throws RoomTypeNotFoundException {
        Query query = em.createQuery("SELECT rt FROM RoomType rt WHERE rt.roomTypeName = :inRoomTypeName");
        query.setParameter("inRoomTypeName", roomTypeName);
        
        try {
            return ((RoomType) query.getSingleResult() != null);
        } catch (NoResultException | NonUniqueResultException ex){
            throw new RoomTypeNotFoundException("Room Type Name" + roomTypeName + " does not exist!");
        }
    }
    
    @Override
    public void updateRoomType(RoomType roomType) {
        em.merge(roomType);
    }
    
}
