/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import util.enumeration.RoomStatus;

/**
 *
 * @author Lawrence
 */
@Entity
public class Room implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;
    @Column(nullable = false, length = 10)
    private Integer roomNumber;
    @Enumerated(EnumType.STRING)
    private RoomStatus roomStatus;
    
    @ManyToMany(mappedBy="rooms")
    private List<ReservationLineItem> reservationLineItems;
    
    @ManyToOne
    private RoomType roomType;
    
    public Room() {
    }

    public Room(Integer roomNumber, RoomStatus roomStatus) {
        this.roomNumber = roomNumber;
        this.roomStatus = roomStatus;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (roomId != null ? roomId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the roomId fields are not set
        if (!(object instanceof Room)) {
            return false;
        }
        Room other = (Room) object;
        if ((this.roomId == null && other.roomId != null) || (this.roomId != null && !this.roomId.equals(other.roomId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Room[ id=" + roomId + " ]";
    }

    /**
     * @return the roomNumber
     */
    public Integer getRoomNumber() {
        return roomNumber;
    }

    /**
     * @param roomNumber the roomNumber to set
     */
    public void setRoomNumber(Integer roomNumber) {
        this.roomNumber = roomNumber;
    }

    /**
     * @return the roomStatus
     */
    public RoomStatus getRoomStatus() {
        return roomStatus;
    }

    /**
     * @param roomStatus the roomStatus to set
     */
    public void setRoomStatus(RoomStatus roomStatus) {
        this.roomStatus = roomStatus;
    }
    /**
     * @return the roomType
     */
    public RoomType getRoomType() {
        return roomType;
    }

    /**
     * @param roomType the roomType to set
     */
    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    /**
     * @return the reservationLineItems
     */
    public List<ReservationLineItem> getReservationLineItems() {
        return reservationLineItems;
    }

    /**
     * @param reservationLineItems the reservationLineItems to set
     */
    public void setReservationLineItems(List<ReservationLineItem> reservationLineItems) {
        this.reservationLineItems = reservationLineItems;
    }
    
}
