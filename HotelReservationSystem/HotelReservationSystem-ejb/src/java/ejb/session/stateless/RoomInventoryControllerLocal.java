/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomInventory;
import entity.RoomRate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import util.exception.CheckRoomInventoryAvailabilityException;
import util.exception.CheckRoomInventoryException;
import util.exception.GeneralException;
import util.exception.RoomInventoryExistException;
import util.exception.RoomInventoryNotFoundException;

public interface RoomInventoryControllerLocal {
    
    public RoomInventory retrieveRoomInventoryByRoomInventoryId(Long roomInventoryId) throws RoomInventoryNotFoundException;
    
    public RoomInventory retrieveRoomInventoryByDate(Date date, Long roomTypeId) throws RoomInventoryNotFoundException;

    public RoomInventory createNewRoomInventory(Date date, Long roomTypeId) throws RoomInventoryExistException, GeneralException;
    
    public List<RoomInventory> retrieveRoomInventoriesByRoomType(Long roomTypeId) throws RoomInventoryNotFoundException;
    
    public List<RoomInventory> retrieveAllRoomInventoriesOnDate(Date date) throws RoomInventoryNotFoundException;
    
    public List<RoomRate> retrieveRoomRatesByTypeOfRoomInventory(Long roomInventoryId) throws RoomInventoryNotFoundException;
    
    public Boolean checkRoomInventoryOnDate(Calendar checkInDate, Calendar checkOutDate) throws CheckRoomInventoryException;
    
    public Boolean checkRoomInventoryAvailability(Calendar checkInDate, Calendar checkOutDate, Long roomTypeId, Integer numRoomsRequested) throws CheckRoomInventoryAvailabilityException;
}