import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DayView extends JFrame{
    private String date;
    private DefaultListModel<Task> taskListModel;
    private JList<Task> taskJList;

    public DayView(String date){
        this.date = date;
        setTitle(date + " Görevleri");
        setSize(400,400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initUI();
        setVisible(true);
    }
    private void initUI(){
        setLayout(new BorderLayout());

        taskListModel = new DefaultListModel<>();
        taskJList = new JList<>(taskListModel);
        taskJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(taskJList);
        add(scrollPane,BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("+ Yeni Görev Ekle");
        JButton editButton = new JButton("Düzenle");
        JButton deleteButton = new JButton("Sil");
        JButton completeButton = new JButton("Tamamlandı");
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(completeButton);
        add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> addTask());
        editButton.addActionListener(e -> editTask());
        deleteButton.addActionListener(e -> deleteTask());
        completeButton.addActionListener(e -> markTaskCompleted());
    }
    private void loadTasks(){
        taskListModel.clear();
        List<Task> tasks = TaskManager.getTasks(date);

        for(Task task : tasks){
            taskListModel.addElement(task);
        }
    }
    private void addTask(){
        JTextField taskField = new JTextField();
        JTextField reminderField = new JTextField();
        Object[] message = {
            "Görev Metni:", taskField,
            "Hatırlatıcı Saati (HH:mm) (Opsiyonel):", reminderField
        };
        int option = JOptionPane.showConfirmDialog(this,message,"Yeni Görev Ekle",JOptionPane.OK_CANCEL_OPTION);

        if(option == JOptionPane.OK_OPTION){
            String text = taskField.getText();
            String reminder = reminderField.getText().trim();

            if(text.isEmpty()){
                JOptionPane.showMessageDialog(this, "Görev metni boş olamaz!");
                return ;
            }
            Task task = new Task(text,false,reminder.isEmpty() ? null : reminder);
            TaskManager.addTask(date,task);
            loadTasks();
        }
    }
    private void editTask(){
        int selectedIndex = taskJList.getSelectedIndex();

        if(selectedIndex == -1){
            JOptionPane.showMessageDialog(this, "Düzenlemek için bir görev seçiniz!");
            return ;
        }
        Task selectedTask = taskListModel.getElementAt(selectedIndex);
        JTextField taskField = new JTextField(selectedTask.getText());
        JTextField reminderField = new JTextField(selectedTask.getReminder() == null ? "" : selectedTask.getReminder());
        Object[] message = {
            "Görev Metni:",taskField,
            "Hatırlatıcı Saati (HH:mm) (Opsiyonel):",reminderField,
        };
        int option = JOptionPane.showConfirmDialog(this,message,"Görevi Düzenle",JOptionPane.OK_CANCEL_OPTION);

        if(option == JOptionPane.OK_OPTION){
            String text = taskField.getText();
            String reminder = reminderField.getText().trim();

            if(text.isEmpty()){
                JOptionPane.showMessageDialog(this, "Görev metni boş olamaz!");
                return ;
            }
            TaskManager.removeTask(date,selectedIndex);
            Task newTask = new Task(text,selectedTask.isCompleted(),reminder.isEmpty() ? null : reminder);
            TaskManager.addTask(date,newTask);
            loadTasks();
        }
    }
    private void deleteTask(){
        int selectedIndex = taskJList.getSelectedIndex();

        if(selectedIndex == -1){
            JOptionPane.showMessageDialog(this,"Silmek için bir görev seçiniz!");
            return ;
        }
        int option = JOptionPane.showConfirmDialog(this, "Görevi silmek istediğinize emin misiniz?","Görev Sil",JOptionPane.YES_NO_OPTION);

        if(option == JOptionPane.YES_OPTION){
            TaskManager.removeTask(date,selectedIndex);
            loadTasks();
        }
    }
    private void markTaskCompleted(){
        int selectedIndex = taskJList.getSelectedIndex();

        if(selectedIndex == -1){
            JOptionPane.showMessageDialog(this, "Tamamlandı olarak işaretlemek için bir görev seçiniz!");
            return ;
        }
        TaskManager.markTaskCompleted(date,selectedIndex,true);
        loadTasks();
    }
}