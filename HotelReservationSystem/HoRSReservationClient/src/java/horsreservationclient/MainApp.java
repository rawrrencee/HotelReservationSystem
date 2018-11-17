/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsreservationclient;

import ejb.session.stateless.GuestControllerRemote;
import ejb.session.stateless.ReservationControllerRemote;
import ejb.session.stateless.RoomControllerRemote;
import ejb.session.stateless.RoomInventoryControllerRemote;
import ejb.session.stateless.RoomRateControllerRemote;
import ejb.session.stateless.RoomTypeControllerRemote;
import entity.Guest;
import entity.OnlineReservation;
import entity.RegisteredGuest;
import entity.Reservation;
import entity.ReservationLineItem;
import entity.RoomInventory;
import entity.RoomNight;
import entity.RoomRate;
import entity.RoomType;
import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Scanner;
import util.exception.CheckRoomInventoryAvailabilityException;
import util.exception.CheckRoomInventoryException;
import util.exception.CreateRoomNightException;
import util.exception.GeneralException;
import util.exception.GuestNotFoundException;
import util.exception.InvalidLoginCredentialException;
import util.exception.LineCalculationException;
import util.exception.LineCreationException;
import util.exception.RegisteredGuestExistException;
import util.exception.RegisteredGuestNotFoundException;
import util.exception.ReservationNotFoundException;
import util.exception.RoomInventoryExistException;
import util.exception.RoomInventoryNotFoundException;
import util.exception.RoomRateNotFoundException;
import util.exception.RoomTypeNotFoundException;

/**
 *
 * @author Lawrence
 */
public class MainApp {

    private RoomControllerRemote roomControllerRemote;
    private RoomRateControllerRemote roomRateControllerRemote;
    private RoomInventoryControllerRemote roomInventoryControllerRemote;
    private GuestControllerRemote guestControllerRemote;
    private ReservationControllerRemote reservationControllerRemote;
    private RoomTypeControllerRemote roomTypeControllerRemote;

    private RegisteredGuest currentRegisteredGuest;
    private Guest currentGuest;

    public MainApp(ReservationControllerRemote reservationControllerRemote, GuestControllerRemote guestControllerRemote, RoomInventoryControllerRemote roomInventoryControllerRemote, RoomRateControllerRemote roomRateControllerRemote, RoomControllerRemote roomControllerRemote, RoomTypeControllerRemote roomTypeControllerRemote) {
        this.reservationControllerRemote = reservationControllerRemote;
        this.roomControllerRemote = roomControllerRemote;
        this.roomRateControllerRemote = roomRateControllerRemote;
        this.roomInventoryControllerRemote = roomInventoryControllerRemote;
        this.guestControllerRemote = guestControllerRemote;
        this.roomTypeControllerRemote = roomTypeControllerRemote;
    }

    public void runApp() {
        Scanner sc = new Scanner(System.in);
        String input;
        Integer response = 0;

        while (true) {
            System.out.println("*** Welcome to HoRS Reservation Client :: Main Page ***\n");
            System.out.println("1: Guest Login");
            System.out.println("2: Register as Guest");
            System.out.println("3: Search Hotel Room");
            System.out.println("4: Exit");
            response = 0;

            while (response < 1 || response > 2) {
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
                        try {
                            doLogin();
                            System.out.println("Login successful!\n");
                            menuMain();
                        } catch (InvalidLoginCredentialException ex) {
                            System.out.println("Invalid login credentials: " + ex.getMessage() + "\n");
                        }
                        break;
                    case 2:
                        registerAsGuest();
                        break;
                    case 3:
                        searchHotelRoom();
                        break;
                    case 4:
                        return;
                    default:
                        System.out.println("Invalid option, please try again!\n");

                }
                break;
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
            currentRegisteredGuest = guestControllerRemote.registeredGuestLogin(username, password);
        } else {
            throw new InvalidLoginCredentialException("Missing login credential!");
        }
    }

