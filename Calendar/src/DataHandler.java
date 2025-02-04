import org.json.JSONObject;
import org.json.JSONArray;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class DataHandler {
    private static final String DATA_FILE = "data.json";

    public static JSONObject loadData(){
        File file = new File(DATA_FILE);

        if(!file.exists()){
            JSONObject data = new JSONObject();
            data.put("recurring_tasks", new JSONArray());
            saveData(data);
            return data;
        }
        try(FileReader reader = new FileReader(file)){
            char[] chars = new char[(int) file.length()];
            reader.read(chars);
            String content = new String(chars);
            return new JSONObject(content);
        }
        catch(IOException e){
            e.printStackTrace();
            return new JSONObject();
        }
    }
    public static void saveData(JSONObject data){
        try(FileWriter writer = new FileWriter(DATA_FILE)){
            writer.write(data.toString(4));
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}