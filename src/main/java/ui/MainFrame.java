package ui;

import javax.swing.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("University Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Students",       new StudentPanel());
        tabs.addTab("Academic Staff", new AcademicStaffPanel());
        tabs.addTab("Courses",        new CoursePanel());
        tabs.addTab("Enrollments",    new EnrollmentPanel());

        add(tabs);
    }
}