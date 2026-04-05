package model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class AcademicStaff {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;

    private String name;
    private String title;
    private String email;

    @OneToMany(
            mappedBy = "academicStaff",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Course> courses=new ArrayList<>();

    public AcademicStaff() {}

    public AcademicStaff(String name, String title, String email) {
        this.name = name;
        this.title = title;
        this.email = email;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    public void addCourse(Course course) {
        courses.add(course);
        course.setAcademicStaff(this);
    }

    public void removeCourse(Course course) {
        courses.remove(course);
        course.setAcademicStaff(null);
    }

    @Override
    public String toString() {
        return String.format("%s %s",title,name);
    }
}
