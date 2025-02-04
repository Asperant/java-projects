import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Calendar;

public class CalendarApp extends JFrame{
    private Calendar calendar;
    private JPanel calendarPanel;
    private JLabel monthLabel;

    public CalendarApp(){
        calendar = Calendar.getInstance();
        setTitle("Takvim Uygulaması");
        setSize(600,400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
        setVisible(true);
    }
    private void initUI(){
        setLayout(new BorderLayout());
        
        JPanel topPanel = new JPanel();
        JButton prewButton = new JButton("<");
        JButton nextButton = new JButton(">");

        monthLabel = new JLabel();
        topPanel.add(prewButton);
        topPanel.add(monthLabel);
        topPanel.add(nextButton);
        add(topPanel,BorderLayout.NORTH);

        calendarPanel = new JPanel();
        calendarPanel.setLayout(new GridLayout(0,7));
        add(calendarPanel,BorderLayout.CENTER);

        JPanel daysPanel = new JPanel(new GridLayout(0,7));
        String[] days = {"P","S","Ç","P","C","C","P"};

        for(String day : days){
            JLabel lbl = new JLabel(day,SwingConstants.CENTER);
            daysPanel.add(lbl);
        }
        add(daysPanel,BorderLayout.SOUTH);

        prewButton.addActionListener(e -> {
            calendar.add(Calendar.MONTH,-1);
            updateCalendar();
        });
        nextButton.addActionListener(e -> {
            calendar.add(Calendar.MONTH,1);
            updateCalendar();
        });
        updateCalendar();
    }
    private void updateCalendar(){
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        String[] months = {"Ocak","Şubat","Mart","Nisan","Mayıs","Haziran","Temmuz","Ağustos","Eylül","Ekim","Kasım","Aralık"};
        monthLabel.setText(months[month] + " " + year);

        calendarPanel.removeAll();

        Calendar cal = (Calendar) calendar.clone();
        cal.set(Calendar.DAY_OF_MONTH,1);
        int firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        int offset = (firstDayOfWeek + 5) % 7;

        for(int i=0;i<offset;i++){
            calendarPanel.add(new JLabel(""));
        }
        int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        for(int day=1;day <= maxDay;day++){
            JButton dayButton = new JButton(String.valueOf(day));

            dayButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e){
                    int selectedDay = Integer.parseInt(dayButton.getText());
                    String dateStr = String.format("%d-%02d-%02d",
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) +1,
                    selectedDay);
                    new DayView(dateStr);
                }
            });
            calendarPanel.add(dayButton);
        }
        calendarPanel.revalidate();
        calendarPanel.repaint();
    }
    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> new CalendarApp());
    }
}