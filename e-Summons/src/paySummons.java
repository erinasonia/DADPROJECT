import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.JButton;
import java.awt.Color;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import javax.swing.table.DefaultTableModel;

public class paySummons {

    JFrame frame;
    private JTable table;
    private String loggedInUsername; // Store logged-in username
    private String loggedInUserID; // Store logged-in user ID
    private DefaultTableModel tableModel;
    private JLabel lblLoggedInUser; // Label to display logged-in username
    private JLabel lblNewLabelNotFound; // Label to display "No unpaid summons found"

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    if (args.length >= 2) { // Check if both username and user ID are provided
                        String loggedInUsername = args[0];
                        String loggedInUserID = args[1];
                        paySummons window = new paySummons(loggedInUsername, loggedInUserID);
                        window.frame.setVisible(true);
                    } else {
                        System.out.println("Insufficient arguments provided. Usage: java paySummons <username> <userID>");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public paySummons(String loggedInUsername, String loggedInUserID) {
        this.loggedInUsername = loggedInUsername; // Store logged-in username
        this.loggedInUserID = loggedInUserID; // Store logged-in user ID
        initialize();

        // Fetch summons data for the logged-in user
        fetchSummonsFromDatabase();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JLabel lblNewLabel = new JLabel("Pay Summons");
        lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
        lblNewLabel.setBounds(240, 20, 150, 25);
        frame.getContentPane().add(lblNewLabel);

        lblLoggedInUser = new JLabel("Logged in as: " + loggedInUsername); // Display logged-in username
        lblLoggedInUser.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 11));
        lblLoggedInUser.setBounds(20, 50, 200, 15);
        frame.getContentPane().add(lblLoggedInUser);

        lblNewLabelNotFound = new JLabel("No unpaid summons found");
        lblNewLabelNotFound.setForeground(Color.RED);
        lblNewLabelNotFound.setFont(new Font("Nirmala UI", Font.BOLD, 12));
        lblNewLabelNotFound.setBounds(200, 150, 250, 25);
        lblNewLabelNotFound.setVisible(false); // Initially hide the label
        frame.getContentPane().add(lblNewLabelNotFound);

        JButton btnPay = new JButton("Pay Selected");
        btnPay.setBounds(240, 300, 120, 25);
        frame.getContentPane().add(btnPay);

        JButton btnLogout = new JButton("Log Out"); // Changed button text to "Log Out"
        btnLogout.setForeground(Color.RED);
        btnLogout.setBounds(480, 20, 80, 25);
        frame.getContentPane().add(btnLogout);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(20, 90, 550, 200);
        frame.getContentPane().add(scrollPane);

        table = new JTable();
        scrollPane.setViewportView(table);

        // Define table model with non-editable cells
        tableModel = new DefaultTableModel() {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
        };

        tableModel.addColumn("Summon ID");
        tableModel.addColumn("Case Type");
        tableModel.addColumn("Amount");
        tableModel.addColumn("Status");

        table.setModel(tableModel);

        // Event listener for Log Out button
        btnLogout.addActionListener(e -> {
            frame.dispose(); // Close the window
            // Redirect to the login page (assuming your login page is named Login.java and in the same package)
            login.main(new String[] {}); // Call the main method of Login.java
        });

        // Event listener for Pay Selected button
        btnPay.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                String summonID = tableModel.getValueAt(selectedRow, 0).toString();
                String caseType = tableModel.getValueAt(selectedRow, 1).toString();
                String amount = tableModel.getValueAt(selectedRow, 2).toString();

                // Perform payment or other action here
                // Example: display message
                JOptionPane.showMessageDialog(frame, "Paid Summon ID: " + summonID + "\nCase Type: " + caseType + "\nAmount: " + amount, "Payment Successful", JOptionPane.INFORMATION_MESSAGE);

                // Update status to "Paid" in the table and database
                updateSummonStatus(summonID, "Paid");

                // Remove row from the table
                tableModel.removeRow(selectedRow);

                // Check if table is empty after removing row
                if (tableModel.getRowCount() == 0) {
                    lblNewLabelNotFound.setVisible(true); // Show the "No unpaid summons" message
                }
            } else {
                // No row selected
                JOptionPane.showMessageDialog(frame, "Please select a summon to pay.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    /**
     * Fetch summons data for the logged-in user from the database.
     */
    private void fetchSummonsFromDatabase() {
        try {
            // Construct URL and open connection
            URL url = new URL("http://localhost/eSummonsSystem/fetch_summons.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Construct POST data
            JSONObject postData = new JSONObject();
            postData.put("user_id", loggedInUserID); // Send user ID to fetch summons based on user

            // Send POST request
            OutputStream os = conn.getOutputStream();
            os.write(postData.toString().getBytes());
            os.flush();
            os.close();

            // Check response code
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read response
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder responseBuilder = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    responseBuilder.append(inputLine);
                }
                in.close();

                // Parse JSON response
                String jsonResponse = responseBuilder.toString().trim();
                System.out.println("JSON Response: " + jsonResponse); // Debugging line

                // Process JSON array if it's not empty
                if (!jsonResponse.isEmpty()) {
                    JSONArray jsonArray = new JSONArray(jsonResponse);
                    processSummonData(jsonArray); // Process the JSON array as needed

                    // Check if table is empty after processing summons
                    if (tableModel.getRowCount() == 0) {
                        lblNewLabelNotFound.setVisible(true); // Show the "No unpaid summons" message
                    }
                } else {
                    // Display message when no summons found
                    lblNewLabelNotFound.setVisible(true); // Show the "No unpaid summons" message
                }
            } else {
                throw new RuntimeException("Failed : HTTP error code : " + responseCode);
            }

            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exceptions or display error messages as needed
        }
    }

    /**
     * Process JSON array containing summons data and populate the table.
     * @param jsonArray JSON array containing summons data.
     * @throws JSONException If there's an error processing JSON data.
     */
    private void processSummonData(JSONArray jsonArray) throws JSONException {
        // Clear existing rows in the table model
        tableModel.setRowCount(0);

        // Process each JSON object in the array
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject summon = jsonArray.getJSONObject(i);
            String summonID = String.valueOf(summon.get("summon_id")); // Convert to string if needed
            String caseType = summon.getString("case_type");
            String amount = summon.getString("amount");
            String status = summon.getString("status");

            // Add row to table model
            tableModel.addRow(new Object[]{summonID, caseType, amount, status});
        }
    }

    /**
     * Update the status of a summon in the database.
     * @param summonID The ID of the summon to update.
     * @param status The new status to set.
     */
    private void updateSummonStatus(String summonID, String status) {
        try {
            // Construct URL and open connection
            URL url = new URL("http://localhost/eSummonsSystem/update_status.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            
            // Construct POST data
            JSONObject postData = new JSONObject();
            postData.put("summon_id", summonID);
            postData.put("status", status);
            
            

            // Send POST request
            OutputStream os = conn.getOutputStream();
            os.write(postData.toString().getBytes());
            os.flush();
            os.close();

            // Check response code
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read response if needed
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                System.out.println("Update Status Response: " + response.toString()); // Debugging line
            } else {
                throw new RuntimeException("Failed : HTTP error code : " + responseCode);
            }

            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exceptions or display error messages as needed
        }
    }
}

