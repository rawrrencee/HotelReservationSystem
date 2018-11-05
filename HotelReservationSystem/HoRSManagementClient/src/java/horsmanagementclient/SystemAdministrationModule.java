/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsmanagementclient;

import ejb.session.stateless.EmployeeControllerRemote;
import ejb.session.stateless.PartnerControllerRemote;
import entity.Employee;
import entity.Partner;
import java.util.List;
import java.util.Scanner;
import util.enumeration.EmployeeAccessRightEnum;
import util.exception.EmployeeExistException;
import util.exception.EmployeeNotFoundException;
import util.exception.GeneralException;
import util.exception.InvalidAccessRightException;
import util.exception.PartnerNotFoundException;

/**
 *
 * @author Lawrence
 */
public class SystemAdministrationModule {

    private EmployeeControllerRemote employeeControllerRemote;
    private PartnerControllerRemote partnerControllerRemote;

    private Employee currentEmployee;

    public SystemAdministrationModule() {
    }

    public SystemAdministrationModule(EmployeeControllerRemote employeeControllerRemote, PartnerControllerRemote partnerControllerRemote, Employee currentEmployee) {
        this.employeeControllerRemote = employeeControllerRemote;
        this.partnerControllerRemote = partnerControllerRemote;
        this.currentEmployee = currentEmployee;
    }

    public void menuMain() throws InvalidAccessRightException {
        if (currentEmployee.getAccessRight() != EmployeeAccessRightEnum.SYSADMIN) {
            throw new InvalidAccessRightException("You don't have SYSTEM ADMINISTRATOR rights to access the System Administration Module!!");
        }

        Scanner sc = new Scanner(System.in);
        String input;
        Integer response = 0;

        while (true) {
            System.out.println("*** Hotel Reservation System :: System Administration ***\n");
            System.out.println("1: Create New Employee");
            System.out.println("2: View All Employees");
            System.out.println("3: Create New Partner");
            System.out.println("4: View All Partners");
            System.out.println("5: Back\n");
            response = 0;

            while (response < 1 || response > 5) {
                System.out.print("> ");
                input = sc.nextLine().trim();
                try {
                    response = Integer.parseInt(input);
                } catch (NumberFormatException ex) {
                    System.out.println("Please enter numerical values.");
                    continue;
                }

                switch (response) {
                    case 1:
                        createNewEmployee();
                        break;
                    case 2:
                        viewAllEmployees();
                        break;
                    case 3:
                        createNewPartner();
                        break;
                    case 4:
                        viewAllPartners();
                        break;
                    case 5:
                        return;
                    default:
                        break;
                }
            }

        }
    }

    private void createNewEmployee() {
        Scanner sc = new Scanner(System.in);
        Employee newEmployee = new Employee();
        Boolean conditionChecker = true;
        
        try {
        System.out.println("*** Hotel Reservation System :: System Administration :: Create New Employee ***\n");

        while (conditionChecker) {
            System.out.print("Enter Username> ");
            String inUsername = sc.nextLine().trim();
            try {
                Boolean employeeExists = employeeControllerRemote.checkEmployeeExists(inUsername);
                if (employeeExists) {
                    System.out.println("Username already taken, please choose another one!");
                }
            } catch (EmployeeNotFoundException ex) {
                System.out.println("Username " + inUsername + " accepted.");
                newEmployee.setUsername(inUsername);
                conditionChecker = false;
            }
        }

        System.out.print("Enter Password> ");
        newEmployee.setPassword(sc.nextLine().trim());
        System.out.print("Enter First Name> ");
        newEmployee.setFirstName(sc.nextLine().trim());
        System.out.print("Enter Last Name> ");
        newEmployee.setLastName(sc.nextLine().trim());

        conditionChecker = true;
        while (conditionChecker) {
            System.out.print("Enter Identification Number> ");
            String inIdentificationNum = sc.nextLine().trim();
            if (inIdentificationNum.length() != 9 || !inIdentificationNum.matches("^[a-zA-Z]+\\p{Digit}+[a-zA-Z]$")) {
                System.out.println("Identification number should be of length 9 and formatted e.g. S1234567A");
                continue;
            }
            newEmployee.setIdentificationNum(inIdentificationNum);
            conditionChecker = false;
        }

        conditionChecker = true;
        while (conditionChecker) {
            System.out.print("Enter Contact Number> ");
            String inContactNum = sc.nextLine().trim();
            
            try {
                Integer.parseInt(inContactNum);
            } catch (NumberFormatException ex) {
                System.out.println("Phone number can only contain numbers");
                continue;
            }
            if (inContactNum.length() != 8) {
                System.out.println("Phone number should be of length 8 E.g. 91234567");
                continue;
            }
            newEmployee.setContactNum(inContactNum);
            conditionChecker = false;
        }
        
        System.out.print("Enter Address Line 1> ");
        newEmployee.setAddressLine1(sc.nextLine().trim());
        System.out.print("Enter Address Line 2> ");
        newEmployee.setAddressLine2(sc.nextLine().trim());
        
        conditionChecker = true;
        while(conditionChecker) {
            System.out.print("Enter Postal Code> ");
            String inPostalCode = sc.nextLine().trim();
            
            try {
                Integer.parseInt(inPostalCode);
            } catch (NumberFormatException ex) {
                System.out.println("Postal code can only contain numbers");
                continue;
            }
            
            if (inPostalCode.length() != 6) {
                System.out.println("Postal code must be 6 digits long");
                continue;
            }
            newEmployee.setPostalCode(inPostalCode);
            conditionChecker = false;
        }

        while (true) {
            System.out.print("Select Access Right (1: System Administrator, 2: Operation Manager, 3: Sales Manager, 4: Guest Relation Officer)> ");
            Integer accessRightInt = sc.nextInt();
            if (accessRightInt >= 1 && accessRightInt <= 4) {
                newEmployee.setAccessRight(EmployeeAccessRightEnum.values()[accessRightInt - 1]);
                break;
            } else {
                System.out.println("Invalid option, please try again!\n");
            }
        }

        sc.nextLine(); //consume empty space
        newEmployee = employeeControllerRemote.createNewEmployee(newEmployee);
        System.out.println("New employee (" + newEmployee.getFirstName() + ") created successfully! Employee ID is " + newEmployee.getEmployeeId() + "\n");
        } catch (EmployeeExistException | GeneralException ex) {
            System.out.println("An error has occurred while creating the new employee: " + ex.getMessage() + "!\n");
        }
    }

