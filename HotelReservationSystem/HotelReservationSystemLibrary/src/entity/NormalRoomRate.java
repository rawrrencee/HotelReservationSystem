/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Entity;

/**
 *
 * @author Lawrence
 */
@Entity
public class NormalRoomRate extends RoomRate implements Serializable {

    private static final long serialVersionUID = 1L;

    public NormalRoomRate() {
    }

    public NormalRoomRate(String roomRateName, BigDecimal ratePerNight, Boolean isDisabled) {
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
        if (!(object instanceof NormalRoomRate)) {
            return false;
        }
        NormalRoomRate other = (NormalRoomRate) object;
        if ((this.roomRateId == null && other.roomRateId != null) || (this.roomRateId != null && !this.roomRateId.equals(other.roomRateId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.NormalRoomRate[ roomRateId=" + roomRateId + " ]";
    }
    
}
