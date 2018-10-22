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
    
    private Date checkInDate;
    private Date checkOutDate;
    private ReservationType reservationType;
    private BigDecimal reservationAmt;
    private Integer numGuests;
    private String reservedBy;
    private Date createdDate;

    public Reservation() {
    }

    public Reservation(Date checkInDate, Date checkOutDate, ReservationType reservationType, BigDecimal reservationAmt, Integer numGuests, String reservedTo, Date creationDate) {
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.reservationType = reservationType;
        this.reservationAmt = reservationAmt;
        this.numGuests = numGuests;
        this.reservedBy = reservedTo;
        this.createdDate = creationDate;
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
    public Date getCheckInDate() {
        return checkInDate;
    }

    /**
     * @param checkInDate the checkInDate to set
     */
    public void setCheckInDate(Date checkInDate) {
        this.checkInDate = checkInDate;
    }

    /**
     * @return the checkOutDate
     */
    public Date getCheckOutDate() {
        return checkOutDate;
    }

    /**
     * @param checkOutDate the checkOutDate to set
     */
    public void setCheckOutDate(Date checkOutDate) {
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
     * @return the reservedBy
     */
    public String getReservedBy() {
        return reservedBy;
    }

    /**
     * @param reservedBy the reservedBy to set
     */
    public void setReservedBy(String reservedBy) {
        this.reservedBy = reservedBy;
    }

    /**
     * @return the createdDate
     */
    public Date getCreatedDate() {
        return createdDate;
    }

    /**
     * @param createdDate the createdDate to set
     */
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
    
}
