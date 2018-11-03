/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Room;
import java.util.List;

public interface RoomControllerLocal {
    
    public List<Room> retrieveAllRooms();
    
}
