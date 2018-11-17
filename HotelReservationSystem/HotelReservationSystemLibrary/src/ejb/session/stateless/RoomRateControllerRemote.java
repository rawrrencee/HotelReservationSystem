/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomRate;
import java.time.LocalDate;
import java.util.List;
import util.exception.GeneralException;
import util.exception.RoomRateExistException;
import util.exception.RoomRateNotFoundException;
import util.exception.RoomTypeNotFoundException;

public interface RoomRateControllerRemote {

    public List<RoomRate> retrieveAllRoomRates();
    
    public RoomRate createNewRoomRate(RoomRate newRoomRate, Long roomTypeId) throws RoomRateExistException, RoomTypeNotFoundException, GeneralException;
    
    public RoomRate retrieveRoomRateByRoomRateId(Long roomRateId) throws RoomRateNotFoundException;
    
    public void updateRoomRate(RoomRate roomRate, Long newRoomTypeId) throws RoomTypeNotFoundException;
    
    public Boolean deleteRoomRate(Long roomRateId) throws RoomRateNotFoundException;
    
    public RoomRate retrieveLowestPublishedRoomRate(Long roomTypeId);
    
    public RoomRate retrieveComplexRoomRate(Long roomTypeId, LocalDate date) throws RoomRateNotFoundException;
   
    
}