    public void menuMain() {
        Scanner sc = new Scanner(System.in);
        String input;
        Integer response = 0;

        while (true) {
            System.out.println("*** HoRS Reservation Client :: Main Menu ***\n");
            System.out.println("You have logged in as " + currentRegisteredGuest.getFirstName() + " " + currentRegisteredGuest.getLastName() + ".\n");
            System.out.println("1: Reserve Hotel Room");
            System.out.println("2: View My Reservation Details");
            System.out.println("3: View All My Reservations");
            System.out.println("4: Logout\n");
            response = 0;

            while (response < 1 || response > 4) {
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
                        reserveHotelRoom();
                        break;
                    case 2:
                        viewMyReservationDetails();
                        break;
                    case 3:
                        viewAllMyReservations();
                        break;
                    case 4:
                        return;
                    default:
                        break;
                }
            }
        }
    }

    public void registerAsGuest() {

        Scanner sc = new Scanner(System.in);
        String input, passportNum;
        RegisteredGuest newRegisteredGuest = new RegisteredGuest();

        System.out.println("*** HoRS Reservation Client :: Register as Guest ***\n");

        System.out.print("Checking if you are a Guest in our database. Enter Passport Number> ");
        passportNum = sc.nextLine().trim();
        try {
            currentGuest = guestControllerRemote.retrieveGuestByPassportNumber(passportNum);
            guestControllerRemote.retrieveRegisteredGuestByPassportNumber(passportNum);
            System.out.println("You are already a guest in the hotel. Please login with your username and password.");
            return;
        } catch (GuestNotFoundException ex) {
            System.out.println("Passport Number not found in database. Creating new Guest account.");
        } catch (RegisteredGuestNotFoundException e) {
            System.out.println("You are a Guest but not a Registered Guest. Please provide a username and password to create an account with your information.");
            newRegisteredGuest.setFirstName(currentGuest.getFirstName());
            newRegisteredGuest.setLastName(currentGuest.getLastName());
            newRegisteredGuest.setEmailAdd(currentGuest.getEmailAdd());
            newRegisteredGuest.setPhoneNum(currentGuest.getPhoneNum());
            newRegisteredGuest.setPassportNum(currentGuest.getPassportNum());

            System.out.print("Enter Username> ");
            String username = sc.nextLine().trim();
            newRegisteredGuest.setUsername(username);
            System.out.print("Enter Password> ");
            String password = sc.nextLine().trim();
            newRegisteredGuest.setPassword(password);

            try {
                guestControllerRemote.createNewRegisteredGuest(newRegisteredGuest);
                System.out.println("Registered Guest with username: " + newRegisteredGuest.getUsername() + " created!");
                return;
            } catch (GeneralException | RegisteredGuestExistException ex) {
                System.out.println("An error occurred: " + ex.getMessage());
                return;
            }
        }
        System.out.print("Enter Username> ");
        String username = sc.nextLine().trim();
        newRegisteredGuest.setUsername(username);
        System.out.print("Enter Password> ");
        String password = sc.nextLine().trim();
        newRegisteredGuest.setPassword(password);

        System.out.print("Enter First Name> ");
        newRegisteredGuest.setFirstName(sc.nextLine().trim());
        System.out.print("Enter Last Name> ");
        newRegisteredGuest.setLastName(sc.nextLine().trim());
        System.out.print("Enter Email Address> ");
        newRegisteredGuest.setEmailAdd(sc.nextLine().trim());

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
            newRegisteredGuest.setPhoneNum(phoneNum);
            break;
        }

        System.out.print("Enter Passport Number> ");
        passportNum = sc.nextLine().trim();
        newRegisteredGuest.setPassportNum(passportNum);
        try {
            guestControllerRemote.createNewRegisteredGuest(newRegisteredGuest);
            System.out.println("Registered Guest with username: " + newRegisteredGuest.getUsername() + " created!");
        } catch (GeneralException | RegisteredGuestExistException ex) {
            System.out.println("An error occurred: " + ex.getMessage());
        }
    }

    public void searchHotelRoom() {

        Scanner sc = new Scanner(System.in);
        List<RoomType> roomTypes = roomTypeControllerRemote.retrieveAllRoomTypes();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
        List<RoomInventory> roomInventories;
        RoomInventory currentRoomInventory;
        Integer numRoomsLeft = 0;
        List<RoomRate> roomRates;
        BigDecimal rate;
        LocalDate checkInDate = LocalDate.now();
        LocalDate checkOutDate = LocalDate.now();
        LocalDate now = LocalDate.now();
        String checkInDateString, checkOutDateString;

        System.out.println("*** HoRS Reservation Client :: Search Hotel Room ***\n");

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
                    RoomType roomType = roomTypeControllerRemote.retrieveRoomTypeByRoomTypeId(roomInventory.getRoomType().getRoomTypeId());
                    rate = (roomRateControllerRemote.retrieveComplexRoomRate(roomType.getRoomTypeId(), date)).getRatePerNight();
                    System.out.println("Room Type: " + roomInventory.getRoomType().getRoomTypeName() + " | Number of rooms left: " + roomInventory.getNumRoomsLeft() + " | Rate: " + rate);
                }
            } catch (RoomInventoryNotFoundException ex) {
                System.out.println("No inventories exist for requested date.");
            } catch (RoomTypeNotFoundException | RoomRateNotFoundException e) {
                System.out.println("An error occurred: " + e.getMessage());
            }
            System.out.println();
        }

    }

    public void reserveHotelRoom() {
        Scanner sc = new Scanner(System.in);
        LocalDate checkInDate = LocalDate.now();
        LocalDate checkOutDate = LocalDate.now();
        LocalDate now = LocalDate.now();
        LocalDateTime nowTime = LocalDateTime.now();
        String checkInDateString, checkOutDateString, input, passportNum;
        BigDecimal rate;
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
                    RoomType roomType = roomTypeControllerRemote.retrieveRoomTypeByRoomTypeId(roomInventory.getRoomType().getRoomTypeId());
                    rate = (roomRateControllerRemote.retrieveComplexRoomRate(roomType.getRoomTypeId(), date)).getRatePerNight();
                    System.out.println("ID: " + roomInventory.getRoomType().getRoomTypeId() + " | Room Type: " + roomInventory.getRoomType().getRoomTypeName() + " | Number of rooms left: " + roomInventory.getNumRoomsLeft() + " | Rate: " + rate);
                }
            } catch (RoomInventoryNotFoundException ex) {
                System.out.println("No inventories exist for requested date.");
            } catch (RoomRateNotFoundException | RoomTypeNotFoundException e) {
                System.out.println("An error occurred: " + e.getMessage());
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
        //create new online reservation
        OnlineReservation onlineReservation = new OnlineReservation();
        onlineReservation.setCheckInDate(checkInDate);
        onlineReservation.setCheckOutDate(checkOutDate);
        onlineReservation.setNumGuests(numGuests);
        onlineReservation.setCreatedDate(LocalDateTime.now());
        onlineReservation.setRegisteredGuest(currentRegisteredGuest);
        onlineReservation = reservationControllerRemote.createNewOnlineReservation(onlineReservation);

        while (true) {
            //create new reservation line item
            ReservationLineItem reservationLineItem = new ReservationLineItem();
            reservationLineItem.setNumRoomsRequested(numRoomsRequested);
            try {
            reservationLineItem = reservationControllerRemote.createNewOnlineReservationLineItem(reservationLineItem, onlineReservation.getReservationId(), reserveRoomTypeId);
            } catch (LineCreationException ex) {
                System.out.println("An error has occurred: " + ex.getMessage());
                return;
            }
            //create new room nights
            try {
                for (LocalDate date = checkInDate; date.isBefore(checkOutDate); date = date.plusDays(1)) {
                    RoomNight roomNight = new RoomNight();
                    roomNight.setDate(date);
                    reservationControllerRemote.createOnlineNewRoomNight(roomNight, reserveRoomTypeId, reservationLineItem.getReservationLineItemId());
                }
            } catch (CreateRoomNightException ex) {
                System.out.println("An error occurred: " + ex.getMessage());
            }

            //calculate total amount in reservation and ask for confirmation
            System.out.println("*** Reservation ID: " + onlineReservation.getReservationId() + " | All Reservations ***");
            try {
                Reservation currentReservation = reservationControllerRemote.retrieveReservationByReservationId(onlineReservation.getReservationId());
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
            } catch (ReservationNotFoundException | LineCalculationException ex) {
                System.out.println("An error has occurred: " + ex.getMessage());
            }
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

    }

    public void viewMyReservationDetails() {

        Scanner sc = new Scanner(System.in);
        String input;
        Long reservationId;
        Reservation currentReservation;
        System.out.println("*** HoRS Reservation Client :: View My Reservation Details ***\n");

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
            List<ReservationLineItem> reservationLineItems = currentReservation.getReservationLineItems();
            for (ReservationLineItem lineItem : reservationLineItems) {
                RoomType roomType = reservationControllerRemote.retrieveRoomTypeByLineId(lineItem.getReservationLineItemId());
                System.out.println(reservationLineItems.indexOf(lineItem) + ". Room Type Requested: " + roomType.getRoomTypeName() + " | Number of rooms requested: " + lineItem.getNumRoomsRequested() + " | Total cost: " + reservationControllerRemote.calculateReservationLineAmount(lineItem.getReservationLineItemId()));
            }
        } catch (ReservationNotFoundException ex) {
            System.out.println("Reservation not found!");
        } catch (LineCalculationException e) {
            System.out.println("An error has occurred: " + e.getMessage());
        }

    }

    public void viewAllMyReservations() {

        System.out.println("*** HoRS Reservation Client :: View All My Reservations ***\n");

        try {
            List<Reservation> reservations = reservationControllerRemote.retrieveAllReservationsByRegisteredGuest(currentRegisteredGuest.getGuestId());

            for (Reservation reservation : reservations) {
                System.out.println("ID: " + reservation.getReservationId() + " | " + " Check In Date: " + reservation.getCheckInDate() + " Check Out Date: " + reservation.getCheckOutDate() + " Total amount spent: " + reservation.getReservationAmt());
            }
        } catch (ReservationNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
