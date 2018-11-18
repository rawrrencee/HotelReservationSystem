/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsmanagementclient;

import ejb.session.stateless.GuestControllerRemote;
import ejb.session.stateless.ReservationControllerRemote;
import ejb.session.stateless.RoomControllerRemote;
import ejb.session.stateless.RoomInventoryControllerRemote;
import ejb.session.stateless.RoomRateControllerRemote;
import ejb.session.stateless.RoomTypeControllerRemote;
import entity.Employee;
import entity.Guest;
import entity.Reservation;
import entity.ReservationLineItem;
import entity.Room;
import entity.RoomInventory;
import entity.RoomNight;
import entity.RoomRate;
import entity.RoomType;
import entity.WalkInReservation;
import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Scanner;
import util.enumeration.EmployeeAccessRightEnum;
import util.exception.CheckRoomInventoryAvailabilityException;
import util.exception.CheckRoomInventoryException;
import util.exception.GeneralException;
import util.exception.GuestExistException;
import util.exception.GuestNotFoundException;
import util.exception.InvalidAccessRightException;
import util.exception.LineCalculationException;
import util.exception.LineCreationException;
import util.exception.ReservationNotFoundException;
import util.exception.RoomAllocationException;
import util.exception.RoomCheckoutException;
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
    private ReservationControllerRemote reservationControllerRemote;

    private Employee currentEmployee;

    public FrontOfficeModule() {
    }

    public FrontOfficeModule(RoomRateControllerRemote roomRateControllerRemote, RoomControllerRemote roomControllerRemote, RoomTypeControllerRemote roomTypeControllerRemote, RoomInventoryControllerRemote roomInventoryControllerRemote, GuestControllerRemote guestControllerRemote, ReservationControllerRemote reservationControllerRemote, Employee currentEmployee) {
        this.roomRateControllerRemote = roomRateControllerRemote;
        this.roomControllerRemote = roomControllerRemote;
        this.roomTypeControllerRemote = roomTypeControllerRemote;
        this.roomInventoryControllerRemote = roomInventoryControllerRemote;
        this.guestControllerRemote = guestControllerRemote;
        this.reservationControllerRemote = reservationControllerRemote;
        this.currentEmployee = currentEmployee;
    }

    public void menuMain() throws InvalidAccessRightException {
        if (currentEmployee.getAccessRight() != EmployeeAccessRightEnum.GUESTRELOFF) {
            throw new InvalidAccessRightException("You don't have GUEST RELATION OFFICER rights to access the Front Office Module!!");
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
        List<RoomInventory> roomInventories;
        RoomInventory currentRoomInventory;
        Integer numRoomsLeft = 0;
        List<RoomRate> roomRates;
        BigDecimal lowestPublishedRate;
        LocalDate checkInDate = LocalDate.now();
        LocalDate checkOutDate = LocalDate.now();
        LocalDate now = LocalDate.now();
        String checkInDateString, checkOutDateString;

        System.out.println("*** Hotel Reservation System :: Front Office :: Walk-in Search Room ***\n");

        while (true) {
            System.out.print("Enter Check-in date in the format ddMMyyyy> ");
            checkInDateString = sc.nextLine().trim();
            try {
                checkInDate = LocalDate.parse(checkInDateString, formatter);
                break;
            } catch (DateTimeParseException ex) {
                System.out.println("Please enter in the right format ddMMyyyy");
            }
        }
        while (true) {
            System.out.print("Enter Check-out date in the format ddMMyyyy> ");
            checkOutDateString = sc.nextLine().trim();
            try {
                checkOutDate = LocalDate.parse(checkOutDateString, formatter);
                break;
            } catch (DateTimeParseException ex) {
                System.out.println("Please enter in the right format ddMMyyyy");
            }
        }

        for (LocalDate date = checkInDate; date.isBefore(checkOutDate); date = date.plusDays(1)) {
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
//                    roomRates = roomInventoryControllerRemote.retrieveRoomRatesByTypeOfRoomInventory(roomInventory.getRoomInventoryId());
//                    for (RoomRate roomRate : roomRates) {
//                        if (lowestPublishedRate.compareTo(roomRate.getRatePerNight()) == 1) {
//                            lowestPublishedRate = roomRate.getRatePerNight();
//                        }
//                    }
                    lowestPublishedRate = (roomRateControllerRemote.retrieveLowestPublishedRoomRate(roomInventory.getRoomType().getRoomTypeId())).getRatePerNight();
                    System.out.println("ID: " +  roomInventory.getRoomType().getRoomTypeId() + " Room Type: " + roomInventory.getRoomType().getRoomTypeName() + " | Number of rooms left: " + roomInventory.getNumRoomsLeft() + " | Published Rate: " + lowestPublishedRate);
                    //lowestPublishedRate = new BigDecimal(Integer.MAX_VALUE);
                }
            } catch (RoomInventoryNotFoundException ex) {
                System.out.println("No inventories exist for requested date.");
            }
            System.out.println();
        }
    }

    public void walkInReserveRoom() {
        Scanner sc = new Scanner(System.in);
        LocalDate checkInDate = LocalDate.now();
        LocalDate checkOutDate = LocalDate.now();
        LocalDate now = LocalDate.now();
        LocalDateTime nowTime = LocalDateTime.now();
        String checkInDateString, checkOutDateString, input, passportNum;
        BigDecimal lowestPublishedRate;
        Long reserveRoomTypeId;
        Integer numGuests, numRoomsRequested;
        Boolean haveRoomsLeft;
        Guest newGuest = new Guest();
        Guest currentGuest;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");

        List<RoomInventory> roomInventories;
        List<RoomRate> roomRates;

        System.out.println("*** Hotel Reservation System :: Front Office :: Walk-in Reserve Room ***\n");

        while (true) {
            System.out.print("Enter Check-in date in the format ddMMyyyy> ");
            checkInDateString = sc.nextLine().trim();
            try {
                checkInDate = LocalDate.parse(checkInDateString, formatter);
                break;
            } catch (DateTimeParseException ex) {
                System.out.println("Please enter in the right format ddMMyyyy");
            }
        }
        while (true) {
            System.out.print("Enter Check-out date in the format ddMMyyyy> ");
            checkOutDateString = sc.nextLine().trim();
            try {
                checkOutDate = LocalDate.parse(checkOutDateString, formatter);
                break;
            } catch (DateTimeParseException ex) {
                System.out.println("Please enter in the right format ddMMyyyy");
            }
        }

        try {
            haveRoomsLeft = roomInventoryControllerRemote.checkRoomInventoryOnDate(checkInDate, checkOutDate);
            if (!haveRoomsLeft) {
                System.out.println("All rooms in hotel for one or more dates have been allocated or are not available for reservation.");
                return;
            }
        } catch (CheckRoomInventoryException ex) {
            System.out.println("An error has occurred: " + ex.getMessage());
        }

        for (LocalDate date = checkInDate; date.isBefore(checkOutDate); date = date.plusDays(1)) {
            System.out.println("\n----------Date: " + date);
            try {
                roomInventories = roomInventoryControllerRemote.retrieveAllRoomInventoriesOnDate(date);
                for (RoomInventory roomInventory : roomInventories) {
//                    roomRates = roomInventoryControllerRemote.retrieveRoomRatesByTypeOfRoomInventory(roomInventory.getRoomInventoryId());
//                    for (RoomRate roomRate : roomRates) {
//                        if (lowestPublishedRate.compareTo(roomRate.getRatePerNight()) == 1) {
//                            lowestPublishedRate = roomRate.getRatePerNight();
//                        }
//                    }
                    lowestPublishedRate = (roomRateControllerRemote.retrieveLowestPublishedRoomRate(roomInventory.getRoomType().getRoomTypeId())).getRatePerNight();
                    System.out.println("Room Type ID: " + roomInventory.getRoomType().getRoomTypeId() + "| Room Type: " + roomInventory.getRoomType().getRoomTypeName() + " | Number of rooms left: " + roomInventory.getNumRoomsLeft() + " | Published Rate: " + lowestPublishedRate);
                    //lowestPublishedRate = new BigDecimal(Integer.MAX_VALUE);
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
            passportNum = sc.nextLine().trim();
            newGuest.setPassportNum(passportNum);

            try {
                if (guestControllerRemote.checkGuestExistsByPassportNum(newGuest.getPassportNum())) {
                    currentGuest = guestControllerRemote.retrieveGuestByPassportNumber(newGuest.getPassportNum());
                    System.out.println("Guest with Passport Number " + newGuest.getPassportNum() + " already exists. Retrieved records of Guest from database.\n");
                }
                break;
            } catch (GuestNotFoundException ex) {
                try {
                    currentGuest = guestControllerRemote.createNewGuest(newGuest);
                    System.out.println("New Guest with Passport Number " + newGuest.getPassportNum() + " created!");
                    break;
                } catch (GuestExistException | GeneralException e) {
                    System.out.println("An unexpected error has occurred: " + e.getMessage());
                    return;
                }
            }
        }

        try {
            //create new walk in reservation
            currentGuest = guestControllerRemote.retrieveGuestByPassportNumber(passportNum);
            WalkInReservation walkInReservation = new WalkInReservation();
            walkInReservation.setCheckInDate(checkInDate);
            walkInReservation.setCheckOutDate(checkOutDate);
            walkInReservation.setNumGuests(numGuests);
            walkInReservation.setCreatedDate(LocalDateTime.now());
            walkInReservation.setGuest(currentGuest);
            walkInReservation.setEmployee(currentEmployee);
            walkInReservation = reservationControllerRemote.createNewWalkInReservation(walkInReservation);

            while (true) {
                //create new reservation line item
                ReservationLineItem reservationLineItem = new ReservationLineItem();
                reservationLineItem.setNumRoomsRequested(numRoomsRequested);
                try {
                reservationLineItem = reservationControllerRemote.createNewReservationLineItem(reservationLineItem, walkInReservation.getReservationId(), reserveRoomTypeId);
                } catch (LineCreationException ex) {
                    System.out.println("An error has occurred: " + ex.getMessage());
                    return;
                }
                //create new room nights
                for (LocalDate date = checkInDate; date.isBefore(checkOutDate); date = date.plusDays(1)) {
                    RoomNight roomNight = new RoomNight();
                    roomNight.setDate(date);
                    reservationControllerRemote.createNewRoomNight(roomNight, reserveRoomTypeId, reservationLineItem.getReservationLineItemId());
                }

                //calculate total amount in reservation and ask for confirmation
                System.out.println("*** Reservation ID: " + walkInReservation.getReservationId() + " | All Reservations ***");
                try {
                    Reservation currentReservation = reservationControllerRemote.retrieveReservationByReservationId(walkInReservation.getReservationId());
                    List<ReservationLineItem> reservationLineItems = currentReservation.getReservationLineItems();
                    BigDecimal totalAmount = BigDecimal.ZERO;
                    for (ReservationLineItem lineItem : reservationLineItems) {
                        RoomType roomType = reservationControllerRemote.retrieveRoomTypeByLineId(lineItem.getReservationLineItemId());
                        BigDecimal amount = reservationControllerRemote.calculateReservationLineAmount(lineItem.getReservationLineItemId());
                        System.out.println(reservationLineItems.indexOf(lineItem) + ". Room Type Requested: " + roomType.getRoomTypeName() + " | Number of rooms requested: " + lineItem.getNumRoomsRequested() + " | Total cost: " + amount);
                        totalAmount = totalAmount.add(amount, new MathContext(11));
                    }
                    reservationControllerRemote.setReservationAmount(currentReservation.getReservationId(), totalAmount);
                    System.out.println("Total Amount: " + totalAmount);
//                    while (true) {
//                        System.out.print("Confirm reservation? Y/N> ");
//                        input = sc.nextLine().trim();
//                        if (input.equals("N")) {
//                            return;
//                        } else if (input.equals("Y")) {
//                            break;
//                        } else {
//                            System.out.println("Please enter Y/N.");
//                        }
//                    }
                } catch (ReservationNotFoundException | LineCalculationException ex) {
                    System.out.println("An error has occurred: " + ex.getMessage());
                }

//                //allocate rooms
//                for (int i = 0; i < numRoomsRequested; i++) {
//                    try {
//                        Room allocatedRoom = reservationControllerRemote.allocateRoom(reserveRoomTypeId, reservationLineItem.getReservationLineItemId(), walkInReservation.getReservationId());
//                        System.out.println("Room " + allocatedRoom.getRoomNumber() + " has been allocated.");
//                    } catch (RoomAllocationException ex) {
//                        System.out.println("An error occurred: " + ex.getMessage());
//                        return;
//                    }
//                }
                //repeat reservation of rooms
                while (true) {
                    System.out.print("Would you like to reserve rooms of another room type? Enter Y/N> ");
                    input = sc.nextLine().trim();
                    if (input.equals("N")) {
                        return;
                    } else if (input.equals("Y")) {
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
                        break;
                    } else {
                        System.out.println("Please enter Y/N.");
                    }
                }
            }
        } catch (GuestNotFoundException ex) {
            System.out.println("An unexpected error has occurred: " + ex.getMessage());
        }

    }

    public void checkInGuest() {
        Scanner sc = new Scanner(System.in);
        String input;
        Long reservationId;
        Reservation currentReservation;
        System.out.println("*** Hotel Reservation System :: Front Office :: Check-in Guest ***\n");

        System.out.print("Enter Reservation ID> ");
        while (true) {
            input = sc.nextLine().trim();
            try {
                reservationId = Long.parseLong(input);
                break;
            } catch (NumberFormatException ex) {
                System.out.println("Please enter numeric values.");
            }
        }
        try {
            currentReservation = reservationControllerRemote.retrieveReservationByReservationId(reservationId);
            LocalDate date = currentReservation.getCheckInDate();
            LocalDate now = LocalDate.now();
            System.out.println(now + " | and checkin date is: " + date);
            if (!now.isEqual(date)) {
                System.out.println("Reservation's check-in date is not today!");
                return;
            }
            List<ReservationLineItem> reservationLineItems = currentReservation.getReservationLineItems();
            for (ReservationLineItem lineItem : reservationLineItems) {
                RoomType roomType = reservationControllerRemote.retrieveRoomTypeByLineId(lineItem.getReservationLineItemId());
                System.out.println(reservationLineItems.indexOf(lineItem) + ". Room Type Requested: " + roomType.getRoomTypeName() + " | Number of rooms requested: " + lineItem.getNumRoomsRequested() + " | Total cost: " + reservationControllerRemote.calculateReservationLineAmount(lineItem.getReservationLineItemId()));
            }
            while (true) {
                System.out.print("Confirm allocation? Y/N> ");
                input = sc.nextLine().trim();
                if (input.toLowerCase().equals("n")) {
                    return;
                } else if (!input.toLowerCase().equals("y")) {
                    System.out.println("Please enter Y/N.");
                } else {
                    break;
                }
            }
            for (ReservationLineItem lineItem : reservationLineItems) {
                Integer numRoomsRequested = lineItem.getNumRoomsRequested();
                RoomType requestedRoomType = reservationControllerRemote.retrieveRoomTypeByLineId(lineItem.getReservationLineItemId());
                for (int i = 0; i < numRoomsRequested; i++) {
                    Room allocatedRoom = reservationControllerRemote.allocateRoom(requestedRoomType.getRoomTypeId(), lineItem.getReservationLineItemId(), currentReservation.getReservationId());
                    System.out.println("Room " + allocatedRoom.getRoomNumber() + " has been allocated.");
                }
            }
        } catch (ReservationNotFoundException | LineCalculationException ex) {
            System.out.println("An error occurred: " + ex.getMessage());
        } catch (RoomAllocationException e) {
            System.out.println("An error has occurred: " + e.getMessage() + " TODO: Make Room Allocation Exception Report!");
        }

    }

    public void checkOutGuest() {
        Scanner sc = new Scanner(System.in);
        String input;
        Long reservationId;
        Reservation currentReservation;
        System.out.println("*** Hotel Reservation System :: Front Office :: Check-out Guest ***\n");

        System.out.print("Enter Reservation ID> ");
        while (true) {
            input = sc.nextLine().trim();
            try {
                reservationId = Long.parseLong(input);
                break;
            } catch (NumberFormatException ex) {
                System.out.println("Please enter numeric values.");
            }
        }

        try {
            currentReservation = reservationControllerRemote.retrieveReservationByReservationId(reservationId);
            LocalDate date = currentReservation.getCheckOutDate();
            LocalDate now = LocalDate.now();
            System.out.println(now + " | and checkin date is: " + date);
            List<ReservationLineItem> reservationLineItems = currentReservation.getReservationLineItems();
            for (ReservationLineItem lineItem : reservationLineItems) {
                reservationControllerRemote.processCheckout(lineItem.getReservationLineItemId(), currentReservation.getReservationId());
            }
        } catch (ReservationNotFoundException | RoomCheckoutException ex) {
            System.out.println("An error occurred: " + ex.getMessage());
        }

        System.out.println("Checkout completed!");
    }
}
