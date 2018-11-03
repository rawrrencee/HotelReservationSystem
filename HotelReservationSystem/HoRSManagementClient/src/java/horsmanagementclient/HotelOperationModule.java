/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsmanagementclient;

import ejb.session.stateless.EmployeeControllerRemote;
import ejb.session.stateless.RoomTypeControllerRemote;
import entity.Employee;
import entity.RoomType;
import java.util.List;
import java.util.Scanner;
import util.enumeration.EmployeeAccessRightEnum;
import util.exception.GeneralException;
import util.exception.InvalidAccessRightException;
import util.exception.RoomTypeExistException;
import util.exception.RoomTypeNotFoundException;

/**
 *
 * @author Lawrence
 */
public class HotelOperationModule {
    
    private EmployeeControllerRemote employeeControllerRemote;
    private RoomTypeControllerRemote roomTypeControllerRemote;
    
    private Employee currentEmployee;

    public HotelOperationModule() {
    }

    public HotelOperationModule(EmployeeControllerRemote employeeControllerRemote, RoomTypeControllerRemote roomTypeControllerRemote, Employee currentEmployee) {
        this.employeeControllerRemote = employeeControllerRemote;
        this.roomTypeControllerRemote = roomTypeControllerRemote;
        this.currentEmployee = currentEmployee;
    }
    
    public void menuMain() throws InvalidAccessRightException {
        if (currentEmployee.getAccessRight() != EmployeeAccessRightEnum.OPMANAGER) {
            throw new InvalidAccessRightException("You don't have OPERATION MANAGER rights to access the Hotel Operation Module.");
        }
        
        Scanner sc = new Scanner(System.in);
        Integer response = 0;
        
        while (true) {
            System.out.println("*** Hotel Reservation System :: Hotel Operations ***\n");
            System.out.println("1: Create New Room Type");
            System.out.println("2: View Room Type Details");
            System.out.println("3: Update Room Type");
            System.out.println("4: Delete Room Type");
            System.out.println("5: View All Room Types");
            System.out.println("6: Create New Room");
            System.out.println("7: Update Room");
            System.out.println("8: Delete Room");
            System.out.println("9: View All Rooms");
            System.out.println("10: View Room Allocation Exception Report");
            System.out.println("11: Create New Room Rate");
            System.out.println("12: View Room Rate Details");
            System.out.println("13: Update Room Rate");
            System.out.println("14: Delete Room Rate");
            System.out.println("15: View All Room Rates");
            System.out.println("16: Walk-in Search Room");
            System.out.println("17: Walk-in Reserve Room");
            System.out.println("18: Check-in Guest");
            System.out.println("19: Check-out Guest");
            System.out.println("20: Back\n");
            response = 0;
            
            while (response < 1 || response > 20) {
                System.out.print("> ");
                response = sc.nextInt();
                
                switch(response) {
                    case 1:
                        createNewRoomType();
                        break;
                    case 2:
                        viewRoomTypeDetails();
                        break;
                    case 3:
                        updateRoomType();
                        break;
                    case 4:
                        deleteRoomType();
                        break;
                    case 5:
                        viewAllRoomTypes();
                        break;
                    case 6:
                        createNewRoom();
                        break;
                    case 7:
                        updateRoom();
                        break;
                    case 8:
                        deleteRoom();
                        break;
                    case 9:
                        viewAllRooms();
                        break;
                    case 10:
                        viewRoomAllocationExceptionReport();
                        break;
                    case 11:
                        createNewRoomRate();
                        break;
                    case 12:
                        viewRoomRateDetails();
                        break;
                    case 13:
                        updateRoomRate();
                        break;
                    case 14:
                        deleteRoomRate();
                        break;
                    case 15:
                        viewAllRoomRates();
                        break;
                    case 16:
                        walkInSearchRoom();
                        break;
                    case 17:
                        walkInReserveRoom();
                        break;
                    case 18:
                        checkInGuest();
                        break;
                    case 19:
                        checkOutGuest();
                        break;
                    case 20:
                        return;
                    default:
                        break;
                }
            }
        }
    }
    
