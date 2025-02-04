import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.prefs.Preferences;
import javax.crypto.SecretKey;

public class PasswordGeneratorApp extends JFrame {
    private JTextField lengthField;
    private JPasswordField passwordField;
    private JTextArea resultArea;
    private JButton generateButton, showButton, copyButton, saveButton, showSavedButton;
    private boolean isPasswordVisible = false;
    private JCheckBox uppercaseCheck, lowercaseCheck, digitsCheck, specialCharsCheck;
    private Preferences prefs;
    private SecretKey aesKey = null;
    private String currentUsername;

    public PasswordGeneratorApp(String username) {
        this.currentUsername = username;

        prefs = Preferences.userNodeForPackage(PasswordGeneratorApp.class);
        
        // Kaydedilen ayarları yükle
        boolean uppercase = prefs.getBoolean("uppercaseCheck", true);
        boolean lowercase = prefs.getBoolean("lowercaseCheck", true);
        boolean digits = prefs.getBoolean("digitsCheck", true);
        boolean specialChars = prefs.getBoolean("specialCharsCheck", true);

        // CheckBox'ları kaydedilen değerlere göre ayarla
        uppercaseCheck = new JCheckBox("Büyük Harfler (A-Z)", uppercase);
        lowercaseCheck = new JCheckBox("Küçük Harfler (a-z)", lowercase);
        digitsCheck = new JCheckBox("Rakamlar (0-9)", digits);
        specialCharsCheck = new JCheckBox("Özel Karakterler (!@#$%^&*()-_=+<>?)", specialChars);

        // Ayarlar değiştiğinde güncelleme yap
        uppercaseCheck.addItemListener(e -> prefs.putBoolean("uppercaseCheck", uppercaseCheck.isSelected()));
        lowercaseCheck.addItemListener(e -> prefs.putBoolean("lowercaseCheck", lowercaseCheck.isSelected()));
        digitsCheck.addItemListener(e -> prefs.putBoolean("digitsCheck", digitsCheck.isSelected()));
        specialCharsCheck.addItemListener(e -> prefs.putBoolean("specialCharsCheck", specialCharsCheck.isSelected()));

        setTitle("Şifre Oluşturucu - " + username);
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Seçenekler");

        JMenuItem showPasswordsItem = new JMenuItem("Kayıtlı Şifreleri Göster");
        showPasswordsItem.addActionListener(e -> showSavedPasswords());

        JMenuItem settingsItem = new JMenuItem("Ayarlar");
        settingsItem.addActionListener(e -> showSettingsDialog());

        JMenuItem logoutItem = new JMenuItem("Çıkış Yap");
        logoutItem.addActionListener(e -> {
            new LoginScreen().setVisible(true);
            dispose(); // Mevcut ekranı kapat
        });

        menu.add(showPasswordsItem);
        menu.add(settingsItem);
        menu.add(logoutItem);
        menuBar.add(menu);
        setJMenuBar(menuBar);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Giriş alanları
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());

        JLabel lengthLabel = new JLabel("Şifre Uzunluğu:");
        lengthField = new JTextField(5);
        lengthField.addActionListener(e -> generatePassword());

        inputPanel.add(lengthLabel);
        inputPanel.add(lengthField);

        passwordField = new JPasswordField(20);
        passwordField.setEchoChar('*');
        passwordField.setEditable(false);

        showButton = new JButton("Göster");
        showButton.addActionListener(e -> {
            if (isPasswordVisible) {
                passwordField.setEchoChar('*');
                showButton.setText("Göster");
            } else {
                passwordField.setEchoChar((char) 0);
                showButton.setText("Gizle");
            }
            isPasswordVisible = !isPasswordVisible;
        });
        showButton.setEnabled(false);

        inputPanel.add(passwordField);
        inputPanel.add(showButton);

        resultArea = new JTextArea();
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        generateButton = new JButton("Şifre Oluştur");
        generateButton.addActionListener(e -> generatePassword());

