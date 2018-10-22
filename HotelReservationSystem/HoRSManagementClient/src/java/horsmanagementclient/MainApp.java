/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsmanagementclient;

import ejb.session.stateless.EmployeeControllerRemote;
import ejb.session.stateless.PartnerControllerRemote;
import entity.Employee;
import java.util.Scanner;
import util.exception.InvalidAccessRightException;
import util.exception.InvalidLoginCredentialException;

/**
 *
 * @author Lawrence
 */
public class MainApp {
    
    private EmployeeControllerRemote employeeControllerRemote;
    private PartnerControllerRemote partnerControllerRemote;
    
    private SystemAdministrationModule systemAdministrationModule;
    
    private Employee currentEmployee;
    
    public MainApp(){
    }

    public MainApp(EmployeeControllerRemote employeeControllerRemote, PartnerControllerRemote partnerControllerRemote) {
        this.employeeControllerRemote = employeeControllerRemote;
        this.partnerControllerRemote = partnerControllerRemote;
    }
    
    
    public void runApp() {
        Scanner sc = new Scanner(System.in);
        Integer response = 0;
        
        while (true) {
            System.out.println("*** Welcome to Hotel Reservation Management Client :: Main Page ***\n");
            System.out.println("1: Login");
            System.out.println("2: Exit");
            response = 0;
            
            while (response <1 || response > 2) {
                System.out.print("> ");
                response = sc.nextInt();
                
                switch(response) {
                    case 1:
                        try {
                            doLogin();
                            System.out.println("Login successful!\n");
                            systemAdministrationModule = new SystemAdministrationModule(employeeControllerRemote, partnerControllerRemote, currentEmployee);
                            menuMain();
                        } catch (InvalidLoginCredentialException ex) {
                            System.out.println("Invalid login credentials: " + ex.getMessage() + "\n");
                        }
                        break;
                    case 2:
                        return;
                    default:
                        System.out.println("Invalid option, please try again!\n");
                        
                }
            }
        }
    }
    
    private void doLogin() throws InvalidLoginCredentialException {
        Scanner sc = new Scanner(System.in);
        String username = "";
        String password = "";
        
        System.out.println("*** Hotel Reservation Management Client :: Login Menu ***\n");
        System.out.print("Enter Username> ");
        username = sc.nextLine().trim();
        System.out.print("Enter Password> ");
        password = sc.nextLine().trim();
        
        if (username.length() > 0 && password.length() > 0) {
            currentEmployee = employeeControllerRemote.employeeLogin(username, password);
        } else {
            throw new InvalidLoginCredentialException("Missing login credential!");
        }
    }
    
    public void menuMain() {
        Scanner sc = new Scanner(System.in);
        Integer response = 0;
        
        while (true) {
            System.out.println("*** Hotel Reservation Management Client :: Main Menu ***\n");
            System.out.println("You have logged in as " + currentEmployee.getFirstName() + " " + currentEmployee.getLastName() + " with " + currentEmployee.getAccessRight().toString() + " rights.\n");
            System.out.println("1: System Administation");
            System.out.println("2: Hotel Operations");
            System.out.println("3: Front Office");
            System.out.println("4: Logout\n");
            response = 0;
            
            while (response < 1 || response > 4) {
                System.out.print("> ");
                response = sc.nextInt();
                
                switch(response) {
                    case 1:
                        try {
                        systemAdministrationModule.menuMain();
                        } catch (InvalidAccessRightException ex) {
                            System.out.println("You lack the proper access rights to use this function. (" + ex.getMessage() + ")\n");
                        }
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        return;
                    default:
                        break;
                }
            }
        }
    }
}
