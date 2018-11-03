/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomRate;
import java.util.List;
import util.exception.GeneralException;
import util.exception.RoomRateExistException;
import util.exception.RoomTypeNotFoundException;

public interface RoomRateControllerLocal {
    
    public List<RoomRate> retrieveAllRoomRates();
    
    public RoomRate createNewRoomRate(RoomRate newRoomRate, Long roomTypeId) throws RoomRateExistException, RoomTypeNotFoundException, GeneralException;
}
