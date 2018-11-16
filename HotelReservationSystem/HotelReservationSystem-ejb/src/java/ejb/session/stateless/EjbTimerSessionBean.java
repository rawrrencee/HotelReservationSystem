/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.ExceptionReport;
import entity.Reservation;
import entity.ReservationLineItem;
import entity.Room;
import entity.RoomInventory;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.enumeration.RoomStatus;
import util.exception.RoomAllocationException;
import util.exception.RoomInventoryNotFoundException;

/**
 *
 * @author Lawrence
 */
@Stateless
public class EjbTimerSessionBean implements EjbTimerSessionBeanRemote, EjbTimerSessionBeanLocal {

    @EJB
    private RoomInventoryControllerLocal roomInventoryControllerLocal;

    @EJB
    private RoomControllerLocal roomControllerLocal;

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    
    @Schedule(hour = "11", minute ="50", info = "roomAllocationTimer")
    public void allocateRoom() {
        Query query = em.createQuery("SELECT r FROM Reservation r WHERE r.checkInDate = :inCheckInDate AND r.reservationLineItems.rooms IS NULL");
        query.setParameter("inCheckInDate", Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        List<Reservation> reservations = (List<Reservation>) query.getResultList();
        
        if (reservations.isEmpty()) {
            return;
        }
        
        for (Reservation reservation : reservations) {
            List<ReservationLineItem> reservationLineItems = reservation.getReservationLineItems();
            for (ReservationLineItem reservationLineItem : reservationLineItems) {
                try {
                    Long roomTypeId = reservationLineItem.getRoomType().getRoomTypeId();
                    Room room = roomControllerLocal.retrieveFirstAvailableRoomOfRoomType(roomTypeId);
                    room.setRoomStatus(RoomStatus.ALLOCATED);
                    reservationLineItem.getRooms().add(room);

                    for (LocalDate date = reservation.getCheckInDate(); date.isBefore(reservation.getCheckOutDate()); date = date.plusDays(1)) {
                        System.out.println("Current date for allocation: " + date);
                        try {
                            System.out.println("Current date for allocation: " + date);
                            RoomInventory roomInventory = roomInventoryControllerLocal.retrieveRoomInventoryByDate(date, roomTypeId);
                            System.out.println("room Inventory id is " + roomInventory.getRoomInventoryId());
                            if (roomInventory.getNumRoomsLeft() == 0) {
                                ExceptionReport exceptionReport = new ExceptionReport();
                                exceptionReport.setReservationId(reservation.getReservationId());
                                exceptionReport.setExReportBody("Allocation of room failed for reservation ID: " + reservation.getReservationId() + " due to no room availability.");
                                exceptionReport.setReservation(reservation);
                                em.persist(exceptionReport);
                                em.flush();
                            }
                            roomInventory.setNumRoomsLeft(roomInventory.getNumRoomsLeft() - 1);
                        } catch (RoomInventoryNotFoundException ex) {
                            ExceptionReport exceptionReport = new ExceptionReport();
                            exceptionReport.setReservationId(reservation.getReservationId());
                            exceptionReport.setExReportBody("Allocation of room failed for reservation ID: " + reservation.getReservationId() + " due to no room availability.");
                            exceptionReport.setReservation(reservation);
                            em.persist(exceptionReport);
                            em.flush();
                        }
                    }
                } catch (NoResultException ex) {
                    ExceptionReport exceptionReport = new ExceptionReport();
                    exceptionReport.setReservationId(reservation.getReservationId());
                    exceptionReport.setExReportBody("Allocation of room failed for reservation ID: " + reservation.getReservationId() + " due to no room availability.");
                    exceptionReport.setReservation(reservation);
                    em.persist(exceptionReport);
                    em.flush();
                }
            }
        }

    }
}
