package ui;

import dao.StudentDao;
import model.Student;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StudentPanel extends JPanel {
    private final StudentDao dao = new StudentDao();
    private final DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Name", "Email", "Fac#"}, 0);
    private final JTable table = new JTable(model);

    private final JTextField sName = new JTextField(10), sFac = new JTextField(10);
    private final JTextField fName = new JTextField(10), fEmail = new JTextField(10), fFac = new JTextField(10);

    public StudentPanel() {
        setLayout(new BorderLayout(10, 10));

        // СЕВЕР: Търсене
        JPanel north = new JPanel();
        north.add(new JLabel("Name:")); north.add(sName);
        north.add(new JLabel("Fac#:"));  north.add(sFac);
        JButton btnSearch = new JButton("Search");
        JButton btnClear = new JButton("Clear");
        north.add(btnSearch); north.add(btnClear);
        add(north, BorderLayout.NORTH);

        add(new JScrollPane(table), BorderLayout.CENTER);

        // ЮГ: Форма
        JPanel south = new JPanel(new GridLayout(2, 4, 5, 5));
        south.add(new JLabel("Name:"));  south.add(fName);
        JButton btnAdd = new JButton("Add");
        JButton btnUpd = new JButton("Update");
        south.add(btnAdd); south.add(btnUpd);

        south.add(new JLabel("Email/Fac:"));
        JPanel pair = new JPanel(new GridLayout(1, 2)); pair.add(fEmail); pair.add(fFac);
        south.add(pair);
        JButton btnDel = new JButton("Delete");
        JButton btnRes = new JButton("Reset");
        south.add(btnDel); south.add(btnRes);
        add(south, BorderLayout.SOUTH);

        // Логика
        btnSearch.addActionListener(e -> search());
        btnClear.addActionListener(e -> { sName.setText(""); sFac.setText(""); load(); });
        btnAdd.addActionListener(e -> { dao.save(new Student(fName.getText(), fEmail.getText(), fFac.getText())); load(); });
        btnUpd.addActionListener(e -> update());
        btnDel.addActionListener(e -> delete());
        btnRes.addActionListener(e -> reset());
        table.getSelectionModel().addListSelectionListener(e -> fillForm());

        load();
    }

    private void load() {
        model.setRowCount(0);
        dao.getAll().forEach(s -> model.addRow(new Object[]{s.getId(), s.getName(), s.getEmail(), s.getFacNum()}));
    }

    private void search() {
        model.setRowCount(0);
        List<Student> list = !sName.getText().isEmpty() ? dao.getByName(sName.getText()) :
                !sFac.getText().isEmpty() ? dao.getByFacNum(sFac.getText()) : dao.getAll();
        list.forEach(s -> model.addRow(new Object[]{s.getId(), s.getName(), s.getEmail(), s.getFacNum()}));
    }

    private void update() {
        int r = table.getSelectedRow();
        if (r != -1) {
            Student s = dao.getStudentById((long)model.getValueAt(r, 0));
            s.setName(fName.getText()); s.setEmail(fEmail.getText()); s.setFacNum(fFac.getText());
            dao.update(s); load();
        }
    }

    private void delete() {
        int r = table.getSelectedRow();
        if (r != -1) { dao.delete(dao.getStudentById((long)model.getValueAt(r, 0))); load(); reset(); }
    }

    private void fillForm() {
        int r = table.getSelectedRow();
        if (r != -1) { fName.setText(model.getValueAt(r,1).toString()); fEmail.setText(model.getValueAt(r,2).toString()); fFac.setText(model.getValueAt(r,3).toString()); }
    }

    private void reset() { fName.setText(""); fEmail.setText(""); fFac.setText(""); table.clearSelection(); }
}