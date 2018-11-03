/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsmanagementclient;

import ejb.session.stateless.EmployeeControllerRemote;
import ejb.session.stateless.RoomControllerRemote;
import ejb.session.stateless.RoomRateControllerRemote;
import ejb.session.stateless.RoomTypeControllerRemote;
import entity.Employee;
import entity.PublishedRoomRate;
import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;
import util.enumeration.EmployeeAccessRightEnum;
import util.enumeration.RoomStatus;
import util.exception.GeneralException;
import util.exception.InvalidAccessRightException;
import util.exception.RoomExistException;
import util.exception.RoomNotFoundException;
import util.exception.RoomTypeExistException;
import util.exception.RoomTypeNotFoundException;

/**
 *
 * @author Lawrence
 */
public class HotelOperationModule {
    
    private EmployeeControllerRemote employeeControllerRemote;
    private RoomTypeControllerRemote roomTypeControllerRemote;
    private RoomControllerRemote roomControllerRemote;
    private RoomRateControllerRemote roomRateControllerRemote;
    
    private Employee currentEmployee;
    private RoomType currentRoomType;
    private Room currentRoom;

    public HotelOperationModule() {
    }

    public HotelOperationModule(EmployeeControllerRemote employeeControllerRemote, RoomControllerRemote roomControllerRemote, RoomTypeControllerRemote roomTypeControllerRemote, Employee currentEmployee, RoomRateControllerRemote roomRateControllerRemote) {
        this.employeeControllerRemote = employeeControllerRemote;
        this.roomControllerRemote = roomControllerRemote;
        this.roomTypeControllerRemote = roomTypeControllerRemote;
        this.currentEmployee = currentEmployee;
        this.roomRateControllerRemote = roomRateControllerRemote;
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
        
        System.out.println("*** Hotel Reservation System :: Hotel Operations :: View Room Type Details ***\n");
        
        while (conditionChecker) {
            System.out.print("Enter Room Type ID to query> ");
            int response = sc.nextInt();
            if (response >= 1 && response <= roomTypes.size()) {
                try {
                    RoomType roomType = roomTypeControllerRemote.retrieveRoomTypeByRoomTypeId(Long.valueOf(response));
                    System.out.println();
                    System.out.println("---------------Room Type Details---------------");
                    System.out.println("Room Type ID | " + roomType.getRoomTypeId() + " |");
                    System.out.println("Room Type Name | " + roomType.getRoomTypeName() + " |");
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
        Scanner sc = new Scanner(System.in);
        String input;
        List<RoomType> roomTypes = roomTypeControllerRemote.retrieveAllRoomTypes();

        System.out.println("*** Hotel Reservation System :: Hotel Operations :: Update Room Type ***\n");

        viewRoomTypeDetails();

        while (true) {
            System.out.print("Enter Room Type ID to update> ");
            int response = sc.nextInt();
            if (response >= 1 && response <= roomTypes.size()) {
                try {
                    currentRoomType = roomTypeControllerRemote.retrieveRoomTypeByRoomTypeId(Long.valueOf(response));
                    break;
                } catch (RoomTypeNotFoundException ex) {
                    System.out.println("Invalid option!\n");
                }
            }
        }
        //Consume empty line
        sc.nextLine();
        System.out.println("Input accepted! Now updating: " + currentRoomType.getRoomTypeId() + ": " + currentRoomType.getRoomTypeName());
        System.out.print("Enter Room Type Name (blank if no change)> ");
        input = sc.nextLine().trim();
        if (input.length() > 0) {
            currentRoomType.setRoomTypeName(input);
        }

        System.out.print("Enter Room Type Description (blank if no change)> ");
        input = sc.nextLine().trim();
        if (input.length() > 0) {
            currentRoomType.setRoomTypeDescription(input);
        }
        
        while (true) {
            System.out.print("Enter Room Size in sq m (blank if no change)> ");
            input = sc.nextLine();
            if (input.length() > 0) {
                try {
                    currentRoomType.setRoomSize(Integer.parseInt(input));
                    break;
                } catch (NumberFormatException ex) {
                    System.out.println("Please enter numeric values.");
                }
            } else {
                break;
            }
        }
        
        System.out.print("Enter Bed Info (blank if no change)> ");
        input = sc.nextLine().trim();
        if (input.length() > 0) {
            currentRoomType.setBedInfo(input);
        }

        while (true) {
            System.out.print("Enter Capacity (blank if no change)> ");
            input = sc.nextLine();
            if (input.length() > 0) {
                try {
                    currentRoomType.setCapacity(Integer.parseInt(input));
                    break;
                } catch (NumberFormatException ex) {
                    System.out.println("Please enter numeric values.");
                }
            } else {
                break;
            }
        }
        
        System.out.print("Enter Amenities (blank if no change)> ");
        input = sc.nextLine().trim();
        if (input.length() > 0) {
            currentRoomType.setAmenities(input);
        }
        
        while (true) {
            System.out.print("Enter Number of Rooms in total (blank if no change)> ");
            input = sc.nextLine();
            if (input.length() > 0) {
                try {
                    currentRoomType.setNumRooms(Integer.parseInt(input));
                    break;
                } catch (NumberFormatException ex) {
                    System.out.println("Please enter numeric values.");
                }
            } else {
                break;
            }
        }
        
        while (true) {
            System.out.print("Enable Room Type? Y/N> ");
            input = sc.nextLine().trim();
            if (input.toLowerCase().equals("y")) {
                currentRoomType.setIsEnabled(true);
                break;
            }
            if (input.toLowerCase().equals("n")) {
                currentRoomType.setIsEnabled(false);
                break;
            }
        }
        
        roomTypeControllerRemote.updateRoomType(currentRoomType);
        System.out.println("-------------------------");
        System.out.println("Room Type updated!\n");
    }
    
    private void deleteRoomType() {
        Scanner sc = new Scanner(System.in);
        String input;
        viewAllRoomTypes();
        while (true) {
            System.out.print("Enter Room Type ID to remove> ");
            input = sc.nextLine().trim();
            try {
                Long roomTypeIdToRemove = Long.parseLong(input);
                try {
                    if (!roomTypeControllerRemote.deleteRoomType(roomTypeIdToRemove)) {
                        System.out.println("Room Type is currently associated with other entities. Set to DISABLED instead.");
                    }
                } catch (RoomTypeNotFoundException ex) {
                    System.out.println("An error occurred: " + ex.getMessage());
                }
                break;
            } catch (NumberFormatException ex) {
                System.out.println("Please enter numeric values.");
                continue;
            }
        }

    }
    
    private void viewAllRoomTypes() {
        Scanner sc = new Scanner(System.in);
        List<RoomType> roomTypes = roomTypeControllerRemote.retrieveAllRoomTypes();
        
        System.out.println("*** List of Room Types ***");
        for (RoomType roomType : roomTypes) {
            System.out.println(roomType.getRoomTypeId() + ": " + roomType.getRoomTypeName());
        }
        System.out.println("-------------------------");
        System.out.print("Press any key to continue...");
        sc.nextLine();
        System.out.println();
    }
    
    private void createNewRoom() {
        Scanner sc = new Scanner(System.in);
        Room newRoom = new Room();
        String input = "";
        Long roomTypeId;
        List<RoomType> roomTypes = roomTypeControllerRemote.retrieveAllRoomTypes();

        System.out.println("*** Hotel Reservation System :: Hotel Operations :: Create New Room ***\n");

        try {
            while (true) {
                System.out.print("Enter Room Number in the format xxyy where xx is the Floor number, and yy is the Sequence number (e.g. 2015)> ");
                input = sc.nextLine().trim();
                if (input.length() != 4) {
                    System.out.println("Room Number must be 4 digits");
                    continue;
                }
                try {
                    if (!roomControllerRemote.checkRoomExistsByRoomNumber(Integer.parseInt(input))) {
                        newRoom.setRoomNumber(Integer.parseInt(input));
                        break;
                    } else {
                        System.out.println("Room Number already exists, please enter another number.");
                    }
                } catch (NumberFormatException ex) {
                    System.out.println("Please enter numeric values.");
                }
            }

            System.out.print("Please set the status of the room (1: AVAILABLE, 2: CLEANING, 3: ALLOCATED, 4: DISABLED)> ");
            input = sc.nextLine().trim();
            while (true) {
                if (input.equals("1")) {
                    newRoom.setRoomStatus(RoomStatus.AVAILABLE);
                    break;
                } else if (input.equals("2")) {
                    newRoom.setRoomStatus(RoomStatus.CLEANING);
                    break;
                } else if (input.equals("3")) {
                    newRoom.setRoomStatus(RoomStatus.ALLOCATED);
                    break;
                } else if (input.equals("4")) {
                    newRoom.setRoomStatus(RoomStatus.DISABLED);
                    break;
                } else {
                    System.out.println("Input not recognised, please re-enter value.");
                }
            }
            System.out.println("*** List of available Room Types ***");
            for (RoomType roomType : roomTypes) {
                System.out.println(roomType.getRoomTypeId() + ": " + roomType.getRoomTypeName());
            }
            System.out.println("-------------------------");
            while (true) {
                System.out.print("Please enter Room Type ID of the Room> ");
                roomTypeId = sc.nextLong();
                if (!roomTypeControllerRemote.retrieveRoomTypeByRoomTypeId(roomTypeId).getIsEnabled()) {
                    System.out.println("Room Type is currently DISABLED. Please select another Room Type.");
                    continue;
                } else {
                    break;
                }
            }
            roomControllerRemote.createNewRoom(newRoom, roomTypeId);
            System.out.println("Room Number: " + newRoom.getRoomNumber()  + " with Room Type " + roomTypeControllerRemote.retrieveRoomTypeByRoomTypeId(roomTypeId).getRoomTypeName() + " created!\n");
        } catch (RoomTypeNotFoundException | RoomExistException | GeneralException ex) {
            System.out.println("An error has occurred while creating the new room: " + ex.getMessage() + "!\n");
        }
    }
    
    private void updateRoom() {
        Scanner sc = new Scanner(System.in);
        String input;
        Long roomTypeId;
        List<Room> rooms = roomControllerRemote.retrieveAllRooms();
        List<RoomType> roomTypes = roomTypeControllerRemote.retrieveAllRoomTypes();

        System.out.println("*** Hotel Reservation System :: Hotel Operations :: Update Room ***\n");
        
        viewAllRooms();

        while (true) {
            System.out.print("Enter Room ID to update> ");
            int response = sc.nextInt();
            if (response >= 1 && response <= rooms.size()) {
                try {
                    currentRoom = roomControllerRemote.retrieveRoomByRoomId(Long.valueOf(response));
                    break;
                } catch (RoomNotFoundException ex) {
                    System.out.println("Invalid option!\n");
                }
            }
        }
        
        //consume new line
        sc.nextLine();
        
        while (true) {
            System.out.print("Enter new Room Number (blank if no change)> ");
            input = sc.nextLine();
            if (input.length() > 0) {
                try {
                    if (!roomControllerRemote.checkRoomExistsByRoomNumber(Integer.parseInt(input))) {
                        currentRoom.setRoomNumber(Integer.parseInt(input));
                        break;
                    } else {
                        System.out.println("Room Number already exists, please enter another number.");
                    }
                } catch (NumberFormatException ex) {
                    System.out.println("Please enter numeric values.");
                }
            } else {
                break;
            }
        }
        
        System.out.println("Please set the status of the room (1: AVAILABLE, 2: CLEANING, 3: ALLOCATED, 4: DISABLED)> ");
        input = sc.nextLine().trim();
        while (true) {
            if (input.equals("1")) {
                currentRoom.setRoomStatus(RoomStatus.AVAILABLE);
                break;
            } else if (input.equals("2")) {
                currentRoom.setRoomStatus(RoomStatus.CLEANING);
                break;
            } else if (input.equals("3")) {
                currentRoom.setRoomStatus(RoomStatus.ALLOCATED);
                break;
            } else if (input.equals("4")) {
                currentRoom.setRoomStatus(RoomStatus.DISABLED);
                break;
            } else {
                System.out.println("Input not recognised, please re-enter value.");
            }
        }
        
        System.out.println("*** List of available Room Types ***");
        for (RoomType roomType : roomTypes) {
            System.out.println(roomType.getRoomTypeId() + ": " + roomType.getRoomTypeName());
        }
        System.out.println("-------------------------");
        System.out.print("Please enter Room Type ID of the Room> ");
        input = sc.nextLine().trim();
        if (input.length() == 0) {
            input = currentRoom.getRoomType().getRoomTypeId().toString();
        }
        try {
            roomControllerRemote.updateRoom(currentRoom, Long.parseLong(input));
            System.out.println("-------------------------");
            System.out.println("Room updated!\n");
        } catch (RoomTypeNotFoundException ex) {
            System.out.println("An error has occurred while updating the room: " + ex.getMessage() + "!\n");
        }
    }
    
    private void deleteRoom() {
        
    }
    
    private void viewAllRooms() {
        Scanner sc = new Scanner(System.in);
        List<Room> rooms = roomControllerRemote.retrieveAllRooms();

        System.out.println("*** List of Rooms ***");
        for (Room room : rooms) {
            System.out.println("Room ID: " + room.getRoomId() + "| Room Number: " + room.getRoomNumber() + " | Status: " + room.getRoomStatus());
        }
        System.out.println("-------------------------");
        System.out.print("Press any key to continue...");
        sc.nextLine();
        System.out.println();
    }
    
    private void viewRoomAllocationExceptionReport() {
        
    }
    
    private void createNewRoomRate() {
        Scanner sc = new Scanner(System.in);
        RoomRate newRoomRate;
        BigDecimal rate;
        Long roomTypeId;
        String input;
        Integer response = 0;
        List<RoomType> roomTypes = roomTypeControllerRemote.retrieveAllRoomTypes();

        System.out.println("*** Hotel Reservation System :: Hotel Operations :: Create New Room Rate ***\n");
        System.out.println("1: Published Room Rate");
        System.out.println("2: Normal Room Rate");
        System.out.println("3: Peak Room Rate");
        System.out.println("4: Promo Room Rate");
        System.out.println("Please select type of Room Rate to create> ");
        response = sc.nextInt();
        //consume empty line
        sc.nextLine();
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
