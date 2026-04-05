package ui;

import dao.StudentDao;
import model.Student;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StudentPanel extends JPanel {
    private final StudentDao dao = new StudentDao();

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Id", "Name", "Email", "Faculty Number"}, 0);

    private final JTable table = new JTable(model);

    //search
    private final JTextField sName = new JTextField(10),
            sFacNum = new JTextField(10);

    //form
    private final JTextField fName = new JTextField(10),
            fEmail = new JTextField(10),
            fFacNum = new JTextField(10);

    public StudentPanel() {
        setLayout(new BorderLayout(10, 10));

        //north
        JPanel north = new JPanel();

        north.add(new JLabel("Name:"));
        north.add(sName);
        north.add(new JLabel("Faculty Number:"));
        north.add(sFacNum);

        JButton btnSearch = new JButton("Search");
        JButton btnClear = new JButton("Clear");
        north.add(btnSearch);
        north.add(btnClear);

        add(north, BorderLayout.NORTH);

        //center
        add(new JScrollPane(table), BorderLayout.CENTER);

        //south
        JPanel south = new JPanel(new GridLayout(3, 4, 30, 10));

        south.add(new JLabel("Name:"));
        south.add(fName);
        south.add(new JLabel("Email:"));
        south.add(fEmail);
        south.add(new JLabel("Faculty Number:"));
        south.add(fFacNum);

        south.add(new JLabel(""));
        south.add(new JLabel(""));

        JButton btnAdd = new JButton("Add");
        JButton btnUpd = new JButton("Update");
        JButton btnDel = new JButton("Delete");
        JButton btnRes = new JButton("Reset");

        south.add(btnAdd);
        south.add(btnUpd);
        south.add(btnDel);
        south.add(btnRes);

        add(south, BorderLayout.SOUTH);

        btnSearch.addActionListener(e -> search());

        btnClear.addActionListener(e -> {
            sName.setText("");
            sFacNum.setText("");
            load();
        });

        btnAdd.addActionListener(e -> {
            dao.save(new Student(
                    fName.getText(),
                    fEmail.getText(),
                    fFacNum.getText()));
            load();
        });

        btnUpd.addActionListener(e -> update());
        btnDel.addActionListener(e -> delete());
        btnRes.addActionListener(e -> reset());

        table.getSelectionModel().addListSelectionListener(e -> fillForm());

        load();
    }

    private void load() {
        model.setRowCount(0);
        dao.getAll().forEach(s -> model.addRow(
                new Object[]{s.getId(), s.getName(), s.getEmail(), s.getFacNum()}));
    }

    private void search() {
        model.setRowCount(0);
        List<Student> list;

        if (!sName.getText().isEmpty() && !sFacNum.getText().isEmpty()) {
            list = dao.getByNameAndFac(sName.getText(), sFacNum.getText());
        } else if (!sName.getText().isEmpty()) {
            list = dao.getByName(sName.getText());
        } else if (!sFacNum.getText().isEmpty()) {
            list = dao.getByFacNum(sFacNum.getText());
        } else {
            list = dao.getAll();
        }

        list.forEach(s -> model.addRow(
                new Object[]{s.getId(), s.getName(), s.getEmail(), s.getFacNum()}));
    }

    private void update() {
        int r = table.getSelectedRow();
        if (r != -1) {
            Student s = dao.getStudentById((long) model.getValueAt(r, 0));
            s.setName(fName.getText());
            s.setEmail(fEmail.getText());
            s.setFacNum(fFacNum.getText());
            dao.update(s);
            load();
        }
    }

    private void delete() {
        int r = table.getSelectedRow();
        if (r != -1) {
            dao.delete(dao.getStudentById((long) model.getValueAt(r, 0)));
            load();
            reset();
        }
    }

    private void fillForm() {
        int r = table.getSelectedRow();
        if (r != -1) {
            fName.setText(model.getValueAt(r, 1).toString());
            fEmail.setText(model.getValueAt(r, 2).toString());
            fFacNum.setText(model.getValueAt(r, 3).toString());
        }
    }

    private void reset() {
        fName.setText("");
        fEmail.setText("");
        fFacNum.setText("");
        table.clearSelection();
    }
}