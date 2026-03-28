package dao;

import jakarta.persistence.EntityManager;
import model.Student;

import static utils.JpaUtil.emf;


public class StudentDao {

    public void save(Student student) {
        try (EntityManager em = emf.createEntityManager()) {
            try {
                em.getTransaction().begin();
                em.persist(student);
                em.getTransaction().commit();
            } catch (Exception e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw new RuntimeException("Error saving student", e);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error saving student", e);
        }
    }

    public Student getStudentById(long id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(Student.class, id);
        } catch (Exception e) {
            throw new RuntimeException("Error finding student", e);
        }
    }

    public void update(Student student) {
        try(EntityManager em = emf.createEntityManager()) {
            try {
                em.getTransaction().begin();
                em.merge(student);
                em.getTransaction().commit();
            } catch (Exception e) {
                if (em != null && em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw new RuntimeException("Error updating student", e);
            }
        }catch(Exception e) {
            throw new RuntimeException("Error updating student", e);
        }
    }

    public void delete(Student student) {
        try (EntityManager em = emf.createEntityManager()) {
            try{
                em.getTransaction().begin();
                em.remove(student);
                em.getTransaction().commit();
            }
          catch(Exception e) {
                if (em != null && em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw new RuntimeException("Error deleting student", e);
          }
        }
        catch (Exception e) {
            throw new RuntimeException("Error deleting student", e);
        }
    }
}
