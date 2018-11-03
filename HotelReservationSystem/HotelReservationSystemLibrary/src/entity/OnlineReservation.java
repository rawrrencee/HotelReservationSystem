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
import javax.persistence.ManyToOne;
import util.exception.IncorrectDateException;

/**
 *
 * @author Lawrence
 */
@Entity
public class OnlineReservation extends Reservation implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @ManyToOne
    private RegisteredGuest registeredGuest;

    public OnlineReservation() {
    }

    public OnlineReservation(Date checkInDate, Date checkOutDate, BigDecimal reservationAmt, Integer numGuests, Date creationDate) throws IncorrectDateException{
                
        this();
        
        if (checkInDate.after(createdDate)) {
        this.checkInDate = checkInDate;
        } else {
            throw new IncorrectDateException("Cannot make reservation for a date in the past!");
        }
        if (checkOutDate.after(checkInDate)) {
        this.checkOutDate = checkOutDate;
        } else {
            throw new IncorrectDateException("Check-out Date cannot be before Check-in Date.");
        }
        this.reservationAmt = reservationAmt;
        this.numGuests = numGuests;
        if (createdDate.before(checkInDate)) {
        this.createdDate = createdDate;
        } else {
            throw new IncorrectDateException("Cannot make reservation for a date in the past!");
        }
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (reservationId != null ? reservationId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof OnlineReservation)) {
            return false;
        }
        OnlineReservation other = (OnlineReservation) object;
        if ((this.reservationId == null && other.reservationId != null) || (this.reservationId != null && !this.reservationId.equals(other.reservationId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.OnlineReservation[ reservationId=" + reservationId + " ]";
    }
    
}
