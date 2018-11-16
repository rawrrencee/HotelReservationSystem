/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

/**
 *
 * @author Lawrence
 */
@Entity
public class RegisteredGuest extends Guest implements Serializable {
    
    @Column (nullable = false)
    private String username;
    @Column (nullable = false)
    private String password;
    
    @OneToMany(mappedBy="registeredGuest")
    private List<OnlineReservation> onlineReservations;

    public RegisteredGuest() {
    }

    public RegisteredGuest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public RegisteredGuest(String username, String password, String firstName, String lastName, String emailAdd, Integer phoneNum, String passportNum) {
        
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAdd = emailAdd;
        this.phoneNum = phoneNum;
        this.passportNum = passportNum;
        
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (guestId != null ? guestId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the guestId fields are not set
        if (!(object instanceof RegisteredGuest)) {
            return false;
        }
        RegisteredGuest other = (RegisteredGuest) object;
        if ((this.guestId == null && other.guestId != null) || (this.guestId != null && !this.guestId.equals(other.guestId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.RegisteredGuest[ id=" + guestId + " ]";
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the onlineReservations
     */
    public List<OnlineReservation> getOnlineReservations() {
        return onlineReservations;
    }

    /**
     * @param onlineReservations the onlineReservations to set
     */
    public void setOnlineReservations(List<OnlineReservation> onlineReservations) {
        this.onlineReservations = onlineReservations;
    }
    
}
