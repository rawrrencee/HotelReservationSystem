/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author Lawrence
 */
@Entity
public class RoomType implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomTypeId;
    
    private String roomTypeName;
    private String roomTypeDescription;
    private Integer roomSize;
    private String bedInfo;
    private Integer capacity;
    private String amenities;

    public RoomType() {
    }

    public RoomType(String roomTypeName, String roomTypeDescription, Integer roomSize, String bedInfo, Integer capacity, String amenities) {
        this.roomTypeName = roomTypeName;
        this.roomTypeDescription = roomTypeDescription;
        this.roomSize = roomSize;
        this.bedInfo = bedInfo;
        this.capacity = capacity;
        this.amenities = amenities;
    }
    
    public Long getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(Long roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (roomTypeId != null ? roomTypeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the roomTypeId fields are not set
        if (!(object instanceof RoomType)) {
            return false;
        }
        RoomType other = (RoomType) object;
        if ((this.roomTypeId == null && other.roomTypeId != null) || (this.roomTypeId != null && !this.roomTypeId.equals(other.roomTypeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.RoomType[ id=" + roomTypeId + " ]";
    }

    /**
     * @return the roomTypeName
     */
    public String getRoomTypeName() {
        return roomTypeName;
    }

    /**
     * @param roomTypeName the roomTypeName to set
     */
    public void setRoomTypeName(String roomTypeName) {
        this.roomTypeName = roomTypeName;
    }

    /**
     * @return the roomTypeDescription
     */
    public String getRoomTypeDescription() {
        return roomTypeDescription;
    }

    /**
     * @param roomTypeDescription the roomTypeDescription to set
     */
    public void setRoomTypeDescription(String roomTypeDescription) {
        this.roomTypeDescription = roomTypeDescription;
    }

    /**
     * @return the roomSize
     */
    public Integer getRoomSize() {
        return roomSize;
    }

    /**
     * @param roomSize the roomSize to set
     */
    public void setRoomSize(Integer roomSize) {
        this.roomSize = roomSize;
    }

    /**
     * @return the bedInfo
     */
    public String getBedInfo() {
        return bedInfo;
    }

    /**
     * @param bedInfo the bedInfo to set
     */
    public void setBedInfo(String bedInfo) {
        this.bedInfo = bedInfo;
    }

    /**
     * @return the capacity
     */
    public Integer getCapacity() {
        return capacity;
    }

    /**
     * @param capacity the capacity to set
     */
    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    /**
     * @return the amenities
     */
    public String getAmenities() {
        return amenities;
    }

    /**
     * @param amenities the amenities to set
     */
    public void setAmenities(String amenities) {
        this.amenities = amenities;
    }
    
}