    private void viewAllEmployees() {
        Scanner sc = new Scanner(System.in);
        List<Employee> employees = employeeControllerRemote.retrieveAllEmployees();
        Long employeeId;

        System.out.println("*** List of Employees ***");
        for (Employee employee : employees) {
            System.out.println(employee.getEmployeeId() + ": " + employee.getFirstName() + " " + employee.getLastName() + " | " + employee.getIdentificationNum() + " | " + employee.getContactNum() + " | " + employee.getAddressLine1() + " " + employee.getAddressLine2() + " | (" + employee.getPostalCode() + ") | Access Right: " + employee.getAccessRight().toString());
        }

        System.out.print("Press any key to continue...");
        sc.nextLine();
    }
    
    private void createNewPartner() {
        Scanner sc = new Scanner(System.in);
        Partner newPartner = new Partner();
        Boolean conditionChecker = true;

        System.out.println("*** Hotel Reservation System :: System Administration :: Create New Partner ***\n");

        while (conditionChecker) {
            System.out.print("Enter Username> ");
            String inUsername = sc.nextLine().trim();
            try {
                Partner getPartner = partnerControllerRemote.retrievePartnerByUsername(inUsername);
                if (getPartner != null) {
                    System.out.println("Username already taken, please choose another one!");
                }
            } catch (PartnerNotFoundException | NullPointerException ex) {
                System.out.println("Username " + inUsername + " accepted.");
                newPartner.setUsername(inUsername);
                conditionChecker = false;
            }
        }
        System.out.print("Enter Password> ");
        newPartner.setPassword(sc.nextLine().trim());
        System.out.print("Enter Organisation Name> ");
        newPartner.setOrgName(sc.nextLine().trim());

        conditionChecker = true;
        while (conditionChecker) {
            System.out.print("Enter Organisation Contact Number> ");
            String inContactNum = sc.nextLine().trim();
            
            try {
                Integer.parseInt(inContactNum);
            } catch (NumberFormatException ex) {
                System.out.println("Phone number can only contain numbers");
                continue;
            }
            if (inContactNum.length() != 8) {
                System.out.println("Phone number should be of length 8 E.g. 91234567");
                continue;
            }
            newPartner.setContactNum(inContactNum);
            conditionChecker = false;
        }
        
        System.out.print("Enter Organisation Address Line 1> ");
        newPartner.setAddressLine1(sc.nextLine().trim());
        System.out.print("Enter Organisation Address Line 2> ");
        newPartner.setAddressLine2(sc.nextLine().trim());
        
        conditionChecker = true;
        while(conditionChecker) {
            System.out.print("Enter Organisation Postal Code> ");
            String inPostalCode = sc.nextLine().trim();
            
            try {
                Integer.parseInt(inPostalCode);
            } catch (NumberFormatException ex) {
                System.out.println("Postal code can only contain numbers");
                continue;
            }
            
            if (inPostalCode.length() != 6) {
                System.out.println("Postal code must be 6 digits long");
                continue;
            }
            newPartner.setPostalCode(inPostalCode);
            conditionChecker = false;
        }

        newPartner = partnerControllerRemote.createNewPartner(newPartner);
        System.out.println("New partner (" + newPartner.getOrgName() + ") created successfully! Partner ID is " + newPartner.getPartnerId() + "\n");
    }
    
    private void viewAllPartners() {
        Scanner sc = new Scanner(System.in);
        List<Partner> partners;
        
        try {
            partners = partnerControllerRemote.retrieveAllPartners();
            System.out.println("*** List of Partners ***");
            for (Partner partner : partners) {
                System.out.println(partner.getPartnerId() + ": " + partner.getOrgName() + " | " + partner.getContactNum() + " | " + partner.getAddressLine1() + " " + partner.getAddressLine2() + " | (" + partner.getPostalCode() + ")");
            }
        } catch (NullPointerException ex) {
            System.out.println("No current partners available.\n");
            return;
        }
        
        System.out.print("Press any key to continue...");
        sc.nextLine();
    }

}
