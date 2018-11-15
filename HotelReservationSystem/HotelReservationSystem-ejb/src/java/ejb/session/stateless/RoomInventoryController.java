/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomInventory;
import entity.RoomRate;
import entity.RoomType;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.CheckRoomInventoryAvailabilityException;
import util.exception.CheckRoomInventoryException;
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

    @EJB
    private RoomTypeControllerLocal roomTypeControllerLocal;

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
    public RoomInventory retrieveRoomInventoryByDate(LocalDate date, Long roomTypeId) throws RoomInventoryNotFoundException {
        Query query = em.createQuery("SELECT ri FROM RoomInventory ri WHERE ri.date = :inDate AND ri.roomType.roomTypeId = :inRoomTypeId");
        query.setParameter("inDate", Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        query.setParameter("inRoomTypeId", roomTypeId);
        
        try {
            return (RoomInventory)query.getSingleResult();
        } catch (NoResultException ex) {
            throw new RoomInventoryNotFoundException("Room Inventory with date " + date + " and Room Type Id " + roomTypeId + " does not exist!");
        }
    }
    
    @Override
    public List<RoomInventory> retrieveAllRoomInventoriesOnDate(LocalDate date) throws RoomInventoryNotFoundException {
        Query query = em.createQuery("SELECT ri FROM RoomInventory ri WHERE ri.date = :inDate");
        query.setParameter("inDate", Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        
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
    public List<RoomRate> retrieveRoomRatesByTypeOfRoomInventory(Long roomInventoryId) throws RoomInventoryNotFoundException {
        RoomInventory roomInventory = retrieveRoomInventoryByRoomInventoryId(roomInventoryId);
        List<RoomRate> roomRates = roomInventory.getRoomType().getRoomRates();
        roomRates.size();
        
        return roomRates;
    }
    
    @Override
    public RoomInventory createNewRoomInventory(LocalDate date, Long roomTypeId) throws RoomInventoryExistException, GeneralException {
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
    
    @Override
    public Boolean checkRoomInventoryOnDate(LocalDate checkInDate, LocalDate checkOutDate) throws CheckRoomInventoryException {
        Integer numRoomsLeft = 0;
        RoomInventory currentRoomInventory;
        List<RoomType> roomTypes = roomTypeControllerLocal.retrieveAllRoomTypes();
        
        for (LocalDate date = checkInDate; date.isBefore(checkOutDate); date = date.plusDays(1)) {
            numRoomsLeft = 0;
            for (RoomType roomType : roomTypes) {
                if(roomType.getIsEnabled()) {
                    try {
                        currentRoomInventory = retrieveRoomInventoryByDate(date, roomType.getRoomTypeId());
                        numRoomsLeft += currentRoomInventory.getNumRoomsLeft();
                    } catch (RoomInventoryNotFoundException ex) {
                        try {
                            currentRoomInventory = createNewRoomInventory(date, roomType.getRoomTypeId());
                            numRoomsLeft += currentRoomInventory.getNumRoomsLeft();
                        } catch (GeneralException | RoomInventoryExistException e) {
                            throw new CheckRoomInventoryException("An unexpected error has occurred: " + e.getMessage());
                        }
                    } 
                }
            }
            if (numRoomsLeft <= 0) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public Boolean checkRoomInventoryAvailability(LocalDate checkInDate, LocalDate checkOutDate, Long roomTypeId, Integer numRoomsRequested) throws CheckRoomInventoryAvailabilityException {
        RoomInventory roomInventory;
        
        try { 
            for (LocalDate date = checkInDate; date.isBefore(checkOutDate); date = date.plusDays(1)) {
                roomInventory = retrieveRoomInventoryByDate(date, roomTypeId);
                if (roomInventory.getNumRoomsLeft() < numRoomsRequested) {
                    return false;
                }
            }
            return true;
        } catch (RoomInventoryNotFoundException ex) {
            throw new CheckRoomInventoryAvailabilityException("An error has occurred: " + ex.getMessage());
        }
    }
}
