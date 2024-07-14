import java.awt.EventQueue;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.JOptionPane;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.json.JSONArray;
import org.json.JSONObject;

public class manageSummons {

    private JFrame frame;
    private JTextField textFieldUsername;
    private JComboBox<String> comboBoxCase;
    private JTextField textFieldAmount;
    private JTable table;
    private DefaultTableModel tableModel;
    private int summonCounter = 1; // Counter for auto-generated summon numbers
    private JLabel lblSummonID;
    private String selectedSummonID = null; // Store the selected summon ID

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    manageSummons window = new manageSummons();
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
    public manageSummons() {
        initialize();
        fetchCases();
        fetchAllSummons();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 800, 400); // Increased frame width for better layout
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JLabel lblNewLabel = new JLabel("Manage Summons");
        lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
        lblNewLabel.setBounds(289, 10, 150, 15); // Adjusted label position
        frame.getContentPane().add(lblNewLabel);

        JLabel lblSummonIDLabel = new JLabel("Summon ID:");
        lblSummonIDLabel.setBounds(10, 50, 112, 13);
        frame.getContentPane().add(lblSummonIDLabel);

        lblSummonID = new JLabel("");
        lblSummonID.setBounds(80, 50, 124, 13);
        frame.getContentPane().add(lblSummonID);

        JLabel lblNewLabel_1 = new JLabel("User ID:");
        lblNewLabel_1.setBounds(10, 72, 66, 13);
        frame.getContentPane().add(lblNewLabel_1);

        JLabel lblNewLabel_2 = new JLabel("Case:");
        lblNewLabel_2.setBounds(10, 106, 66, 13);
        frame.getContentPane().add(lblNewLabel_2);

        JLabel lblNewLabel_3 = new JLabel("Amount:");
        lblNewLabel_3.setBounds(10, 137, 66, 13);
        frame.getContentPane().add(lblNewLabel_3);

        textFieldUsername = new JTextField();
        textFieldUsername.setBounds(80, 69, 124, 19);
        frame.getContentPane().add(textFieldUsername);
        textFieldUsername.setColumns(10);

        comboBoxCase = new JComboBox<>();
        comboBoxCase.setBounds(80, 103, 124, 19);
        frame.getContentPane().add(comboBoxCase);

        textFieldAmount = new JTextField();
        textFieldAmount.setBounds(80, 136, 124, 19);
        frame.getContentPane().add(textFieldAmount);
        textFieldAmount.setColumns(10);

        JButton btnAdd = new JButton("Add");
        btnAdd.setForeground(new Color(255, 255, 255));
        btnAdd.setBackground(new Color(0, 128, 0));
        btnAdd.setBounds(229, 133, 71, 21);
        frame.getContentPane().add(btnAdd);

        JButton btnDelete = new JButton("Delete");
        btnDelete.setForeground(new Color(255, 255, 255));
        btnDelete.setBackground(new Color(255, 0, 0));
        btnDelete.setBounds(320, 133, 85, 21);
        frame.getContentPane().add(btnDelete);

        JButton btnEdit = new JButton("Edit");
        btnEdit.setBounds(320, 106, 85, 21);
        frame.getContentPane().add(btnEdit);
        
        JButton btnLogOut = new JButton("Log Out");
        btnLogOut.setForeground(Color.RED);
        btnLogOut.setBounds(690, 10, 80, 25);
        frame.getContentPane().add(btnLogOut);

        // Add action listener for Log Out button
        btnLogOut.addActionListener(e -> {
            // Perform logout actions here, such as closing the current window and opening the login window
            frame.dispose(); // Close the current window

            // Open the login window (assuming `login.java` is your login page)
            login.main(new String[]{});
        });


        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(10, 160, 760, 190);
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
        tableModel.addColumn("User ID");
        tableModel.addColumn("Case");
        tableModel.addColumn("Amount (RM)");
        tableModel.addColumn("Status"); // New column for status

        table.setModel(tableModel);

        // Adjust column widths for better display
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(150);
        columnModel.getColumn(1).setPreferredWidth(200);
        columnModel.getColumn(2).setPreferredWidth(150);
        columnModel.getColumn(3).setPreferredWidth(100);
        columnModel.getColumn(4).setPreferredWidth(100); // Adjusted width for status column

        // Add mouse listener to the table to handle row clicks
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    // Get the data from the selected row
                    selectedSummonID = tableModel.getValueAt(selectedRow, 0).toString();
                    String username = tableModel.getValueAt(selectedRow, 1).toString();
                    String caseType = tableModel.getValueAt(selectedRow, 2).toString();
                    String amount = tableModel.getValueAt(selectedRow, 3).toString();
                    // String status = tableModel.getValueAt(selectedRow, 4).toString(); // Uncomment if using status in UI

