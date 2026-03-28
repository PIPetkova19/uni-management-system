package model;

import jakarta.persistence.*;

import java.util.Set;

@Entity //student course join table
public class Enrollment {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    private Grade grade;

    @ManyToOne
    private Student student;

    @ManyToOne
    private Course course;

    public Enrollment() {}

    public Enrollment(Grade grade, Student student, Course course) {
        this.grade = grade;
        this.student = student;
        this.course = course;
    }

    public long getId() {
        return id;
    }

    public Grade getGrade() {
        return grade;
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    @Override
    public String toString() {
        return "Enrollment{" +
                "id=" + id +
                ", grade=" + grade +
                ", student=" + student +
                ", course=" + course +
                '}';
    }
}