    private void createNewRoomType() {
        Scanner sc = new Scanner(System.in);
        RoomType newRoomType = new RoomType();
        Boolean conditionChecker = true;
        
        try {
            System.out.println("*** Hotel Reservation System :: Hotel Operations :: Create New Room Type ***\n");
            
            while (conditionChecker) {
                System.out.print("Enter Room Type Name> ");
                String roomTypeName = sc.nextLine().trim();
                try {
                    Boolean roomTypeExists = roomTypeControllerRemote.checkRoomTypeExists(roomTypeName);
                    if (roomTypeExists) {
                        System.out.println("Room Type name already taken, please choose another one!");
                    }
                } catch (RoomTypeNotFoundException ex){
                    System.out.println("Room Type Name "  + roomTypeName + " accepted.");
                    newRoomType.setRoomTypeName(roomTypeName);
                    conditionChecker = false;
                }
            }
            
            System.out.print("Enter Room Type Description> ");
            newRoomType.setRoomTypeDescription(sc.nextLine().trim());
            System.out.print("Enter Room Size> ");
            newRoomType.setRoomSize(sc.nextInt());
            
            //Consume next Line
            sc.nextLine();
            
            System.out.print("Enter Bed Info> ");
            newRoomType.setBedInfo(sc.nextLine().trim());
            System.out.print("Enter Capacity> ");
            newRoomType.setCapacity(sc.nextInt());
            
            //Consume next Line
            sc.nextLine();
            
            System.out.print("Enter Amenities> ");
            newRoomType.setAmenities(sc.nextLine());
            System.out.print("Enter Number of Rooms of this Room Type> ");
            newRoomType.setNumRooms(sc.nextInt());
            
            //Consume next Line
            sc.nextLine();
            
            conditionChecker = true;
            while (conditionChecker) {
                System.out.print("Enable Room Type? Y/N> ");
                String input = sc.nextLine().trim();
                if (input.toLowerCase().equals("y")) {
                    newRoomType.setIsEnabled(true);
                    conditionChecker = false;
                }
                if (input.toLowerCase().equals("n")){
                    newRoomType.setIsEnabled(false);
                    conditionChecker = false;
                }
            }
            
            newRoomType = roomTypeControllerRemote.createNewRoomType(newRoomType);
            System.out.println("New Room Type (" + newRoomType.getRoomTypeName() + ") created successfully! Room Type ID is " + newRoomType.getRoomTypeId() + "\n");
        } catch (RoomTypeExistException | GeneralException ex) {
            System.out.println("An error has occurred while creating the new room type: " + ex.getMessage() + " !\n");
        }
    }
    
    private void viewRoomTypeDetails() {
        Scanner sc = new Scanner(System.in);
        Boolean conditionChecker = true;
        List<RoomType> roomTypes = roomTypeControllerRemote.retrieveAllRoomTypes();
        
        System.out.println("*** List of Room Types ***");
        for (RoomType roomType : roomTypes) {
            System.out.println(roomType.getRoomTypeId() + ": " + roomType.getRoomTypeName());
        }
        System.out.println("-------------------------");
        
        while (conditionChecker) {
            System.out.print("Enter Room Type ID to query> ");
            int response = sc.nextInt();
            if (response >= 1 && response <= roomTypes.size()) {
                try {
                    RoomType roomType = roomTypeControllerRemote.retrieveRoomTypeByRoomTypeId(Long.valueOf(response));
                    System.out.println("Room Type Details: ");
                    System.out.println("Room Type Name | " + roomType.getRoomTypeName());
                    System.out.println("Description: " + roomType.getRoomTypeDescription());
                    System.out.println("Room Size: " + roomType.getRoomSize() + "sq m");
                    System.out.println("Bed Info: " + roomType.getBedInfo());
                    System.out.println("Capacity: " + roomType.getCapacity() + " guests");
                    System.out.println("Amenities: " + roomType.getAmenities());
                    System.out.println("Number of Rooms available (incl. Reserved Rooms): " + roomType.getNumRooms());
                    if (roomType.getIsEnabled()) {
                        System.out.println("Room Type Status: ENABLED\n");
                    } else {
                        System.out.println("Room Type Status: DISABLED\n");
                    }
                    conditionChecker = false;
                } catch (RoomTypeNotFoundException ex) {
                    System.out.println("Invalid option!\n");
                    continue;
                } catch (Exception ex) {
                    System.out.println("An unexpected error occurred: " + ex.getMessage());
                }
            }
        }
    }
    
    private void updateRoomType() {
        
    }
    
    private void deleteRoomType() {
        
    }
    
    private void viewAllRoomTypes() {
        
    }
    
    private void createNewRoom() {
        
    }
    
    private void updateRoom() {
        
    }
    
    private void deleteRoom() {
        
    }
    
    private void viewAllRooms() {
        
    }
    
    private void viewRoomAllocationExceptionReport() {
        
    }
    
    private void createNewRoomRate() {
        
    }
    
    private void viewRoomRateDetails() {
        
    }
    
    private void updateRoomRate() {
        
    }
    
    private void deleteRoomRate() {
        
    }
    
    private void viewAllRoomRates() {
        
    }
    
    private void walkInSearchRoom() {
        
    }
    
    private void walkInReserveRoom() {
        
    }
    
    private void checkInGuest() {
        
    }
    
    private void checkOutGuest() {
        
    }
}
