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
import entity.NormalRoomRate;
import entity.PeakRoomRate;
import entity.PromoRoomRate;
import entity.PublishedRoomRate;
import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import util.enumeration.EmployeeAccessRightEnum;
import util.enumeration.RoomStatus;
import util.exception.GeneralException;
import util.exception.InvalidAccessRightException;
import util.exception.RoomExistException;
import util.exception.RoomInventoryNotFoundException;
import util.exception.RoomNotFoundException;
import util.exception.RoomRateExistException;
import util.exception.RoomRateNotFoundException;
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
    private RoomRate currentRoomRate;

    public HotelOperationModule() {
    }

    public HotelOperationModule(EmployeeControllerRemote employeeControllerRemote, RoomControllerRemote roomControllerRemote, RoomTypeControllerRemote roomTypeControllerRemote, Employee currentEmployee, RoomRateControllerRemote roomRateControllerRemote) {
        this.employeeControllerRemote = employeeControllerRemote;
        this.roomControllerRemote = roomControllerRemote;
        this.roomTypeControllerRemote = roomTypeControllerRemote;
        this.currentEmployee = currentEmployee;
        this.roomRateControllerRemote = roomRateControllerRemote;
    }

    public void menuOP() throws InvalidAccessRightException {
        if (currentEmployee.getAccessRight() != EmployeeAccessRightEnum.OPMANAGER) {
            throw new InvalidAccessRightException("You don't have OPERATION MANAGER rights to access the Hotel Operation Module.");
        }

        Scanner sc = new Scanner(System.in);
        Integer response = 0;
        String input;

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
            System.out.println("11: Back\n");
            response = 0;

            while (response < 1 || response > 20) {
                System.out.print("> ");
                input = sc.nextLine().trim();
                try {
                    response = Integer.parseInt(input);
                } catch (NumberFormatException ex) {
                    System.out.println("Please enter a numerical value.");
                    continue;
                }

                switch (response) {
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
                        return;
                    default:
                        break;
                }
            }
        }
    }

    public void menuSA() throws InvalidAccessRightException {
        if (currentEmployee.getAccessRight() != EmployeeAccessRightEnum.SAMANAGER) {
            throw new InvalidAccessRightException("You don't have SALES MANAGER rights to access the Hotel Operation Module.");
        }

        Scanner sc = new Scanner(System.in);
        Integer response = 0;
        String input;

        while (true) {
            System.out.println("*** Hotel Reservation System :: Hotel Operations ***\n");
            System.out.println("1: Create New Room Rate");
            System.out.println("2: View Room Rate Details");
            System.out.println("3: Update Room Rate");
            System.out.println("4: Delete Room Rate");
            System.out.println("5: View All Room Rates");
            System.out.println("6: Back\n");
            response = 0;

            while (response < 1 || response > 20) {
                System.out.print("> ");
                input = sc.nextLine().trim();
                try {
                    response = Integer.parseInt(input);
                } catch (NumberFormatException ex) {
                    System.out.println("Please enter a numerical value.");
                    continue;
                }

                switch (response) {
                    case 1:
                        createNewRoomRate();
                        break;
                    case 2:
                        viewRoomRateDetails();
                        break;
                    case 3:
                        updateRoomRate();
                        break;
                    case 4:
                        deleteRoomRate();
                        break;
                    case 5:
                        viewAllRoomRates();
                        break;
                    case 6:
                        return;
                    default:
                        break;
                }
            }
        }
    }

    private void createNewRoomType() {
        Scanner sc = new Scanner(System.in);
        String input;
        Integer value = 0;
        Integer count = 1;
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
                } catch (RoomTypeNotFoundException ex) {
                    System.out.println("Room Type Name " + roomTypeName + " accepted.");
                    newRoomType.setRoomTypeName(roomTypeName);
                    conditionChecker = false;
                }
            }

            System.out.print("Enter Room Type Description> ");
            newRoomType.setRoomTypeDescription(sc.nextLine().trim());

            while (true) {
                System.out.print("Enter Room Size in sq m> ");
                input = sc.nextLine().trim();
                try {
                    value = Integer.parseInt(input);
                    newRoomType.setRoomSize(value);
                    break;
                } catch (NumberFormatException ex) {
                    System.out.println("Please enter a numerical value.");
                }
            }

            System.out.print("Enter Bed Info> ");
            newRoomType.setBedInfo(sc.nextLine().trim());

            while (true) {
                System.out.print("Enter Capacity> ");
                input = sc.nextLine().trim();
                try {
                    value = Integer.parseInt(input);
                    newRoomType.setCapacity(value);
                    break;
                } catch (NumberFormatException ex) {
                    System.out.println("Please enter a numerical value.");
                }
            }

            System.out.print("Enter Amenities> ");
            newRoomType.setAmenities(sc.nextLine().trim());

            while (true) {
                System.out.print("Enter Number of Rooms of this Room Type> ");
                input = sc.nextLine().trim();
                try {
                    value = Integer.parseInt(input);
                    newRoomType.setNumRooms(value);
                    break;
                } catch (NumberFormatException ex) {
                    System.out.println("Please enter a numerical value.");
                }
            }

            while (true) {
                System.out.print("Enable Room Type? Y/N> ");
                input = sc.nextLine().trim();
                if (input.toLowerCase().equals("y")) {
                    newRoomType.setIsEnabled(true);
                    break;
                }
                if (input.toLowerCase().equals("n")) {
                    newRoomType.setIsEnabled(false);
                    break;
                }
                System.out.println("Please enter Y/N.");
            }

            newRoomType = roomTypeControllerRemote.createNewRoomType(newRoomType);
            System.out.println("New Room Type (" + newRoomType.getRoomTypeName() + ") created successfully! Room Type ID is " + newRoomType.getRoomTypeId() + "\n");
        } catch (RoomTypeExistException | GeneralException ex) {
            System.out.println("An error has occurred while creating the new room type: " + ex.getMessage() + " !\n");
        }

        while (count <= value) {
            Room newRoom = new Room();
            Long roomTypeId;
            List<RoomType> roomTypes = roomTypeControllerRemote.retrieveAllRoomTypes();

            System.out.println("*** Hotel Reservation System :: Hotel Operations :: Create New Room (" + count + "/" + value + ") ***\n");

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

                while (true) {
                    System.out.print("Please set the status of the room (1: AVAILABLE, 2: CLEANING, 3: ALLOCATED, 4: DISABLED)> ");
                    input = sc.nextLine().trim();
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
                while (true) {
                    System.out.println("Room Type ID automatically selected: Room Type ID | " + newRoomType.getRoomTypeId() + " | " + "Room Type Name: " + newRoomType.getRoomTypeName());
                    roomTypeId = newRoomType.getRoomTypeId();
                    if (!roomTypeControllerRemote.retrieveRoomTypeByRoomTypeId(roomTypeId).getIsEnabled()) {
                        System.out.println("Room Type is currently DISABLED. Please enable Room Type via Menu manually.");
                        return;
                    } else {
                        break;
                    }
                }
                roomControllerRemote.createNewRoom(newRoom, roomTypeId);
                count++;
                System.out.println("Room Number: " + newRoom.getRoomNumber() + " with Room Type " + roomTypeControllerRemote.retrieveRoomTypeByRoomTypeId(roomTypeId).getRoomTypeName() + " created!\n");
            } catch (RoomTypeNotFoundException | RoomExistException | GeneralException ex) {
                System.out.println("An error has occurred while creating the new room: " + ex.getMessage() + "!\n");
            }
        }
    }

    private void viewRoomTypeDetails() {
        Scanner sc = new Scanner(System.in);
        String input;
        Integer response;
        Boolean conditionChecker = true;
        List<RoomType> roomTypes = roomTypeControllerRemote.retrieveAllRoomTypes();

        System.out.println("*** Hotel Reservation System :: Hotel Operations :: View Room Type Details ***\n");

        while (conditionChecker) {
            System.out.print("Enter Room Type ID to query> ");
            try {
                input = sc.nextLine().trim();
                response = Integer.parseInt(input);
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a numerical value.");
                continue;
            }
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
                } catch (Exception ex) {
                    System.out.println("An unexpected error occurred: " + ex.getMessage());
                }
            } else {
                System.out.println("Invalid option!! Please retry.");
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
                        System.out.println("Room Type is currently associated with other entities. Set to DISABLED instead.\n");
                    } else {
                        System.out.println("Room Type " + roomTypeIdToRemove + " removed from database.");
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

            while (true) {
                System.out.print("Please set the status of the room (1: AVAILABLE, 2: CLEANING, 3: ALLOCATED, 4: DISABLED)> ");
                input = sc.nextLine().trim();
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
                System.out.print("Please enter Room Type ID of the Room (-1 to cancel Room creation) > ");
                input = sc.nextLine().trim();
                roomTypeId = -1l;
                try {
                    roomTypeId = Long.parseLong(input);
                    if (roomTypeId == -1) {
                        break;
                    }
                } catch (NumberFormatException ex) {
                    System.out.println("Please enter a numerical value.");
                }
                try {
                    if (!roomTypeControllerRemote.retrieveRoomTypeByRoomTypeId(roomTypeId).getIsEnabled()) {
                        System.out.println("Room Type is currently DISABLED. Please select another Room Type.");
                        continue;
                    } else if (roomTypeControllerRemote.retrieveRoomTypeByRoomTypeId(roomTypeId).getNumRooms() == roomControllerRemote.retrieveRoomsByRoomType(roomTypeId).size()) {
                        System.out.println("Room Type is at its pre-defined maximum number of rooms. Please select another Room Type or enter -1 to exit.");
                    } else {
                        break;
                    }
                } catch (RoomNotFoundException ex) {
                    System.out.println("Input not accepted. Please select another Room Type.");
                    continue;
                }
            }
            roomControllerRemote.createNewRoom(newRoom, roomTypeId);
            System.out.println("Room Number: " + newRoom.getRoomNumber() + " with Room Type " + roomTypeControllerRemote.retrieveRoomTypeByRoomTypeId(roomTypeId).getRoomTypeName() + " created!\n");
        } catch (RoomTypeNotFoundException | RoomExistException | GeneralException ex) {
            System.out.println("An error has occurred while creating the new room: " + ex.getMessage() + "!\n");
        }
    }

    private void updateRoom() {
        Scanner sc = new Scanner(System.in);
        String input;
        Integer response;
        Integer statusChanged = 0;
        List<Room> rooms = roomControllerRemote.retrieveAllRooms();
        List<RoomType> roomTypes = roomTypeControllerRemote.retrieveAllRoomTypes();

        System.out.println("*** Hotel Reservation System :: Hotel Operations :: Update Room ***\n");

        System.out.println("*** List of Rooms ***");
        for (Room room : rooms) {
            System.out.println("Room ID: " + room.getRoomId() + "| Room Number: " + room.getRoomNumber() + "| Room Type: " + room.getRoomType().getRoomTypeName() + " | Status: " + room.getRoomStatus());
        }
        System.out.println("-------------------------");

        while (true) {
            System.out.print("Enter Room ID to update> ");
            input = sc.nextLine().trim();
            try {
                response = Integer.parseInt(input);
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a numerical value.");
                continue;
            }
            if (response > 0) {
                try {
                    currentRoom = roomControllerRemote.retrieveRoomByRoomId(Long.valueOf(response));
                    break;
                } catch (RoomNotFoundException ex) {
                    System.out.println("Invalid option!\n");
                }
            }
        }

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

        while (true) {
            System.out.print("Please set the status of the room (1: AVAILABLE, 2: CLEANING, 3: ALLOCATED, 4: DISABLED)> ");
            input = sc.nextLine().trim();
            if (input.equals("1")) {
                if (currentRoom.getRoomStatus().equals(RoomStatus.AVAILABLE) || currentRoom.getRoomStatus().equals(RoomStatus.CLEANING)) {
                    statusChanged = 0;
                }
                if (currentRoom.getRoomStatus().equals(RoomStatus.ALLOCATED) || currentRoom.getRoomStatus().equals(RoomStatus.DISABLED)) {
                    statusChanged = 1;
                }
                currentRoom.setRoomStatus(RoomStatus.AVAILABLE);
                break;
            } else if (input.equals("2")) {
                if (currentRoom.getRoomStatus().equals(RoomStatus.AVAILABLE) || currentRoom.getRoomStatus().equals(RoomStatus.CLEANING)) {
                    statusChanged = 0;
                }
                if (currentRoom.getRoomStatus().equals(RoomStatus.ALLOCATED) || currentRoom.getRoomStatus().equals(RoomStatus.DISABLED)) {
                    statusChanged = 1;
                }
                currentRoom.setRoomStatus(RoomStatus.CLEANING);
                break;
            } else if (input.equals("3")) {
                if (currentRoom.getRoomStatus().equals(RoomStatus.AVAILABLE) || currentRoom.getRoomStatus().equals(RoomStatus.CLEANING)) {
                    statusChanged = -1;
                }
                if (currentRoom.getRoomStatus().equals(RoomStatus.ALLOCATED) || currentRoom.getRoomStatus().equals(RoomStatus.DISABLED)) {
                    statusChanged = 0;
                }
                currentRoom.setRoomStatus(RoomStatus.ALLOCATED);
                break;
            } else if (input.equals("4")) {
                if (currentRoom.getRoomStatus().equals(RoomStatus.AVAILABLE) || currentRoom.getRoomStatus().equals(RoomStatus.CLEANING)) {
                    statusChanged = -1;
                }
                if (currentRoom.getRoomStatus().equals(RoomStatus.ALLOCATED) || currentRoom.getRoomStatus().equals(RoomStatus.DISABLED)) {
                    statusChanged = 0;
                }
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
        while (true) {
            System.out.print("Please enter Room Type ID of the Room> ");
            input = sc.nextLine().trim();
            if (input.length() == 0 || input.length() > 0) {
                //if (input.length() == 0) {
                input = currentRoom.getRoomType().getRoomTypeId().toString();
                System.out.println("Room Type ID cannot be changed at this time. Selected previous ID: " + input + " by default.");
            }
            try {
                if (!roomTypeControllerRemote.retrieveRoomTypeByRoomTypeId(Long.parseLong(input)).getIsEnabled()) {
                    System.out.println("Room Type is currently DISABLED. Please select another Room Type.");
                } else {
                    break;
                }
            } catch (RoomTypeNotFoundException ex) {
                System.out.println("An error has occurred while updating the room: " + ex.getMessage() + "!\n");
            }
        }
        try {
            roomControllerRemote.updateRoom(currentRoom, Long.parseLong(input), statusChanged);
            System.out.println("-------------------------");
            System.out.println("Room updated!\n");
        } catch (RoomTypeNotFoundException | RoomInventoryNotFoundException ex) {
            System.out.println("An error has occurred while updating the room: " + ex.getMessage() + "!\n");
        }
    }

    private void deleteRoom() {
        Scanner sc = new Scanner(System.in);
        String input;
        Long roomId;
        Boolean deleteResult;
        List<Room> rooms = roomControllerRemote.retrieveAllRooms();

        System.out.println("*** List of Rooms ***");
        for (Room room : rooms) {
            System.out.println("Room ID: " + room.getRoomId() + "| Room Number: " + room.getRoomNumber() + " | Status: " + room.getRoomStatus());
        }
        System.out.println("-------------------------");

        while (true) {
            System.out.print("Enter Room ID to delete> ");
            input = sc.nextLine().trim();
            try {
                roomId = Long.parseLong(input);
                try {
                    deleteResult = roomControllerRemote.deleteRoom(roomId);
                } catch (RoomNotFoundException | RoomInventoryNotFoundException ex) {
                    System.out.println("Please enter a valid Room ID.");
                    continue;
                }
                break;
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a numerical value.");
            }
        }

        if (deleteResult) {
            System.out.println("Room " + roomId + " removed from the database.");
        } else {
            System.out.println("Room " + roomId + " is in use. Set to DISABLED.");
        }

    }

    private void viewAllRooms() {
        Scanner sc = new Scanner(System.in);
        List<Room> rooms = roomControllerRemote.retrieveAllRooms();

        System.out.println("*** List of Rooms ***");
        for (Room room : rooms) {
            System.out.println("Room ID: " + room.getRoomId() + "| Room Number: " + room.getRoomNumber() + "| Room Type: " + room.getRoomType().getRoomTypeName() + " | Status: " + room.getRoomStatus());
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
        //RoomRate newRoomRate;
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

        while (true) {
            System.out.print("Please select type of Room Rate to create> ");
            try {
                input = sc.nextLine().trim();
                try {
                    response = Integer.parseInt(input);
                } catch (NumberFormatException ex) {
                    System.out.println("Please enter numeric values.");
                    continue;
                }
                if (response > 0 || response < 5) {
                    switch (response) {
                        case 1:
                            PublishedRoomRate newPublishedRoomRate = new PublishedRoomRate();
                            System.out.println("Published Room Rate selected for creation.");
                            createNewPublishedRoomRate(sc, newPublishedRoomRate, roomTypes);
                            break;
                        case 2:
                            NormalRoomRate newNormalRoomRate = new NormalRoomRate();
                            System.out.println("Normal Room Rate selected for creation.");
                            createNewNormalRoomRate(sc, newNormalRoomRate, roomTypes);
                            break;
                        case 3:
                            PeakRoomRate newPeakRoomRate = new PeakRoomRate();
                            System.out.println("Peak Room Rate selected for creation.");
                            createNewPeakRoomRate(sc, newPeakRoomRate, roomTypes);
                            break;
                        case 4:
                            PromoRoomRate newPromoRoomRate = new PromoRoomRate();
                            System.out.println("Promo Room Rate selected for creation.");
                            createNewPromoRoomRate(sc, newPromoRoomRate, roomTypes);
                            break;
                        default:
                            System.out.println("Please make a selection between the provided values.");
                            continue;
                    }
                } else {
                    System.out.println("Please make a selection between the provided values.");
                    continue;
                }
                break;
            } catch (InputMismatchException ex) {
                System.out.println("Please enter numeric values.");
                continue;
            }
        }
    }

    private void createNewPublishedRoomRate(Scanner sc, PublishedRoomRate newRoomRate, List<RoomType> roomTypes) {
        String input;
        BigDecimal ratePerNight;
        System.out.print("Enter Room Rate Name> ");
        input = sc.nextLine().trim();
        newRoomRate.setRoomRateName(input);

        while (true) {
            System.out.print("Enter Room Rate per night (in SGD)> ");
            input = sc.nextLine().trim();
            try {
                ratePerNight = new BigDecimal(input);
                System.out.println("Rate entered: " + ratePerNight.toString());
                newRoomRate.setRatePerNight(ratePerNight);
                break;
            } catch (NumberFormatException ex) {
                System.out.println("Please enter numeric values.");
            }
        }

        while (true) {
            System.out.print("Enable Room Rate? Y/N> ");
            input = sc.nextLine().trim();
            if (input.toLowerCase().equals("y")) {
                newRoomRate.setIsEnabled(true);
                break;
            }
            if (input.toLowerCase().equals("n")) {
                newRoomRate.setIsEnabled(false);
                break;
            }
            System.out.println("Input not recognised! Please enter Y/N.");
        }

        System.out.println("*** List of available Room Types ***");
        for (RoomType roomType : roomTypes) {
            System.out.println(roomType.getRoomTypeId() + ": " + roomType.getRoomTypeName());
        }
        System.out.println("-------------------------");
        while (true) {
            System.out.print("Please enter Room Type ID of the Room Rate> ");
            input = sc.nextLine().trim();
            try {
                roomRateControllerRemote.createNewRoomRate(newRoomRate, Long.parseLong(input));
                System.out.println("-------------------------");
                System.out.println("Room Rate " + newRoomRate.getRoomRateName() + " created!\n");
                break;
            } catch (RoomRateExistException | GeneralException ex) {
                System.out.println("An error has occurred while creating the room rate: " + ex.getMessage() + "!\n");
            } catch (RoomTypeNotFoundException ex) {
                System.out.println("Room type not found error: " + ex.getMessage() + "!\n");
            }
        }
    }

    private void createNewNormalRoomRate(Scanner sc, NormalRoomRate newRoomRate, List<RoomType> roomTypes) {
        String input;
        BigDecimal ratePerNight;
        System.out.print("Enter Room Rate Name> ");
        input = sc.nextLine().trim();
        newRoomRate.setRoomRateName(input);

        while (true) {
            System.out.print("Enter Room Rate per night (in SGD)> ");
            input = sc.nextLine().trim();
            try {
                ratePerNight = new BigDecimal(input);
                System.out.println("Rate entered: " + ratePerNight.toString());
                newRoomRate.setRatePerNight(ratePerNight);
                break;
            } catch (NumberFormatException ex) {
                System.out.println("Please enter numeric values.");
            }
        }

        while (true) {
            System.out.print("Enable Room Rate? Y/N> ");
            input = sc.nextLine().trim();
            if (input.toLowerCase().equals("y")) {
                newRoomRate.setIsEnabled(true);
                break;
            }
            if (input.toLowerCase().equals("n")) {
                newRoomRate.setIsEnabled(false);
                break;
            }
            System.out.println("Input not recognised! Please enter Y/N.");
        }

        System.out.println("*** List of available Room Types ***");
        for (RoomType roomType : roomTypes) {
            System.out.println(roomType.getRoomTypeId() + ": " + roomType.getRoomTypeName());
        }
        System.out.println("-------------------------");
        while (true) {
            System.out.print("Please enter Room Type ID of the Room Rate> ");
            input = sc.nextLine().trim();
            try {
                roomRateControllerRemote.createNewRoomRate(newRoomRate, Long.parseLong(input));
                System.out.println("-------------------------");
                System.out.println("Room Rate " + newRoomRate.getRoomRateName() + " created!\n");
                break;
            } catch (RoomRateExistException | GeneralException ex) {
                System.out.println("An error has occurred while creating the room rate: " + ex.getMessage() + "!\n");
            } catch (RoomTypeNotFoundException ex) {
                System.out.println("Room type not found error: " + ex.getMessage() + "!\n");
            }
        }
    }

    private void createNewPeakRoomRate(Scanner sc, PeakRoomRate newRoomRate, List<RoomType> roomTypes) {
        String input, sDateTime, eDateTime;
        BigDecimal ratePerNight;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy HHmm");
        LocalDateTime startDateTime = LocalDateTime.now();
        LocalDateTime endDateTime = LocalDateTime.now();
        LocalDateTime now = LocalDateTime.now();

        System.out.print("Enter Room Rate Name> ");
        input = sc.nextLine().trim();
        newRoomRate.setRoomRateName(input);

        while (true) {
            System.out.print("Enter Room Rate per night (in SGD)> ");
            input = sc.nextLine().trim();
            try {
                ratePerNight = new BigDecimal(input);
                System.out.println("Rate entered: " + ratePerNight.toString());
                newRoomRate.setRatePerNight(ratePerNight);
                break;
            } catch (NumberFormatException ex) {
                System.out.println("Please enter numeric values.");
            }
        }

        while (true) {
            System.out.print("Enter starting date and time in the format (ddMMyyyy HHmm)> ");
            sDateTime = sc.nextLine().trim();
            try {
                startDateTime = LocalDateTime.parse(sDateTime, formatter);
            } catch (DateTimeParseException ex) {
                System.out.println("Please enter the date in the right format!");
                continue;
            }
            newRoomRate.setStartDate(startDateTime);
            break;
        }
        
        while(true) {
        System.out.print("Enter end date and time in the format (ddMMyyyy HHmm)> ");
        eDateTime = sc.nextLine().trim();
        try {
        endDateTime = LocalDateTime.parse(eDateTime, formatter);
        } catch (DateTimeParseException ex) {
            System.out.println("Please enter the date in the right format!");
            continue;
        }
        newRoomRate.setEndDate(endDateTime);
        break;
        }

        while (true) {
            System.out.print("Enable Room Rate? Y/N> ");
            input = sc.nextLine().trim();
            if (input.toLowerCase().equals("y")) {
                newRoomRate.setIsEnabled(true);
                break;
            }
            if (input.toLowerCase().equals("n")) {
                newRoomRate.setIsEnabled(false);
                break;
            }
            System.out.println("Input not recognised! Please enter Y/N.");
        }

        System.out.println("*** List of available Room Types ***");
        for (RoomType roomType : roomTypes) {
            System.out.println(roomType.getRoomTypeId() + ": " + roomType.getRoomTypeName());
        }
        System.out.println("-------------------------");
        while (true) {
            System.out.print("Please enter Room Type ID of the Room Rate> ");
            input = sc.nextLine().trim();
            try {
                roomRateControllerRemote.createNewRoomRate(newRoomRate, Long.parseLong(input));
                System.out.println("-------------------------");
                System.out.println("Room Rate " + newRoomRate.getRoomRateName() + " created!\n");
                break;
            } catch (RoomRateExistException | GeneralException ex) {
                System.out.println("An error has occurred while creating the room rate: " + ex.getMessage() + "!\n");
            } catch (RoomTypeNotFoundException ex) {
                System.out.println("Room type not found error: " + ex.getMessage() + "!\n");
            }
        }

    }

    private void createNewPromoRoomRate(Scanner sc, PromoRoomRate newRoomRate, List<RoomType> roomTypes) {
        String input, sDateTime, eDateTime;
        BigDecimal ratePerNight;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy HHmm");
        LocalDateTime startDateTime = LocalDateTime.now();
        LocalDateTime endDateTime = LocalDateTime.now();
        LocalDateTime now = LocalDateTime.now();

        System.out.print("Enter Room Rate Name> ");
        input = sc.nextLine().trim();
        newRoomRate.setRoomRateName(input);

        while (true) {
            System.out.print("Enter Room Rate per night (in SGD)> ");
            input = sc.nextLine().trim();
            try {
                ratePerNight = new BigDecimal(input);
                System.out.println("Rate entered: " + ratePerNight.toString());
                newRoomRate.setRatePerNight(ratePerNight);
                break;
            } catch (NumberFormatException ex) {
                System.out.println("Please enter numeric values.");
            }
        }

        System.out.print("Enter starting date and time in the format (ddMMyyyy HHmm)> ");
        sDateTime = sc.nextLine().trim();
        startDateTime = LocalDateTime.parse(sDateTime, formatter);
        newRoomRate.setStartDate(startDateTime);

        System.out.print("Enter end date and time in the format (ddMMyyyy HHmm)> ");
        eDateTime = sc.nextLine().trim();
        endDateTime = LocalDateTime.parse(eDateTime, formatter);
        newRoomRate.setEndDate(endDateTime);

        while (true) {
            System.out.print("Enable Room Rate? Y/N> ");
            input = sc.nextLine().trim();
            if (input.toLowerCase().equals("y")) {
                newRoomRate.setIsEnabled(true);
                break;
            }
            if (input.toLowerCase().equals("n")) {
                newRoomRate.setIsEnabled(false);
                break;
            }
            System.out.println("Input not recognised! Please enter Y/N.");
        }

        System.out.println("*** List of available Room Types ***");
        for (RoomType roomType : roomTypes) {
            System.out.println(roomType.getRoomTypeId() + ": " + roomType.getRoomTypeName());
        }
        System.out.println("-------------------------");
        while (true) {
            System.out.print("Please enter Room Type ID of the Room Rate> ");
            input = sc.nextLine().trim();
            try {
                roomRateControllerRemote.createNewRoomRate(newRoomRate, Long.parseLong(input));
                System.out.println("-------------------------");
                System.out.println("Room Rate " + newRoomRate.getRoomRateName() + " created!\n");
                break;
            } catch (RoomRateExistException | GeneralException ex) {
                System.out.println("An error has occurred while creating the room rate: " + ex.getMessage() + "!\n");
            } catch (RoomTypeNotFoundException ex) {
                System.out.println("Room type not found error: " + ex.getMessage() + "!\n");
            }
        }

    }

    private void viewRoomRateDetails() {
        Scanner sc = new Scanner(System.in);
        RoomRate roomRate;
        String input;
        Long roomRateId;

        while (true) {
            System.out.print("Please enter Room Rate ID of the Room Rate to query> ");
            input = sc.nextLine().trim();
            try {
                roomRateId = Long.parseLong(input);
                break;
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a numerical value.");
            }
        }

        try {
            roomRate = roomRateControllerRemote.retrieveRoomRateByRoomRateId(roomRateId);
            System.out.println("*** Room Rate Query ***");
            System.out.println("Room Rate ID: " + roomRate.getRoomRateId() + " | " + roomRate.getRoomRateName() + " | Rate per night: " + roomRate.getRatePerNight() + " | Room Type: " + roomRate.getRoomType().getRoomTypeName() + " | Enabled: " + roomRate.getIsEnabled());
        } catch (NullPointerException ex) {
            System.out.println("No current room rates available.");
            return;
        } catch (RoomRateNotFoundException ex) {
            System.out.println("Room Rate ID does not exist!");
        }

        System.out.print("Press any key to continue...");
        sc.nextLine();
    }

    private void updateRoomRate() {
        Scanner sc = new Scanner(System.in);
        String input, dateTime;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy HHmm");
        Integer response;
        BigDecimal ratePerNight;
        List<RoomRate> roomRates;
        List<RoomType> roomTypes = roomTypeControllerRemote.retrieveAllRoomTypes();
        Boolean containsDate = false;
        LocalDateTime startDateTime = LocalDateTime.now();
        LocalDateTime endDateTime = LocalDateTime.now();
        LocalDateTime now = LocalDateTime.now();
        int count = 0;
        int year, month, day, hour, min;

        try {
            roomRates = roomRateControllerRemote.retrieveAllRoomRates();
            System.out.println("*** List of Room Rates ***");
            for (RoomRate roomRate : roomRates) {
                System.out.println("Room Rate ID: " + roomRate.getRoomRateId() + " | " + roomRate.getRoomRateName() + " | Rate per night: " + roomRate.getRatePerNight() + " | Room Type: " + roomRate.getRoomType().getRoomTypeName() + " | Enabled: " + roomRate.getIsEnabled());
            }
        } catch (NullPointerException ex) {
            System.out.println("No current room rates available.");
            return;
        }

        while (true) {
            System.out.print("Enter Room Rate ID to update> ");
            input = sc.nextLine().trim();
            try {
                response = Integer.parseInt(input);
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a numerical value.");
                continue;
            }
            if (response >= 1 && response <= roomRates.size()) {
                try {
                    currentRoomRate = roomRateControllerRemote.retrieveRoomRateByRoomRateId(Long.valueOf(response));
                    break;
                } catch (RoomRateNotFoundException ex) {
                    System.out.println("Invalid option!\n");
                }
            } else {
                System.out.println("Please enter an existing Room Rate ID.");
            }
        }

        System.out.print("Enter Room Rate Name (blank if no change)> ");
        input = sc.nextLine().trim();
        if (input.length() > 0) {
            currentRoomRate.setRoomRateName(input);
        }

        while (true) {
            System.out.print("Enter Room Rate per night (in SGD) (blank if no change)> ");
            input = sc.nextLine().trim();
            if (input.length() > 0) {
                try {
                    ratePerNight = new BigDecimal(input);
                    System.out.println("Rate entered: " + ratePerNight.toString());
                    currentRoomRate.setRatePerNight(ratePerNight);
                    break;
                } catch (NumberFormatException ex) {
                    System.out.println("Please enter numeric values.");
                }
            } else {
                break;
            }
        }
        System.out.println("Current RoomRate class is " + currentRoomRate.getClass().getName());
        if (currentRoomRate.getClass().getName().equals("entity.PeakRoomRate") || currentRoomRate.getClass().getName().equals("entity.PromoRoomRate")) {
            containsDate = true;
        }

        if (containsDate) {
            System.out.print("Enter starting date and time in the format (ddMMyyyy HHmm) (blank if no change)> ");
            dateTime = sc.nextLine().trim();
            startDateTime = LocalDateTime.parse(dateTime, formatter);

            if (!dateTime.isEmpty()) {
                if (response == 3) {
                    ((PeakRoomRate) currentRoomRate).setStartDate(startDateTime);
                }
                if (response == 4) {
                    ((PromoRoomRate) currentRoomRate).setStartDate(startDateTime);
                }
            }

            System.out.print("Enter ending date and time in the format (yyyymmddhhmm) (blank if no change)> ");
            dateTime = sc.nextLine().trim();
            endDateTime = LocalDateTime.parse(dateTime, formatter);

            if (!dateTime.isEmpty()) {
                if (response == 3) {
                    ((PeakRoomRate) currentRoomRate).setEndDate(endDateTime);
                }
                if (response == 4) {
                    ((PromoRoomRate) currentRoomRate).setEndDate(endDateTime);
                }
            }
        }

        while (true) {
            System.out.print("Enable Room Rate? Y/N> ");
            input = sc.nextLine().trim();
            if (input.toLowerCase().equals("y")) {
                currentRoomRate.setIsEnabled(true);
                break;
            }
            if (input.toLowerCase().equals("n")) {
                currentRoomRate.setIsEnabled(false);
                break;
            }
            System.out.println("Input not recognised! Please enter Y/N.");
        }

        System.out.println("*** List of available Room Types ***");
        for (RoomType roomType : roomTypes) {
            System.out.println(roomType.getRoomTypeId() + ": " + roomType.getRoomTypeName());
        }
        System.out.println("-------------------------");
        while (true) {
            System.out.print("Please enter Room Type ID of the Room (blank if no change)> ");
            input = sc.nextLine().trim();
            if (input.length() == 0) {
                input = currentRoomRate.getRoomType().getRoomTypeId().toString();
            }
            try {
                if (!roomTypeControllerRemote.retrieveRoomTypeByRoomTypeId(Long.parseLong(input)).getIsEnabled()) {
                    System.out.println("Room Type is currently DISABLED. Please select another Room Type.");
                } else {
                    break;
                }
            } catch (RoomTypeNotFoundException ex) {
                System.out.println("An error has occurred while updating the room: " + ex.getMessage() + "!\n");
            }
        }
        try {
            roomRateControllerRemote.updateRoomRate(currentRoomRate, Long.parseLong(input));
            System.out.println("-------------------------");
            System.out.println("Room Rate updated!\n");
        } catch (RoomTypeNotFoundException ex) {
            System.out.println("An error has occurred while updating the room: " + ex.getMessage() + "!\n");
        }
    }

    private void deleteRoomRate() {
        Scanner sc = new Scanner(System.in);
        String input;
        Long roomRateId;
        Boolean deleteResult;
        List<RoomRate> roomRates;

        try {
            roomRates = roomRateControllerRemote.retrieveAllRoomRates();
            System.out.println("*** List of Room Rates ***");
            for (RoomRate roomRate : roomRates) {
                System.out.println("Room Rate ID: " + roomRate.getRoomRateId() + " | " + roomRate.getRoomRateName() + " | Rate per night: " + roomRate.getRatePerNight() + " | Room Type: " + roomRate.getRoomType().getRoomTypeName() + " | Enabled: " + roomRate.getIsEnabled());
            }
        } catch (NullPointerException ex) {
            System.out.println("No current room rates available.");
            return;
        }

        while (true) {
            System.out.print("Enter Room Rate to delete> ");
            input = sc.nextLine().trim();
            try {
                roomRateId = Long.parseLong(input);
                try {
                    deleteResult = roomRateControllerRemote.deleteRoomRate(roomRateId);
                } catch (RoomRateNotFoundException ex) {
                    System.out.println("Please enter a valid Room Rate ID.");
                    continue;
                }
                break;
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a numerical value.");
            }
        }

        if (deleteResult) {
            System.out.println("Room " + roomRateId + " removed from the database.\n");
        } else {
            System.out.println("Room " + roomRateId + " is in use. Set to DISABLED.\n");
        }
    }

    private void viewAllRoomRates() {
        Scanner sc = new Scanner(System.in);
        List<RoomRate> roomRates;

        try {
            roomRates = roomRateControllerRemote.retrieveAllRoomRates();
            System.out.println("*** List of Room Rates ***");
            for (RoomRate roomRate : roomRates) {
                System.out.println("Room Rate ID: " + roomRate.getRoomRateId() + " | " + roomRate.getRoomRateName() + " | Rate per night: " + roomRate.getRatePerNight() + " | Room Type: " + roomRate.getRoomType().getRoomTypeName() + " | Enabled: " + roomRate.getIsEnabled());
            }
        } catch (NullPointerException ex) {
            System.out.println("No current room rates available.");
            return;
        }

        System.out.print("Press any key to continue...");
        sc.nextLine();
    }
}
