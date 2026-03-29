import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class EMSOrganizerGUI extends JFrame {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/EMSDB?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "Milo-098";

    private final int organizerId;
    private DefaultTableModel eventModel;
    private DefaultTableModel ticketModel;
    private JTable eventTable;
    private JTable ticketTable;

    public EMSOrganizerGUI() {
        this.organizerId = getOrganizerIdByEmail(EMSLogin.getLoggedInEmail());
        if (this.organizerId == -1) {
            JOptionPane.showMessageDialog(this, "Organizer account not found.");
            dispose();
            new EMSLogin().setVisible(true);
            return;
        }

        setTitle("EMS - Organizer Dashboard");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        setContentPane(root);

        JPanel header = new EMSLogin.GradientHeaderPanel();
        header.setLayout(new BorderLayout());
        header.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("Organizer Dashboard");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);

        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(e -> {
            dispose();
            new EMSLogin().setVisible(true);
        });

        header.add(lblTitle, BorderLayout.WEST);
        header.add(btnLogout, BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("My Events", createEventsPanel());
        tabs.addTab("Manage Tickets", createTicketsPanel());
        root.add(tabs, BorderLayout.CENTER);
    }

    private JPanel createEventsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;

        JTextField tfTitle = new JTextField(15);
        JTextField tfDescription = new JTextField(15);
        JTextField tfDate = new JTextField(15);   // yyyy-mm-dd
        JTextField tfTime = new JTextField(15);   // hh:mm:ss
        JTextField tfLocation = new JTextField(15);

        JButton btnCreate = new JButton("Create Event");
        JButton btnRefresh = new JButton("Refresh");

        c.gridx = 0; c.gridy = 0; form.add(new JLabel("Title:"), c);
        c.gridx = 1; form.add(tfTitle, c);

        c.gridx = 0; c.gridy = 1; form.add(new JLabel("Description:"), c);
        c.gridx = 1; form.add(tfDescription, c);

        c.gridx = 0; c.gridy = 2; form.add(new JLabel("Date (yyyy-mm-dd):"), c);
        c.gridx = 1; form.add(tfDate, c);

        c.gridx = 0; c.gridy = 3; form.add(new JLabel("Time (hh:mm:ss):"), c);
        c.gridx = 1; form.add(tfTime, c);

        c.gridx = 0; c.gridy = 4; form.add(new JLabel("Location:"), c);
        c.gridx = 1; form.add(tfLocation, c);

        c.gridx = 0; c.gridy = 5; form.add(btnCreate, c);
        c.gridx = 1; form.add(btnRefresh, c);

        panel.add(form, BorderLayout.NORTH);

        eventModel = new DefaultTableModel(
                new String[]{"EventID", "Title", "Date", "Time", "Location", "ApprovalStatus"}, 0
        );
        eventTable = new JTable(eventModel);
        panel.add(new JScrollPane(eventTable), BorderLayout.CENTER);

        btnCreate.addActionListener(e -> {
            String title = tfTitle.getText().trim();
            String description = tfDescription.getText().trim();
            String date = tfDate.getText().trim();
            String time = tfTime.getText().trim();
            String location = tfLocation.getText().trim();

            if (title.isEmpty() || date.isEmpty() || time.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Title, date, and time are required.");
                return;
            }

            String sql = "INSERT INTO Events (OrganizerID, Title, Description, EventDate, EventTime, Location, ApprovalStatus) " +
                    "VALUES (?, ?, ?, ?, ?, ?, 'Pending')";

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, organizerId);
                ps.setString(2, title);
                ps.setString(3, description);
                ps.setDate(4, Date.valueOf(date));
                ps.setTime(5, Time.valueOf(time));
                ps.setString(6, location);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Event created and sent for approval.");
                tfTitle.setText("");
                tfDescription.setText("");
                tfDate.setText("");
                tfTime.setText("");
                tfLocation.setText("");

                loadMyEvents();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "Invalid date or time format.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
            }
        });

        btnRefresh.addActionListener(e -> loadMyEvents());

        loadMyEvents();
        return panel;
    }

    private JPanel createTicketsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JPanel top = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;

        JTextField tfEventId = new JTextField(10);
        JTextField tfType = new JTextField(10);
        JTextField tfPrice = new JTextField(10);
        JTextField tfQuantity = new JTextField(10);

        JButton btnAdd = new JButton("Add Ticket");
        JButton btnRefresh = new JButton("Refresh");

        c.gridx = 0; c.gridy = 0; top.add(new JLabel("Event ID:"), c);
        c.gridx = 1; top.add(tfEventId, c);

        c.gridx = 0; c.gridy = 1; top.add(new JLabel("Ticket Type:"), c);
        c.gridx = 1; top.add(tfType, c);

        c.gridx = 0; c.gridy = 2; top.add(new JLabel("Price:"), c);
        c.gridx = 1; top.add(tfPrice, c);

        c.gridx = 0; c.gridy = 3; top.add(new JLabel("Quantity:"), c);
        c.gridx = 1; top.add(tfQuantity, c);

        c.gridx = 0; c.gridy = 4; top.add(btnAdd, c);
        c.gridx = 1; top.add(btnRefresh, c);

        panel.add(top, BorderLayout.NORTH);

        ticketModel = new DefaultTableModel(
                new String[]{"TicketID", "EventID", "Event Title", "Type", "Price", "Quantity"}, 0
        );
        ticketTable = new JTable(ticketModel);
        panel.add(new JScrollPane(ticketTable), BorderLayout.CENTER);

        btnAdd.addActionListener(e -> {
            String eventIdText = tfEventId.getText().trim();
            String type = tfType.getText().trim();
            String priceText = tfPrice.getText().trim();
            String quantityText = tfQuantity.getText().trim();

            if (eventIdText.isEmpty() || type.isEmpty() || priceText.isEmpty() || quantityText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required.");
                return;
            }

            try {
                int eventId = Integer.parseInt(eventIdText);
                double price = Double.parseDouble(priceText);
                int quantity = Integer.parseInt(quantityText);

                if (!eventBelongsToOrganizer(eventId)) {
                    JOptionPane.showMessageDialog(this, "You can only add tickets to your own events.");
                    return;
                }

                String sql = "INSERT INTO Tickets (EventID, Type, Price, Quantity) VALUES (?, ?, ?, ?)";

                try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                     PreparedStatement ps = conn.prepareStatement(sql)) {

                    ps.setInt(1, eventId);
                    ps.setString(2, type);
                    ps.setDouble(3, price);
                    ps.setInt(4, quantity);
                    ps.executeUpdate();

                    JOptionPane.showMessageDialog(this, "Ticket added successfully.");
                    tfEventId.setText("");
                    tfType.setText("");
                    tfPrice.setText("");
                    tfQuantity.setText("");

                    loadMyTickets();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Event ID, price, and quantity must be valid numbers.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
            }
        });

        btnRefresh.addActionListener(e -> loadMyTickets());

        loadMyTickets();
        return panel;
    }

    private void loadMyEvents() {
        eventModel.setRowCount(0);

        String sql = "SELECT EventID, Title, EventDate, EventTime, Location, ApprovalStatus " +
                "FROM Events WHERE OrganizerID = ? ORDER BY EventID DESC";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, organizerId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                eventModel.addRow(new Object[]{
                        rs.getInt("EventID"),
                        rs.getString("Title"),
                        rs.getDate("EventDate"),
                        rs.getTime("EventTime"),
                        rs.getString("Location"),
                        rs.getString("ApprovalStatus")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading events: " + ex.getMessage());
        }
    }

    private void loadMyTickets() {
        ticketModel.setRowCount(0);

        String sql = "SELECT t.TicketID, t.EventID, e.Title, t.Type, t.Price, t.Quantity " +
                "FROM Tickets t " +
                "JOIN Events e ON t.EventID = e.EventID " +
                "WHERE e.OrganizerID = ? " +
                "ORDER BY t.TicketID DESC";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, organizerId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ticketModel.addRow(new Object[]{
                        rs.getInt("TicketID"),
                        rs.getInt("EventID"),
                        rs.getString("Title"),
                        rs.getString("Type"),
                        rs.getBigDecimal("Price"),
                        rs.getInt("Quantity")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading tickets: " + ex.getMessage());
        }
    }

    private boolean eventBelongsToOrganizer(int eventId) {
        String sql = "SELECT 1 FROM Events WHERE EventID = ? AND OrganizerID = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, eventId);
            ps.setInt(2, organizerId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error checking event ownership: " + ex.getMessage());
            return false;
        }
    }

    private int getOrganizerIdByEmail(String email) {
        if (email == null || email.isBlank()) return -1;

        String sql = "SELECT UserID FROM Users WHERE Email = ? AND Role = 'Organizer'";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("UserID");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error finding organizer: " + ex.getMessage());
        }
        return -1;
    }
}
