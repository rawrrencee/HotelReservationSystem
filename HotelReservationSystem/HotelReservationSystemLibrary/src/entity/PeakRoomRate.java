/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Entity;

/**
 *
 * @author Lawrence
 */
@Entity
public class PeakRoomRate extends RoomRate implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private Date startDate;
    private Date endDate;

    public PeakRoomRate() {
    }

    public PeakRoomRate(Date startDate, Date endDate, String roomRateName, BigDecimal ratePerNight, Boolean isDisabled) {
        this.startDate = startDate;
        this.endDate = endDate;
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
        if (!(object instanceof PeakRoomRate)) {
            return false;
        }
        PeakRoomRate other = (PeakRoomRate) object;
        if ((this.roomRateId == null && other.roomRateId != null) || (this.roomRateId != null && !this.roomRateId.equals(other.roomRateId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.PeakRoomRate[ roomRateId=" + roomRateId + " ]";
    }

    /**
     * @return the startDate
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * @param startDate the startDate to set
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * @return the endDate
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * @param endDate the endDate to set
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    
}
