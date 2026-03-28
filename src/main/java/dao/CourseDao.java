package dao;

import jakarta.persistence.EntityManager;
import model.AcademicStaff;
import model.Course;

import java.util.List;

import static utils.JpaUtil.emf;

public class CourseDao {

    public void save(Course course) {
        try (EntityManager em = emf.createEntityManager()) {
            try {
                em.getTransaction().begin();

                // If a staff member is assigned, use the helper to link both sides
                AcademicStaff staff = course.getAcademicStaff();
                if (staff != null) {
                    AcademicStaff managedStaff = em.merge(staff);
                    // Reset first so addCourse can set both sides cleanly
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

    public void update(Course course) {
        try (EntityManager em = emf.createEntityManager()) {
            try {
                em.getTransaction().begin();

                Course managedCourse = em.merge(course);

                // Re-link the staff using the helper so both sides stay in sync
                AcademicStaff newStaff = course.getAcademicStaff();

                // Remove from old staff if different
                AcademicStaff oldStaff = managedCourse.getAcademicStaff();
                if (oldStaff != null && !oldStaff.equals(newStaff)) {
                    oldStaff.removeCourse(managedCourse);
                }

                // Add to new staff
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

                Course managedCourse = em.merge(course);

                // Use the helper to cleanly unlink from staff on both sides
                AcademicStaff staff = managedCourse.getAcademicStaff();
                if (staff != null) {
                    staff.removeCourse(managedCourse);
                }

                em.remove(managedCourse);
                em.getTransaction().commit();

            } catch (Exception e) {
                if (em.getTransaction().isActive()) em.getTransaction().rollback();
                throw new RuntimeException("Error deleting course", e);
            }
        }
    }
}