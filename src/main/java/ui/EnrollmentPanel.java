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

    private final JTextField sStud = new JTextField(10),
                            sCour = new JTextField(10);
    private final JComboBox<Student> cbStud = new JComboBox<>();
    private final JComboBox<Course> cbCour = new JComboBox<>();
    private final JComboBox<Grade> cbGrad = new JComboBox<>(Grade.values());

    public EnrollmentPanel() {
        setLayout(new BorderLayout(5, 5));

        JPanel north = new JPanel();

        north.add(new JLabel("Student:"));
        north.add(sStud);
        north.add(new JLabel("Course:"));
        north.add(sCour);
        JButton btnSearch = new JButton("Search");
        JButton btnClear = new JButton("Clear");
        north.add(btnSearch);
        north.add(btnClear);

        add(north, BorderLayout.NORTH);

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel south = new JPanel(new GridLayout(4, 3, 5, 5));
        south.add(new JLabel("Student:"));
        south.add(cbStud);
        JButton btnAdd = new JButton("Enroll");
        south.add(btnAdd);
        south.add(new JLabel("Course:"));
        south.add(cbCour);
        JButton btnUpd = new JButton("Update");
        south.add(btnUpd);
        south.add(new JLabel("Grade:"));
        south.add(cbGrad);
        JButton btnDel = new JButton("Remove");
        south.add(btnDel);

        south.add(new JLabel(""));
        south.add(new JLabel(""));

        JButton btnRef = new JButton("Refresh");
        south.add(btnRef);

        add(south, BorderLayout.SOUTH);

        btnSearch.addActionListener(e -> search());
        btnClear.addActionListener(e -> {
            sStud.setText("");
            sCour.setText("");
            load();
        });

        btnAdd.addActionListener(e -> {
            eDao.save((Student)cbStud.getSelectedItem(),
                    (Course)cbCour.getSelectedItem(),
                    (Grade)cbGrad.getSelectedItem());
            load();
        });

        btnUpd.addActionListener(e -> update());

        btnDel.addActionListener(e -> delete());

        btnRef.addActionListener(e -> fullRefresh());

        table.getSelectionModel().addListSelectionListener(e -> {
            if(table.getSelectedRow() != -1)
                cbGrad.setSelectedItem(model.getValueAt(table.getSelectedRow(), 3));
        });

        fullRefresh();
    }

    private void fullRefresh() {
        cbStud.removeAllItems();
        sDao.getAll().forEach(cbStud::addItem);
        cbCour.removeAllItems();
        cDao.getAll().forEach(cbCour::addItem);
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
        if (!sStud.getText().isEmpty()) {
            list = eDao.getByStudentName(sStud.getText());
        } else if (!sCour.getText().isEmpty()) {
            list = eDao.getByCourseName(sCour.getText());
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
            en.setGrade((Grade) cbGrad.getSelectedItem());
            en.setStudent((Student) cbStud.getSelectedItem());
            en.setCourse((Course) cbCour.getSelectedItem());
            eDao.update(en);
            load();
        }
    }

    private void delete() {
        int r = table.getSelectedRow();
        if (r != -1) {
            long id = (long) model.getValueAt(r, 0);
            if (JOptionPane.showConfirmDialog(this,
                    "Remove this enrollment?") == JOptionPane.YES_OPTION) {
                eDao.delete(eDao.getEnrollmentById(id));
                load();
            }
        }
    }
}