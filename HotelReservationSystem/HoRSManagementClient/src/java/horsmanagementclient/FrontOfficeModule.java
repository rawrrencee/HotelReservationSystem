/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsmanagementclient;

import ejb.session.stateless.GuestControllerRemote;
import ejb.session.stateless.RoomControllerRemote;
import ejb.session.stateless.RoomInventoryControllerRemote;
import ejb.session.stateless.RoomRateControllerRemote;
import ejb.session.stateless.RoomTypeControllerRemote;
import entity.Employee;
import entity.Guest;
import entity.RoomInventory;
import entity.RoomRate;
import entity.RoomType;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import util.enumeration.EmployeeAccessRightEnum;
import util.exception.CheckRoomInventoryAvailabilityException;
import util.exception.CheckRoomInventoryException;
import util.exception.GeneralException;
import util.exception.GuestExistException;
import util.exception.GuestNotFoundException;
import util.exception.InvalidAccessRightException;
import util.exception.RoomInventoryExistException;
import util.exception.RoomInventoryNotFoundException;

/**
 *
 * @author Lawrence
 */
public class FrontOfficeModule {

    private RoomRateControllerRemote roomRateControllerRemote;
    private RoomControllerRemote roomControllerRemote;
    private RoomTypeControllerRemote roomTypeControllerRemote;
    private RoomInventoryControllerRemote roomInventoryControllerRemote;
    private GuestControllerRemote guestControllerRemote;

    private Employee currentEmployee;

    public FrontOfficeModule() {
    }

    public FrontOfficeModule(RoomRateControllerRemote roomRateControllerRemote, RoomControllerRemote roomControllerRemote, RoomTypeControllerRemote roomTypeControllerRemote, RoomInventoryControllerRemote roomInventoryControllerRemote, GuestControllerRemote guestControllerRemote, Employee currentEmployee) {
        this.roomRateControllerRemote = roomRateControllerRemote;
        this.roomControllerRemote = roomControllerRemote;
        this.roomTypeControllerRemote = roomTypeControllerRemote;
        this.roomInventoryControllerRemote = roomInventoryControllerRemote;
        this.guestControllerRemote = guestControllerRemote;
        this.currentEmployee = currentEmployee;
    }

