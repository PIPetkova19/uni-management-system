package dao;

import jakarta.persistence.EntityManager;
import model.AcademicStaff;
import model.Course;

import java.util.List;

import static utils.JpaUtil.emf;

public class CourseDao {

    public void save(Course course) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            AcademicStaff staff = course.getAcademicStaff();
            if (staff != null) {
                staff = em.merge(staff);
                staff.addCourse(course);
            }
            em.persist(course);
            em.getTransaction().commit();
        } catch (Exception e) {
            throw new RuntimeException("Error saving course", e);
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

    public List<Course> getByAcademicStaff(String academicStaff) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(
                            "SELECT c FROM Course c JOIN c.academicStaff a WHERE a.name = :name", Course.class)
                    .setParameter("name", academicStaff)
                    .getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error finding courses by academic staff", e);
        }
    }

    public List<Course> getByNameAndAcademicStaff(String name, String academicStaff) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(
                            "SELECT c FROM Course c WHERE c.name = :name AND c.academicStaff.name = :academicStaff",
                            Course.class)
                    .setParameter("name", name)
                    .setParameter("academicStaff", academicStaff)
                    .getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error finding courses by name and academic staff", e);
        }
    }

    public void update(Course course) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Course managedCourse = em.merge(course);
            AcademicStaff newStaff = course.getAcademicStaff();
            if (newStaff != null) {
                newStaff = em.merge(newStaff);
                newStaff.addCourse(managedCourse);
            } else if (managedCourse.getAcademicStaff() != null) {
                managedCourse.getAcademicStaff().removeCourse(managedCourse);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            throw new RuntimeException("Error updating course", e);
        }
    }

    public void delete(Course course) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Course managedCourse = em.find(Course.class, course.getId());
            if (managedCourse != null) {
                managedCourse.getEnrollments().clear();
                if (managedCourse.getAcademicStaff() != null) {
                    managedCourse.getAcademicStaff().removeCourse(managedCourse);
                }
                em.remove(managedCourse);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            throw new RuntimeException("Error deleting course", e);
        }
    }
}