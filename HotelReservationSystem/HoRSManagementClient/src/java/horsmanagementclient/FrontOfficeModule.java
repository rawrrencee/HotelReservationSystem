/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsmanagementclient;

import ejb.session.stateless.RoomControllerRemote;
import ejb.session.stateless.RoomInventoryControllerRemote;
import ejb.session.stateless.RoomRateControllerRemote;
import ejb.session.stateless.RoomTypeControllerRemote;
import entity.Employee;
import java.util.Scanner;
import util.enumeration.EmployeeAccessRightEnum;
import util.exception.InvalidAccessRightException;

/**
 *
 * @author Lawrence
 */
public class FrontOfficeModule {

    private RoomRateControllerRemote roomRateControllerRemote;
    private RoomControllerRemote roomControllerRemote;
    private RoomTypeControllerRemote roomTypeControllerRemote;
    private RoomInventoryControllerRemote roomInventoryControllerRemote;

    private Employee currentEmployee;

    public FrontOfficeModule() {
    }

    public FrontOfficeModule(RoomRateControllerRemote roomRateControllerRemote, RoomControllerRemote roomControllerRemote, RoomTypeControllerRemote roomTypeControllerRemote, RoomInventoryControllerRemote roomInventoryControllerRemote, Employee currentEmployee) {
        this.roomRateControllerRemote = roomRateControllerRemote;
        this.roomControllerRemote = roomControllerRemote;
        this.roomTypeControllerRemote = roomTypeControllerRemote;
        this.roomInventoryControllerRemote = roomInventoryControllerRemote;
        this.currentEmployee = currentEmployee;
    }

    public void menuMain() throws InvalidAccessRightException {
        if (currentEmployee.getAccessRight() != EmployeeAccessRightEnum.GUESTRELOFF) {
            throw new InvalidAccessRightException("You don't have GUEST RELATION OFFICER rights to access the System Administration Module!!");
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
    
    public void walkInSearchRoom(){
    }
    
    public void walkInReserveRoom(){
    }
    
    public void checkInGuest(){
    }
    
    public void checkOutGuest(){
    }
}
