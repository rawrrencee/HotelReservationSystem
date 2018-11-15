/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import javax.persistence.Entity;

/**
 *
 * @author Lawrence
 */
@Entity
public class PromoRoomRate extends RoomRate implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private Date startDate;
    private Date endDate;

    public PromoRoomRate() {
        super();
    }
    
    public PromoRoomRate(LocalDateTime startDate, LocalDateTime endDate, String roomRateName, BigDecimal ratePerNight, Boolean isDisabled) {
        this.startDate = Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant());
        this.endDate = Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant());
        this.roomRateName = roomRateName;
        this.ratePerNight = ratePerNight;
        this.isEnabled = isDisabled;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (roomRateId != null ? roomRateId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the roomRateId fields are not set
        if (!(object instanceof PromoRoomRate)) {
            return false;
        }
        PromoRoomRate other = (PromoRoomRate) object;
        if ((this.roomRateId == null && other.roomRateId != null) || (this.roomRateId != null && !this.roomRateId.equals(other.roomRateId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.PromoRoomRate[ roomRateId=" + roomRateId + " ]";
    }

    /**
     * @return the startDate
     */
    public LocalDateTime getStartDate() {
        return LocalDateTime.ofInstant(startDate.toInstant(), ZoneId.systemDefault());
    }

    /**
     * @param startDate the startDate to set
     */
    public void setStartDate(LocalDateTime startDate) {
        this.startDate = Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * @return the endDate
     */
    public LocalDateTime getEndDate() {
        return LocalDateTime.ofInstant(endDate.toInstant(), ZoneId.systemDefault());
    }

    /**
     * @param endDate the endDate to set
     */
    public void setEndDate(LocalDateTime endDate) {
        this.endDate = Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant());
    }
    
}
