/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package holidayreservationsystem;

import ejb.session.stateless.PartnerControllerRemote;
import ejb.session.stateless.ReservationControllerRemote;
import ejb.session.stateless.RoomControllerRemote;
import ejb.session.stateless.RoomInventoryControllerRemote;
import ejb.session.stateless.RoomRateControllerRemote;
import ejb.session.stateless.RoomTypeControllerRemote;
import entity.OnlineReservation;
import entity.Partner;
import entity.PartnerReservation;
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
import java.util.List;
import java.util.Scanner;
import util.exception.CheckRoomInventoryAvailabilityException;
import util.exception.CheckRoomInventoryException;
import util.exception.CreateRoomNightException;
import util.exception.GeneralException;
import util.exception.InvalidLoginCredentialException;
import util.exception.LineCalculationException;
import util.exception.LineCreationException;
import util.exception.ReservationNotFoundException;
import util.exception.RoomInventoryExistException;
import util.exception.RoomInventoryNotFoundException;
import util.exception.RoomRateNotFoundException;
import util.exception.RoomTypeNotFoundException;

/**
 *
 * @author ynaun
 */
public class MainApp {

    private RoomControllerRemote roomControllerRemote;
    private RoomRateControllerRemote roomRateControllerRemote;
    private RoomInventoryControllerRemote roomInventoryControllerRemote;
    private PartnerControllerRemote partnerControllerRemote;
    private ReservationControllerRemote reservationControllerRemote;
    private RoomTypeControllerRemote roomTypeControllerRemote;

    private Partner currentPartner;

    public MainApp(RoomControllerRemote roomControllerRemote, RoomRateControllerRemote roomRateControllerRemote, RoomInventoryControllerRemote roomInventoryControllerRemote, PartnerControllerRemote partnerControllerRemote, ReservationControllerRemote reservationControllerRemote, RoomTypeControllerRemote roomTypeControllerRemote) {
        this.roomControllerRemote = roomControllerRemote;
        this.roomRateControllerRemote = roomRateControllerRemote;
        this.roomInventoryControllerRemote = roomInventoryControllerRemote;
        this.partnerControllerRemote = partnerControllerRemote;
        this.reservationControllerRemote = reservationControllerRemote;
        this.roomTypeControllerRemote = roomTypeControllerRemote;
    }

    public void runApp() {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("*** Howdy Partner! Welcome to Holiday Reservation System Client :: Main Page ***\n");
            System.out.println("Please Log In");

            try {
                doLogin();
                System.out.println("Login successful!\n");
                menuMain();
            } catch (InvalidLoginCredentialException ex) {
                System.out.println("Invalid login credentials: " + ex.getMessage() + "\n");
            }
            break;

        }
    }

    public void doLogin() throws InvalidLoginCredentialException {
        Scanner sc = new Scanner(System.in);
        String username = "";
        String password = "";

        System.out.println("*** Holiday Reservation Management Client :: Login Menu ***\n");
        System.out.print("Enter Username> ");
        username = sc.nextLine().trim();
        System.out.print("Enter Password> ");
        password = sc.nextLine().trim();

        if (username.length() > 0 && password.length() > 0) {
            currentPartner = partnerControllerRemote.partnerLogin(username, password);
        } else {
            throw new InvalidLoginCredentialException("Missing login credential!");
        }
    }

    public void menuMain() {
        Scanner sc = new Scanner(System.in);
        String input;
        Integer response = 0;

        while (true) {
            System.out.println("*** Holiday Reservation Client :: Main Menu ***\n");
            System.out.println("You have logged in as " + currentPartner.getOrgName() + ".\n");
            System.out.println("1: Search Hotel Room");
            System.out.println("2: Reserve Hotel Room");
            System.out.println("3: View Reservation Details");
            System.out.println("4: View All Reservations");
            System.out.println("5: Logout\n");
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
                        searchHotelRoom();
                        break;
                    case 2:
                        reserveHotelRoom();
                        break;
                    case 3:
                        viewReservationDetails();
                        break;
                    case 4:
                        viewAllReservations();
                        break;
                    case 5:
                        return;
                    default:
                        break;
                }
            }
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

        System.out.println("*** Holiday Reservation Client :: Search Hotel Room ***\n");

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
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");

        List<RoomInventory> roomInventories;
        List<RoomRate> roomRates;
        System.out.println("*** Holiday Reservation System :: Partner Module :: Partner Reserve Room ***\n");

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
        PartnerReservation partnerReservation = new PartnerReservation();
        partnerReservation.setCheckInDate(checkInDate);
        partnerReservation.setCheckOutDate(checkOutDate);
        partnerReservation.setNumGuests(numGuests);
        partnerReservation.setCreatedDate(LocalDateTime.now());
        partnerReservation.setPartner(currentPartner);
        partnerReservation = reservationControllerRemote.createNewPartnerReservation(partnerReservation);

        while (true) {
            //create new reservation line item
            ReservationLineItem reservationLineItem = new ReservationLineItem();
            reservationLineItem.setNumRoomsRequested(numRoomsRequested);
            try {
            reservationLineItem = reservationControllerRemote.createNewPartnerReservationLineItem(reservationLineItem, partnerReservation.getReservationId(), reserveRoomTypeId);
            } catch (LineCreationException ex) {
                System.out.println("An error has occurred: " + ex.getMessage());
                return;
            }
            //create new room nights
            try {
                for (LocalDate date = checkInDate; date.isBefore(checkOutDate); date = date.plusDays(1)) {
                    RoomNight roomNight = new RoomNight();
                    roomNight.setDate(date);
                    reservationControllerRemote.createNewPartnerRoomNight(roomNight, reserveRoomTypeId, reservationLineItem.getReservationLineItemId());
                }
            } catch (CreateRoomNightException ex) {
                System.out.println("An error occurred: " + ex.getMessage());
            }

            //calculate total amount in reservation and ask for confirmation
            System.out.println("*** Reservation ID: " + partnerReservation.getReservationId() + " | All Reservations ***");
            try {
                Reservation currentReservation = reservationControllerRemote.retrieveReservationByReservationId(partnerReservation.getReservationId());
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

    public void viewReservationDetails() {

        Scanner sc = new Scanner(System.in);
        String input;
        Long reservationId;
        Reservation currentReservation;
        System.out.println("*** Holiday Reservation Client :: View My Reservation Details ***\n");

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

    public void viewAllReservations() {
        System.out.println("*** Holiday Reservation Client :: View All My Reservations ***\n");

        try {
            List<Reservation> reservations = reservationControllerRemote.retrieveAllReservationsByPartner(currentPartner.getPartnerId());
            
            for (Reservation reservation : reservations) {
                System.out.println("ID: " + reservation.getReservationId() + " | " + " Check In Date: " + reservation.getCheckInDate() + " Check Out Date: " + reservation.getCheckOutDate() + " Total amount spent: " + reservation.getReservationAmt());
            }
        } catch (ReservationNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }
    }

