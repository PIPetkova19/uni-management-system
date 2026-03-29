package ui;

import dao.StudentDao;
import model.Student;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StudentPanel extends JPanel {
    private final StudentDao dao = new StudentDao();
    //Това е "мозъкът" на таблицата. модел на таблица (данните + колоните).
   //0 = няма начални редове
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Id", "Name", "Email", "Faculty Number"}, 0);
    // Създаваш таблица, която използва този модел.
    private final JTable table = new JTable(model);

    // Полета за търсене
    private final JTextField sName = new JTextField(10),
                             sFac = new JTextField(10);
    // Полета за форма
    private final JTextField fName = new JTextField(10),
                             fEmail = new JTextField(10),
                             fFac = new JTextField(10);

    //Изпълнява се при създаване на панела.
    public StudentPanel() {
        //Основен layout:NORTH (горе),CENTER (център),SOUTH (долу),10px разстояние
        setLayout(new BorderLayout(10, 10));

        // СЕВЕР: Търсене
        JPanel north = new JPanel();
        //Label + поле за име
        north.add(new JLabel("Name:"));
        north.add(sName);
        north.add(new JLabel("Faculty Number:"));
        north.add(sFac);
        //Бутони
        JButton btnSearch = new JButton("Search");
        JButton btnClear = new JButton("Clear");
        north.add(btnSearch);
        north.add(btnClear);
        //Слагаш панела горе
        add(north, BorderLayout.NORTH);

        //Таблицата се слага в scroll (може да се скролва)
        add(new JScrollPane(table), BorderLayout.CENTER);

        // ЮГ: Форма
        //Табличен layout:2 реда,4 колони,spacing 5px
        JPanel south = new JPanel(new GridLayout(3, 4, 30, 10));
        //➡️ Поле за име
        south.add(new JLabel("Name:"));
        south.add(fName);
        south.add(new JLabel("Email:"));
        south.add(fEmail);
        south.add(new JLabel("Faculty Number:"));
        south.add(fFac);
        //prazno mqsto
        south.add(new JLabel(""));
        south.add(new JLabel(""));
        //Бутони
        JButton btnAdd = new JButton("Add");
        JButton btnUpd = new JButton("Update");
        JButton btnDel = new JButton("Delete");
        JButton btnRes = new JButton("Reset");
        south.add(btnAdd);
        south.add(btnUpd);
        south.add(btnDel);
        south.add(btnRes);
        add(south, BorderLayout.SOUTH);

        //При клик → извиква search()
        btnSearch.addActionListener(e -> search());
        //Изчиства полетата и зарежда всички данни
        btnClear.addActionListener(e -> {
            sName.setText("");
            sFac.setText("");
            load();
        });
        //Създава нов Student и го записва в DB
        btnAdd.addActionListener(e -> {
            dao.save(new Student(
                    fName.getText(),
                    fEmail.getText(),
                    fFac.getText()));
            load();
        });
        btnUpd.addActionListener(e -> update());
        btnDel.addActionListener(e -> delete());
        btnRes.addActionListener(e -> reset());
        //Когато потребителят избере ред от таблицата → извикай метода fillForm()
        table.getSelectionModel().addListSelectionListener(e -> fillForm());

        load();
    }

//Зарежда всички студенти
    private void load() {
        model.setRowCount(0);//Изчиства таблицата
        dao.getAll().forEach(s -> model.addRow( //➡️ Взима всички студенти ot db
                new Object[]{s.getId(), s.getName(), s.getEmail(), s.getFacNum()})); //Добавя ги като редове
    }

    private void search() {
        model.setRowCount(0);
        List<Student> list;
        //Ако има име → търси по име;ако има факултетен → търси по него;inace vrashta vs
        if (!sName.getText().isEmpty()) {
            list = dao.getByName(sName.getText());
        } else if (!sFac.getText().isEmpty()) {
            list = dao.getByFacNum(sFac.getText());
        } else {
            list=dao.getAll();
        }
        fillForm();

        list.forEach(s -> model.addRow(//➡️ Взима всички студенти ot list
                new Object[]{s.getId(), s.getName(), s.getEmail(), s.getFacNum()}));
    }

    private void update() {
        //Взима избрания ред
        int r = table.getSelectedRow();
        //Ако има избран ред
        if (r != -1) {
            //Взима студента по ID
            Student s = dao.getStudentById((long) model.getValueAt(r, 0));
            //Обновява данните
            s.setName(fName.getText());
            s.setEmail(fEmail.getText());
            s.setFacNum(fFac.getText());
            //zapisva v bd
            dao.update(s);
            load();
        }
    }

    private void delete() {
        int r = table.getSelectedRow();
        if (r != -1) {
            //Изтрива студента
            dao.delete(dao.getStudentById((long) model.getValueAt(r, 0)));
            load();
            //Чисти формата
            reset();
        }
    }

    //Пълни формата от таблицата
    private void fillForm() {
        int r = table.getSelectedRow();
        if (r != -1) {
            fName.setText(model.getValueAt(r, 1).toString());
            fEmail.setText(model.getValueAt(r, 2).toString());
            fFac.setText(model.getValueAt(r, 3).toString());
        }
    }

    private void reset() {
        fName.setText("");
        fEmail.setText("");
        fFac.setText("");
        //Премахва маркирания ред
        table.clearSelection();
    }
}