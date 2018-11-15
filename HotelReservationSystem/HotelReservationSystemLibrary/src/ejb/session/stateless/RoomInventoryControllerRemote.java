/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomInventory;
import entity.RoomRate;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import util.exception.CheckRoomInventoryAvailabilityException;
import util.exception.CheckRoomInventoryException;
import util.exception.GeneralException;
import util.exception.RoomInventoryExistException;
import util.exception.RoomInventoryNotFoundException;

public interface RoomInventoryControllerRemote {

    public RoomInventory retrieveRoomInventoryByRoomInventoryId(Long roomInventoryId) throws RoomInventoryNotFoundException;

    public RoomInventory retrieveRoomInventoryByDate(LocalDate date, Long roomTypeId) throws RoomInventoryNotFoundException;
    
    public RoomInventory createNewRoomInventory(LocalDate date, Long roomTypeId) throws RoomInventoryExistException, GeneralException;

    public List<RoomInventory> retrieveRoomInventoriesByRoomType(Long roomTypeId) throws RoomInventoryNotFoundException;

    public List<RoomInventory> retrieveAllRoomInventoriesOnDate(LocalDate date) throws RoomInventoryNotFoundException;

    public List<RoomRate> retrieveRoomRatesByTypeOfRoomInventory(Long roomInventoryId) throws RoomInventoryNotFoundException;

    public Boolean checkRoomInventoryOnDate(LocalDate checkInDate, LocalDate checkOutDate) throws CheckRoomInventoryException;

    public Boolean checkRoomInventoryAvailability(LocalDate checkInDate, LocalDate checkOutDate, Long roomTypeId, Integer numRoomsRequested) throws CheckRoomInventoryAvailabilityException;
    
}
