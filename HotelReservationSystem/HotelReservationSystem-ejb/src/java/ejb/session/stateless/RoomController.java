/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Room;
import entity.RoomType;
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
import util.enumeration.RoomStatus;
import util.exception.GeneralException;
import util.exception.RoomExistException;
import util.exception.RoomNotFoundException;
import util.exception.RoomTypeNotFoundException;

/**
 *
 * @author Lawrence
 */
@Stateless
@Local(RoomControllerLocal.class)
@Remote(RoomControllerRemote.class)
public class RoomController implements RoomControllerLocal, RoomControllerRemote {

    @EJB
    private RoomTypeControllerLocal roomTypeControllerLocal;

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    
    

    public RoomController() {
    }

    @Override
    public List<Room> retrieveAllRooms() {
        Query query = em.createQuery("SELECT r FROM Room r");

        return query.getResultList();
    }
    
    @Override
    public Room retrieveRoomByRoomId(Long roomId) throws RoomNotFoundException {
        Room room = em.find(Room.class, roomId);
        if (room != null) {
            return room;
        } else {
            throw new RoomNotFoundException("Room " + roomId + " does not exist!");
        }
    }

    @Override
    public Room retrieveRoomByRoomNumber(Integer roomNumber) throws RoomNotFoundException {
        Query query = em.createQuery("SELECT r FROM Room r WHERE r.roomNumber = :inRoomNumber");
        query.setParameter("inRoomNumber", roomNumber);

        return (Room) query.getSingleResult();
    }

    @Override
    public Room createNewRoom(Room newRoom, Long roomTypeId) throws RoomTypeNotFoundException, RoomExistException, GeneralException {
        try {
            
            RoomType roomType = roomTypeControllerLocal.retrieveRoomTypeByRoomTypeId(roomTypeId);
            em.persist(newRoom);
            
            newRoom.setRoomType(roomType);
            roomType.getRooms().add(newRoom);
            
            em.flush();
            em.refresh(newRoom);
            
            return newRoom;
        } catch (RoomTypeNotFoundException ex) {
            
            throw new RoomTypeNotFoundException("Unable to create new Room as the Room Type record does not exist!");
        
        } catch (PersistenceException ex) {
            if (ex.getCause() != null && ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getSimpleName().equals("SQLIntegrityConstraintViolationException")) {
                throw new RoomExistException("A room with the provided information already exists!");
            } else {
                throw new GeneralException("An unexpected error has occurred: " + ex.getMessage());
            }
        }
    }
    
    @Override
    public Boolean checkRoomExistsByRoomNumber(Integer roomNumber){
        Query query = em.createQuery("SELECT r FROM Room r WHERE r.roomNumber = :inRoomNumber");
        query.setParameter("inRoomNumber", roomNumber);
        
        try {
            query.getSingleResult();
            return ((Room) query.getSingleResult()).getRoomNumber().equals(roomNumber);
        } catch (NoResultException ex) {
            return false;
        }
    }
    
    @Override
    public void updateRoom(Room room, Long newRoomTypeId) throws RoomTypeNotFoundException {
        Long currentRoomTypeId = room.getRoomType().getRoomTypeId();
        RoomType currentRoomType = roomTypeControllerLocal.retrieveRoomTypeByRoomTypeId(currentRoomTypeId);
        
        try {
            if (!currentRoomTypeId.equals(newRoomTypeId)) {
                RoomType newRoomType = roomTypeControllerLocal.retrieveRoomTypeByRoomTypeId(newRoomTypeId);
                room.setRoomType(newRoomType);
                newRoomType.getRooms().add(room);
                currentRoomType.getRooms().remove(room);
            }
        } catch (RoomTypeNotFoundException ex) {
            System.out.println("Room type does not exist!");
        }
        em.merge(room);     
    }
    
    @Override
    public Boolean deleteRoom(Long roomId) throws RoomNotFoundException {
        Room roomToRemove = retrieveRoomByRoomId(roomId);
        RoomType roomType = roomToRemove.getRoomType();
        
        if (roomToRemove.getRoomStatus().equals(RoomStatus.ALLOCATED) || roomToRemove.getRoomStatus().equals(RoomStatus.CLEANING)) {
            roomToRemove.setRoomStatus(RoomStatus.DISABLED);
            return false;
        } else {
            roomType.getRooms().remove(roomToRemove);
            em.remove(roomToRemove);
            return true;
        }
    }
}