        copyButton = new JButton("Panoya Kopyala");
        copyButton.addActionListener(e -> {
            String password = new String(passwordField.getPassword());
            if (password.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Şifre oluşturulmadan kopyalanamaz!", "Hata", JOptionPane.ERROR_MESSAGE);
            } else {
                StringSelection stringSelection = new StringSelection(password);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(stringSelection, null);
                JOptionPane.showMessageDialog(null, "Şifre panoya kopyalandı!");
            }
        });

        saveButton = new JButton("Şifreyi Kaydet");
        saveButton.addActionListener(e -> savePassword());

        showSavedButton = new JButton("Kayıtlı Şifreleri Göster");
        showSavedButton.addActionListener(e -> showSavedPasswords());

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        buttonPanel.add(generateButton);
        buttonPanel.add(copyButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(showSavedButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);
    }

    private void generatePassword() {
        int length;
        try {
            length = Integer.parseInt(lengthField.getText());
            if (length <= 0) {
                throw new NumberFormatException(); // Negatif ya da sıfır uzunluk için hata oluştur
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Lütfen geçerli bir uzunluk girin!", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        StringBuilder characters = new StringBuilder();
        if (uppercaseCheck.isSelected()) {
            characters.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        }
        if (lowercaseCheck.isSelected()) {
            characters.append("abcdefghijklmnopqrstuvwxyz");
        }
        if (digitsCheck.isSelected()) {
            characters.append("0123456789");
        }
        if (specialCharsCheck.isSelected()) {
            characters.append("!@#$%^&*()-_=+<>?");
        }
    
        if (characters.length() == 0) {
            JOptionPane.showMessageDialog(this, "Lütfen en az bir karakter seti seçin!", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        Random random = new Random();
        StringBuilder password = new StringBuilder(length);
    
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            password.append(characters.charAt(index));
        }
    
        passwordField.setText(password.toString());
        resultArea.setText("Şifre başarıyla oluşturuldu.");
        showButton.setEnabled(true); // Şifre oluşturuldu, butonu etkinleştir
    }  
    
    private SecretKey getOrCreateKey() {
        try {
            if (aesKey == null) {
                aesKey = AESUtils.generateKey(); // Anahtar oluştur
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return aesKey;
    }

    private void savePassword() {
        SecretKey key = getOrCreateKey(); // Anahtarı alın
        Map<String, String[]> passwordMap = readPasswordsFromFile(key);
        
        String password = new String(passwordField.getPassword());
        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen önce bir şifre oluşturun!", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String name = JOptionPane.showInputDialog(this, "Bu şifreye bir isim verin:");
        if (name != null && !name.trim().isEmpty()) {
            String date = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
            passwordMap.put(name, new String[]{password, date});
            saveUpdatedPasswords(passwordMap, key); // `key` burada kullanılıyor
            JOptionPane.showMessageDialog(this, "Şifre kaydedildi!");
        } else {
            JOptionPane.showMessageDialog(this, "Geçerli bir isim girilmedi.", "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showSavedPasswords() {
        File file = new File(getUserPasswordFilePath());
        if (!file.exists()) {
            JOptionPane.showMessageDialog(this, "Daha önce şifre kaydedilmemiştir.", "Bilgi", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
    
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
        JPanel passwordsPanel = new JPanel();
        passwordsPanel.setLayout(new BoxLayout(passwordsPanel, BoxLayout.Y_AXIS));
    
        JScrollPane scrollPane = new JScrollPane(passwordsPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
    
        boolean hasPasswords = false;
    
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            Map<String, String[]> passwordMap = new HashMap<>();
            Map<String, JPanel> passwordPanels = new HashMap<>();
    
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 4) {  // Ad, şifre, tarih ve şifreleme anahtarı olmalı
                    hasPasswords = true;
    
                    String name = parts[0];
                    String encryptedPassword = parts[1];
                    String date = parts[2];
                    SecretKey key = AESUtils.decodeKey(parts[3]);
    
                    String decryptedPassword = AESUtils.decrypt(encryptedPassword, key);
                    passwordMap.put(name, new String[]{decryptedPassword, date});
    
                    JPanel passwordPanel = createPasswordPanel(name, decryptedPassword, date, passwordMap, passwordsPanel);
                    passwordPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
                    passwordPanels.put(name.toLowerCase(), passwordPanel);
                    passwordsPanel.add(passwordPanel);
                }
            }
    
            if (!hasPasswords) {
                passwordsPanel.add(new JLabel("Kayıtlı şifre yok."));
            } else {
                JPanel searchPanel = new JPanel(new BorderLayout());
                searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    
                JTextField searchField = new JTextField(20);
                searchField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
    
                searchPanel.add(new JLabel("Arama: "), BorderLayout.WEST);
                searchPanel.add(searchField, BorderLayout.CENTER);
    
                mainPanel.add(searchPanel, BorderLayout.NORTH);
    
                searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                    public void insertUpdate(javax.swing.event.DocumentEvent e) {
                        filterPasswords();
                    }
    
                    public void removeUpdate(javax.swing.event.DocumentEvent e) {
                        filterPasswords();
                    }
    
                    public void changedUpdate(javax.swing.event.DocumentEvent e) {
                        filterPasswords();
                    }
    
                    private void filterPasswords() {
                        String searchText = searchField.getText().toLowerCase();
                        passwordsPanel.removeAll();
    
                        for (String key : passwordPanels.keySet()) {
                            if (key.contains(searchText)) {
                                passwordsPanel.add(passwordPanels.get(key));
                            }
                        }
    
                        passwordsPanel.revalidate();
                        passwordsPanel.repaint();
                    }
                });
            }
    
            JOptionPane.showMessageDialog(null, mainPanel, "Kayıtlı Şifreler", JOptionPane.PLAIN_MESSAGE);
    
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }    
    
    private JPanel createPasswordPanel(String name, String password, String date, Map<String, String[]> passwordMap, JPanel passwordsPanel) {
        JPanel passwordPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Bileşenler arasında boşluk
        gbc.anchor = GridBagConstraints.WEST;
    
        JLabel nameLabel = new JLabel(name + ": ");
        JLabel passwordLabel = new JLabel("**********");
        passwordLabel.putClientProperty("actualPassword", password); // Şifreyi sakla
        JLabel dateLabel = new JLabel("Kaydedilme Tarihi: " + date);
        JButton showButton = new JButton("Göster");
        JButton copyButton = new JButton("Kopyala");
        JButton deleteButton = new JButton("Sil");
        JButton editButton = new JButton("Düzenle");
    
        final boolean[] isPasswordVisible = {false}; // Şifrenin görünürlük durumunu boolean array olarak tanımlıyoruz
    
        showButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (isPasswordVisible[0]) {
                    passwordLabel.setText("**********");
                    showButton.setText("Göster");
                } else {
                    String actualPassword = (String) passwordLabel.getClientProperty("actualPassword");
                    passwordLabel.setText(actualPassword);
                    showButton.setText("Gizle");
                }
                isPasswordVisible[0] = !isPasswordVisible[0];
            }
        });
    
        editButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                passwordLabel.setText("**********"); // Şifreyi otomatik olarak gizle
                showButton.setText("Göster"); // Göster butonunun metnini güncelle
                isPasswordVisible[0] = false; // Şifrenin görünürlük durumunu false yap
    
                editPassword(name, password, passwordLabel, dateLabel); // Düzenleme penceresini aç ve label'ı güncelle
            }
        });
    
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(null, "Bu şifreyi silmek istediğinize emin misiniz?", "Silme Onayı", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    passwordMap.remove(name); // Map'ten sil
                    passwordsPanel.remove(passwordPanel); // Panelden sil
                    passwordsPanel.revalidate();
                    passwordsPanel.repaint();
    
                    // Burada key'i tanımlayın
                    SecretKey key = getOrCreateKey(); // Anahtarı alın
                    saveUpdatedPasswords(passwordMap, key); // Dosyayı güncelle
    
                    JOptionPane.showMessageDialog(null, "Şifre silindi!");
                }
            }
        });
    
        copyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String actualPassword = (String) passwordLabel.getClientProperty("actualPassword");
                StringSelection stringSelection = new StringSelection(actualPassword);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(stringSelection, null);
                JOptionPane.showMessageDialog(null, "Şifre panoya kopyalandı!");
            }
        });
    
        gbc.gridx = 0;
        gbc.gridy = 0;
        passwordPanel.add(nameLabel, gbc);
    
        gbc.gridx = 1;
        passwordPanel.add(passwordLabel, gbc);
    
        gbc.gridx = 2;
        passwordPanel.add(showButton, gbc);
    
        gbc.gridx = 3;
        passwordPanel.add(copyButton, gbc);
    
        gbc.gridx = 4;
        passwordPanel.add(deleteButton, gbc);
    
        gbc.gridx = 5;
        passwordPanel.add(editButton, gbc);
    
        gbc.gridx = 6;
        passwordPanel.add(dateLabel, gbc);
    
        return passwordPanel;
    }
    
    private void editPassword(String oldName, String oldPassword, JLabel passwordLabel, JLabel dateLabel) {
        JDialog editDialog = new JDialog(this, "Şifreyi Düzenle", true);
        editDialog.setSize(400, 200);
        editDialog.setLayout(new GridBagLayout());
        editDialog.setLocationRelativeTo(this);
    
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
    
        // Şifre ismi alanı
        gbc.gridx = 0;
        gbc.gridy = 0;
        editDialog.add(new JLabel("Şifre İsmi:"), gbc);
    
        JTextField nameField = new JTextField(oldName, 20);
        gbc.gridx = 1;
        editDialog.add(nameField, gbc);
    
        // Şifre alanı
        gbc.gridx = 0;
        gbc.gridy = 1;
        editDialog.add(new JLabel("Şifre:"), gbc);
    
        JPasswordField passwordField = new JPasswordField(oldPassword, 20);
        passwordField.setEchoChar('*');
        gbc.gridx = 1;
        editDialog.add(passwordField, gbc);
    
        // Göster / Gizle butonu
        JButton showButton = new JButton("Göster");
        showButton.addActionListener(new ActionListener() {
            boolean isPasswordVisible = false;
    
            public void actionPerformed(ActionEvent e) {
                if (isPasswordVisible) {
                    passwordField.setEchoChar('*');
                    showButton.setText("Göster");
                } else {
                    passwordField.setEchoChar((char) 0);
                    showButton.setText("Gizle");
                }
                isPasswordVisible = !isPasswordVisible;
            }
        });
    
        gbc.gridx = 2;
        editDialog.add(showButton, gbc);
    
        // Rastgele şifre oluşturma butonu
        JButton generateButton = new JButton("Rastgele Şifre Oluştur");
        generateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                passwordField.setText(generateRandomPassword(12)); // 12 karakter uzunluğunda rastgele şifre oluştur
            }
        });
    
        gbc.gridx = 1;
        gbc.gridy = 2;
        editDialog.add(generateButton, gbc);
    
        // Kaydet ve İptal butonları
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Kaydet");
        JButton cancelButton = new JButton("İptal");
    
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String newName = nameField.getText().trim();
                String newPassword = new String(passwordField.getPassword()).trim();
    
                if (newName.isEmpty() || newPassword.isEmpty()) {
                    JOptionPane.showMessageDialog(editDialog, "Şifre ismi ve şifre boş bırakılamaz.", "Hata", JOptionPane.ERROR_MESSAGE);
                    return;
                }
    
                updatePassword(oldName, newName, newPassword); // Şifreyi güncelle
    
                // Şifreyi güncelledikten sonra gösterme butonunun işlevselliğini yeniden ayarla
                passwordLabel.putClientProperty("actualPassword", newPassword); // Yeni şifreyi sakla
                passwordLabel.setText("**********"); // Şifreyi gizli göster
                showButton.setText("Göster"); // Göster butonunu sıfırla
                isPasswordVisible = false; // Şifrenin görünürlük durumunu false yap
    
                // Yeni tarihi al ve güncelle
                String newDate = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
                dateLabel.setText("Kaydedilme Tarihi: " + newDate); // Tarihi güncelle
    
                editDialog.dispose(); // Pencereyi kapat
            }
        });
    
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editDialog.dispose();
            }
        });
    
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
    
        gbc.gridx = 1;
        gbc.gridy = 3;
        editDialog.add(buttonPanel, gbc);
    
        editDialog.setVisible(true);
    }    
    
    private void updatePassword(String oldName, String newName, String newPassword) {
        SecretKey key = getOrCreateKey(); // Anahtarı alın
        File file = new File(getUserPasswordFilePath());
        Map<String, String[]> passwordMap = new HashMap<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 4) { // Ad, şifre, tarih ve anahtar
                    String name = parts[0];
                    String encryptedPassword = parts[1];
                    String date = parts[2];
                    if (name.equals(oldName)) {
                        // Yeni şifreyi ve tarihi güncelle
                        String newDate = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
                        passwordMap.put(newName, new String[]{newPassword, newDate});
                    } else {
                        // Eski veriyi koru
                        try {
                            passwordMap.put(name, new String[]{AESUtils.decrypt(encryptedPassword, key), date});
                        } catch (Exception ex) {
                            ex.printStackTrace(); // İstisnayı konsola yazdır
                            // Hata durumunu yönetmek için başka işlemler de ekleyebilirsiniz.
                        }
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        saveUpdatedPasswords(passwordMap, key); // Haritayı dosyaya yaz
    }       
    
    private String generateRandomPassword(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+<>?";
        Random random = new Random();
        StringBuilder password = new StringBuilder(length);
    
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            password.append(characters.charAt(index));
        }
    
        return password.toString();
    }
    
    private void saveUpdatedPasswords(Map<String, String[]> passwordMap, SecretKey key) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(getUserPasswordFilePath(), false))) {
            StringBuilder data = new StringBuilder();
            for (Map.Entry<String, String[]> entry : passwordMap.entrySet()) {
                String name = entry.getKey();
                String[] values = entry.getValue();
                String password = values[0];
                String date = values[1];
                // Şifreyi yeniden şifrele ve dosyaya yaz
                String encryptedPassword = AESUtils.encrypt(password, key);
                data.append(name).append(";").append(encryptedPassword).append(";").append(date).append(";").append(AESUtils.encodeKey(key)).append("\n");
            }
            writer.println(data.toString());
            System.out.println("Şifreler dosyaya yazıldı: \n" + data.toString()); // Kontrol için
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }        

    private Map<String, String[]> readPasswordsFromFile(SecretKey key) {
        Map<String, String[]> passwordMap = new HashMap<>();
        File file = new File(getUserPasswordFilePath());
        if (!file.exists()) {
            return passwordMap; // Dosya yoksa boş bir harita döndür
        }
    
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 4) { // Name, encryptedPassword, date ve key
                    String name = parts[0];
                    String encryptedPassword = parts[1];
                    String date = parts[2];
                    SecretKey readKey = AESUtils.decodeKey(parts[3]);
                    // Doğru anahtar mı?
                    if (!key.equals(readKey)) {
                        System.out.println("Anahtar uyuşmuyor!");
                        continue;
                    }
                    String decryptedPassword = AESUtils.decrypt(encryptedPassword, key);
                    passwordMap.put(name, new String[]{decryptedPassword, date});
                }
            }
            System.out.println("Şifreler dosyadan okundu: \n" + passwordMap.toString()); // Kontrol için
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    
        return passwordMap;
    }    
    
    private void showSettingsDialog() {
        JDialog settingsDialog = new JDialog(this, "Ayarlar", true);
        settingsDialog.setSize(300, 200);
        settingsDialog.setLayout(new GridLayout(5, 1));
        settingsDialog.setLocationRelativeTo(this);
    
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton okButton = new JButton("Tamam");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                settingsDialog.dispose();  // Pencereyi kapat
            }
        });
    
        buttonPanel.add(okButton);
        settingsDialog.add(uppercaseCheck);
        settingsDialog.add(lowercaseCheck);
        settingsDialog.add(digitsCheck);
        settingsDialog.add(specialCharsCheck);
        settingsDialog.add(buttonPanel);
    
        settingsDialog.setVisible(true);
    }    

    // Kullanıcının şifre dosya yolunu belirleme
    private String getUserPasswordFilePath() {
        return currentUsername + "_passwords.txt";
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new PasswordGeneratorApp("testUser").setVisible(true); // Örnek bir kullanıcı adı
            }
        });
    }
}