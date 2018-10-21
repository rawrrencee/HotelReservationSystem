/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsreservationclient;

import ejb.session.stateless.RoomSessionBeanRemote;
import entity.Room;
import java.util.List;
import javax.ejb.EJB;

/**
 *
 * @author Lawrence
 */
public class Main {

    @EJB
    private static RoomSessionBeanRemote roomSessionBeanRemote;
    
    public static void main(String[] args) {
        List<Room> rooms = roomSessionBeanRemote.retrieveAllRooms();
        
        for (Room room:rooms) {
            System.out.println(room.getRoomId() + ": " + room.getRoomName()); 
        }
    }
    
}
