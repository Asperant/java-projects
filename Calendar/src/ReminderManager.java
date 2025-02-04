import java.awt.*;
import java.awt.TrayIcon.MessageType;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import javazoom.jl.player.Player;

public class ReminderManager {
    private static Timer timer = new Timer();
    
    public static void scheduleReminder(String date,String timeStr,String taskText){
        try{
            String[] dateParts = date.split("-");
            int year = Integer.parseInt(dateParts[0]);
            int month = Integer.parseInt(dateParts[1]) -1;
            int day = Integer.parseInt(dateParts[2]);

            String[] timeParts = timeStr.split(":");
            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);

            Calendar reminderTime = Calendar.getInstance();
            reminderTime.set(Calendar.YEAR, year);
            reminderTime.set(Calendar.MONTH, month);
            reminderTime.set(Calendar.DAY_OF_MONTH, day);
            reminderTime.set(Calendar.HOUR_OF_DAY, hour);
            reminderTime.set(Calendar.MINUTE, minute);
            reminderTime.set(Calendar.SECOND, 0);
            reminderTime.set(Calendar.MILLISECOND, 0);

            Date now = new Date();
            Date scheduledTime = reminderTime.getTime();

            if(scheduledTime.before(now)){
                System.out.println("Hatırlatıcı zamanı geçmiş. Hatırlatıcı zamanlanmadı.");
                return ;
            }
            timer.schedule(new TimerTask() {
                @Override
                public void run(){
                    playSound("notification.mp3");
                    showNotification("Hatırlatıcı",taskText);
                }                
            }, scheduledTime);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    private static void playSound(String filename){
        try{
            FileInputStream fis = new FileInputStream(filename);
            BufferedInputStream bis = new BufferedInputStream(fis);
            Player player = new Player(bis);

            new Thread(() -> {
                try{
                    player.play();
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }).start();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    private static void showNotification(String title, String message){
        if(SystemTray.isSupported()){
            try{
                SystemTray tray = SystemTray.getSystemTray();
                Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
                TrayIcon trayIcon = new TrayIcon(image, "Hatırlatıcı");
                trayIcon.setImageAutoSize(true);
                trayIcon.setToolTip("Hatırlatıcı");
                tray.add(trayIcon);
                trayIcon.displayMessage(title, message, MessageType.INFO);

                Thread.sleep(5000);
                tray.remove(trayIcon);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        else{
            System.out.println("Sistem tepkisi desteklenmiyor");
        }
    }
}