                    // Populate the text fields and label with the data
                    lblSummonID.setText(selectedSummonID);
                    textFieldUsername.setText(username);
                    comboBoxCase.setSelectedItem(caseType);
                    textFieldAmount.setText(amount);
                }
            }
        });

        // Add action listener for the Add button
        btnAdd.addActionListener(e -> {
            String username = textFieldUsername.getText().trim();
            String caseType = comboBoxCase.getSelectedItem().toString();
            String amount = textFieldAmount.getText().trim();

            // Validate input fields before adding
            if (username.isEmpty() || amount.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Username and Amount are required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Generate summon number as 001, 002, 003, ...
            String summonNumber = String.format("%03d", summonCounter++);

            // Send data to PHP script to store in database
            sendAddRequest(summonNumber, username, caseType, amount);

            // Fetch updated list of summons
            fetchAllSummons();

            // Clear input fields after adding
            textFieldUsername.setText("");
            textFieldAmount.setText("");
        });

        // Add action listener for the Edit button
        btnEdit.addActionListener(e -> {
            if (selectedSummonID == null) {
                JOptionPane.showMessageDialog(frame, "No summon selected.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String username = textFieldUsername.getText().trim();
            String caseType = comboBoxCase.getSelectedItem().toString();
            String amount = textFieldAmount.getText().trim();

            // Validate input fields before editing
            if (username.isEmpty() || amount.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Username and Amount are required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Send data to PHP script to update the summon in the database
            sendEditRequest(selectedSummonID, username, caseType, amount);

            // Fetch updated list of summons
            fetchAllSummons();

            // Clear input fields after editing
            lblSummonID.setText("");
            textFieldUsername.setText("");
            textFieldAmount.setText("");
            selectedSummonID = null;
        });

        // Add action listener for the Delete button
        btnDelete.addActionListener(e -> {
            if (selectedSummonID == null) {
                JOptionPane.showMessageDialog(frame, "No summon selected.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int confirmation = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete this summon?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirmation == JOptionPane.YES_OPTION) {
                // Send data to PHP script to delete the summon from the database
                sendDeleteRequest(selectedSummonID);

                // Fetch updated list of summons
                fetchAllSummons();

                // Clear input fields after deleting
                lblSummonID.setText("");
                textFieldUsername.setText("");
                textFieldAmount.setText("");
                selectedSummonID = null;
            }
        });
    }

    private void fetchCases() {
        try {
            URL url = new URL("http://localhost/eSummonsSystem/fetch_cases.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine())!= null) {
                response.append(inputLine);
            }
            in.close();

            // Assuming the response is in JSON format, parse it and add items to the comboBoxCase
            JSONArray jsonArray = new JSONArray(response.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String caseType = jsonObject.getString("case_type");
                comboBoxCase.addItem(caseType);
            }

            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    private void sendAddRequest(String summonNumber, String username, String caseType, String amount) {
        try {
            URL url = new URL("http://localhost/eSummonsSystem/add_summons.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json; utf-8");

            // Construct JSON object with summon data
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("summon_id", summonNumber);
            jsonParam.put("username", username);
            jsonParam.put("case_type", caseType);
            jsonParam.put("amount", amount);

            // Send POST request
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonParam.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Read response from server
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

                // Parse the response (optional)
                JSONObject jsonResponse = new JSONObject(response.toString());
                if ("success".equals(jsonResponse.getString("status"))) {
                    JOptionPane.showMessageDialog(frame, "Summon added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(frame, "Failed to add summon: " + jsonResponse.getString("message"), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Failed to add summon.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sendEditRequest(String summonID, String username, String caseType, String amount) {
        try {
            URL url = new URL("http://localhost/eSummonsSystem/edit_summons.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            // Construct JSON object with summon data
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("summon_id", summonID);
            jsonParam.put("username", username);
            jsonParam.put("case_type", caseType);
            jsonParam.put("amount", amount);

            // Send POST request
            conn.getOutputStream().write(jsonParam.toString().getBytes("UTF-8"));
            conn.getOutputStream().flush();
            conn.getOutputStream().close();

            // Read response from server (if needed)
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Handle response as needed
            JOptionPane.showMessageDialog(frame, "Summon updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Failed to update summon.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sendDeleteRequest(String summonID) {
        try {
            URL url = new URL("http://localhost/eSummonsSystem/delete_summons.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            // Construct JSON object with summon ID
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("summon_id", summonID);

            // Send POST request
            conn.getOutputStream().write(jsonParam.toString().getBytes("UTF-8"));
            conn.getOutputStream().flush();
            conn.getOutputStream().close();

            // Read response from server (if needed)
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Handle response as needed
            JOptionPane.showMessageDialog(frame, "Summon deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Failed to delete summon.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fetchAllSummons() {
        try {
            URL url = new URL("http://localhost/eSummonsSystem/fetch_all_summons.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONArray summons = new JSONArray(response.toString());

            // Sort summons array by summon_id
            ArrayList<JSONObject> summonList = new ArrayList<>();
            for (int i = 0; i < summons.length(); i++) {
                summonList.add(summons.getJSONObject(i));
            }
            Collections.sort(summonList, new Comparator<JSONObject>() {
                @Override
                public int compare(JSONObject a, JSONObject b) {
                    try {
                        return a.getString("summon_id").compareTo(b.getString("summon_id"));
                    } catch (Exception e) {
                        e.printStackTrace();
                        return 0;
                    }
                }
            });

            // Clear existing rows in the table model
            tableModel.setRowCount(0);

            for (JSONObject summon : summonList) {
                String summonID = summon.getString("summon_id");
                String username = summon.getString("username");
                String caseType = summon.getString("case_type");
                String amount = summon.getString("amount");
                String status = summon.getString("status"); // Retrieve status from JSON

                // Add row to table model
                tableModel.addRow(new Object[]{summonID, username, caseType, amount, status});
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


 }
