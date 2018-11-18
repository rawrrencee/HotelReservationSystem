/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.NormalRoomRate;
import entity.PeakRoomRate;
import entity.PromoRoomRate;
import entity.RoomRate;
import entity.RoomType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.GeneralException;
import util.exception.RoomRateExistException;
import util.exception.RoomRateNotFoundException;
import util.exception.RoomTypeNotFoundException;

/**
 *
 * @author Lawrence
 */
@Stateless
@Local(RoomRateControllerLocal.class)
@Remote(RoomRateControllerRemote.class)
public class RoomRateController implements RoomRateControllerLocal, RoomRateControllerRemote {

    @EJB
    private RoomTypeControllerLocal roomTypeControllerLocal;

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    public RoomRateController() {
    }

    @Override
    public List<RoomRate> retrieveAllRoomRates() {
        Query query = em.createQuery("SELECT rr FROM RoomRate rr");

        return query.getResultList();
    }

    public RoomRate createNewRoomRate(RoomRate newRoomRate, Long roomTypeId) throws RoomRateExistException, RoomTypeNotFoundException, GeneralException {
        try {
            RoomType roomType = roomTypeControllerLocal.retrieveRoomTypeByRoomTypeId(roomTypeId);
            em.persist(newRoomRate);

            newRoomRate.setRoomType(roomType);
            roomType.getRoomRates().add(newRoomRate);

            em.flush();
            em.refresh(newRoomRate);

            return newRoomRate;
        } catch (RoomTypeNotFoundException ex) {

            throw new RoomTypeNotFoundException("Unable to create new Room Rate as the Room Type record does not exist!");

        } catch (PersistenceException ex) {
            if (ex.getCause() != null && ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getSimpleName().equals("SQLIntegrityConstraintViolationException")) {
                throw new RoomRateExistException("A room rate with the provided information already exists!");
            } else {
                throw new GeneralException("An unexpected error has occurred: " + ex.getMessage());
            }
        }
    }

    @Override
    public RoomRate retrieveRoomRateByRoomRateId(Long roomRateId) throws RoomRateNotFoundException {
        RoomRate roomRate = em.find(RoomRate.class, roomRateId);

        if (roomRate != null) {
            return roomRate;
        } else {
            throw new RoomRateNotFoundException("Room Rate ID " + roomRateId + " does not exist!");
        }
    }

    @Override
    public void updateRoomRate(RoomRate roomRate, Long newRoomTypeId) throws RoomTypeNotFoundException {
        Long currentRoomTypeId = roomRate.getRoomType().getRoomTypeId();
        RoomType currentRoomType = roomTypeControllerLocal.retrieveRoomTypeByRoomTypeId(currentRoomTypeId);

        try {
            if (!currentRoomTypeId.equals(newRoomTypeId)) {
                RoomType newRoomType = roomTypeControllerLocal.retrieveRoomTypeByRoomTypeId(newRoomTypeId);
                roomRate.setRoomType(newRoomType);
                newRoomType.getRoomRates().add(roomRate);
                currentRoomType.getRoomRates().remove(roomRate);
            }
        } catch (RoomTypeNotFoundException ex) {
            System.out.println("Room rate does not exist!");
        }
        em.merge(roomRate);
    }

    @Override
    public Boolean deleteRoomRate(Long roomRateId) throws RoomRateNotFoundException {
        RoomRate roomRateToRemove = retrieveRoomRateByRoomRateId(roomRateId);
        RoomType roomType = roomRateToRemove.getRoomType();

//        try {
//            roomRateToRemove.getRoomNights();
//            roomRateToRemove.setIsEnabled(Boolean.FALSE);
//            return false;
//        } catch (NullPointerException ex) {
//            roomType.getRoomRates().remove(roomRateToRemove);
//            em.remove(roomRateToRemove);
//            return true;
//        }
        if (roomRateToRemove.getRoomNights().isEmpty() || roomRateToRemove.getRoomNights() == null) {
            roomType.getRoomRates().remove(roomRateToRemove);
            em.remove(roomRateToRemove);
            return true;
        } else {
            roomRateToRemove.setIsEnabled(Boolean.FALSE);
            return false;
        }
    }

    @Override
    public RoomRate retrieveLowestPublishedRoomRate(Long roomTypeId) {
        Query query = em.createQuery("SELECT p FROM PublishedRoomRate p WHERE p.roomType.roomTypeId = :inRoomTypeId ORDER BY p.ratePerNight ASC");
        query.setParameter("inRoomTypeId", roomTypeId);
        query.setFirstResult(0);
        query.setMaxResults(1);

        return (RoomRate) query.getSingleResult();
    }

