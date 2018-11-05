/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import ejb.session.stateless.EmployeeControllerLocal;
import ejb.session.stateless.RoomTypeControllerLocal;
import entity.Employee;
import entity.RoomType;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import util.enumeration.EmployeeAccessRightEnum;
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
            roomTypeControllerLocal.createNewRoomType(new RoomType("DELUXE", "Deluxe Room for Family", 1500 , "2 beds with silk covers", 4, "No Air con", 1, true));
        } catch (Exception ex) {
            System.err.println("********** DataInitializationSessionBean.initializeData(): An error has occurred while loading initial test data: " + ex.getMessage());
        }
    }
}
