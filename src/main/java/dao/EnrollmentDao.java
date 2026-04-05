package dao;

import jakarta.persistence.EntityManager;
import model.*;

import java.util.List;

import static utils.JpaUtil.emf;

public class EnrollmentDao {

    public void save(Student student, Course course, Grade grade) {
        try (EntityManager em = emf.createEntityManager()) {
            try {
                em.getTransaction().begin();
                Student managedStudent = em.merge(student);
                Course  managedCourse  = em.merge(course);
                Enrollment enrollment = new Enrollment();
                enrollment.setGrade(grade);
                managedStudent.addEnrollment(enrollment);
                managedCourse.addEnrollment(enrollment);
                em.persist(enrollment);
                em.getTransaction().commit();
            } catch (Exception e) {
                if (em.getTransaction().isActive()) em.getTransaction().rollback();
                throw new RuntimeException("Error saving enrollment", e);
            }
        }
    }

    public Enrollment getEnrollmentById(long id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(Enrollment.class, id);
        } catch (Exception e) {
            throw new RuntimeException("Error finding enrollment", e);
        }
    }

    public List<Enrollment> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("SELECT e FROM Enrollment e", Enrollment.class).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error finding enrollment", e);
        }
    }

    public List<Enrollment> getByCourseName(String name) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(
                            "SELECT e FROM Enrollment e JOIN e.course c WHERE c.name = :name", Enrollment.class)
                    .setParameter("name",  name)
                    .getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error finding enrollments by course name", e);
        }
    }

    public List<Enrollment> getByStudentName(String name) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(
                            "SELECT e FROM Enrollment e JOIN e.student s WHERE s.name = :name", Enrollment.class)
                    .setParameter("name",  name )
                    .getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error finding enrollments by student name", e);
        }
    }

    public List<Enrollment> getByStuNameAndCourName(String stuName, String courName) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(
                            "SELECT e FROM Enrollment e WHERE e.student.name = :stuName AND e.course.name = :courName",
                            Enrollment.class)
                    .setParameter("stuName", stuName )
                    .setParameter("courName", courName )
                    .getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error finding enrollments by student name and course name", e);
        }
    }

    public void update(Enrollment enrollment) {
        try (EntityManager em = emf.createEntityManager()) {
            try {
                em.getTransaction().begin();
                em.merge(enrollment);
                em.getTransaction().commit();
            } catch (Exception e) {
                if (em.getTransaction().isActive()) em.getTransaction().rollback();
            }
        } catch (Exception e) {
            throw new RuntimeException("Error updating enrollment", e);
        }
    }

    public void delete(Enrollment enrollment) {
        try (EntityManager em = emf.createEntityManager()) {
            try {
                em.getTransaction().begin();
                Enrollment managed = em.merge(enrollment);
                if (managed.getStudent() != null) managed.getStudent().removeEnrollment(managed);
                if (managed.getCourse()  != null) managed.getCourse().removeEnrollment(managed);
                em.remove(managed);
                em.getTransaction().commit();
            } catch (Exception e) {
                if (em.getTransaction().isActive()) em.getTransaction().rollback();
                throw new RuntimeException("Error deleting enrollment", e);
            }
        }
    }
}