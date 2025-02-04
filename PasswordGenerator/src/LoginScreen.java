import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class LoginScreen extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, registerButton, showPasswordButton;
    private boolean isPasswordVisible = false;
    
    public LoginScreen() {
        setTitle("Giriş Yap");
        setSize(500, 300); // Boyutu büyüttük
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Kullanıcı Adı Etiketi ve Alanı
        JLabel usernameLabel = new JLabel("Kullanıcı Adı:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(usernameLabel, gbc);

        usernameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(usernameField, gbc);

        // Şifre Etiketi ve Alanı
        JLabel passwordLabel = new JLabel("Şifre:");
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

        // Giriş Butonu
        loginButton = new JButton("Giriş");
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(loginButton, gbc);

        // Hesap Oluşturma Butonu
        registerButton = new JButton("Hesap Oluştur");
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(registerButton, gbc);

        // Giriş Butonuna Tıklama İşlemi
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (validateLogin(username, password)) {
                new PasswordGeneratorApp(username).setVisible(true);
                dispose(); // Login ekranını kapat
            } else {
                JOptionPane.showMessageDialog(this, "Geçersiz kullanıcı adı veya şifre!", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Hesap Oluşturma Butonuna Tıklama İşlemi
        registerButton.addActionListener(e -> {
            new RegisterScreen().setVisible(true);
            dispose(); // Login ekranını kapat
        });

        // Enter Tuşu ile Giriş Yapma İşlemi
        passwordField.addActionListener(e -> loginButton.doClick());

        add(panel);
        setVisible(true);
    }

    private boolean validateLogin(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 2) {
                    String storedUsername = parts[0];
                    String storedPassword = parts[1];
                    if (storedUsername.equals(username) && storedPassword.equals(password)) {
                        return true;  // Giriş başarılı
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;  // Giriş başarısız
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginScreen());
    }
}