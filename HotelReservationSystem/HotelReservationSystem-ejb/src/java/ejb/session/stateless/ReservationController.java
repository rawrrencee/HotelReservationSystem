/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.OnlineReservation;
import entity.PartnerReservation;
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
    public List<Reservation> retrieveAllReservationsByPartner(Long partnerId) throws ReservationNotFoundException{
        Query query = em.createQuery("SELECT p FROM PartnerReservation p WHERE p.partner.partnerId = :inPartnerId");
        query.setParameter("inPartnerId",partnerId);
        
        if(!query.getResultList().isEmpty()){
            return query.getResultList();
        }else{
            throw new ReservationNotFoundException("No results in query List.");
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
    public PartnerReservation createNewPartnerReservation(PartnerReservation newPartnerReservation){
        em.persist(newPartnerReservation);
        em.flush();
        em.refresh(newPartnerReservation);
        
        return newPartnerReservation;
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
    public ReservationLineItem createNewPartnerReservationLineItem(ReservationLineItem newReservationLineItem, Long partnerReservationId, Long roomTypeId) throws LineCreationException {
        newReservationLineItem.setRoomType(em.find(RoomType.class, roomTypeId));
        em.persist(newReservationLineItem);
        PartnerReservation partnerReservation = em.find(PartnerReservation.class, partnerReservationId);
        partnerReservation.getReservationLineItems().add(newReservationLineItem);
        LocalDate checkInDate = partnerReservation.getCheckInDate();
        LocalDate checkOutDate = partnerReservation.getCheckOutDate();
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
    public RoomNight createNewPartnerRoomNight(RoomNight newRoomNight, Long roomTypeId, Long reservationLineItemId) throws CreateRoomNightException{
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
    public Room allocateRoom(Long roomTypeId, Long reservationLineItemId, Long reservationId) throws RoomAllocationException {
        ReservationLineItem reservationLineItem = em.find(ReservationLineItem.class, reservationLineItemId);
        Reservation reservation = em.find(Reservation.class, reservationId);
        
        if (reservation.getCheckedOut()) {
            throw new RoomAllocationException("Reservation has already been checked-in before. Please make a new reservation.");
        }

        try {
            Room room = roomControllerLocal.retrieveFirstAvailableRoomOfRoomType(roomTypeId);
            room.setRoomStatus(RoomStatus.ALLOCATED);
            reservationLineItem.getRooms().add(room);
            return room;
        } catch (NoResultException ex) {
            throw new RoomAllocationException("No available rooms of room type!");
        }
    }

    @Override
    public void processCheckout(Long reservationLineItemId, Long reservationId) throws RoomCheckoutException {
        ReservationLineItem reservationLineItem = em.find(ReservationLineItem.class, reservationLineItemId);
        Reservation reservation = em.find(Reservation.class, reservationId);

        try {
            if (reservation.getCheckOutDate().equals(LocalDate.now()) || reservation.getCheckOutDate().isBefore(LocalDate.now())) {
                RoomType roomType = reservationLineItem.getRoomType();
                RoomInventory roomInventory = roomInventoryControllerLocal.retrieveRoomInventoryByDate(LocalDate.now(), roomType.getRoomTypeId());
                roomInventory.setNumRoomsLeft(roomInventory.getNumRoomsLeft() + reservationLineItem.getNumRoomsRequested());
                List<Room> rooms = reservationLineItem.getRooms();
                for (Room room : rooms) {
                    room.setRoomStatus(RoomStatus.AVAILABLE);
                }
                reservationLineItem.getRooms().clear();
            } else if (reservation.getCheckOutDate().isAfter(LocalDate.now())) {
                RoomType roomType = reservationLineItem.getRoomType();
                for (LocalDate date = LocalDate.now(); date.isBefore(reservation.getCheckOutDate()); date = date.plusDays(1)) {
                    RoomInventory roomInventory = roomInventoryControllerLocal.retrieveRoomInventoryByDate(date, roomType.getRoomTypeId());
                    roomInventory.setNumRoomsLeft(roomInventory.getNumRoomsLeft() + reservationLineItem.getNumRoomsRequested());
                }
                List<Room> rooms = reservationLineItem.getRooms();
                for (Room room : rooms) {
                    room.setRoomStatus(RoomStatus.AVAILABLE);
                }
                reservationLineItem.getRooms().clear();
            }
            reservation.setCheckedOut(Boolean.TRUE);
        } catch (RoomInventoryNotFoundException ex) {
            throw new RoomCheckoutException(ex.getMessage());
        }
    }

}
