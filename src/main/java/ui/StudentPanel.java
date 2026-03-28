package ui;

import dao.StudentDao;
import model.Student;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StudentPanel extends JPanel {

    // ── DAO ───────────────────────────────────────────────────────────────
    private final StudentDao dao = new StudentDao();

    // ── Table ─────────────────────────────────────────────────────────────
    private final String[] COLUMNS = {"ID", "Name", "Email", "Faculty №"};
    private final DefaultTableModel tableModel = new DefaultTableModel(COLUMNS, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(tableModel);

    // ── Input fields ──────────────────────────────────────────────────────
    private final JTextField nameField   = new JTextField(20);
    private final JTextField emailField  = new JTextField(20);
    private final JTextField facNumField = new JTextField(20);

    public StudentPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Table (left / center)
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> onRowSelected());
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Right panel: form + buttons
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBorder(BorderFactory.createTitledBorder("Student"));

        right.add(new JLabel("Name:"));
        right.add(nameField);
        right.add(Box.createVerticalStrut(6));

        right.add(new JLabel("Email:"));
        right.add(emailField);
        right.add(Box.createVerticalStrut(6));

        right.add(new JLabel("Faculty №:"));
        right.add(facNumField);
        right.add(Box.createVerticalStrut(12));

        // Buttons
        JButton btnAdd    = new JButton("Add");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnClear  = new JButton("Clear");

        btnAdd.addActionListener(e    -> add());
        btnUpdate.addActionListener(e -> update());
        btnDelete.addActionListener(e -> delete());
        btnClear.addActionListener(e  -> clear());

        for (JButton b : new JButton[]{btnAdd, btnUpdate, btnDelete, btnClear}) {
            b.setAlignmentX(Component.LEFT_ALIGNMENT);
            right.add(b);
            right.add(Box.createVerticalStrut(4));
        }

        add(right, BorderLayout.EAST);
        refresh();
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    /** Reload all rows from the database. */
    private void refresh() {
        tableModel.setRowCount(0);
        for (Student s : dao.getAll()) {
            tableModel.addRow(new Object[]{s.getId(), s.getName(), s.getEmail(), s.getFacNum()});
        }
    }

    /** Fill form when user clicks a table row. */
    private void onRowSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        nameField.setText((String) tableModel.getValueAt(row, 1));
        emailField.setText((String) tableModel.getValueAt(row, 2));
        facNumField.setText((String) tableModel.getValueAt(row, 3));
    }

    /** Reset form and table selection. */
    private void clear() {
        nameField.setText("");
        emailField.setText("");
        facNumField.setText("");
        table.clearSelection();
    }

    /** Get the ID of the selected row, or -1 if nothing selected. */
    private long selectedId() {
        int row = table.getSelectedRow();
        if (row < 0) return -1L;
        return (long) tableModel.getValueAt(row, 0);
    }

    // ── CRUD ──────────────────────────────────────────────────────────────

    private void add() {
        String name   = nameField.getText().trim();
        String email  = emailField.getText().trim();
        String facNum = facNumField.getText().trim();

        if (name.isEmpty() || email.isEmpty() || facNum.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }
        try {
            dao.save(new Student(name, email, facNum));
            refresh();
            clear();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Save failed: " + ex.getMessage());
        }
    }

    private void update() {
        long id = selectedId();
        if (id < 0) { JOptionPane.showMessageDialog(this, "Select a student first."); return; }

        try {
            // Always fetch fresh from DB before modifying
            Student s = dao.getStudentById(id);
            s.setName(nameField.getText().trim());
            s.setEmail(emailField.getText().trim());
            s.setFacNum(facNumField.getText().trim());
            dao.update(s);
            refresh();
            clear();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Update failed: " + ex.getMessage());
        }
    }

    private void delete() {
        long id = selectedId();
        if (id < 0) { JOptionPane.showMessageDialog(this, "Select a student first."); return; }

        int confirm = JOptionPane.showConfirmDialog(this, "Delete this student?");
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            // Fetch fresh from DB — passing a detached object causes errors
            Student s = dao.getStudentById(id);
            dao.delete(s);
            refresh();
            clear();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Delete failed: " + ex.getMessage());
        }
    }
}