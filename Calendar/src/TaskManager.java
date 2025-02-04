import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class TaskManager {
    public static List<Task> getTasks(String date){
        JSONObject data = DataHandler.loadData();
        List<Task> tasks = new ArrayList<>();

        if(data.has(date)){
            JSONObject dayData = data.getJSONObject(date);
            
            if(dayData.has("tasks")){
                JSONArray tasksArray = dayData.getJSONArray("tasks");

                for(int i=0;i<tasksArray.length();i++){
                    JSONObject taskObj = tasksArray.getJSONObject(i);
                    tasks.add(Task.fromJSON(taskObj));
                }
            }
        }
        return tasks;
    }
    public static void addTask(String date,Task task){
        JSONObject data = DataHandler.loadData();
        JSONObject dayData;

        if(data.has(date)){
            dayData = data.getJSONObject(date);
        }
        else{
            dayData = new JSONObject();
            dayData.put("tasks", new JSONArray());
            dayData.put("notes",new JSONArray());
            data.put(date,dayData);
        }
        JSONArray tasksArray = dayData.getJSONArray("tasks");
        tasksArray.put(task.toJSON());
        DataHandler.saveData(data);

        if(task.getReminder() != null){
            ReminderManager.scheduleReminder(date, task.getReminder(), task.getText());
        }
    }
    public static void removeTask(String date,int index){
        JSONObject data = DataHandler.loadData();

        if(data.has(date)){
            JSONObject dayData = data.getJSONObject(date);

            if(dayData.has("tasks")){
                JSONArray tasksArray = dayData.getJSONArray("tasks");

                if(index >= 0 && index < tasksArray.length()){
                    tasksArray.remove(index);
                    DataHandler.saveData(data);
                }
            }
        }
    }
    public static void markTaskCompleted(String date,int index,boolean completed){
        JSONObject data = DataHandler.loadData();

        if(data.has(date)){
            JSONObject dayData = data.getJSONObject(date);

            if(dayData.has("tasks")){
                JSONArray tasksArray = dayData.getJSONArray("tasks");

                if(index >= 0 && index < tasksArray.length()){
                    JSONObject taskObj = tasksArray.getJSONObject(index);
                    taskObj.put("Completed",completed);
                    DataHandler.saveData(data);
                }
            }
        }
    }
}