    @Override
    public RoomRate retrieveComplexRoomRate(Long roomTypeId, LocalDate date) throws RoomRateNotFoundException {
        Boolean hasNormal = false;
        Boolean hasPromo = false;
        Boolean hasPeak = false;
        RoomRate finalRate = null;

        Query query = em.createQuery("SELECT nrr FROM NormalRoomRate nrr WHERE nrr.roomType.roomTypeId = :inRoomTypeId ORDER BY nrr.ratePerNight ASC");
        query.setParameter("inRoomTypeId", roomTypeId);
        query.setFirstResult(0);
        query.setMaxResults(1);
        if (!query.getResultList().isEmpty()) {
            hasNormal = true;
        }

        Query query2 = em.createQuery("SELECT prr FROM PromoRoomRate prr WHERE prr.roomType.roomTypeId = :inRoomTypeId ORDER BY prr.ratePerNight ASC");
        query2.setParameter("inRoomTypeId", roomTypeId);
        if (!query2.getResultList().isEmpty()) {
            hasPromo = true;
        }

        Query query3 = em.createQuery("SELECT perr FROM PeakRoomRate perr WHERE perr.roomType.roomTypeId = :inRoomTypeId ORDER BY perr.ratePerNight ASC");
        query3.setParameter("inRoomTypeId", roomTypeId);
        if (!query3.getResultList().isEmpty()) {
            hasPeak = true;
        }

        if (hasNormal && !hasPromo && !hasPeak) {
            finalRate = (NormalRoomRate) query.getSingleResult();
            return finalRate;
        }
        if (hasNormal && hasPromo && !hasPeak) {
            List<PromoRoomRate> promoRoomRates = (List<PromoRoomRate>) query2.getResultList();
            for (PromoRoomRate promoRoomRate : promoRoomRates) {
                if (promoRoomRate.getEndDate().isAfter(date.atTime(0, 0)) || promoRoomRate.getEndDate().isEqual(date.atTime(0, 0))) {
                    if (promoRoomRate.getStartDate().isBefore(date.atTime(0, 0)) || promoRoomRate.getStartDate().isEqual(date.atTime(0, 0))) {
                        finalRate = (RoomRate) promoRoomRate;
                        break;
                    }
                }
            }
            if (finalRate == null) {
                finalRate = (NormalRoomRate) query.getSingleResult();
            }
            return finalRate;
        }
        if (hasNormal && !hasPromo && hasPeak) {
            List<PeakRoomRate> peakRoomRates = (List<PeakRoomRate>) query3.getResultList();
            for (PeakRoomRate peakRoomRate : peakRoomRates) {
                if (peakRoomRate.getEndDate().isAfter(date.atTime(0, 0)) || peakRoomRate.getEndDate().isEqual(date.atTime(0, 0))) {
                    if (peakRoomRate.getStartDate().isBefore(date.atTime(0, 0)) || peakRoomRate.getStartDate().isEqual(date.atTime(0, 0))) {
                        finalRate = (RoomRate) peakRoomRate;
                        break;
                    }
                }
            }
            if (finalRate == null) {
                finalRate = (NormalRoomRate) query.getSingleResult();
            }
            return finalRate;
        }

        if (!hasNormal && hasPromo && hasPeak) {
            List<PeakRoomRate> peakRoomRates = (List<PeakRoomRate>) query3.getResultList();
            finalRate = (PeakRoomRate) query3.getResultList().get(0);
            for (PeakRoomRate peakRoomRate : peakRoomRates) {
                // ongoing promo room rate
                if (peakRoomRate.getEndDate().isAfter(date.atTime(0, 0)) || peakRoomRate.getEndDate().isEqual(date.atTime(0, 0))) {
                    if (peakRoomRate.getStartDate().isBefore(date.atTime(0, 0)) || peakRoomRate.getStartDate().isEqual(date.atTime(0, 0))) {
                        finalRate = (RoomRate) peakRoomRate;
                        break;
                    }
                }
            }
            if (finalRate == null) {
                finalRate = (NormalRoomRate) query.getSingleResult();
            }
            return finalRate;
        }
        if (hasNormal && hasPromo && hasPeak) {
            List<PromoRoomRate> promoRoomRates = (List<PromoRoomRate>) query2.getResultList();
            for (PromoRoomRate promoRoomRate : promoRoomRates) {
                if (promoRoomRate.getEndDate().isAfter(date.atTime(0, 0)) || promoRoomRate.getEndDate().isEqual(date.atTime(0, 0))) {
                    if (promoRoomRate.getStartDate().isBefore(date.atTime(0, 0)) || promoRoomRate.getStartDate().isEqual(date.atTime(0, 0))) {
                        finalRate = (RoomRate) promoRoomRate;
                        break;
                    }
                }
            }
            if (finalRate == null) {
                finalRate = (NormalRoomRate) query.getSingleResult();
            }
            return finalRate;
        }
        try {
            return (RoomRate) query.getSingleResult();
        } catch (NoResultException ex) {
            throw new RoomRateNotFoundException("Normal Room Rate not found");
        }
    }

}
