/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Partner;
import java.util.List;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.InvalidLoginCredentialException;
import util.exception.PartnerNotFoundException;

/**
 *
 * @author Lawrence
 */
@Stateless
@Local(PartnerControllerLocal.class)
@Remote(PartnerControllerRemote.class)

public class PartnerController implements PartnerControllerLocal, PartnerControllerRemote {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    public PartnerController() {
    }
    
    @Override
    public Partner createNewPartner(Partner newPartner) {
        em.persist(newPartner);
        em.flush();
        
        return newPartner;
    }
    
    @Override
    public List<Partner> retrieveAllPartners() {
        Query query = em.createQuery("SELECT p FROM Partner p");
        
        return query.getResultList();
    }
    
    @Override
    public Partner retrievePartnerByPartnerId(Long partnerId) throws PartnerNotFoundException {
        Partner partner = em.find(Partner.class, partnerId);
        
        if (partner != null) {
            return partner;
        } else {
            throw new PartnerNotFoundException("Partner ID " + partnerId + " does not exist!");
        }
    }
    
    @Override
    public Partner retrievePartnerByUsername(String username) throws PartnerNotFoundException {
        Query query = em.createQuery("SELECT p FROM Partner p WHERE p.username = :inUsername");
        query.setParameter("inUsername", username);
        
        try {
            return (Partner)query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new PartnerNotFoundException("Partner Username " + username + " does not exist!");
        }
    }
    
    @Override
    public Partner partnerLogin(String username, String password) throws InvalidLoginCredentialException {
        try {
            Partner partner = retrievePartnerByUsername(username);
            
            if (partner.getPassword().equals(password)) {
                partner.getContactNum();
                
                return partner;
            } else {
                throw new InvalidLoginCredentialException("Username does not exist or invalid password!");
            }
        } catch (PartnerNotFoundException ex) {
            throw new InvalidLoginCredentialException("Username does not exist or invalid password!");
        }
    }
}
