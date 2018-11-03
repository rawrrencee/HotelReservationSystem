/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 *
 * @author Lawrence
 */
@Entity
public class RoomInventory implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomInventoryId;
    
    private Date date;
    
    @ManyToOne
    private RoomType roomType;

    public RoomInventory() {
    }

    public RoomInventory(Date date) {
        this.date = date;
    }

    public Long getRoomInventoryId() {
        return roomInventoryId;
    }

    public void setRoomInventoryId(Long roomInventoryId) {
        this.roomInventoryId = roomInventoryId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (roomInventoryId != null ? roomInventoryId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the roomInventoryId fields are not set
        if (!(object instanceof RoomInventory)) {
            return false;
        }
        RoomInventory other = (RoomInventory) object;
        if ((this.roomInventoryId == null && other.roomInventoryId != null) || (this.roomInventoryId != null && !this.roomInventoryId.equals(other.roomInventoryId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.RoomInventory[ id=" + roomInventoryId + " ]";
    }

    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(Date date) {
        this.date = date;
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
    
}
