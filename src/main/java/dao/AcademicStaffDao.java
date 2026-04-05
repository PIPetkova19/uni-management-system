package dao;

import jakarta.persistence.EntityManager;
import model.AcademicStaff;
import model.Course;

import java.util.List;

import static utils.JpaUtil.emf;

public class AcademicStaffDao {

    public void save(AcademicStaff academicStaff) {
        try (EntityManager em = emf.createEntityManager()) {
            try {
                em.getTransaction().begin();
                em.persist(academicStaff);
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

    public List<AcademicStaff> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("SELECT a FROM AcademicStaff a", AcademicStaff.class).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error finding academic staff", e);
        }
    }


    public List<AcademicStaff> getByName(String name) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(
                            "SELECT a FROM AcademicStaff a WHERE a.name = :name", AcademicStaff.class)
                    .setParameter("name",  name )
                    .getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error finding academic staff by name", e);
        }
    }

    public List<AcademicStaff> getByEmail(String email) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(
                            "SELECT a FROM AcademicStaff a WHERE a.email = :email", AcademicStaff.class)
                    .setParameter("email",  email )
                    .getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error finding academic staff by email", e);
        }
    }

    public List<AcademicStaff> getByNameAndEmail(String name, String email) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(
                            "SELECT a FROM AcademicStaff a WHERE a.name = :name AND a.email = :email",
                            AcademicStaff.class)
                    .setParameter("name", name )
                    .setParameter("email",  email )
                    .getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error finding academic staff by name and email", e);
        }
    }

    public void update(AcademicStaff academicStaff) {
        try (EntityManager em = emf.createEntityManager()) {
            try {
                em.getTransaction().begin();
                em.merge(academicStaff);
                em.getTransaction().commit();
            } catch (Exception e) {
                if (em.getTransaction().isActive()) em.getTransaction().rollback();
                throw new RuntimeException("Error updating academic staff", e);
            }
        }
    }

    public void delete(AcademicStaff academicStaff) {
        try (EntityManager em = emf.createEntityManager()) {
            try {
                em.getTransaction().begin();
                AcademicStaff managed = em.merge(academicStaff);
                for (Course course : List.copyOf(managed.getCourses())) {
                    managed.removeCourse(course);
                }
                em.remove(managed);
                em.getTransaction().commit();
            } catch (Exception e) {
                if (em.getTransaction().isActive()) em.getTransaction().rollback();
                throw new RuntimeException("Error deleting academic staff", e);
            }
        }
    }
}