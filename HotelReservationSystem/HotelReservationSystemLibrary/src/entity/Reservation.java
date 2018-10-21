/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import util.enumeration.ReservationType;

/**
 *
 * @author Lawrence
 */
@Entity
public class Reservation implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;
    
    private LocalDateTime checkInDate;
    private LocalDateTime checkOutDate;
    private ReservationType reservationType;
    private BigDecimal reservationAmt;
    private Integer numGuests;
    private String reservedTo;
    private LocalDateTime creationDate;

    public Reservation() {
    }

    public Reservation(LocalDateTime checkInDate, LocalDateTime checkOutDate, ReservationType reservationType, BigDecimal reservationAmt, Integer numGuests, String reservedTo, LocalDateTime creationDate) {
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.reservationType = reservationType;
        this.reservationAmt = reservationAmt;
        this.numGuests = numGuests;
        this.reservedTo = reservedTo;
        this.creationDate = creationDate;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (reservationId != null ? reservationId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the reservationId fields are not set
        if (!(object instanceof Reservation)) {
            return false;
        }
        Reservation other = (Reservation) object;
        if ((this.reservationId == null && other.reservationId != null) || (this.reservationId != null && !this.reservationId.equals(other.reservationId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Reservation[ id=" + reservationId + " ]";
    }

    /**
     * @return the checkInDate
     */
    public LocalDateTime getCheckInDate() {
        return checkInDate;
    }

    /**
     * @param checkInDate the checkInDate to set
     */
    public void setCheckInDate(LocalDateTime checkInDate) {
        this.checkInDate = checkInDate;
    }

    /**
     * @return the checkOutDate
     */
    public LocalDateTime getCheckOutDate() {
        return checkOutDate;
    }

    /**
     * @param checkOutDate the checkOutDate to set
     */
    public void setCheckOutDate(LocalDateTime checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    /**
     * @return the reservationType
     */
    public ReservationType getReservationType() {
        return reservationType;
    }

    /**
     * @param reservationType the reservationType to set
     */
    public void setReservationType(ReservationType reservationType) {
        this.reservationType = reservationType;
    }

    /**
     * @return the reservationAmt
     */
    public BigDecimal getReservationAmt() {
        return reservationAmt;
    }

    /**
     * @param reservationAmt the reservationAmt to set
     */
    public void setReservationAmt(BigDecimal reservationAmt) {
        this.reservationAmt = reservationAmt;
    }

    /**
     * @return the numGuests
     */
    public Integer getNumGuests() {
        return numGuests;
    }

    /**
     * @param numGuests the numGuests to set
     */
    public void setNumGuests(Integer numGuests) {
        this.numGuests = numGuests;
    }

    /**
     * @return the reservedTo
     */
    public String getReservedTo() {
        return reservedTo;
    }

    /**
     * @param reservedTo the reservedTo to set
     */
    public void setReservedTo(String reservedTo) {
        this.reservedTo = reservedTo;
    }

    /**
     * @return the creationDate
     */
    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    /**
     * @param creationDate the creationDate to set
     */
    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }
    
}
