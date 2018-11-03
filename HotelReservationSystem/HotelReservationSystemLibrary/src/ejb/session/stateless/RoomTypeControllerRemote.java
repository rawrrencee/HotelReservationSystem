/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomType;
import java.util.List;
import util.exception.GeneralException;
import util.exception.RoomTypeExistException;
import util.exception.RoomTypeNotFoundException;

public interface RoomTypeControllerRemote {

    public List<RoomType> retrieveAllRoomTypes();

    public RoomType retrieveRoomTypeByRoomTypeId(Long roomTypeId) throws RoomTypeNotFoundException;

    public RoomType createNewRoomType(RoomType roomType) throws RoomTypeExistException, GeneralException;

    public Boolean checkRoomTypeExists(String roomTypeName) throws RoomTypeNotFoundException;

    public void updateRoomType(RoomType roomType);
    
}
