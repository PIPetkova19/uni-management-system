package dao;

import jakarta.persistence.EntityManager;
import model.AcademicStaff;
import model.Course;

import static utils.JpaUtil.emf;

public class AcademicStaffDao {

    public void save(AcademicStaff staff) {
        try (EntityManager em = emf.createEntityManager()) {
            try {
                em.getTransaction().begin();
                em.persist(staff);
                em.getTransaction().commit();
            } catch (Exception e) {
                if (em.getTransaction().isActive()) em.getTransaction().rollback();
                throw new RuntimeException("Error saving academic staff", e);
            }
        }
    }

    public AcademicStaff getStaffById(long id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(AcademicStaff.class, id);
        } catch (Exception e) {
            throw new RuntimeException("Error finding academic staff", e);
        }
    }

    public void update(AcademicStaff staff) {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            em.getTransaction().begin();

            em.merge(staff);

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new RuntimeException("Error updating academic staff", e);
        } finally {
            if (em != null) em.close();
        }
    }

    public void delete(AcademicStaff staff) {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            em.getTransaction().begin();

            // !
            for (Course c : staff.getCourses()) {
                c.setAcademicStaff(null);
                em.merge(c);
            }

            em.remove(em.contains(staff) ? staff : em.merge(staff));

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new RuntimeException("Error deleting academic staff", e);
        } finally {
            if (em != null) em.close();
        }
    }
}