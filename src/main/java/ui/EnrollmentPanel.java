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
import java.util.ArrayList;
import java.util.List;

public class EnrollmentPanel extends JPanel {

    private final EnrollmentDao enrollmentDao = new EnrollmentDao();
    private final StudentDao    studentDao    = new StudentDao();
    private final CourseDao     courseDao     = new CourseDao();

    private final String[] COLUMNS = {"ID", "Student", "Course", "Grade"};
    private final DefaultTableModel tableModel = new DefaultTableModel(COLUMNS, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(tableModel);

    // Search fields
    private final JTextField searchStudent = new JTextField(15);
    private final JTextField searchCourse  = new JTextField(15);

    // Form fields
    private final JComboBox<Student> studentCombo = new JComboBox<>();
    private final JComboBox<Course>  courseCombo  = new JComboBox<>();
    private final JComboBox<Grade>   gradeCombo   = new JComboBox<>(Grade.values());

    public EnrollmentPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ── Search bar ────────────────────────────────────────────────────
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        searchBar.setBorder(BorderFactory.createTitledBorder("Search"));
        searchBar.add(new JLabel("Student Name:"));
        searchBar.add(searchStudent);
        searchBar.add(new JLabel("Course Name:"));
        searchBar.add(searchCourse);
        JButton btnSearch      = new JButton("Search");
        JButton btnClearSearch = new JButton("Clear");
        btnSearch.addActionListener(e      -> applySearch());
        btnClearSearch.addActionListener(e -> { searchStudent.setText(""); searchCourse.setText(""); refresh(); });
        searchBar.add(btnSearch);
        searchBar.add(btnClearSearch);
        add(searchBar, BorderLayout.NORTH);

        // ── Table ─────────────────────────────────────────────────────────
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

        // ── Form + buttons ────────────────────────────────────────────────
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

        JButton btnAdd     = new JButton("Enroll");
        JButton btnUpdate  = new JButton("Update Grade");
        JButton btnDelete  = new JButton("Remove");
        JButton btnRefresh = new JButton("Refresh Lists");

        btnAdd.addActionListener(e     -> add());
        btnUpdate.addActionListener(e  -> update());
        btnDelete.addActionListener(e  -> delete());
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

    // ── Search ────────────────────────────────────────────────────────────

    private void applySearch() {
        String student = searchStudent.getText().trim();
        String course  = searchCourse.getText().trim();

        List<Enrollment> results;

        if (!student.isEmpty()) {
            results = enrollmentDao.getByStudentName(student);
        } else if (!course.isEmpty()) {
            results = enrollmentDao.getByCourseName(course);
        } else {
            results = new ArrayList<>();
        }

        loadTable(results);
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private void loadCombos() {
        studentCombo.removeAllItems();
        for (Student s : studentDao.getAll()) studentCombo.addItem(s);

        courseCombo.removeAllItems();
        for (Course c : courseDao.getAll()) courseCombo.addItem(c);
    }

    private void refresh() {
        loadTable(enrollmentDao.getAll());
    }

    private void loadTable(List<Enrollment> enrollments) {
        tableModel.setRowCount(0);
        for (Enrollment e : enrollments) {
            String studentName = (e.getStudent() != null) ? e.getStudent().getName() : "—";
            String courseName  = (e.getCourse()  != null) ? e.getCourse().getName()  : "—";
            tableModel.addRow(new Object[]{e.getId(), studentName, courseName, e.getGrade()});
        }
    }

    private void onRowSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        gradeCombo.setSelectedItem(tableModel.getValueAt(row, 3));
    }

    private long selectedId() {
        int row = table.getSelectedRow();
        return row < 0 ? -1L : (long) tableModel.getValueAt(row, 0);
    }

    // ── CRUD ──────────────────────────────────────────────────────────────

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
            enrollmentDao.delete(enrollmentDao.getEnrollmentById(id));
            refresh();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Delete failed: " + ex.getMessage());
        }
    }
}