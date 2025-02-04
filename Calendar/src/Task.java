import org.json.JSONObject;

public class Task {
    private String text;
    private boolean completed;
    private String reminder;

    public Task(String text,boolean completed, String reminder){
        this.text = text;
        this.completed = completed;
        this.reminder = reminder;
    }
    public Task(String text){
        this(text,false,null);
    }
    public String getText() { return text ;}
    public void setTex(String text) { this.text = text; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public String getReminder() { return reminder; }
    public void setReminder(String reminder) { this.reminder = reminder; }

    public JSONObject toJSON(){
        JSONObject obj = new JSONObject();
        obj.put("text",text);
        obj.put("completed", completed);

        if(reminder != null){
            obj.put("reminder",reminder);
        }
        return obj;
    }
    public static Task fromJSON(JSONObject obj){
        String text = obj.getString("text");
        boolean completed = obj.getBoolean("completed");
        String reminder = obj.has("reminder") ? obj.getString("reminder") : null;
        return new Task(text,completed,reminder);
    }
    @Override
    public String toString(){
        return (completed ? "✅ " : "❌ ") + text + (reminder != null ? " ⏰ Hatırlatıcı: " + reminder : "");
    }
}