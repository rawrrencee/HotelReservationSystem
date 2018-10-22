/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Employee;
import java.util.List;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.EmployeeNotFoundException;
import util.exception.InvalidLoginCredentialException;

/**
 *
 * @author Lawrence
 */
@Stateless
@Local(EmployeeControllerLocal.class)
@Remote(EmployeeControllerRemote.class)

public class EmployeeController implements EmployeeControllerLocal, EmployeeControllerRemote {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    public EmployeeController() {
    }
    
    @Override
    public Employee createNewEmployee(Employee newEmployee) {
        em.persist(newEmployee);
        em.flush();
        
        return newEmployee;
    }
    
    @Override
    public List<Employee> retrieveAllEmployees() {
        Query query = em.createQuery("SELECT e FROM Employee e");
        
        return query.getResultList();
    }
    
    @Override
    public Employee retrieveEmployeeByEmployeeId(Long employeeId) throws EmployeeNotFoundException {
        Employee employee = em.find(Employee.class, employeeId);
        
        if (employee != null) {
            return employee;
        } else {
            throw new EmployeeNotFoundException("Employee ID " + employeeId + " does not exist!");
        }
    }
    
    @Override
    public Employee retrieveEmployeeByUsername(String username) throws EmployeeNotFoundException {
        Query query = em.createQuery("SELECT e FROM Employee e WHERE e.username = :inUsername");
        query.setParameter("inUsername", username);
        
        try {
            return (Employee)query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new EmployeeNotFoundException("Employee Username " + username + " does not exist!");
        }
    }
    
    @Override
    public Employee employeeLogin(String username, String password) throws InvalidLoginCredentialException {
        try {
            Employee employee = retrieveEmployeeByUsername(username);
            
            if (employee.getPassword().equals(password)) {
                employee.getAccessRight();
                
                return employee;
            } else {
                throw new InvalidLoginCredentialException("Username does not exist or invalid password!");
            }
        } catch (EmployeeNotFoundException ex) {
            throw new InvalidLoginCredentialException("Username does not exist or invalid password!");
        }
    }
}
