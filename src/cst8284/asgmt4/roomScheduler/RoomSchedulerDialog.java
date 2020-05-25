package cst8284.asgmt4.roomScheduler;

import cst8284.asgmt4.room.Room;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

public class RoomSchedulerDialog {

    private static final GridBagConstraints labelConstraints = new GridBagConstraints(
            0, GridBagConstraints.RELATIVE, 1, 1, 1, 1,
            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 10, 20), 0, 0);
    private static final GridBagConstraints textConstraints = new GridBagConstraints(
            1, GridBagConstraints.RELATIVE, 1, 1, 1, 1, // gridx, gridy, gridwidth, gridheight, weightx, weighty
            GridBagConstraints.EAST, 0, new Insets(5, 5, 5, 10), 20, 20); // anchor, fill, insets, ipadx, ipady
    private static final GridBagConstraints btnConstraints = new GridBagConstraints(
            0, GridBagConstraints.RELATIVE, 2, 1, 0.5, 0.5,
            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 10, 20), 0, 0);

    private static Panel cp = new Panel();
    private static final int labelWidth = 24;
    private static final Font defaultFont = new Font("SansSerif", Font.PLAIN, 20);
    private static final FlowLayout btnRow = new FlowLayout();
    private static final JPanel btnPanel = new JPanel();
    private static RoomScheduler roomScheduler;
    private static final Toolkit tk = Toolkit.getDefaultToolkit();
    private static final Dimension screenSize = tk.getScreenSize();
    private static final JTextArea scrollText = new JTextArea();

    private static ArrayList<String> listOfStrings = null;
//			private static File file = null;

    private static JFrame frame;

    public static void showAppointmentDialog(RoomScheduler roomScheduler1, Room room) {

        roomScheduler = roomScheduler1;
        frame = new JFrame();
        SimpleDateFormat format = new SimpleDateFormat("MMMM. dd, YYYY");

String dateString = format.format( new Date()   );  
        frame.setTitle("Room bookings for "+room.getRoomNumber()+" for "+ dateString);
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        int screenX = (int) screenSize.getWidth() / 2;
        int screenY = (int) (7 * screenSize.getHeight() / 8);

        if (new File("CurrentRoomBookings.book").exists()) {
            ArrayList<RoomBooking> array = roomScheduler.loadBookingsFromFile();
            roomScheduler.setRoomBookings(array);
            if (array.size() > 0) {
                reloadJTextArea(array.get(0).getTimeBlock().getStartTime());
            }
        }
        frame.add(getWestPanel(), BorderLayout.WEST);
        frame.add(getCenterPanel(scrollText, screenY), BorderLayout.CENTER);
        frame.setPreferredSize(new Dimension(screenX, screenY));
        frame.pack();
        frame.setVisible(true);
    }

    private static JTextField setRow(String label, char keyboardShortcut) {
        JLabel l;
        JTextField t;
        cp.add(l = new JLabel(label, SwingConstants.RIGHT), labelConstraints);
        l.setFont(defaultFont);
        l.setDisplayedMnemonic(keyboardShortcut);
        cp.add(t = new JTextField(labelWidth), textConstraints);
        t.setFont(defaultFont);
        t.setFocusAccelerator(keyboardShortcut);
        return t;
    }

    private static JButton setBtnRow(String label, char keyboardShortcut) {
        JButton btn = new JButton(label);
        btn.setFont(defaultFont);

        btn.setMnemonic(keyboardShortcut);
        btnPanel.add(btn);
        return btn;
    }

    public static JPanel getCenterPanel(JTextArea jta, int height) {
        JScrollPane centerPane = new JScrollPane(jta);
        centerPane.setPreferredSize(new Dimension(400, 7 * height / 8));
        JPanel jp = new JPanel();
        jp.add(centerPane);
        return jp;
    }

    public static JPanel getWestPanel() {
        JPanel controlPanel = new JPanel(new GridLayout(6, 1));
        JPanel westPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.weighty = 1;

        controlPanel.add(setWestPanelBtns("Add Booking ", e -> {
            RoomBookingDialog.showAppointmentDialog(roomScheduler, false);
        }));

        controlPanel.add(setWestPanelBtns("Display Existing Booking ", e -> {
            RoomBookingDialog.showAppointmentDialog(roomScheduler, true);
            return;
        }));

        controlPanel.add(setWestPanelBtns("Backup Bookings to File", e -> {
            boolean check = roomScheduler.saveBookingsToFile();
            if (check == true) {
                JOptionPane.showMessageDialog(null, "Backup Bookings to File success.");

            } else {
                JOptionPane.showMessageDialog(null, "Backup Bookings to File Fail.");
            }
            return;

        }));
        controlPanel.add(setWestPanelBtns("Load Bookings from file", e -> {
            if (new File("CurrentRoomBookings.book").exists()) {
                ArrayList<RoomBooking> array = roomScheduler.loadBookingsFromFile();
                roomScheduler.setRoomBookings(array);
                if (array.size() > 0) {
                    reloadJTextArea(array.get(0).getTimeBlock().getStartTime());
                    RoomBooking rb = array.get(0);
                    
                }
                JOptionPane.showMessageDialog(null, "Load Bookings from file success.");
            } else {
                JOptionPane.showMessageDialog(null, "Load Bookings from file Fail.");
            }

            return;
        }));
        controlPanel.add(setWestPanelBtns("Exit", e -> {
            frame.dispose();

            return;
        }));

        westPanel.add(controlPanel, gbc);
        return westPanel;
    }

    private static JButton setWestPanelBtns(String btnLabel, ActionListener act) {
        final Font font = new Font("SansSerif", Font.PLAIN, 20);
        JButton btn = new JButton(btnLabel);
        btn.setFont(font);
        btn.addActionListener(act);
        return btn;
    }

    // Adapted from: https://www.mkyong.com/swing/java-swing-jfilechooser-example/
    private static File getFileFromUser(String fileName) {
        File f = null;
        JFileChooser fc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        fc.setFileFilter(new FileNameExtensionFilter(".txt Files", "txt"));
        do {
            int returnValue = fc.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                f = fc.getSelectedFile();

            } else if (returnValue == JFileChooser.CANCEL_OPTION) {
                return null;
            }
        } while (true);
    }

    public static ArrayList<String> getListOfStrings() {
        //  return listOfStrings;
        return null;
    }

    public static void reloadJTextArea(Calendar cal) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 8, 0);
        scrollText.setText(roomScheduler.displayDayBookings(calendar));
    }

}
