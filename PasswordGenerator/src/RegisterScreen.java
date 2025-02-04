import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class RegisterScreen extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton registerButton, backButton, showPasswordButton;
    private boolean isPasswordVisible = false;

    public RegisterScreen() {
        setTitle("Hesap Oluştur");
        setSize(500, 300); // Boyutu büyüttük
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Kullanıcı Adı Etiketi ve Alanı
        JLabel usernameLabel = new JLabel("Yeni Kullanıcı Adı:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(usernameLabel, gbc);

        usernameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(usernameField, gbc);

        // Şifre Etiketi ve Alanı
        JLabel passwordLabel = new JLabel("Yeni Şifre:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(passwordField, gbc);

        // Göster/Gizle Butonu
        showPasswordButton = new JButton("Göster");
        gbc.gridx = 2;
        gbc.gridy = 1;
        panel.add(showPasswordButton, gbc);

        // Göster/Gizle Butonuna Tıklama İşlemi
        showPasswordButton.addActionListener(e -> {
            if (isPasswordVisible) {
                passwordField.setEchoChar('*');
                showPasswordButton.setText("Göster");
            } else {
                passwordField.setEchoChar((char) 0);
                showPasswordButton.setText("Gizle");
            }
            isPasswordVisible = !isPasswordVisible;
        });

        // Hesap Oluştur Butonu
        registerButton = new JButton("Hesap Oluştur");
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(registerButton, gbc);

        // Geri Dön Butonu
        backButton = new JButton("Geri Dön");
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(backButton, gbc);

        // Hesap Oluşturma Butonuna Tıklama İşlemi
        registerButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Kullanıcı adı ve şifre boş bırakılamaz!", "Hata", JOptionPane.ERROR_MESSAGE);
            } else if (isUsernameTaken(username)) {
                JOptionPane.showMessageDialog(this, "Bu kullanıcı adı zaten alınmış!", "Hata", JOptionPane.ERROR_MESSAGE);
            } else {
                saveUser(username, password);
                JOptionPane.showMessageDialog(this, "Hesap başarıyla oluşturuldu!");
                new LoginScreen().setVisible(true);
                dispose(); // Register ekranını kapat
            }
        });

        // Enter Tuşu ile Hesap Oluşturma İşlemi
        passwordField.addActionListener(e -> registerButton.doClick());

        // Geri Dön Butonuna Tıklama İşlemi
        backButton.addActionListener(e -> {
            new LoginScreen().setVisible(true);
            dispose(); // Register ekranını kapat
        });

        add(panel);
        setVisible(true);
    }

    private boolean isUsernameTaken(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 2 && parts[0].equals(username)) {
                    return true;  // Kullanıcı adı zaten mevcut
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;  // Kullanıcı adı kullanılabilir
    }

    private void saveUser(String username, String password) {
        try (FileWriter writer = new FileWriter("users.txt", true)) {
            writer.write(username + ";" + password + "\n");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RegisterScreen());
    }
}