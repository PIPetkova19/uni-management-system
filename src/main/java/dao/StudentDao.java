package dao;

import jakarta.persistence.EntityManager;
import model.Course;
import model.Enrollment;
import model.Student;

import java.util.List;

import static utils.JpaUtil.emf;

public class StudentDao {

    public void save(Student student) {
        try (EntityManager em = emf.createEntityManager()) {
            try {
                em.getTransaction().begin();
                em.persist(student);
                em.getTransaction().commit();
            } catch (Exception e) {
                if (em.getTransaction().isActive()) em.getTransaction().rollback();
                throw new RuntimeException("Error saving student", e);
            }
        }
    }

    public Student getStudentById(long id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(Student.class, id);
        } catch (Exception e) {
            throw new RuntimeException("Error finding student", e);
        }
    }

    public List<Student> getByNameAndFac(String name, String fac) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(
                            "SELECT s FROM Student s WHERE s.name = :name AND s.facNum = :fac",
                            Student.class)
                    .setParameter("name", name )
                    .setParameter("fac",  fac )
                    .getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error finding students by name and faculty number", e);
        }
    }
    public List<Student> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("SELECT s FROM Student s", Student.class).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error finding students", e);
        }
    }

    public List<Student> getByName(String name) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(
                            "SELECT s FROM Student s WHERE s.name = :name", Student.class)
                    .setParameter("name",  name )
                    .getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error finding students by name", e);
        }
    }

    public List<Student> getByFacNum(String facNum) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(
                            "SELECT s FROM Student s WHERE s.facNum = :facNum", Student.class)
                    .setParameter("facNum", facNum )
                    .getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error finding students by faculty number", e);
        }
    }

    public void update(Student student) {
        try (EntityManager em = emf.createEntityManager()) {
            try {
                em.getTransaction().begin();
                em.merge(student);
                em.getTransaction().commit();
            } catch (Exception e) {
                if (em.getTransaction().isActive()) em.getTransaction().rollback();
                throw new RuntimeException("Error updating student", e);
            }
        }
    }

    public void delete(Student student) {
        try (EntityManager em = emf.createEntityManager()) {
            try {
                em.getTransaction().begin();
                Student managed = em.merge(student);
                managed.getEnrollments().forEach(enrollment -> {
                    if (enrollment.getCourse() != null) {
                        enrollment.getCourse().removeEnrollment(enrollment);
                    }
                });
                em.remove(managed);
                em.getTransaction().commit();
            } catch (Exception e) {
                if (em.getTransaction().isActive()) em.getTransaction().rollback();
                throw new RuntimeException("Error deleting student", e);
            }
        }
    }
}