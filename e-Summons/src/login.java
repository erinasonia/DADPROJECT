import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JButton;
import java.awt.Color;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class login {

    private JFrame frame;
    private JTextField textFieldID;
    private JPasswordField passwordField;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    login window = new login();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public login() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 450, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel lblNewLabel = new JLabel("Students e-Summons System");
        lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
        lblNewLabel.setBounds(108, 33, 273, 34);

        JLabel lblNewLabel_1 = new JLabel("Your ID:");
        lblNewLabel_1.setBounds(81, 94, 77, 13);

        JLabel lblNewLabel_2 = new JLabel("Password:");
        lblNewLabel_2.setBounds(81, 131, 77, 13);

        textFieldID = new JTextField();
        textFieldID.setBounds(152, 91, 96, 19);
        textFieldID.setColumns(10);

        passwordField = new JPasswordField();
        passwordField.setBounds(152, 128, 96, 19);

        JButton btnNewButtonLogin = new JButton("Login");
        btnNewButtonLogin.setBounds(152, 171, 68, 34);
        btnNewButtonLogin.setForeground(new Color(255, 255, 255));
        btnNewButtonLogin.setBackground(new Color(0, 128, 128));
        frame.getContentPane().setLayout(null);
        frame.getContentPane().add(lblNewLabel);
        frame.getContentPane().add(lblNewLabel_1);
        frame.getContentPane().add(lblNewLabel_2);
        frame.getContentPane().add(textFieldID);
        frame.getContentPane().add(passwordField);
        frame.getContentPane().add(btnNewButtonLogin);

        // Action listener for login button
        btnNewButtonLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = textFieldID.getText().trim();
                String password = new String(passwordField.getPassword()).trim(); // Get password as a string

                // Perform authentication via PHP script
                try {
                    // Construct POST data
                    JSONObject postData = new JSONObject();
                    postData.put("usernameLogin", username);
                    postData.put("password", password);

                    // URL of login.php script
                    URL url = new URL("http://localhost/eSummonsSystem/login.php");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);

                    // Send POST request
                    OutputStream os = conn.getOutputStream();
                    os.write(postData.toString().getBytes());
                    os.flush();
                    os.close(); // Close the output stream

                    // Check response code
                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // Read response
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        StringBuilder responseBuilder = new StringBuilder();
                        String output;
                        while ((output = br.readLine()) != null) {
                            responseBuilder.append(output);
                        }
                        br.close();

                        // Parse JSON response
                        String jsonResponseString = responseBuilder.toString().trim();
                        System.out.println("JSON Response: " + jsonResponseString); // Debugging line

                        JSONObject jsonResponse = new JSONObject(jsonResponseString);

                        if (jsonResponse.has("user_id") && jsonResponse.has("role")) {
                            String role = jsonResponse.getString("role");
                            if ("admin".equals(role)) {
                                // Redirect to manageSummons.java
                                frame.dispose(); // Close current window
                                manageSummons.main(new String[] {username}); // Redirect to ManageSummons page
                            } else if ("user".equals(role)) {
                                frame.dispose(); // Close current window
                                paySummons window = new paySummons(username, jsonResponse.getString("user_id"));
                                window.frame.setVisible(true); // Open paySummons window
                                showWelcomeMessage(username); // Display welcome message
                            } else {
                                // Handle invalid role (should not occur in this setup)
                                System.out.println("Invalid role.");
                            }
                        } else if (jsonResponse.has("error")) {
                            String errorMessage = jsonResponse.getString("error");
                            // Display error message in a pop-up dialog
                            JOptionPane.showMessageDialog(frame, errorMessage, "Login Error", JOptionPane.ERROR_MESSAGE);
                        } else {
                            System.out.println("Unexpected JSON response from server.");
                        }
                    } else {
                        throw new RuntimeException("Failed : HTTP error code : " + responseCode);
                    }

                    // Close connection
                    conn.disconnect();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    /**
     * Show a welcome message dialog with the username.
     * 
     * @param username The username of the logged-in user.
     */
    private void showWelcomeMessage(String username) {
        JOptionPane.showMessageDialog(frame, "Welcome to e-Summons, " + username, "Login Success",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
