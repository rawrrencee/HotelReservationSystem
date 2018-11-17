/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import ejb.session.stateless.EmployeeControllerLocal;
import ejb.session.stateless.RoomControllerLocal;
import ejb.session.stateless.RoomRateControllerLocal;
import ejb.session.stateless.RoomTypeControllerLocal;
import entity.Employee;
import entity.NormalRoomRate;
import entity.PublishedRoomRate;
import entity.Room;
import entity.RoomType;
import java.math.BigDecimal;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import util.enumeration.EmployeeAccessRightEnum;
import util.enumeration.RoomStatus;
import util.exception.EmployeeNotFoundException;
import util.exception.RoomTypeNotFoundException;

/**
 *
 * @author Lawrence
 */
@Singleton
@LocalBean
@Startup
public class InitSessionBean {

    @EJB
    private RoomRateControllerLocal roomRateControllerLocal1;

    @EJB
    private RoomControllerLocal roomControllerLocal;

    @EJB
    private RoomRateControllerLocal roomRateControllerLocal;

    @EJB
    private RoomTypeControllerLocal roomTypeControllerLocal;

    @EJB
    private EmployeeControllerLocal employeeControllerLocal;

    @PostConstruct
    public void PostConstruct() {

        try {
            employeeControllerLocal.retrieveEmployeeByUsername("sysadmin");
            employeeControllerLocal.retrieveEmployeeByUsername("opmanager");
            roomTypeControllerLocal.retrieveRoomTypeByRoomTypeId(Long.valueOf(1));
        } catch (EmployeeNotFoundException | RoomTypeNotFoundException ex) {
            initialiseData();
        }
    }

