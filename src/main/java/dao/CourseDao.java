package dao;

import jakarta.persistence.EntityManager;
import model.AcademicStaff;
import model.Course;
import model.Enrollment;

import java.util.ArrayList;
import java.util.List;

import static utils.JpaUtil.emf;

public class CourseDao {

    public void save(Course course) {
        try (EntityManager em = emf.createEntityManager()) {
            try {
                em.getTransaction().begin();
                AcademicStaff academicStaff = course.getAcademicStaff();
                if (academicStaff != null) {
                    AcademicStaff managedStaff = em.merge(academicStaff);
                    course.setAcademicStaff(null);
                    managedStaff.addCourse(course);
                }
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

    public List<Course> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("SELECT c FROM Course c", Course.class).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error finding courses", e);
        }
    }

    public List<Course> getByName(String name) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(
                            "SELECT c FROM Course c WHERE c.name = :name", Course.class)
                    .setParameter("name", name)
                    .getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error finding courses by name", e);
        }
    }

    public List<Course> getByInstructor(String instructorName) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(
                            "SELECT c FROM Course c JOIN c.academicStaff a WHERE a.name = :name", Course.class)
                    .setParameter("name", instructorName)
                    .getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error finding courses by instructor", e);
        }
    }

    public List<Course> getByNameAndInstructor(String name, String instructorName) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(
                            "SELECT c FROM Course c WHERE c.name = :name AND c.academicStaff.name = :instructorName",
                            Course.class)
                    .setParameter("name", name)
                    .setParameter("instructorName", instructorName)
                    .getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error finding courses by name and instructor", e);
        }
    }

    public void update(Course course) {
        try (EntityManager em = emf.createEntityManager()) {
            try {
                em.getTransaction().begin();
                Course managedCourse = em.merge(course);
                AcademicStaff newStaff = course.getAcademicStaff();
                AcademicStaff oldStaff = managedCourse.getAcademicStaff();
                if (oldStaff != null && !oldStaff.equals(newStaff)) {
                    oldStaff.removeCourse(managedCourse);
                }
                if (newStaff != null) {
                    AcademicStaff managedStaff = em.merge(newStaff);
                    managedStaff.addCourse(managedCourse);
                } else {
                    managedCourse.setAcademicStaff(null);
                }
                em.getTransaction().commit();
            } catch (Exception e) {
                if (em.getTransaction().isActive()) em.getTransaction().rollback();
                throw new RuntimeException("Error updating course", e);
            }
        }
    }
    public void delete(Course course) {
        try (EntityManager em = emf.createEntityManager()) {
            try {
                em.getTransaction().begin();
                Course managedCourse = em.find(Course.class, course.getId());
                if (managedCourse != null) {
                    managedCourse.getEnrollments().clear();
                    AcademicStaff staff = managedCourse.getAcademicStaff();
                    if (staff != null) {
                        staff.getCourses().remove(managedCourse);
                    }
                    em.remove(managedCourse);
                }
                em.getTransaction().commit();
            } catch (Exception e) {
                if (em.getTransaction().isActive()) em.getTransaction().rollback();
                throw new RuntimeException("Error deleting course", e);
            }
        }
    }
}