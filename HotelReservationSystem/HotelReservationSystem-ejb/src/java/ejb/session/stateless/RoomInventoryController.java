/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomInventory;
import entity.RoomType;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.GeneralException;
import util.exception.RoomInventoryExistException;
import util.exception.RoomInventoryNotFoundException;

/**
 *
 * @author Lawrence
 */
@Stateless
@Local(RoomInventoryControllerLocal.class)
@Remote(RoomInventoryControllerRemote.class)
public class RoomInventoryController implements RoomInventoryControllerLocal, RoomInventoryControllerRemote {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    
    @Override
    public RoomInventory retrieveRoomInventoryByRoomInventoryId(Long roomInventoryId) throws RoomInventoryNotFoundException {
        RoomInventory roomInventory = em.find(RoomInventory.class, roomInventoryId);
        
        if (roomInventory != null) {
            return roomInventory;
        } else {
            throw new RoomInventoryNotFoundException("Room Inventory ID " + roomInventoryId + " does not exist!");
        }
    }
    
    @Override
    public RoomInventory retrieveRoomInventoryByDate(Date date, Long roomTypeId) throws RoomInventoryNotFoundException {
        Query query = em.createQuery("SELECT ri FROM RoomInventory ri WHERE ri.date = :inDate AND ri.roomType.roomTypeId = :inRoomTypeId");
        query.setParameter("inDate", date);
        query.setParameter("inRoomTypeId", roomTypeId);
        
        try {
            return (RoomInventory)query.getSingleResult();
        } catch (NoResultException ex) {
            throw new RoomInventoryNotFoundException("Room Inventory with date " + date + " and Room Type Id " + roomTypeId + " does not exist!");
        }
    }
    
    @Override
    public List<RoomInventory> retrieveAllRoomInventoriesOnDate(Date date) throws RoomInventoryNotFoundException {
        Query query = em.createQuery("SELECT ri FROM RoomInventory ri WHERE ri.date = :inDate");
        query.setParameter("inDate", date);
        
        try {
            return query.getResultList();
        } catch (NoResultException ex) {
            throw new RoomInventoryNotFoundException("No room inventories with date " + date + " exist.");
        }
    }
    
    @Override
    public List<RoomInventory> retrieveRoomInventoriesByRoomType(Long roomTypeId) throws RoomInventoryNotFoundException {
        Query query = em.createQuery("SELECT ri FROM RoomInventory ri WHERE ri.roomType.roomTypeId = :inRoomTypeId");
        query.setParameter("inRoomTypeId", roomTypeId);
        
        try {
            return query.getResultList();
        } catch (NoResultException ex) {
            throw new RoomInventoryNotFoundException("Room Inventory with Room Type Id " + roomTypeId + " does not exist!");
        }
    }
    
    @Override
    public RoomInventory createNewRoomInventory(Date date, Long roomTypeId) throws RoomInventoryExistException, GeneralException {
        RoomType roomType = em.find(RoomType.class, roomTypeId);
        RoomInventory newRoomInventory = new RoomInventory();
        
        Integer initialInventoryCount;
        
        initialInventoryCount = roomType.getNumRooms();
        try {
            newRoomInventory.setDate(date);
            newRoomInventory.setNumRoomsLeft(initialInventoryCount);
            newRoomInventory.setRoomType(roomType);
            if (!roomType.getRoomInventories().contains(newRoomInventory)) {
                roomType.getRoomInventories().add(newRoomInventory);
            }
            em.persist(newRoomInventory);
            em.flush();
            return newRoomInventory;
        }  catch (PersistenceException ex) {
            if (ex.getCause() != null && ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getSimpleName().equals("SQLIntegrityConstraintViolationException")) {
                throw new RoomInventoryExistException("A room inventory with the provided details already exists!");
            }
            else {
                throw new GeneralException("An unexpected error has occurred: " + ex.getMessage());
            }
        }
    }
}
