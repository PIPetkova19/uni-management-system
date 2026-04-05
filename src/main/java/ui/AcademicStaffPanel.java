package ui;

import dao.AcademicStaffDao;
import model.AcademicStaff;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AcademicStaffPanel extends JPanel {
    private final AcademicStaffDao dao = new AcademicStaffDao();

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Id", "Name", "Title", "Email"}, 0);

    private final JTable table = new JTable(model);

    private final JTextField sName = new JTextField(10),
                            sEmail = new JTextField(10);

    private final JTextField fName = new JTextField(10),
                            fTitle = new JTextField(10),
                            fEmail = new JTextField(10);

    public AcademicStaffPanel() {
        setLayout(new BorderLayout(10, 10));

        //north
        JPanel north = new JPanel();

        north.add(new JLabel("Name:"));
        north.add(sName);
        north.add(new JLabel("Email:"));
        north.add(sEmail);

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
        south.add(new JLabel("Title:"));
        south.add(fTitle);
        south.add(new JLabel("Email:"));
        south.add(fEmail);

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
                sEmail.setText("");
                load();
        });
        btnAdd.addActionListener(e -> {
                dao.save(new AcademicStaff(fName.getText(),
                                            fTitle.getText(),
                                            fEmail.getText()));
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
        dao.getAll().forEach(a -> model.addRow(
                new Object[]{a.getId(), a.getName(), a.getTitle(), a.getEmail()})
        );
    }

    private void search() {
        model.setRowCount(0);
        List<AcademicStaff> list;
        if (!sName.getText().isEmpty() && !sEmail.getText().isEmpty()) {
            list = dao.getByNameAndEmail(sName.getText(), sEmail.getText());
        } else if (!sName.getText().isEmpty()) {
            list = dao.getByName(sName.getText());
        } else if (!sEmail.getText().isEmpty()) {
            list = dao.getByEmail(sEmail.getText());
        } else {
            list = dao.getAll();
        }
        list.forEach(a -> model.addRow(
                new Object[]{a.getId(), a.getName(), a.getTitle(), a.getEmail()})
        );
    }

    private void update() {
        int r = table.getSelectedRow();
        if (r != -1) {
            AcademicStaff a = dao.getStaffById((long)model.getValueAt(r, 0));
            a.setName(fName.getText()); a.setTitle(fTitle.getText()); a.setEmail(fEmail.getText());
            dao.update(a);
            load();
        }
    }

    private void delete() {
        int r = table.getSelectedRow();
        if (r != -1) {
            dao.delete(dao.getStaffById((long)model.getValueAt(r, 0)));
            load();
            reset();
        }
    }

    private void fillForm() {
        int r = table.getSelectedRow();
        if (r != -1) { fName.setText(model.getValueAt(r,1).toString());
            fTitle.setText(model.getValueAt(r,2).toString());
            fEmail.setText(model.getValueAt(r,3).toString());
        }
    }

    private void reset() {
        fName.setText("");
        fTitle.setText("");
        fEmail.setText("");
        table.clearSelection();
    }
}