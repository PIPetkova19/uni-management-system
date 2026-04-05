package ui;

import dao.*;
import model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CoursePanel extends JPanel {
    private final CourseDao cDao = new CourseDao();

    private final AcademicStaffDao asDao = new AcademicStaffDao();

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Id", "Course", "Academic Staff"}, 0);

    private final JTable table = new JTable(model);

    private final JTextField sName = new JTextField(10),
            sAcademicStaff = new JTextField(10),
            fName = new JTextField(10);

    private final JComboBox<AcademicStaff> cbAcademicStaff = new JComboBox<>();

    public CoursePanel() {
        setLayout(new BorderLayout(10, 10));

        //north
        JPanel north = new JPanel();
        north.add(new JLabel("Course:"));
        north.add(sName);
        north.add(new JLabel("Academic Staff:"));
        north.add(sAcademicStaff);

        JButton btnSearch = new JButton("Search");
        JButton btnClear = new JButton("Clear");
        north.add(btnSearch);
        north.add(btnClear);

        add(north, BorderLayout.NORTH);

        //center
        add(new JScrollPane(table), BorderLayout.CENTER);

        //south
        JPanel south = new JPanel(new GridLayout(2, 4, 5, 5));
        south.add(new JLabel("Course Name:"));
        south.add(fName);
        JButton btnAdd = new JButton("Add");
        JButton btnUpd = new JButton("Update");
        south.add(btnAdd);
        south.add(btnUpd);

        south.add(new JLabel("Academic Staff:"));
        south.add(cbAcademicStaff);

        JButton btnDel = new JButton("Delete");
        JButton btnRef = new JButton("Refresh");
        south.add(btnDel);
        south.add(btnRef);

        add(south, BorderLayout.SOUTH);

        btnSearch.addActionListener(e -> search());
        btnClear.addActionListener(e -> {
                    sName.setText("");
                    sAcademicStaff.setText("");
                    load();
                }
        );
        btnAdd.addActionListener(e -> {
            cDao.save(new Course(fName.getText(),
                    (AcademicStaff) cbAcademicStaff.getSelectedItem()));
            load();
        });
        btnUpd.addActionListener(e -> update());
        btnDel.addActionListener(e -> delete());
        btnRef.addActionListener(e -> fullRefresh());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (table.getSelectedRow() != -1)
                fName.setText(model.getValueAt(table.getSelectedRow(), 1).toString());
        });

        fullRefresh();
    }

    private void fullRefresh() {
        cbAcademicStaff.removeAllItems();
        asDao.getAll().forEach(cbAcademicStaff::addItem);
        load();
    }

    private void load() {
        model.setRowCount(0);
        cDao.getAll().forEach(c -> model.addRow(
                new Object[]{c.getId(), c.getName(), c.getAcademicStaff().getName()}));
    }

    private void search() {
        model.setRowCount(0);
        List<Course> list;
        if (!sName.getText().isEmpty() && !sAcademicStaff.getText().isEmpty()) {
            list = cDao.getByNameAndAcademicStaff(sName.getText(), sAcademicStaff.getText());
        } else if (!sName.getText().isEmpty()) {
            list = cDao.getByName(sName.getText());
        } else if (!sAcademicStaff.getText().isEmpty()) {
            list = cDao.getByAcademicStaff(sAcademicStaff.getText());
        } else {
            list = cDao.getAll();
        }
        list.forEach(c -> model.addRow(
                new Object[]{c.getId(), c.getName(), c.getAcademicStaff().getName()}));
    }

    private void update() {
        int r = table.getSelectedRow();
        if (r != -1) {
            Course c = cDao.getCourseById((long) model.getValueAt(r, 0));
            c.setName(fName.getText());
            c.setAcademicStaff((AcademicStaff) cbAcademicStaff.getSelectedItem());
            cDao.update(c);
            load();
        }
    }

    private void delete() {
        int r = table.getSelectedRow();
        if (r != -1) {
            cDao.delete(cDao.getCourseById((long) model.getValueAt(r, 0)));
            load();
        }
    }
}