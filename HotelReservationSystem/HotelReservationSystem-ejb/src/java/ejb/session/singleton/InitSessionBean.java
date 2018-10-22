/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import ejb.session.stateless.EmployeeControllerLocal;
import entity.Employee;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import util.enumeration.EmployeeAccessRightEnum;
import util.exception.EmployeeNotFoundException;

/**
 *
 * @author Lawrence
 */
@Singleton
@LocalBean
@Startup
public class InitSessionBean {

    @EJB
    private EmployeeControllerLocal employeeControllerLocal;
    
    @PostConstruct
    public void PostConstruct() {
        
        try {
            employeeControllerLocal.retrieveEmployeeByUsername("sysadmin");
        } catch (EmployeeNotFoundException ex) {
            initialiseData();
        }
    }
    
    private void initialiseData() {
        try {
            employeeControllerLocal.createNewEmployee(new Employee("sysadmin", "password", "Default", "System Administrator", "S0000001A", "90123456", "Singapore Address Line 1", "Singapore Address Line 2", "600001", EmployeeAccessRightEnum.SYSADMIN));
        } catch (Exception ex) {
            System.err.println("********** DataInitializationSessionBean.initializeData(): An error has occurred while loading initial test data: " + ex.getMessage());
        }
    }
}
