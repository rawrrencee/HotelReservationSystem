/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
/**
 *
 * @author Lawrence
 */
@Entity
@Inheritance(strategy= InheritanceType.JOINED)
public abstract class Reservation implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long reservationId;
    
    protected Date checkInDate;
    protected Date checkOutDate;
    @Column(precision = 11, scale = 2)
    protected BigDecimal reservationAmt;
    protected Integer numGuests;
    protected Date createdDate;
    
    @ManyToOne
    private Guest guest;
    
    @OneToMany
    private List<ReservationLineItem> reservationLineItems;

    public Reservation() {
        reservationLineItems = new ArrayList<>();
    }

    public Reservation(Date checkInDate, Date checkOutDate, BigDecimal reservationAmt, Integer numGuests, Date creationDate) {
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.reservationAmt = reservationAmt;
        this.numGuests = numGuests;
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

    /**
     * @return the guest
     */
    public Guest getGuest() {
        return guest;
    }

    /**
     * @param guest the guest to set
     */
    public void setGuest(Guest guest) {
        this.guest = guest;
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
