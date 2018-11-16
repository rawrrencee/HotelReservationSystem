/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

    /**
     * @return the registeredGuest
     */
    public RegisteredGuest getRegisteredGuest() {
        return registeredGuest;
    }

    /**
     * @param registeredGuest the registeredGuest to set
     */
    public void setRegisteredGuest(RegisteredGuest registeredGuest) {
        this.registeredGuest = registeredGuest;
    }

    private static final long serialVersionUID = 1L;
    
    @ManyToOne
    private RegisteredGuest registeredGuest;

    public OnlineReservation() {
    }

    public OnlineReservation(LocalDate checkInDate, LocalDate checkOutDate, BigDecimal reservationAmt, Integer numGuests, LocalDateTime createdDate) throws IncorrectDateException{
                
        this();
        
        if (checkInDate.isAfter(createdDate.toLocalDate())) {
        this.checkInDate = Date.from(checkInDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        } else {
            throw new IncorrectDateException("Cannot make reservation for a date in the past!");
        }
        if (checkOutDate.isAfter(checkInDate)) {
        this.checkOutDate = Date.from(checkOutDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        } else {
            throw new IncorrectDateException("Check-out Date cannot be before Check-in Date.");
        }
        this.reservationAmt = reservationAmt;
        this.numGuests = numGuests;
        if (createdDate.toLocalDate().isBefore(checkInDate)) {
        this.createdDate = Date.from(createdDate.atZone(ZoneId.systemDefault()).toInstant());
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
