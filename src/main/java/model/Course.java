package model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Course {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;

    private String name;

    @ManyToOne
    private AcademicStaff academicStaff;

    @OneToMany(
            mappedBy = "course",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Enrollment> enrollments= new ArrayList<>();

    public Course() {}

    public Course(String name, AcademicStaff academicStaff) {
        this.name = name;
        this.academicStaff = academicStaff;
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

    public AcademicStaff getAcademicStaff() {
        return academicStaff;
    }

    public void setAcademicStaff(AcademicStaff academicStaff) {
        this.academicStaff = academicStaff;
    }

    public List<Enrollment> getEnrollments() {
        return enrollments;
    }

    public void setEnrollments(List<Enrollment> enrollments) {
        this.enrollments = enrollments;
    }

    public void addEnrollment(Enrollment enrollment) {
        enrollments.add(enrollment);
        enrollment.setCourse(this);
    }

    public void removeEnrollment(Enrollment enrollment) {
        enrollments.remove(enrollment);
        enrollment.setCourse(null);
    }

    @Override
    public String toString() {
        return String.format("Course: %s - %s",name,academicStaff.getName());
    }
}
