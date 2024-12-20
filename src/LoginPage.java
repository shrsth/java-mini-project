import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class LoginPage {

    private static final String USER_DATA_FILE = "user_data.txt";

    public static void main(String[] args) {
        // Create a new frame
        JFrame frame = new JFrame("Music Streaming App - Login");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // Center the frame on the screen

        // Create a panel to hold the components
        JPanel panel = new JPanel();
        panel.setBackground(Color.BLACK); // Set background color to black
        frame.add(panel);
        placeComponents(panel, frame);

        // Set the frame to be visible
        frame.setVisible(true);
    }

    private static void placeComponents(JPanel panel, JFrame frame) {
        panel.setLayout(null);

        // Create JLabel for username
        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(50, 50, 80, 25);
        userLabel.setForeground(Color.RED); // Set text color to red
        panel.add(userLabel);

        // Create a text field for entering the username
        JTextField userText = new JTextField(20);
        userText.setBounds(150, 50, 165, 25);
        panel.add(userText);

        // Create JLabel for password
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(50, 100, 80, 25);
        passwordLabel.setForeground(Color.RED); // Set text color to red
        panel.add(passwordLabel);

        // Create a password field for entering the password
        JPasswordField passwordText = new JPasswordField(20);
        passwordText.setBounds(150, 100, 165, 25);
        panel.add(passwordText);

        // Create a login button
        JButton loginButton = new JButton("Login");
        loginButton.setBounds(50, 150, 100, 25);
        loginButton.setBackground(Color.RED); // Set background color to red
        loginButton.setForeground(Color.BLACK); // Set text color to black
        panel.add(loginButton);

        // Create a register button
        JButton registerButton = new JButton("Register");
        registerButton.setBounds(200, 150, 100, 25);
        registerButton.setBackground(Color.RED); // Set background color to red
        registerButton.setForeground(Color.BLACK); // Set text color to black
        panel.add(registerButton);

        // Add action listener for the login button
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userText.getText();
                String password = new String(passwordText.getPassword());

                if (authenticateUser(username, password)) {
                    JOptionPane.showMessageDialog(frame, "Login successful! Welcome to the Music Streaming App.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    frame.dispose(); // Close the login frame
                    launchMusicPlayer(); // Open the music player GUI
                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid username or password.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Add action listener for the register button
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userText.getText();
                String password = new String(passwordText.getPassword());

                if (registerUser(username, password)) {
                    JOptionPane.showMessageDialog(frame, "Registration successful! You can now log in.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(frame, "Registration failed. Username might already exist.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private static void launchMusicPlayer() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MusicPlayerGUI().setVisible(true);
            }
        });
    }

    private static boolean authenticateUser(String username, String password) {
        try (Scanner scanner = new Scanner(new File(USER_DATA_FILE))) {
            String encryptedPassword = encryptPassword(password);
            while (scanner.hasNextLine()) {
                String[] userDetails = scanner.nextLine().split(",");
                if (userDetails[0].equals(username) && userDetails[1].equals(encryptedPassword)) {
                    return true;
                }
            }
        } catch (IOException | NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private static boolean registerUser(String username, String password) {
        try {
            if (isUsernameTaken(username)) {
                return false;
            }

            String encryptedPassword = encryptPassword(password);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_DATA_FILE, true))) {
                writer.write(username + "," + encryptedPassword);
                writer.newLine();
            }
            return true;
        } catch (IOException | NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private static boolean isUsernameTaken(String username) {
        try (Scanner scanner = new Scanner(new File(USER_DATA_FILE))) {
            while (scanner.hasNextLine()) {
                String[] userDetails = scanner.nextLine().split(",");
                if (userDetails[0].equals(username)) {
                    return true;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private static String encryptPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(password.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
