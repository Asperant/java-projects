import java.io.*;
import java.nio.file.*;
import java.util.*;
import org.json.JSONObject;
import org.json.JSONException;
import org.json.JSONTokener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Notebook {
    private static final String FILE_NAME = "notes.json";
    private static Map<String,JSONObject> notes = new LinkedHashMap<>();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void main(String[] args){
        loadNotes();
        Scanner scanner = new Scanner(System.in);
        while(true){
            System.out.println("\n==== NotePad ====");
            System.out.println("1. Add New Note");
            System.out.println("2. List Notes");
            System.out.println("3. View Note by Title");
            System.out.println("4. Delete Note");
            System.out.println("5. Edit Note");
            System.out.println("6. Search Notes");
            System.out.println("7. List Notes by Category");
            System.out.println("8. Exit");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch(choice){
                case 1:
                    addNote(scanner);
                    break;
                case 2:
                    listNotes();
                    break;
                case 3:
                    viewNote(scanner);
                    break;
                case 4:
                    deleteNote(scanner);
                    break;
                case 5:
                    editNote(scanner);
                    break;
                case 6:
                    searchNotes(scanner);
                    break;
                case 7:
                    listNotesByCategory(scanner);
                    break;                
                case 8:
                    System.out.println("Exiting...");
                    return ;
                default:
                    System.out.println("İnvalid choice!");
            }
        }
    }

    private static void addNote(Scanner scanner){
        System.out.println("Enter Note Title: ");
        String title = scanner.nextLine();
        if(notes.containsKey(title)){
            System.out.println("This title already exists!");
            return ;
        }
        System.out.println("Enter Note Content: ");
        String content = scanner.nextLine();
        System.out.println("Enter category: ");
        String category = scanner.nextLine();

        JSONObject note = new JSONObject();
        note.put("content", content);
        note.put("category", category);
        note.put("created_at", LocalDateTime.now().format(FORMATTER));
        note.put("updated_at", LocalDateTime.now().format(FORMATTER));

        notes.put(title, note);
        saveNotes();
        System.out.println("Note added successfully.");
    }

    private static void listNotes(){
        if(notes.isEmpty()){
            System.out.println("No notes added yet.");
            return ;
        }
        System.out.println("\nSaved Notes: ");
        for(Map.Entry<String,JSONObject> entry : notes.entrySet()){
            System.out.println("- " + entry.getKey() + "(Created: " + entry.getValue().getString("created_at") + ", Last Updated: " + entry.getValue().getString("updated_at") + ")");
        }
    }

    private static void viewNote(Scanner scanner){
        System.out.println("Enter the title of the note to view: ");
        String title = scanner.nextLine();
        if(notes.containsKey(title)){
            System.out.println("\n=== " + title + " ===");
            System.out.println(notes.get(title));
        }
        else{
            System.out.println("No note found with this title");
        }
    }

    private static void deleteNote(Scanner scanner){
        System.out.println("Enter the title of the note to delete: ");
        String title = scanner.nextLine();
        if(notes.remove(title) != null){
            saveNotes();
            System.out.println("Note deleted successfully.");
        }
        else{
            System.out.println("No note found with this title.");
        }
    }

    private static void editNote(Scanner scanner){
        System.out.println("Enter the title of the note to edit: ");
        String title = scanner.nextLine();
        if(notes.containsKey(title)){
            JSONObject note = notes.get(title);
            System.out.println("Enter new content: ");
            String content = scanner.nextLine();
            note.put("content", content);
            note.put("updated_at", LocalDateTime.now().format(FORMATTER));
            saveNotes();
            System.out.println("Note updated successfully.");
        }
        else{
            System.out.println("No note found with this title.");
        }
    }

    private static void searchNotes(Scanner scanner){
        System.out.println("Enter keyword to search in notes: ");
        String keyword = scanner.nextLine();
        boolean found = false;
        
        for(Map.Entry<String,JSONObject> entry : notes.entrySet()){
            String title = entry.getKey().toLowerCase();

            if(title.contains(keyword)){
                System.out.println("- " + entry.getKey());
                found = true;
            }
        }
        
        if(!found){
            System.out.println("No matching notes found.");
        }
    }

    private static void listNotesByCategory(Scanner scanner){
        System.out.println("Enter category to list notes: ");
        String category = scanner.nextLine();
        boolean found = false;

        for(Map.Entry<String,JSONObject> entry : notes.entrySet()){
            if(entry.getValue().getString("category").equalsIgnoreCase(category)){
                System.out.println("- " + entry.getKey());
                found = true;
            }
        }

        if(!found){
            System.out.println("No notes found in this category");
        }
    }

    private static void saveNotes(){
        try(FileWriter file = new FileWriter(FILE_NAME)){
            JSONObject json = new JSONObject(notes);
            file.write(json.toString(4));
        }
        catch(IOException e){
            System.out.println("Error saving notes!");
        }
    }

    private static void loadNotes(){
        try{
            String data = new String(Files.readAllBytes(Paths.get(FILE_NAME)));
            JSONObject json = new JSONObject(new JSONTokener(data));
            for(String key : json.keySet()){
                notes.put(key,json.getJSONObject(key));
            }
        }
        catch(IOException | JSONException e){
            System.out.println("Notes file not found or unreadable, a new file will be created");
        }
    }
}