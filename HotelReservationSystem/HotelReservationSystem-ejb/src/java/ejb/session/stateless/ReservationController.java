/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Reservation;
import entity.ReservationLineItem;
import entity.Room;
import entity.RoomInventory;
import entity.RoomNight;
import entity.RoomRate;
import entity.RoomType;
import entity.WalkInReservation;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import util.enumeration.RoomStatus;
import util.exception.ReservationNotFoundException;
import util.exception.RoomAllocationException;
import util.exception.RoomInventoryNotFoundException;

@Local(ReservationControllerLocal.class)
@Remote(ReservationControllerRemote.class)
@Stateless
public class ReservationController implements ReservationControllerRemote, ReservationControllerLocal {

    @EJB
    private RoomControllerLocal roomControllerLocal;

    @EJB
    private RoomInventoryControllerLocal roomInventoryControllerLocal;

    @EJB
    private RoomRateControllerLocal roomRateControllerLocal;

    @EJB
    private GuestControllerLocal guestControllerLocal;

    @EJB
    private EmployeeControllerLocal employeeControllerLocal;

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    public void persist(Object object) {
        em.persist(object);
    }
    
    @Override
    public Reservation retrieveReservationByReservationId(Long reservationId) throws ReservationNotFoundException {
        Reservation reservation = em.find(Reservation.class, reservationId);
        if (reservation != null) {
            reservation.getReservationLineItems().size();
            return reservation;
        } else {
            throw new ReservationNotFoundException("Reservation " + reservationId + " does not exist!");
        }
    }
    
    @Override
    public RoomType retrieveRoomTypeByLineId(Long reservationLineItemId) {
        ReservationLineItem reservationLineItem = em.find(ReservationLineItem.class, reservationLineItemId);
        return reservationLineItem.getRoomType();
    }
    
    @Override
    public WalkInReservation createNewWalkInReservation(WalkInReservation newWalkInReservation) {
        em.persist(newWalkInReservation);
        em.flush();
        em.refresh(newWalkInReservation);
        
        return newWalkInReservation;
    }
    
    @Override
    public ReservationLineItem createNewReservationLineItem(ReservationLineItem newReservationLineItem, Long walkInReservationId, Long roomTypeId) {
        newReservationLineItem.setRoomType(em.find(RoomType.class, roomTypeId));
        em.persist(newReservationLineItem);
        WalkInReservation walkInReservation = em.find(WalkInReservation.class, walkInReservationId);
        walkInReservation.getReservationLineItems().add(newReservationLineItem);
        
        em.flush();
        em.refresh(newReservationLineItem);
        return newReservationLineItem;
    }
    
    @Override
    public RoomNight createNewRoomNight(RoomNight newRoomNight, Long roomTypeId, Long reservationLineItemId) {
        RoomRate lowestPublished = roomRateControllerLocal.retrieveLowestPublishedRoomRate(roomTypeId);
        ReservationLineItem reservationLineItem = em.find(ReservationLineItem.class, reservationLineItemId);
        reservationLineItem.getRoomNights().add(newRoomNight);
        newRoomNight.setReservationLineItem(reservationLineItem);
        newRoomNight.setRoomRate(lowestPublished);
        em.persist(newRoomNight);
        
        em.flush();
        em.refresh(newRoomNight);
        return newRoomNight;
    }
    
    @Override
    public BigDecimal calculateReservationLineAmount(Long reservationLineItemId) {
        ReservationLineItem reservationLineItem = em.find(ReservationLineItem.class, reservationLineItemId);
        List<RoomNight> roomNights = reservationLineItem.getRoomNights();
        BigDecimal amount = BigDecimal.ZERO;
        
        for (RoomNight roomNight: roomNights) {
            amount = (roomNight.getRoomRate().getRatePerNight()).add(amount);
        }
        amount = amount.multiply(BigDecimal.valueOf(reservationLineItem.getNumRoomsRequested()));
        return amount;
    }
    
    @Override
    public Room allocateRoom(Long roomTypeId, Long reservationLineItemId, Long walkInReservationId) throws RoomAllocationException {
        ReservationLineItem reservationLineItem = em.find(ReservationLineItem.class, reservationLineItemId);
        WalkInReservation walkInReservation = em.find(WalkInReservation.class, walkInReservationId);
        
        Date checkInDate = walkInReservation.getCheckInDate();
        System.out.println("walkin reservation checkindate: " + checkInDate);
        Date checkOutDate = walkInReservation.getCheckOutDate();
        System.out.println("walkin reservation checkkOutdate: " + checkOutDate);
        Calendar checkInDateCal = Calendar.getInstance();
        checkInDateCal.setTime(checkInDate);
        Calendar checkOutDateCal = Calendar.getInstance();
        checkOutDateCal.setTime(checkOutDate);
        
        try {
            Room room = roomControllerLocal.retrieveFirstAvailableRoomOfRoomType(roomTypeId);
            room.setRoomStatus(RoomStatus.ALLOCATED);
            reservationLineItem.getRooms().add(room);

            for (Date date = checkInDateCal.getTime(); checkInDateCal.before(checkOutDateCal); checkInDateCal.add(Calendar.DATE, 1), date = checkInDateCal.getTime()) {
                System.out.println("Current date for allocation: " + date);
                try {
                    System.out.println("Current date for allocation: " + date);
                    RoomInventory roomInventory = roomInventoryControllerLocal.retrieveRoomInventoryByDate(date, roomTypeId);
                    System.out.println("room Inventory id is " + roomInventory.getRoomInventoryId());
                    if (roomInventory.getNumRoomsLeft() == 0) {
                        throw new RoomAllocationException("Room allocation failed as no rooms are available.");
                    }
                    roomInventory.setNumRoomsLeft(roomInventory.getNumRoomsLeft() - 1);
                } catch (RoomInventoryNotFoundException ex) {
                    throw new RoomAllocationException(ex.getMessage());
                }
            }
            return room;
        } catch (NoResultException ex) {
            throw new RoomAllocationException("No available rooms of room type!");
        }
    }

}