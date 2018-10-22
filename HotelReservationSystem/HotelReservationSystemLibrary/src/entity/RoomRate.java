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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import util.enumeration.RoomRateEnum;

/**
 *
 * @author Lawrence
 */
@Entity
public class RoomRate implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomRateId;
    
    private String roomRateName;
    private Date rateStartDate;
    private Date rateEndDate;
    private RoomRateEnum roomRateEnum;
    private BigDecimal ratePerNight;
    private Boolean isDisabled;

    public RoomRate() {
    }

    public RoomRate(String roomRateName, Date rateStartDate, Date rateEndDate, RoomRateEnum roomRateEnum, BigDecimal ratePerNight, Boolean isDisabled) {
        this.roomRateName = roomRateName;
        this.rateStartDate = rateStartDate;
        this.rateEndDate = rateEndDate;
        this.roomRateEnum = roomRateEnum;
        this.ratePerNight = ratePerNight;
        this.isDisabled = isDisabled;
    }

    public Long getRoomRateId() {
        return roomRateId;
    }

    public void setRoomRateId(Long roomRateId) {
        this.roomRateId = roomRateId;
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
        if (!(object instanceof RoomRate)) {
            return false;
        }
        RoomRate other = (RoomRate) object;
        if ((this.roomRateId == null && other.roomRateId != null) || (this.roomRateId != null && !this.roomRateId.equals(other.roomRateId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.RoomRate[ id=" + roomRateId + " ]";
    }

    /**
     * @return the roomRateName
     */
    public String getRoomRateName() {
        return roomRateName;
    }

    /**
     * @param roomRateName the roomRateName to set
     */
    public void setRoomRateName(String roomRateName) {
        this.roomRateName = roomRateName;
    }

    /**
     * @return the rateStartDate
     */
    public Date getRateStartDate() {
        return rateStartDate;
    }

    /**
     * @param rateStartDate the rateStartDate to set
     */
    public void setRateStartDate(Date rateStartDate) {
        this.rateStartDate = rateStartDate;
    }

    /**
     * @return the rateEndDate
     */
    public Date getRateEndDate() {
        return rateEndDate;
    }

    /**
     * @param rateEndDate the rateEndDate to set
     */
    public void setRateEndDate(Date rateEndDate) {
        this.rateEndDate = rateEndDate;
    }

    /**
     * @return the roomRateEnum
     */
    public RoomRateEnum getRoomRateEnum() {
        return roomRateEnum;
    }

    /**
     * @param roomRateEnum the roomRateEnum to set
     */
    public void setRoomRateEnum(RoomRateEnum roomRateEnum) {
        this.roomRateEnum = roomRateEnum;
    }

    /**
     * @return the ratePerNight
     */
    public BigDecimal getRatePerNight() {
        return ratePerNight;
    }

    /**
     * @param ratePerNight the ratePerNight to set
     */
    public void setRatePerNight(BigDecimal ratePerNight) {
        this.ratePerNight = ratePerNight;
    }

    /**
     * @return the isDisabled
     */
    public Boolean getIsDisabled() {
        return isDisabled;
    }

    /**
     * @param isDisabled the isDisabled to set
     */
    public void setIsDisabled(Boolean isDisabled) {
        this.isDisabled = isDisabled;
    }
    
}
