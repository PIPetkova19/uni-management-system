package ui;

import dao.*;
import model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class EnrollmentPanel extends JPanel {
    private final EnrollmentDao eDao = new EnrollmentDao();

    private final StudentDao sDao = new StudentDao();

    private final CourseDao cDao = new CourseDao();

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Id", "Student", "Course", "Grade"}, 0);

    private final JTable table = new JTable(model);

    private final JTextField sStudent = new JTextField(10),
            sCourse = new JTextField(10);

    private final JComboBox<Student> cbStudent = new JComboBox<>();
    private final JComboBox<Course> cbCourse = new JComboBox<>();
    private final JComboBox<Grade> cbGrade = new JComboBox<>(Grade.values());

    public EnrollmentPanel() {
        setLayout(new BorderLayout(5, 5));

        //north
        JPanel north = new JPanel();

        north.add(new JLabel("Student:"));
        north.add(sStudent);
        north.add(new JLabel("Course:"));
        north.add(sCourse);
        JButton btnSearch = new JButton("Search");
        JButton btnClear = new JButton("Clear");
        north.add(btnSearch);
        north.add(btnClear);

        add(north, BorderLayout.NORTH);

        //center
        add(new JScrollPane(table), BorderLayout.CENTER);

        //south
        JPanel south = new JPanel(new GridLayout(4, 3, 5, 5));
        south.add(new JLabel("Student:"));
        south.add(cbStudent);
        JButton btnAdd = new JButton("Enroll");
        south.add(btnAdd);
        south.add(new JLabel("Course:"));
        south.add(cbCourse);
        JButton btnUpd = new JButton("Update");
        south.add(btnUpd);
        south.add(new JLabel("Grade:"));
        south.add(cbGrade);
        JButton btnDel = new JButton("Remove");
        south.add(btnDel);

        south.add(new JLabel(""));
        south.add(new JLabel(""));

        JButton btnRef = new JButton("Refresh");
        south.add(btnRef);

        add(south, BorderLayout.SOUTH);

        btnSearch.addActionListener(e -> search());
        btnClear.addActionListener(e -> {
            sStudent.setText("");
            sCourse.setText("");
            load();
        });
        btnAdd.addActionListener(e -> {
            eDao.save((Student) cbStudent.getSelectedItem(),
                    (Course) cbCourse.getSelectedItem(),
                    (Grade) cbGrade.getSelectedItem());
            load();
        });
        btnUpd.addActionListener(e -> update());
        btnDel.addActionListener(e -> delete());
        btnRef.addActionListener(e -> fullRefresh());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (table.getSelectedRow() != -1)
                cbGrade.setSelectedItem(model.getValueAt(table.getSelectedRow(), 3));
        });

        fullRefresh();
    }

    private void fullRefresh() {
        cbStudent.removeAllItems();
        sDao.getAll().forEach(cbStudent::addItem);
        cbCourse.removeAllItems();
        cDao.getAll().forEach(cbCourse::addItem);
        load();
    }

    private void load() {
        model.setRowCount(0);
        eDao.getAll().forEach(e -> model.addRow
                (new Object[]{e.getId(), e.getStudent().getName(), e.getCourse().getName(), e.getGrade()}));
    }

    private void search() {
        model.setRowCount(0);
        List<Enrollment> list;
        if (!sStudent.getText().isEmpty() && !sCourse.getText().isEmpty()) {
            list = eDao.getByStuNameAndCourName(sStudent.getText(), sCourse.getText());
        } else if (!sStudent.getText().isEmpty()) {
            list = eDao.getByStudentName(sStudent.getText());
        } else if (!sCourse.getText().isEmpty()) {
            list = eDao.getByCourseName(sCourse.getText());
        } else {
            list = eDao.getAll();
        }
        list.forEach(e -> model.addRow(
                new Object[]{e.getId(), e.getStudent().getName(), e.getCourse().getName(), e.getGrade()}));
    }

    private void update() {
        int r = table.getSelectedRow();
        if (r != -1) {
            Enrollment en = eDao.getEnrollmentById((long) model.getValueAt(r, 0));
            en.setGrade((Grade) cbGrade.getSelectedItem());
            en.setStudent((Student) cbStudent.getSelectedItem());
            en.setCourse((Course) cbCourse.getSelectedItem());
            eDao.update(en);
            load();
        }
    }

    private void delete() {
        int r = table.getSelectedRow();
        if (r != -1) {
            eDao.delete(eDao.getEnrollmentById((long) model.getValueAt(r, 0)));
            load();
        }
    }
}