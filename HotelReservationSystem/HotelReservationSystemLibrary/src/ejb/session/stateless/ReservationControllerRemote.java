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
import entity.RoomNight;
import entity.RoomType;
import entity.WalkInReservation;
import java.math.BigDecimal;
import java.util.List;
import util.exception.CreateRoomNightException;
import util.exception.LineCalculationException;
import util.exception.LineCreationException;
import util.exception.ReservationNotFoundException;
import util.exception.RoomAllocationException;
import util.exception.RoomCheckoutException;

public interface ReservationControllerRemote {

    public WalkInReservation createNewWalkInReservation(WalkInReservation newWalkInReservation);

    public ReservationLineItem createNewReservationLineItem(ReservationLineItem newReservationLineItem, Long walkInReservationId, Long roomTypeId) throws LineCreationException;

    public RoomNight createNewRoomNight(RoomNight newRoomNight, Long roomTypeId, Long reservationLineItemId);

    public Room allocateRoom(Long roomTypeId, Long reservationLineItemId, Long walkInReservationId) throws RoomAllocationException;

    public BigDecimal calculateReservationLineAmount(Long reservationLineItemId) throws LineCalculationException ;

    public Reservation retrieveReservationByReservationId(Long reservationId) throws ReservationNotFoundException;

    public RoomType retrieveRoomTypeByLineId(Long reservationLineItemId);

    public void processCheckout(Long reservationLineItemId, Long walkInReservationId) throws RoomCheckoutException;

    public void setReservationAmount(Long reservationId, BigDecimal amount);

    public OnlineReservation createNewOnlineReservation(OnlineReservation newOnlineReservation);
    
    public ReservationLineItem createNewOnlineReservationLineItem(ReservationLineItem newReservationLineItem, Long onlineReservationId, Long roomTypeId) throws LineCreationException;
    
    public List<Reservation> retrieveAllReservationsByRegisteredGuest(Long registeredGuestId) throws ReservationNotFoundException;

    public RoomNight createOnlineNewRoomNight(RoomNight newRoomNight, Long roomTypeId, Long reservationLineItemId) throws CreateRoomNightException;

    
       
}
