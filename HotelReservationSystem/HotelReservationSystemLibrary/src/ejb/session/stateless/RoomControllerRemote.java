/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Room;
import java.util.List;
import util.exception.GeneralException;
import util.exception.RoomExistException;
import util.exception.RoomNotFoundException;
import util.exception.RoomTypeNotFoundException;

public interface RoomControllerRemote {

    public List<Room> retrieveAllRooms();

    public Room retrieveRoomByRoomNumber(Integer roomNumber) throws RoomNotFoundException;

    public Room createNewRoom(Room newRoom, Long roomTypeId) throws RoomTypeNotFoundException, RoomExistException, GeneralException;

    public Boolean checkRoomExistsByRoomNumber(Integer roomNumber);
    
}
