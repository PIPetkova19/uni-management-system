package dao;

import jakarta.persistence.EntityManager;
import model.Course;
import model.AcademicStaff;

import static utils.JpaUtil.emf;

public class CourseDao {

    public void save(Course course) {
        try (EntityManager em = emf.createEntityManager()) {
            try {
                em.getTransaction().begin();
                em.persist(course);
                em.getTransaction().commit();
            } catch (Exception e) {
                if (em.getTransaction().isActive()) em.getTransaction().rollback();
                throw new RuntimeException("Error saving course", e);
            }
        }
    }

    public Course getCourseById(long id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(Course.class, id);
        } catch (Exception e) {
            throw new RuntimeException("Error finding course", e);
        }
    }

    public void update(Course course) {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            em.getTransaction().begin();
            em.merge(course);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new RuntimeException("Error updating course", e);
        } finally {
            if (em != null) em.close();
        }
    }

    public void delete(Course course) {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            em.getTransaction().begin();

            // Премахваме връзките с academicStaff
            AcademicStaff staff = course.getAcademicStaff();
            if (staff != null) {
                staff.getCourses().remove(course);
                em.merge(staff);
            }

            em.remove(em.contains(course) ? course : em.merge(course));

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new RuntimeException("Error deleting course", e);
        } finally {
            if (em != null) em.close();
        }
    }
}