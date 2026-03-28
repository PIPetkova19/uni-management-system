package ui;

import dao.CourseDao;
import dao.EnrollmentDao;
import dao.StudentDao;
import model.Course;
import model.Enrollment;
import model.Grade;
import model.Student;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class EnrollmentPanel extends JPanel {

    private final EnrollmentDao enrollmentDao = new EnrollmentDao();
    private final StudentDao    studentDao    = new StudentDao();
    private final CourseDao     courseDao     = new CourseDao();

    private final String[] COLUMNS = {"ID", "Student", "Course", "Grade"};
    private final DefaultTableModel tableModel = new DefaultTableModel(COLUMNS, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(tableModel);

    private final JComboBox<Student> studentCombo = new JComboBox<>();
    private final JComboBox<Course>  courseCombo  = new JComboBox<>();
    private final JComboBox<Grade>   gradeCombo   = new JComboBox<>(Grade.values());

    public EnrollmentPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> onRowSelected());
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Show student name in dropdown
        studentCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Student s) setText(s.getName() + " [" + s.getFacNum() + "]");
                return this;
            }
        });

        // Show course name in dropdown
        courseCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Course c) setText(c.getName());
                return this;
            }
        });

        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBorder(BorderFactory.createTitledBorder("Enrollment"));

        right.add(new JLabel("Student:"));
        right.add(studentCombo);
        right.add(Box.createVerticalStrut(6));

        right.add(new JLabel("Course:"));
        right.add(courseCombo);
        right.add(Box.createVerticalStrut(6));

        right.add(new JLabel("Grade:"));
        right.add(gradeCombo);
        right.add(Box.createVerticalStrut(12));

        JButton btnAdd    = new JButton("Enroll");
        JButton btnUpdate = new JButton("Update Grade");
        JButton btnDelete = new JButton("Remove");
        JButton btnRefresh = new JButton("Refresh Lists");

        btnAdd.addActionListener(e     -> add());
        btnUpdate.addActionListener(e  -> update());
        btnDelete.addActionListener(e  -> delete());
        // Refresh reloads students/courses in case new ones were added in other tabs
        btnRefresh.addActionListener(e -> { loadCombos(); refresh(); });

        for (JButton b : new JButton[]{btnAdd, btnUpdate, btnDelete, btnRefresh}) {
            b.setAlignmentX(Component.LEFT_ALIGNMENT);
            right.add(b);
            right.add(Box.createVerticalStrut(4));
        }

        add(right, BorderLayout.EAST);
        loadCombos();
        refresh();
    }

    private void loadCombos() {
        studentCombo.removeAllItems();
        for (Student s : studentDao.getAll()) studentCombo.addItem(s);

        courseCombo.removeAllItems();
        for (Course c : courseDao.getAll()) courseCombo.addItem(c);
    }

    private void refresh() {
        tableModel.setRowCount(0);
        // Use getAll() — student and course names are stored as strings in the table
        // so we are NOT relying on lazy-loaded references later
        for (Enrollment e : enrollmentDao.getAll()) {
            String studentName = (e.getStudent() != null) ? e.getStudent().getName() : "—";
            String courseName  = (e.getCourse()  != null) ? e.getCourse().getName()  : "—";
            tableModel.addRow(new Object[]{e.getId(), studentName, courseName, e.getGrade()});
        }
    }

    private void onRowSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        // Only sync the grade combo — student/course can't be changed after enrolling
        Object grade = tableModel.getValueAt(row, 3);
        gradeCombo.setSelectedItem(grade);
    }

    private long selectedId() {
        int row = table.getSelectedRow();
        return row < 0 ? -1L : (long) tableModel.getValueAt(row, 0);
    }

    private void add() {
        Student student = (Student) studentCombo.getSelectedItem();
        Course  course  = (Course)  courseCombo.getSelectedItem();
        Grade   grade   = (Grade)   gradeCombo.getSelectedItem();

        if (student == null || course == null) {
            JOptionPane.showMessageDialog(this, "Please select a student and a course.");
            return;
        }
        try {
            enrollmentDao.save(student, course, grade);
            refresh();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Enroll failed: " + ex.getMessage());
        }
    }

    private void update() {
        long id = selectedId();
        if (id < 0) { JOptionPane.showMessageDialog(this, "Select an enrollment first."); return; }
        try {
            // Fetch fresh from DB, then only change the grade
            Enrollment enrollment = enrollmentDao.getEnrollmentById(id);
            enrollment.setGrade((Grade) gradeCombo.getSelectedItem());
            enrollmentDao.update(enrollment);
            refresh();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Update failed: " + ex.getMessage());
        }
    }

    private void delete() {
        long id = selectedId();
        if (id < 0) { JOptionPane.showMessageDialog(this, "Select an enrollment first."); return; }
        if (JOptionPane.showConfirmDialog(this, "Remove this enrollment?") != JOptionPane.YES_OPTION) return;
        try {
            // Fetch fresh from DB — the lazy student/course fields will be null
            // on a detached object, which breaks EnrollmentDao.delete()
            Enrollment enrollment = enrollmentDao.getEnrollmentById(id);
            enrollmentDao.delete(enrollment);
            refresh();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Delete failed: " + ex.getMessage());
        }
    }
}