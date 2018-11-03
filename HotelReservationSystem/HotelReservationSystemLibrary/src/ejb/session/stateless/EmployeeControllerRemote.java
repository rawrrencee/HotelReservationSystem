/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Employee;
import java.util.List;
import util.exception.EmployeeExistException;
import util.exception.EmployeeNotFoundException;
import util.exception.GeneralException;
import util.exception.InvalidLoginCredentialException;

public interface EmployeeControllerRemote {
    
    public Employee createNewEmployee(Employee newEmployee) throws EmployeeExistException, GeneralException;

    public List<Employee> retrieveAllEmployees();

    public Employee retrieveEmployeeByEmployeeId(Long employeeId) throws EmployeeNotFoundException;

    public Employee retrieveEmployeeByUsername(String username) throws EmployeeNotFoundException;

    public Employee employeeLogin(String username, String password) throws InvalidLoginCredentialException;

    public Boolean checkEmployeeExists(String username) throws EmployeeNotFoundException;
}
