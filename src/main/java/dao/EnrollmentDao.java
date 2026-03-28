package dao;

import jakarta.persistence.EntityManager;
import model.Course;
import model.Enrollment;
import model.Student;
import model.Grade;

import static utils.JpaUtil.emf;

public class EnrollmentDao {

    public void save(Student student, Course course, Grade grade) {
        try (EntityManager em = emf.createEntityManager()) {
            try {
                em.getTransaction().begin();

                Enrollment enrollment = new Enrollment();
                enrollment.setStudent(student);
                enrollment.setCourse(course);
                enrollment.setGrade(grade);

                //!
                student.getEnrollments().add(enrollment);
                course.getEnrollments().add(enrollment);

                em.persist(enrollment);

                // Ако student или course са detached, merge ги
                em.merge(student);
                em.merge(course);

                em.getTransaction().commit();
            } catch (Exception e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
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

    public void update(Enrollment enrollment) {
        try (EntityManager em = emf.createEntityManager()) {
            try {

                em.getTransaction().begin();
                em.merge(enrollment);
                em.getTransaction().commit();
            } catch (Exception e) {
                if (em.getTransaction().isActive()) em.getTransaction().rollback();
                throw new RuntimeException("Error updating enrollment", e);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error updating enrollment", e);
        }
    }

    public void delete(Enrollment enrollment) {
        try (EntityManager em = emf.createEntityManager()) {
            try {
                em.getTransaction().begin();

                // !
                Student student = enrollment.getStudent();
                Course course = enrollment.getCourse();

                if (student != null) student.getEnrollments().remove(enrollment);
                if (course != null) course.getEnrollments().remove(enrollment);

                if (student != null) em.merge(student);
                if (course != null) em.merge(course);

                em.remove(em.contains(enrollment) ? enrollment : em.merge(enrollment));

                em.getTransaction().commit();
            } catch (Exception e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw new RuntimeException("Error deleting enrollment", e);
            }
        }
    }
}