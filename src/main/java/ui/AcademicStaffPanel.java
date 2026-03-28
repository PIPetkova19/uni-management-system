package ui;

import dao.AcademicStaffDao;
import model.AcademicStaff;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AcademicStaffPanel extends JPanel {

    private final AcademicStaffDao dao = new AcademicStaffDao();

    private final String[] COLUMNS = {"ID", "Name", "Title", "Email"};
    private final DefaultTableModel tableModel = new DefaultTableModel(COLUMNS, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(tableModel);

    // Search fields
    private final JTextField searchName  = new JTextField(15);
    private final JTextField searchEmail = new JTextField(15);

    // Form fields
    private final JTextField nameField  = new JTextField(20);
    private final JTextField titleField = new JTextField(20);
    private final JTextField emailField = new JTextField(20);

    public AcademicStaffPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ── Search bar ────────────────────────────────────────────────────
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        searchBar.setBorder(BorderFactory.createTitledBorder("Search"));
        searchBar.add(new JLabel("Name:"));
        searchBar.add(searchName);
        searchBar.add(new JLabel("Email:"));
        searchBar.add(searchEmail);
        JButton btnSearch      = new JButton("Search");
        JButton btnClearSearch = new JButton("Clear");
        btnSearch.addActionListener(e      -> applySearch());
        btnClearSearch.addActionListener(e -> { searchName.setText(""); searchEmail.setText(""); refresh(); });
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
        right.setBorder(BorderFactory.createTitledBorder("Academic Staff"));

        right.add(new JLabel("Name:"));
        right.add(nameField);
        right.add(Box.createVerticalStrut(6));
        right.add(new JLabel("Title (e.g. Professor):"));
        right.add(titleField);
        right.add(Box.createVerticalStrut(6));
        right.add(new JLabel("Email:"));
        right.add(emailField);
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
        String name  = searchName.getText().trim();
        String email = searchEmail.getText().trim();

        List<AcademicStaff> results;

        if (!name.isEmpty()) {
            results = dao.getByName(name);
        } else if (!email.isEmpty()) {
            results = dao.getByEmail(email);
        } else {
            results = new ArrayList<>();
        }

        loadTable(results);
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private void refresh() {
        loadTable(dao.getAll());
    }

    private void loadTable(List<AcademicStaff> list) {
        tableModel.setRowCount(0);
        for (AcademicStaff a : list) {
            tableModel.addRow(new Object[]{a.getId(), a.getName(), a.getTitle(), a.getEmail()});
        }
    }

    private void onRowSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        nameField.setText((String)  tableModel.getValueAt(row, 1));
        titleField.setText((String) tableModel.getValueAt(row, 2));
        emailField.setText((String) tableModel.getValueAt(row, 3));
    }

    private void clear() {
        nameField.setText(""); titleField.setText(""); emailField.setText("");
        table.clearSelection();
    }

    private long selectedId() {
        int row = table.getSelectedRow();
        return row < 0 ? -1L : (long) tableModel.getValueAt(row, 0);
    }

    // ── CRUD ──────────────────────────────────────────────────────────────

    private void add() {
        String name  = nameField.getText().trim();
        String title = titleField.getText().trim();
        String email = emailField.getText().trim();
        if (name.isEmpty() || title.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }
        try {
            dao.save(new AcademicStaff(name, title, email));
            refresh(); clear();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Save failed: " + ex.getMessage());
        }
    }

    private void update() {
        long id = selectedId();
        if (id < 0) { JOptionPane.showMessageDialog(this, "Select a staff member first."); return; }
        try {
            AcademicStaff a = dao.getStaffById(id);
            a.setName(nameField.getText().trim());
            a.setTitle(titleField.getText().trim());
            a.setEmail(emailField.getText().trim());
            dao.update(a);
            refresh(); clear();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Update failed: " + ex.getMessage());
        }
    }

    private void delete() {
        long id = selectedId();
        if (id < 0) { JOptionPane.showMessageDialog(this, "Select a staff member first."); return; }
        if (JOptionPane.showConfirmDialog(this, "Delete this staff member?") != JOptionPane.YES_OPTION) return;
        try {
            dao.delete(dao.getStaffById(id));
            refresh(); clear();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Delete failed: " + ex.getMessage());
        }
    }
}