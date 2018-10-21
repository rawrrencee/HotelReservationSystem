/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Room;
import java.util.List;

/**
 *
 * @author Lawrence
 */
public interface RoomSessionBeanRemote {
   
    public Long createRoom(Room newRoom);

    public List<Room> retrieveAllRooms();
    
}
