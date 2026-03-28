package ui;

import dao.StudentDao;
import model.Student;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class StudentPanel extends JPanel {

    private final StudentDao dao = new StudentDao();

    private final String[] COLUMNS = {"ID", "Name", "Email", "Faculty Number"};
    private final DefaultTableModel tableModel = new DefaultTableModel(COLUMNS, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(tableModel);

    // Search fields
    private final JTextField searchName   = new JTextField(15);
    private final JTextField searchFacNum = new JTextField(10);

    // Form fields
    private final JTextField nameField   = new JTextField(10);
    private final JTextField emailField  = new JTextField(10);
    private final JTextField facNumField = new JTextField(10);

    public StudentPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ── Search bar ────────────────────────────────────────────────────
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        searchBar.setBorder(BorderFactory.createTitledBorder("Search"));
        searchBar.add(new JLabel("Name:"));
        searchBar.add(searchName);
        searchBar.add(new JLabel("Faculty Number:"));
        searchBar.add(searchFacNum);
        JButton btnSearch = new JButton("Search");
        JButton btnClearSearch = new JButton("Clear");
        btnSearch.addActionListener(e -> applySearch());
        btnClearSearch.addActionListener(e -> {
            searchName.setText(""); searchFacNum.setText(""); refresh(); });
        searchBar.add(btnSearch);
        searchBar.add(btnClearSearch);
        add(searchBar, BorderLayout.NORTH);

        // ── Table ─────────────────────────────────────────────────────────
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> onRowSelected());
        add(new JScrollPane(table), BorderLayout.CENTER);

        // ── Form + buttons ────────────────────────────────────────────────
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBorder(BorderFactory.createTitledBorder("Student"));

        right.add(new JLabel("Name:"));
        right.add(nameField);
        right.add(Box.createVerticalStrut(6));
        right.add(new JLabel("Email:"));
        right.add(emailField);
        right.add(Box.createVerticalStrut(6));
        right.add(new JLabel("Faculty Number:"));
        right.add(facNumField);
        right.add(Box.createVerticalStrut(12));

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

    // ── Search ────────────────────────────────────────────────────────────

    private void applySearch() {
        String name   = searchName.getText().trim();
        String facNum = searchFacNum.getText().trim();

        List<Student> results;

        if (!name.isEmpty()) {
            results = dao.getByName(name);
        } else if (!facNum.isEmpty()) {
            results = dao.getByFacNum(facNum);
        } else {
            // Both empty — show nothing
            results = new ArrayList<>();
        }

        loadTable(results);
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private void refresh() {
        loadTable(dao.getAll());
    }

    private void loadTable(List<Student> students) {
        tableModel.setRowCount(0);
        for (Student s : students) {
            tableModel.addRow(new Object[]{s.getId(), s.getName(), s.getEmail(), s.getFacNum()});
        }
    }

    private void onRowSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        nameField.setText((String)   tableModel.getValueAt(row, 1));
        emailField.setText((String)  tableModel.getValueAt(row, 2));
        facNumField.setText((String) tableModel.getValueAt(row, 3));
    }

    private void clear() {
        nameField.setText(""); emailField.setText(""); facNumField.setText("");
        table.clearSelection();
    }

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
            refresh(); clear();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Save failed: " + ex.getMessage());
        }
    }

    private void update() {
        long id = selectedId();
        if (id < 0) { JOptionPane.showMessageDialog(this, "Select a student first."); return; }
        try {
            Student s = dao.getStudentById(id);
            s.setName(nameField.getText().trim());
            s.setEmail(emailField.getText().trim());
            s.setFacNum(facNumField.getText().trim());
            dao.update(s);
            refresh(); clear();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Update failed: " + ex.getMessage());
        }
    }

    private void delete() {
        long id = selectedId();
        if (id < 0) { JOptionPane.showMessageDialog(this, "Select a student first."); return; }
        if (JOptionPane.showConfirmDialog(this, "Delete this student?") != JOptionPane.YES_OPTION) return;
        try {
            dao.delete(dao.getStudentById(id));
            refresh(); clear();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Delete failed: " + ex.getMessage());
        }
    }
}