/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 *
 * @author Lawrence
 */
@Entity
public class ReservationLineItem implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationLineItemId;
    @Column (precision = 11, scale = 2)
    private BigDecimal amount;
    @Column (length = 10)
    private Integer numRoomsRequested;
    
    @ManyToOne
    private RoomType roomType;
    
    @OneToMany(mappedBy="reservationLineItem")
    private List<RoomNight> roomNights;
    
    @ManyToMany
    private List<Room> rooms;

    public ReservationLineItem() {
        roomNights = new ArrayList<>();
        rooms = new ArrayList<>();
    }

    public ReservationLineItem(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public List<RoomNight> getRoomNights() {
        return roomNights;
    }

    public void setRoomNights(List<RoomNight> roomNights) {
        this.roomNights = roomNights;
    }

    public Long getReservationLineItemId() {
        return reservationLineItemId;
    }

    public void setReservationLineItemId(Long reservationLineItemId) {
        this.reservationLineItemId = reservationLineItemId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (reservationLineItemId != null ? reservationLineItemId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the reservationLineItemId fields are not set
        if (!(object instanceof ReservationLineItem)) {
            return false;
        }
        ReservationLineItem other = (ReservationLineItem) object;
        if ((this.reservationLineItemId == null && other.reservationLineItemId != null) || (this.reservationLineItemId != null && !this.reservationLineItemId.equals(other.reservationLineItemId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.ReservationLineItem[ id=" + reservationLineItemId + " ]";
    }

    /**
     * @return the rooms
     */
    public List<Room> getRooms() {
        return rooms;
    }

    /**
     * @param rooms the rooms to set
     */
    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    /**
     * @return the numRoomsRequested
     */
    public Integer getNumRoomsRequested() {
        return numRoomsRequested;
    }

    /**
     * @param numRoomsRequested the numRoomsRequested to set
     */
    public void setNumRoomsRequested(Integer numRoomsRequested) {
        this.numRoomsRequested = numRoomsRequested;
    }
    
}
