/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import ejb.session.stateless.RoomSessionBeanLocal;
import entity.Room;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;

/**
 *
 * @author Lawrence
 */
@Singleton
@LocalBean
@Startup

public class InitSessionBean {

    @EJB
    private RoomSessionBeanLocal roomSessionBeanLocal;
    
    @PostConstruct
    public void PostConstruct() {
        roomSessionBeanLocal.createRoom(new Room("Room 1"));
    }
}