    private void initialiseData() {
        try {
            employeeControllerLocal.createNewEmployee(new Employee("sysadmin", "password", "Default", "System Administrator", "S0000001A", "90123456", "Singapore Address Line 1", "Singapore Address Line 2", "600001", EmployeeAccessRightEnum.SYSADMIN));
            employeeControllerLocal.createNewEmployee(new Employee("opmanager", "password", "OPERATION", "MANAGER", "S0000002A", "90123457", "Singapore Address Line 1", "Singapore Address Line 2", "600002", EmployeeAccessRightEnum.OPMANAGER));
            employeeControllerLocal.createNewEmployee(new Employee("guestreloff", "password", "GUEST RELATION", "OFFICER", "S0000003A", "90123458", "Singapore Address Line 1", "Singapore Address Line 2", "600003", EmployeeAccessRightEnum.GUESTRELOFF));
            employeeControllerLocal.createNewEmployee(new Employee("samanager", "password", "SALES", "MANAGER", "S0000004A", "90127458", "Singapore Address Line 1", "Singapore Address Line 2", "600004", EmployeeAccessRightEnum.SAMANAGER));

            Long deluxeRoomTypeId = (roomTypeControllerLocal.createNewRoomType(new RoomType("DELUXE ROOM", "Deluxe Room!", 1000, "1 x Single Beds", 4, "Air-Conditioned", 5, true))).getRoomTypeId();
            Long premierRoomTypeId = (roomTypeControllerLocal.createNewRoomType(new RoomType("PREMIER ROOM", "Premier Room!", 2000, "2 x Double Beds", 4, "Air-Conditioned", 5, true))).getRoomTypeId();
            Long familyRoomTypeId = (roomTypeControllerLocal.createNewRoomType(new RoomType("FAMILY ROOM", "Family Room!", 3000, "3 x Triple Beds", 4, "Air-Conditioned", 5, true))).getRoomTypeId();
            Long juniorRoomTypeId = (roomTypeControllerLocal.createNewRoomType(new RoomType("JUNIOR SUITE", "Junior Room!", 4000, "4 x Quadruple Beds", 4, "Air-Conditioned", 5, true))).getRoomTypeId();
            Long grandRoomTypeId = (roomTypeControllerLocal.createNewRoomType(new RoomType("GRAND SUITE", "Grand Room!", 5000, "5 x Quintuple Beds", 4, "Air-Conditioned", 5, true))).getRoomTypeId();

            roomControllerLocal.createNewRoom(new Room(1000, RoomStatus.AVAILABLE), deluxeRoomTypeId);
            roomControllerLocal.createNewRoom(new Room(1001, RoomStatus.AVAILABLE), deluxeRoomTypeId);
            roomControllerLocal.createNewRoom(new Room(1002, RoomStatus.AVAILABLE), deluxeRoomTypeId);
            roomControllerLocal.createNewRoom(new Room(1003, RoomStatus.AVAILABLE), deluxeRoomTypeId);
            roomControllerLocal.createNewRoom(new Room(1004, RoomStatus.AVAILABLE), deluxeRoomTypeId);

            roomControllerLocal.createNewRoom(new Room(2000, RoomStatus.AVAILABLE), premierRoomTypeId);
            roomControllerLocal.createNewRoom(new Room(2001, RoomStatus.AVAILABLE), premierRoomTypeId);
            roomControllerLocal.createNewRoom(new Room(2002, RoomStatus.AVAILABLE), premierRoomTypeId);
            roomControllerLocal.createNewRoom(new Room(2003, RoomStatus.AVAILABLE), premierRoomTypeId);
            roomControllerLocal.createNewRoom(new Room(2004, RoomStatus.AVAILABLE), premierRoomTypeId);

            roomControllerLocal.createNewRoom(new Room(3000, RoomStatus.AVAILABLE), familyRoomTypeId);
            roomControllerLocal.createNewRoom(new Room(3001, RoomStatus.AVAILABLE), familyRoomTypeId);
            roomControllerLocal.createNewRoom(new Room(3002, RoomStatus.AVAILABLE), familyRoomTypeId);
            roomControllerLocal.createNewRoom(new Room(3003, RoomStatus.AVAILABLE), familyRoomTypeId);
            roomControllerLocal.createNewRoom(new Room(3004, RoomStatus.AVAILABLE), familyRoomTypeId);

            roomControllerLocal.createNewRoom(new Room(4000, RoomStatus.AVAILABLE), juniorRoomTypeId);
            roomControllerLocal.createNewRoom(new Room(4001, RoomStatus.AVAILABLE), juniorRoomTypeId);
            roomControllerLocal.createNewRoom(new Room(4002, RoomStatus.AVAILABLE), juniorRoomTypeId);
            roomControllerLocal.createNewRoom(new Room(4003, RoomStatus.AVAILABLE), juniorRoomTypeId);
            roomControllerLocal.createNewRoom(new Room(4004, RoomStatus.AVAILABLE), juniorRoomTypeId);

            roomControllerLocal.createNewRoom(new Room(5000, RoomStatus.AVAILABLE), grandRoomTypeId);
            roomControllerLocal.createNewRoom(new Room(5001, RoomStatus.AVAILABLE), grandRoomTypeId);
            roomControllerLocal.createNewRoom(new Room(5002, RoomStatus.AVAILABLE), grandRoomTypeId);
            roomControllerLocal.createNewRoom(new Room(5003, RoomStatus.AVAILABLE), grandRoomTypeId);
            roomControllerLocal.createNewRoom(new Room(5004, RoomStatus.AVAILABLE), grandRoomTypeId);

            PublishedRoomRate deluxePub = new PublishedRoomRate("Deluxe Published Room Rate", new BigDecimal(100.10), true);
            roomRateControllerLocal.createNewRoomRate(deluxePub, deluxeRoomTypeId);
            NormalRoomRate deluxeNormal = new NormalRoomRate("Deluxe Normal Room Rate", new BigDecimal(100.99), true);
            roomRateControllerLocal.createNewRoomRate(deluxeNormal, deluxeRoomTypeId);

            PublishedRoomRate premierPub = new PublishedRoomRate("Premier Published Room Rate", new BigDecimal(200.10), true);
            roomRateControllerLocal.createNewRoomRate(premierPub, premierRoomTypeId);
            NormalRoomRate premierNormal = new NormalRoomRate("Premier Normal Room Rate", new BigDecimal(200.99), true);
            roomRateControllerLocal.createNewRoomRate(premierNormal, premierRoomTypeId);

            PublishedRoomRate familyPub = new PublishedRoomRate("Family Published Room Rate", new BigDecimal(300.10), true);
            roomRateControllerLocal.createNewRoomRate(familyPub, familyRoomTypeId);
            NormalRoomRate familyNormal = new NormalRoomRate("Family Normal Room Rate", new BigDecimal(300.99), true);
            roomRateControllerLocal.createNewRoomRate(familyNormal, familyRoomTypeId);

            PublishedRoomRate juniorPub = new PublishedRoomRate("Junior Suite Published Room Rate", new BigDecimal(400.10), true);
            roomRateControllerLocal.createNewRoomRate(juniorPub, juniorRoomTypeId);
            NormalRoomRate juniorNormal = new NormalRoomRate("Junior Suite Normal Room Rate", new BigDecimal(400.99), true);
            roomRateControllerLocal.createNewRoomRate(juniorNormal, juniorRoomTypeId);

            PublishedRoomRate grandPub = new PublishedRoomRate("Grand Suite Published Room Rate", new BigDecimal(400.10), true);
            roomRateControllerLocal.createNewRoomRate(grandPub, grandRoomTypeId);
            NormalRoomRate grandNormal = new NormalRoomRate("Grand Suite Normal Room Rate", new BigDecimal(400.99), true);
            roomRateControllerLocal.createNewRoomRate(grandNormal, grandRoomTypeId);

        } catch (Exception ex) {
            System.err.println("********** DataInitializationSessionBean.initializeData(): An error has occurred while loading initial test data: " + ex.getMessage());
        }
    }
}
