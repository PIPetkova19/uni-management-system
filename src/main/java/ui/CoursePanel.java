package ui;

import dao.AcademicStaffDao;
import dao.CourseDao;
import model.AcademicStaff;
import model.Course;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CoursePanel extends JPanel {

    private final CourseDao        courseDao = new CourseDao();
    private final AcademicStaffDao staffDao  = new AcademicStaffDao();

    private final String[] COLUMNS = {"ID", "Course Name", "Instructor"};
    private final DefaultTableModel tableModel = new DefaultTableModel(COLUMNS, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(tableModel);

    // Search fields
    private final JTextField searchName       = new JTextField(15);
    private final JTextField searchInstructor = new JTextField(15);

    // Form fields
    private final JTextField nameField = new JTextField(20);
    private final JComboBox<AcademicStaff> staffCombo = new JComboBox<>();

    public CoursePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ── Search bar ────────────────────────────────────────────────────
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        searchBar.setBorder(BorderFactory.createTitledBorder("Search"));
        searchBar.add(new JLabel("Course Name:"));
        searchBar.add(searchName);
        searchBar.add(new JLabel("Instructor:"));
        searchBar.add(searchInstructor);
        JButton btnSearch      = new JButton("Search");
        JButton btnClearSearch = new JButton("Clear");
        btnSearch.addActionListener(e      -> applySearch());
        btnClearSearch.addActionListener(e -> { searchName.setText(""); searchInstructor.setText(""); refresh(); });
        searchBar.add(btnSearch);
        searchBar.add(btnClearSearch);
        add(searchBar, BorderLayout.NORTH);

        // ── Table ─────────────────────────────────────────────────────────
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> onRowSelected());
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Show staff name in the dropdown
        staffCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof AcademicStaff a) setText(a.getName());
                else setText("-- none --");
                return this;
            }
        });

        // ── Form + buttons ────────────────────────────────────────────────
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBorder(BorderFactory.createTitledBorder("Course"));

        right.add(new JLabel("Course Name:"));
        right.add(nameField);
        right.add(Box.createVerticalStrut(6));
        right.add(new JLabel("Instructor:"));
        right.add(staffCombo);
        right.add(Box.createVerticalStrut(12));

        JButton btnAdd     = new JButton("Add");
        JButton btnUpdate  = new JButton("Update");
        JButton btnDelete  = new JButton("Delete");
        JButton btnClear   = new JButton("Clear");
        JButton btnRefresh = new JButton("Refresh Lists");

        btnAdd.addActionListener(e     -> add());
        btnUpdate.addActionListener(e  -> update());
        btnDelete.addActionListener(e  -> delete());
        btnClear.addActionListener(e   -> clear());
        btnRefresh.addActionListener(e -> { loadStaffCombo(); refresh(); });

        for (JButton b : new JButton[]{btnAdd, btnUpdate, btnDelete, btnClear, btnRefresh}) {
            b.setAlignmentX(Component.LEFT_ALIGNMENT);
            right.add(b);
            right.add(Box.createVerticalStrut(4));
        }

        add(right, BorderLayout.EAST);
        loadStaffCombo();
        refresh();
    }

    // ── Search ────────────────────────────────────────────────────────────

    private void applySearch() {
        String name       = searchName.getText().trim();
        String instructor = searchInstructor.getText().trim();

        List<Course> results;

        if (!name.isEmpty()) {
            results = courseDao.getByName(name);
        } else if (!instructor.isEmpty()) {
            results = courseDao.getByInstructor(instructor);
        } else {
            results = new ArrayList<>();
        }

        loadTable(results);
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    public void loadStaffCombo() {
        staffCombo.removeAllItems();
        staffCombo.addItem(null);
        for (AcademicStaff a : staffDao.getAll()) staffCombo.addItem(a);
    }

    public void refresh() {
        loadTable(courseDao.getAll());
    }

    private void loadTable(List<Course> courses) {
        tableModel.setRowCount(0);
        for (Course c : courses) {
            String instructor = (c.getAcademicStaff() != null) ? c.getAcademicStaff().getName() : "—";
            tableModel.addRow(new Object[]{c.getId(), c.getName(), instructor});
        }
    }

    private void onRowSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        nameField.setText((String) tableModel.getValueAt(row, 1));
        String instructorName = (String) tableModel.getValueAt(row, 2);
        for (int i = 0; i < staffCombo.getItemCount(); i++) {
            AcademicStaff a = staffCombo.getItemAt(i);
            if (a != null && a.getName().equals(instructorName)) {
                staffCombo.setSelectedIndex(i);
                return;
            }
        }
        staffCombo.setSelectedIndex(0);
    }

    private void clear() {
        nameField.setText("");
        staffCombo.setSelectedIndex(0);
        table.clearSelection();
    }

    private long selectedId() {
        int row = table.getSelectedRow();
        return row < 0 ? -1L : (long) tableModel.getValueAt(row, 0);
    }

    // ── CRUD ──────────────────────────────────────────────────────────────

    private void add() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) { JOptionPane.showMessageDialog(this, "Enter a course name."); return; }
        AcademicStaff staff = (AcademicStaff) staffCombo.getSelectedItem();
        try {
            courseDao.save(new Course(name, staff));
            refresh(); clear();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Save failed: " + ex.getMessage());
        }
    }

    private void update() {
        long id = selectedId();
        if (id < 0) { JOptionPane.showMessageDialog(this, "Select a course first."); return; }
        try {
            Course c = courseDao.getCourseById(id);
            c.setName(nameField.getText().trim());
            c.setAcademicStaff((AcademicStaff) staffCombo.getSelectedItem());
            courseDao.update(c);
            refresh(); clear();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Update failed: " + ex.getMessage());
        }
    }

    private void delete() {
        long id = selectedId();
        if (id < 0) { JOptionPane.showMessageDialog(this, "Select a course first."); return; }
        if (JOptionPane.showConfirmDialog(this, "Delete this course?") != JOptionPane.YES_OPTION) return;
        try {
            courseDao.delete(courseDao.getCourseById(id));
            refresh(); clear();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Delete failed: " + ex.getMessage());
        }
    }
}