    public void menuMain() throws InvalidAccessRightException {
        if (currentEmployee.getAccessRight() != EmployeeAccessRightEnum.GUESTRELOFF) {
            throw new InvalidAccessRightException("You don't have GUEST RELATION OFFICER rights to access the Front Office4 Module!!");
        }

        Scanner sc = new Scanner(System.in);
        String input;
        Integer response = 0;

        while (true) {
            System.out.println("*** Hotel Reservation System :: Front Office ***\n");
            System.out.println("1: Walk-in Search Room");
            System.out.println("2: Walk-in Reserve Room");
            System.out.println("3: Check-in Guest");
            System.out.println("4: Check-out Guest");
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
                        walkInSearchRoom();
                        break;
                    case 2:
                        walkInReserveRoom();
                        break;
                    case 3:
                        checkInGuest();
                        break;
                    case 4:
                        checkOutGuest();
                        break;
                    case 5:
                        return;
                    default:
                        break;
                }
            }
        }
    }

    public void walkInSearchRoom() {
        Scanner sc = new Scanner(System.in);
        List<RoomType> roomTypes = roomTypeControllerRemote.retrieveAllRoomTypes();
        List<RoomInventory> roomInventories;
        RoomInventory currentRoomInventory;
        Integer numRoomsLeft = 0;
        List<RoomRate> roomRates;
        BigDecimal lowestPublishedRate = new BigDecimal(Integer.MAX_VALUE);
        Calendar checkInDate = Calendar.getInstance();
        Calendar checkOutDate = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        String checkInDateString, checkOutDateString;
        int year, month, day;
        int count = 0;

        System.out.println("*** Hotel Reservation System :: Front Office :: Walk-in Search Room ***\n");

        do {
            if (count > 0) {
                System.out.println("Start date and time cannot be before current date and time!");
            }
            System.out.print("Enter Check-in date in the format (yyyymmdd)> ");
            checkInDateString = sc.nextLine().trim();
            if (checkInDateString.isEmpty() || checkInDateString.length() != 8) {
                System.out.println("Check-in date value cannot be empty and should contain 8 numbers.");
                count = -1;
                checkInDate.set(1990, 0, 1, 0, 0);
                continue;
            }

            try {
                year = Integer.parseInt(checkInDateString.substring(0, 4).trim());
                month = Integer.parseInt(checkInDateString.substring(4, 6).trim());
                day = Integer.parseInt(checkInDateString.substring(6, 8).trim());
                checkInDate.clear();
                checkInDate.set(year, month - 1, day);
                checkInDate.set(Calendar.HOUR_OF_DAY, 0);
                checkInDate.set(Calendar.MINUTE, 0);
                checkInDate.set(Calendar.SECOND, 0);
                checkInDate.set(Calendar.MILLISECOND, 0);
            } catch (NumberFormatException ex) {
                System.out.println("Please enter numeric values.");
                checkInDate.set(1990, 0, 1, 0, 0);
                count = 0;
                continue;
            }
            count++;
        } while (now.compareTo(checkInDate) > 0);

        count = 0;

        do {
            if (count > 0) {
                System.out.println("Check-out date must be later than start date!");
            }
            System.out.print("Enter Check-out date in the format (yyyymmdd)> ");
            checkOutDateString = sc.nextLine().trim();

            if (checkOutDateString.isEmpty() || checkOutDateString.length() < 8) {
                System.out.println("Check-out date value cannot be empty and should contain 8 numbers!");
                checkOutDate.set(1990, 0, 1, 0, 0);
                count = 0;
                continue;
            }
            try {
                year = Integer.parseInt(checkOutDateString.substring(0, 4).trim());
                month = Integer.parseInt(checkOutDateString.substring(4, 6).trim());
                day = Integer.parseInt(checkOutDateString.substring(6, 8).trim());
                checkOutDate.clear();
                checkOutDate.set(year, month - 1, day);
                checkOutDate.set(Calendar.HOUR_OF_DAY, 0);
                checkOutDate.set(Calendar.MINUTE, 0);
                checkOutDate.set(Calendar.SECOND, 0);
                checkOutDate.set(Calendar.MILLISECOND, 0);
            } catch (NumberFormatException ex) {
                System.out.println("Please enter numeric values.");
                checkOutDate.set(1990, 0, 1, 0, 0);
                count = 0;
                continue;
            }
            count++;
        } while (now.compareTo(checkOutDate) > 0 || Long.parseLong(checkOutDateString) < Long.parseLong(checkInDateString));

        for (Date date = checkInDate.getTime(); checkInDate.before(checkOutDate); checkInDate.add(Calendar.DATE, 1), date = checkInDate.getTime()) {
            // Do your job here with `date`.
            numRoomsLeft = 0;
            for (RoomType roomType : roomTypes) {
                if (roomType.getIsEnabled()) {
                    try {
                        currentRoomInventory = roomInventoryControllerRemote.retrieveRoomInventoryByDate(date, roomType.getRoomTypeId());
                        numRoomsLeft += currentRoomInventory.getNumRoomsLeft();
                    } catch (RoomInventoryNotFoundException ex) {
                        try {
                            currentRoomInventory = roomInventoryControllerRemote.createNewRoomInventory(date, roomType.getRoomTypeId());
                            numRoomsLeft += currentRoomInventory.getNumRoomsLeft();
                        } catch (GeneralException | RoomInventoryExistException e) {
                            System.out.println("An unexpected error has occured during creation of Room Inventory: " + e.getMessage());
                        }
                    }
                }
            }
            if (numRoomsLeft == 0) {
                System.out.println("All rooms in hotel for one or more dates have been allocated or are not available for reservation.");
                return;
            }
            System.out.println("\n----------Date: " + date);
            try {
                roomInventories = roomInventoryControllerRemote.retrieveAllRoomInventoriesOnDate(date);
                for (RoomInventory roomInventory : roomInventories) {
                    roomRates = roomInventoryControllerRemote.retrieveRoomRatesByTypeOfRoomInventory(roomInventory.getRoomInventoryId());
                    for (RoomRate roomRate : roomRates) {
                        if (lowestPublishedRate.compareTo(roomRate.getRatePerNight()) == 1) {
                            lowestPublishedRate = roomRate.getRatePerNight();
                        }
                    }
                    System.out.println("Room Type: " + roomInventory.getRoomType().getRoomTypeName() + " | Number of rooms left: " + roomInventory.getNumRoomsLeft() + " | Published Rate: " + lowestPublishedRate);
                    lowestPublishedRate = new BigDecimal(Integer.MAX_VALUE);
                }
            } catch (RoomInventoryNotFoundException ex) {
                System.out.println("No inventories exist for requested date.");
            }
            System.out.println();
        }
    }

    public void walkInReserveRoom() {
        Scanner sc = new Scanner(System.in);
        Calendar checkInDate = Calendar.getInstance();
        Calendar checkOutDate = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        String checkInDateString, checkOutDateString, input;
        BigDecimal lowestPublishedRate = new BigDecimal(Integer.MAX_VALUE);
        int year, month, day;
        int count = 0;
        Long reserveRoomTypeId;
        Integer numGuests, numRoomsRequested;
        Boolean haveRoomsLeft;
        Calendar checkInDateTemp;
        Calendar checkOutDateTemp;
        Guest newGuest = new Guest();
        Guest currentGuest;

        List<RoomInventory> roomInventories;
        List<RoomRate> roomRates;

        System.out.println("*** Hotel Reservation System :: Front Office :: Walk-in Reserve Room ***\n");

        do {
            if (count > 0) {
                System.out.println("Start date and time cannot be before current date and time!");
            }
            System.out.print("Enter Check-in date in the format (yyyymmdd)> ");
            checkInDateString = sc.nextLine().trim();
            if (checkInDateString.isEmpty() || checkInDateString.length() != 8) {
                System.out.println("Check-in date value cannot be empty and should contain 8 numbers.");
                count = -1;
                checkInDate.set(1990, 0, 1, 0, 0);
                continue;
            }

            try {
                year = Integer.parseInt(checkInDateString.substring(0, 4).trim());
                month = Integer.parseInt(checkInDateString.substring(4, 6).trim());
                day = Integer.parseInt(checkInDateString.substring(6, 8).trim());
                checkInDate.clear();
                checkInDate.set(year, month - 1, day);
                checkInDate.set(Calendar.HOUR_OF_DAY, 0);
                checkInDate.set(Calendar.MINUTE, 0);
                checkInDate.set(Calendar.SECOND, 0);
                checkInDate.set(Calendar.MILLISECOND, 0);
            } catch (NumberFormatException ex) {
                System.out.println("Please enter numeric values.");
                checkInDate.set(1990, 0, 1, 0, 0);
                count = 0;
                continue;
            }
            count++;
        } while (now.compareTo(checkInDate) > 0);

        count = 0;

        do {
            if (count > 0) {
                System.out.println("Check-out date must be later than start date!");
            }
            System.out.print("Enter Check-out date in the format (yyyymmdd)> ");
            checkOutDateString = sc.nextLine().trim();

            if (checkOutDateString.isEmpty() || checkOutDateString.length() < 8) {
                System.out.println("Check-out date value cannot be empty and should contain 8 numbers!");
                checkOutDate.set(1990, 0, 1, 0, 0);
                count = 0;
                continue;
            }
            try {
                year = Integer.parseInt(checkOutDateString.substring(0, 4).trim());
                month = Integer.parseInt(checkOutDateString.substring(4, 6).trim());
                day = Integer.parseInt(checkOutDateString.substring(6, 8).trim());
                checkOutDate.clear();
                checkOutDate.set(year, month - 1, day);
                checkOutDate.set(Calendar.HOUR_OF_DAY, 0);
                checkOutDate.set(Calendar.MINUTE, 0);
                checkOutDate.set(Calendar.SECOND, 0);
                checkOutDate.set(Calendar.MILLISECOND, 0);
            } catch (NumberFormatException ex) {
                System.out.println("Please enter numeric values.");
                checkOutDate.set(1990, 0, 1, 0, 0);
                count = 0;
                continue;
            }
            count++;
        } while (now.compareTo(checkOutDate) > 0 || Long.parseLong(checkOutDateString) < Long.parseLong(checkInDateString));

        try {
            haveRoomsLeft = roomInventoryControllerRemote.checkRoomInventoryOnDate(checkInDate, checkOutDate);
            if (!haveRoomsLeft) {
                System.out.println("All rooms in hotel for one or more dates have been allocated or are not available for reservation.");
                return;
            }
        } catch (CheckRoomInventoryException ex) {
            System.out.println("An error has occurred: " + ex.getMessage());
        }

        checkInDateTemp = checkInDate;
        checkOutDateTemp = checkOutDate;

        for (Date date = checkInDateTemp.getTime(); checkInDateTemp.before(checkOutDateTemp); checkInDateTemp.add(Calendar.DATE, 1), date = checkInDateTemp.getTime()) {
            System.out.println("\n----------Date: " + date);
            try {
                roomInventories = roomInventoryControllerRemote.retrieveAllRoomInventoriesOnDate(date);
                for (RoomInventory roomInventory : roomInventories) {
                    roomRates = roomInventoryControllerRemote.retrieveRoomRatesByTypeOfRoomInventory(roomInventory.getRoomInventoryId());
                    for (RoomRate roomRate : roomRates) {
                        if (lowestPublishedRate.compareTo(roomRate.getRatePerNight()) == 1) {
                            lowestPublishedRate = roomRate.getRatePerNight();
                        }
                    }
                    System.out.println("Room Type ID: " + roomInventory.getRoomType().getRoomTypeId() + "| Room Type: " + roomInventory.getRoomType().getRoomTypeName() + " | Number of rooms left: " + roomInventory.getNumRoomsLeft() + " | Published Rate: " + lowestPublishedRate);
                    lowestPublishedRate = new BigDecimal(Integer.MAX_VALUE);
                }
            } catch (RoomInventoryNotFoundException ex) {
                System.out.println("No inventories exist for requested date.");
            }
        }

        while (true) {
            System.out.print("\nEnter Number of Guests> ");
            input = sc.nextLine().trim();
            try {
                numGuests = Integer.parseInt(input);
                break;
            } catch (NumberFormatException ex) {
                System.out.println("Please input numerical values.");
            }
        }

        while (true) {
            System.out.print("Enter Room Type ID to reserve> ");
            input = sc.nextLine().trim();
            try {
                reserveRoomTypeId = Long.parseLong(input);
                break;
            } catch (NumberFormatException ex) {
                System.out.println("Please input numerical values.");
            }
        }

        while (true) {
            System.out.print("Enter Number of Rooms Required for Room Type> ");
            input = sc.nextLine().trim();
            try {
                numRoomsRequested = Integer.parseInt(input);
                try {
                    if (!roomInventoryControllerRemote.checkRoomInventoryAvailability(checkInDate, checkOutDate, reserveRoomTypeId, numRoomsRequested)) {
                        System.out.println("Room Inventory of Room Type selected is insufficient for booking, please select another Room Type.");
                        continue;
                    }
                } catch (CheckRoomInventoryAvailabilityException ex) {
                    System.out.println("Room Inventory is not available for this Room Type: " + ex.getMessage());
                    return;
                }
                break;
            } catch (NumberFormatException ex) {
                System.out.println("Please input numerical values.");
            }
        }

        System.out.print("Enter First Name> ");
        newGuest.setFirstName(sc.nextLine().trim());
        System.out.print("Enter Last Name> ");
        newGuest.setLastName(sc.nextLine().trim());
        System.out.print("Enter Email Address> ");
        newGuest.setEmailAdd(sc.nextLine().trim());

        while (true) {
            System.out.print("Enter Contact Number> ");
            String inPhoneNum = sc.nextLine().trim();
            Integer phoneNum;

            try {
                phoneNum = Integer.parseInt(inPhoneNum);
            } catch (NumberFormatException ex) {
                System.out.println("Phone number can only contain numbers");
                continue;
            }
            if (inPhoneNum.length() != 8) {
                System.out.println("Phone number should be of length 8 E.g. 91234567");
                continue;
            }
            newGuest.setPhoneNum(phoneNum);
            break;
        }

        while (true) {
            System.out.print("Enter Passport Number> ");
            newGuest.setPassportNum(sc.nextLine().trim());

            try {
                if (guestControllerRemote.checkGuestExistsByPassportNum(newGuest.getPassportNum())) {
                    currentGuest = guestControllerRemote.retrieveGuestByPassportNumber(newGuest.getPassportNum());
                    System.out.println("Guest with Passport Number " + newGuest.getPassportNum() + " already exists. Retrieved records of Guest from database.\n");
                }
                break;
            } catch (GuestNotFoundException ex) {
                try {
                    guestControllerRemote.createNewGuest(newGuest);
                } catch (GuestExistException | GeneralException e) {
                    System.out.println("An unexpected error has occurred: " + e.getMessage());
                    return;
                }
            }
        }
    }

    public void checkInGuest() {
    }

    public void checkOutGuest() {
    }
}
