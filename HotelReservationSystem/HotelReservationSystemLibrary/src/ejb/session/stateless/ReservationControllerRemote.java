/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Reservation;
import entity.ReservationLineItem;
import entity.Room;
import entity.RoomNight;
import entity.RoomType;
import entity.WalkInReservation;
import java.math.BigDecimal;
import util.exception.ReservationNotFoundException;
import util.exception.RoomAllocationException;

public interface ReservationControllerRemote {

    public WalkInReservation createNewWalkInReservation(WalkInReservation newWalkInReservation);

    public ReservationLineItem createNewReservationLineItem(ReservationLineItem newReservationLineItem, Long walkInReservationId, Long roomTypeId);

    public RoomNight createNewRoomNight(RoomNight newRoomNight, Long roomTypeId, Long reservationLineItemId);

    public Room allocateRoom(Long roomTypeId, Long reservationLineItemId, Long walkInReservationId) throws RoomAllocationException;

    public BigDecimal calculateReservationLineAmount(Long reservationLineItemId);

    public Reservation retrieveReservationByReservationId(Long reservationId) throws ReservationNotFoundException;

    public RoomType retrieveRoomTypeByLineId(Long reservationLineItemId);
       
}
