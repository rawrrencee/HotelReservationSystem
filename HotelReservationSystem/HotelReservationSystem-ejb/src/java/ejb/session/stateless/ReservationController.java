/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.OnlineReservation;
import entity.Reservation;
import entity.ReservationLineItem;
import entity.Room;
import entity.RoomInventory;
import entity.RoomNight;
import entity.RoomRate;
import entity.RoomType;
import entity.WalkInReservation;
import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.enumeration.RoomStatus;
import util.exception.CreateRoomNightException;
import util.exception.LineCalculationException;
import util.exception.LineCreationException;
import util.exception.ReservationNotFoundException;
import util.exception.RoomAllocationException;
import util.exception.RoomCheckoutException;
import util.exception.RoomInventoryNotFoundException;
import util.exception.RoomRateNotFoundException;

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
    public List<Reservation> retrieveAllReservationsByRegisteredGuest(Long registeredGuestId) throws ReservationNotFoundException {
        Query query = em.createQuery("SELECT r FROM OnlineReservation r WHERE r.registeredGuest.guestId = :inGuestId");
        query.setParameter("inGuestId", registeredGuestId);

        if (!query.getResultList().isEmpty()) {
            return query.getResultList();
        } else {
            throw new ReservationNotFoundException("No results in query list.");
        }
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
    public OnlineReservation createNewOnlineReservation(OnlineReservation newOnlineReservation) {
        em.persist(newOnlineReservation);
        em.flush();
        em.refresh(newOnlineReservation);

        return newOnlineReservation;
    }

    @Override
    public ReservationLineItem createNewReservationLineItem(ReservationLineItem newReservationLineItem, Long walkInReservationId, Long roomTypeId) throws LineCreationException {
        newReservationLineItem.setRoomType(em.find(RoomType.class, roomTypeId));
        em.persist(newReservationLineItem);
        WalkInReservation walkInReservation = em.find(WalkInReservation.class, walkInReservationId);
        walkInReservation.getReservationLineItems().add(newReservationLineItem);
        LocalDate checkInDate = walkInReservation.getCheckInDate();
        LocalDate checkOutDate = walkInReservation.getCheckOutDate();
        Integer numRoomsRequested = newReservationLineItem.getNumRoomsRequested();

        try {
            for (LocalDate date = checkInDate; date.isBefore(checkOutDate); date = date.plusDays(1)) {
                RoomInventory roomInventory = roomInventoryControllerLocal.retrieveRoomInventoryByDate(date, roomTypeId);
                roomInventory.setNumRoomsLeft(roomInventory.getNumRoomsLeft() - numRoomsRequested);
            }
        } catch (RoomInventoryNotFoundException ex) {
            throw new LineCreationException(ex.getMessage());
        }

        em.flush();
        em.refresh(newReservationLineItem);
        return newReservationLineItem;
    }

    @Override
    public ReservationLineItem createNewOnlineReservationLineItem(ReservationLineItem newReservationLineItem, Long onlineReservationId, Long roomTypeId) throws LineCreationException {
        newReservationLineItem.setRoomType(em.find(RoomType.class, roomTypeId));
        em.persist(newReservationLineItem);
        OnlineReservation onlineReservation = em.find(OnlineReservation.class, onlineReservationId);
        onlineReservation.getReservationLineItems().add(newReservationLineItem);
        LocalDate checkInDate = onlineReservation.getCheckInDate();
        LocalDate checkOutDate = onlineReservation.getCheckOutDate();
        Integer numRoomsRequested = newReservationLineItem.getNumRoomsRequested();

        try {
            for (LocalDate date = checkInDate; date.isBefore(checkOutDate); date = date.plusDays(1)) {
                RoomInventory roomInventory = roomInventoryControllerLocal.retrieveRoomInventoryByDate(date, roomTypeId);
                roomInventory.setNumRoomsLeft(roomInventory.getNumRoomsLeft() - numRoomsRequested);
            }
        } catch (RoomInventoryNotFoundException ex) {
            throw new LineCreationException(ex.getMessage());
        }

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
    public RoomNight createOnlineNewRoomNight(RoomNight newRoomNight, Long roomTypeId, Long reservationLineItemId) throws CreateRoomNightException {
        try {
            RoomRate finalRate = roomRateControllerLocal.retrieveComplexRoomRate(roomTypeId, newRoomNight.getDate());
            ReservationLineItem reservationLineItem = em.find(ReservationLineItem.class, reservationLineItemId);
            reservationLineItem.getRoomNights().add(newRoomNight);
            newRoomNight.setReservationLineItem(reservationLineItem);
            newRoomNight.setRoomRate(finalRate);
            em.persist(newRoomNight);

            em.flush();
            em.refresh(newRoomNight);
            return newRoomNight;
        } catch (RoomRateNotFoundException ex) {
            throw new CreateRoomNightException("Room rate not found!");
        }
    }

    @Override
    public BigDecimal calculateReservationLineAmount(Long reservationLineItemId) throws LineCalculationException {
        ReservationLineItem reservationLineItem = em.find(ReservationLineItem.class, reservationLineItemId);
        List<RoomNight> roomNights = reservationLineItem.getRoomNights();
        //System.out.println("roomnights size is " + roomNights.size());
        BigDecimal amount = BigDecimal.ZERO;

        try {
            for (RoomNight roomNight : roomNights) {
                amount = amount.add((roomRateControllerLocal.retrieveComplexRoomRate(reservationLineItem.getRoomType().getRoomTypeId(), roomNight.getDate()).getRatePerNight()), new MathContext(11));
                //System.out.println("amount is " + amount);
            }
        } catch (RoomRateNotFoundException ex) {
            throw new LineCalculationException("Room rate not found!");
        }
        //System.out.println("line item num room requested " + reservationLineItem.getNumRoomsRequested());
        amount = amount.multiply(BigDecimal.valueOf(reservationLineItem.getNumRoomsRequested()));
        reservationLineItem.setAmount(amount);
        return amount;
    }

    @Override
    public void setReservationAmount(Long reservationId, BigDecimal amount) {
        Reservation reservation = em.find(Reservation.class, reservationId);
        reservation.setReservationAmt(amount);
    }

    @Override
    public Room allocateRoom(Long roomTypeId, Long reservationLineItemId, Long walkInReservationId) throws RoomAllocationException {
        ReservationLineItem reservationLineItem = em.find(ReservationLineItem.class, reservationLineItemId);
        WalkInReservation walkInReservation = em.find(WalkInReservation.class, walkInReservationId);

        LocalDate checkInDate = walkInReservation.getCheckInDate();
        System.out.println("walkin reservation checkindate: " + checkInDate);
        LocalDate checkOutDate = walkInReservation.getCheckOutDate();
        System.out.println("walkin reservation checkkOutdate: " + checkOutDate);

        try {
            Room room = roomControllerLocal.retrieveFirstAvailableRoomOfRoomType(roomTypeId);
            room.setRoomStatus(RoomStatus.ALLOCATED);
            reservationLineItem.getRooms().add(room);

            for (LocalDate date = checkInDate; date.isBefore(checkOutDate); date = date.plusDays(1)) {
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

    @Override
    public void processCheckout(Long reservationLineItemId, Long walkInReservationId) throws RoomCheckoutException {
        ReservationLineItem reservationLineItem = em.find(ReservationLineItem.class, reservationLineItemId);
        WalkInReservation walkInReservation = em.find(WalkInReservation.class, walkInReservationId);

        try {
            if (walkInReservation.getCheckOutDate().equals(LocalDate.now()) || walkInReservation.getCheckOutDate().isBefore(LocalDate.now())) {
                List<Room> rooms = reservationLineItem.getRooms();
                for (Room room : rooms) {
                    room.setRoomStatus(RoomStatus.AVAILABLE);
                }
            } else if (walkInReservation.getCheckOutDate().isAfter(LocalDate.now())) {
                RoomType roomType = reservationLineItem.getRoomType();
                for (LocalDate date = LocalDate.now(); date.isBefore(walkInReservation.getCheckOutDate()); date = date.plusDays(1)) {
                    RoomInventory roomInventory = roomInventoryControllerLocal.retrieveRoomInventoryByDate(date, roomType.getRoomTypeId());
                    roomInventory.setNumRoomsLeft(roomInventory.getNumRoomsLeft() + 1);
                }
                List<Room> rooms = reservationLineItem.getRooms();
                for (Room room : rooms) {
                    room.setRoomStatus(RoomStatus.AVAILABLE);
                }
            }
        } catch (RoomInventoryNotFoundException ex) {
            throw new RoomCheckoutException(ex.getMessage());
        }
    }

}
