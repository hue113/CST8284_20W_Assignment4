package cst8284.asgmt4.roomScheduler;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

/* Adapted, with considerable modification, from 
 * http://www.java2s.com/Code/Java/Swing-JFC/TextAcceleratorExample.htm,
 * which is sloppy code and should not be emulated.
 */
public class RoomBookingDialog {

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
    private static FlowLayout btnRow = new FlowLayout();
    private static JPanel btnPanel = new JPanel();
    private static RoomScheduler roomScheduler;

    public static void showAppointmentDialog(RoomScheduler roomScheduler1, boolean isShow) {
        cp = new Panel();
        btnRow = new FlowLayout();
        btnPanel = new JPanel();
        roomScheduler = roomScheduler1;
        JFrame f = new JFrame("");
        cp.setLayout(new GridBagLayout());

        // Set the first seven rows with Label/TextField
        JTextField clientDateInput = setRow("Booking Date (YYYYMMDD):", 'e');
        JTextField clientStartTimeInput = setRow("Start Time (2 p.m. or 14:00):", 't');
        JTextField clientendTimeInput = setRow("End Time (2 p.m. or 14:00):", 't');
        JTextField clientNameInput = setRow("Client Name (FirstName LastName):", 'n');
        JTextField clientPhoneInput = setRow("Phone Number (e.g. 613-555-1212):", 'p');
        JTextField clientOrganizaInput = setRow("Organization (optional):", 'o');
        JTextField clientCategoryInput = setRow("Event Category:", 'c');
        JTextField clientDescriptionInput = setRow("Description:", 'd');

        // Load the buttons across the bottom  
        btnPanel.setLayout(btnRow);
        JButton searchButton = setBtnRow("   Search   ", 'r');
        JButton saveButton = setBtnRow(!isShow? "    Save    ":"    Change    ", 's');
        JButton deleteButton = setBtnRow("   Delete   ", 'l');
        JButton exitButton = setBtnRow("    Exit    ", 'x');
        if (isShow) {
            saveButton.setEnabled(false);
            deleteButton.setEnabled(false);
            clientNameInput.setEditable(false);
            clientPhoneInput.setEditable(false);
            clientOrganizaInput.setEditable(false);
            clientCategoryInput.setEditable(false);
            clientDescriptionInput.setEditable(false);
            clientendTimeInput.setEditable(false);
        } else {
            searchButton.setEnabled(false);
        }

        cp.add(btnPanel, btnConstraints);

        saveButton.addActionListener(e -> {
            String fullName = clientNameInput.getText();
            String phoneNumber = clientPhoneInput.getText();
            String date = clientDateInput.getText();
            String Starttime = clientStartTimeInput.getText();
            String EndTime = clientendTimeInput.getText();
            String category = clientCategoryInput.getText();
            String Organization = clientOrganizaInput.getText();
            String Description = clientDescriptionInput.getText();

            RoomBooking rb = roomScheduler.makeBookingFromUserInput(fullName, phoneNumber, Organization, category, Description, Starttime, EndTime, date);
            if (rb != null) {
                if (!isShow) {
                    roomScheduler.saveRoomBooking(rb);
                    if (roomScheduler.getRoomBookings().size() > 0) {
                        Calendar time = roomScheduler.getRoomBookings().get(0).getTimeBlock().getStartTime();
                        RoomSchedulerDialog.reloadJTextArea(time);
                    }
                } else {
                    Calendar cal = roomScheduler.makeCalendarFromUserInput(null, true, clientStartTimeInput.getText(), clientDateInput.getText());
                    roomScheduler.changeBooking(cal, Starttime, EndTime, date);
                    if (roomScheduler.getRoomBookings().size() > 0) {
                        RoomSchedulerDialog.reloadJTextArea(roomScheduler.getRoomBookings().get(0).getTimeBlock().getStartTime());
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "Cannot save room booking");
            }

            //            int reply = JOptionPane.showConfirmDialog(null, "Would you like to save this appointment?", "Please confirm",
            //                    JOptionPane.YES_NO_OPTION);
            //            if (reply == JOptionPane.YES_OPTION) {
            //                String activityType = (String) JOptionPane.showInputDialog(null, "Choose now...",
            //                        "The Choice of a Lifetime", JOptionPane.QUESTION_MESSAGE, null,
            //                        scheduler.getEmployee().getActivityType().toArray(), // Array of choices
            //                        scheduler.getEmployee().getActivityType().get(0)); // Initial choice
            //                if (scheduler.saveAppointment(scheduler.makeAppointmentFromUserInput(fullName, phoneNumber, date, time, category, activityType))) {
            //                    JOptionPane.showMessageDialog(null, "Your appointment has been saved succesfully");
            //                } else {
            //                    JOptionPane.showMessageDialog(null, "An appointment with this time has already existed");
            //                }
            //            }
        }
        );
        searchButton.addActionListener(e
                -> {
            Calendar cal = roomScheduler.makeCalendarFromUserInput(null, true, clientStartTimeInput.getText(), clientDateInput.getText());
            RoomBooking apt = roomScheduler.findBooking(cal);
            if (apt != null) {
                JOptionPane.showMessageDialog(null, apt.toString());

                clientNameInput.setText(apt.getContactInfo().getFirstName() + " " + apt.getContactInfo().getLastName());
                clientPhoneInput.setText(apt.getContactInfo().getPhoneNumber());

                //  clientDateInput.setText(apt.getTimeBlock().getStartTime().get(Calendar.DAY_OF_MONTH) + "" + apt.getTimeBlock().getStartTime().get(Calendar.MONTH) + "" + apt.getTimeBlock().getStartTime().get(Calendar.YEAR));
                //   clientStartTimeInput.setText(apt.getTimeBlock().getStartTime().get(Calendar.HOUR_OF_DAY) + "");
                clientendTimeInput.setText(apt.getTimeBlock().getEndTime().get(Calendar.HOUR_OF_DAY) + ":00");
                clientCategoryInput.setText(apt.getActivity().getCategory());
                clientOrganizaInput.setText(apt.getContactInfo().getOrganization());
                clientDescriptionInput.setText(apt.getActivity().getDescription());

                saveButton.setEnabled(true);
                deleteButton.setEnabled(true);
                clientNameInput.setEditable(true);
                clientPhoneInput.setEditable(true);
                clientOrganizaInput.setEditable(true);
                clientCategoryInput.setEditable(true);
                clientDescriptionInput.setEditable(true);
                clientendTimeInput.setEditable(true);

            } else {
                JOptionPane.showMessageDialog(null, "Cannot find room booking");
            }
            // roomScheduler.saveRoomBooking();
//            Calendar cal = Scheduler.makeCalendarFromUserInput(false, clientDateInput.getText(), clientTimeInput.getText());
//            Appointment apt = scheduler.findAppointment(cal);
//            if (apt != null) {
//                JOptionPane.showMessageDialog(null, apt.toString());
//            } else {
//                JOptionPane.showMessageDialog(null, "Cannot find appointment");
//            }

        }
        );

        deleteButton.addActionListener(e
                -> {
            Calendar cal = roomScheduler.makeCalendarFromUserInput(null, true, clientStartTimeInput.getText(), clientDateInput.getText());
            boolean check = roomScheduler.deleteBooking(cal);
            if (check) {
                JOptionPane.showMessageDialog(null, " delete room booking success");
                if (roomScheduler.getRoomBookings().size() > 0) {
                    RoomSchedulerDialog.reloadJTextArea(roomScheduler.getRoomBookings().get(0).getTimeBlock().getStartTime());
                }
            } else {
                JOptionPane.showMessageDialog(null, "Cannot delete room booking");
            }
//            Calendar cal = Scheduler.makeCalendarFromUserInput(false, clientDateInput.getText(), clientTimeInput.getText());
//            Appointment apt = scheduler.findAppointment(cal);
//            if (apt != null) {
//
//                int delete = JOptionPane.showConfirmDialog(null, "Would you like to delete this appointment?", "Please confirm",
//                        JOptionPane.YES_NO_OPTION);
//                if (delete == JOptionPane.YES_OPTION) {
////	   	        	DeleteAppointmentDialog.showChangeAppointmentDialog(scheduler, apt);
//                 //   scheduler.deleteAppointment(cal);
//                    JOptionPane.showMessageDialog(null, "Your appointment has been deleted");
//                    f.dispose();
//                }
//
//            }

        }
        );

        exitButton.addActionListener(e
                -> {
            f.dispose();
        }
        );
        f.add(cp);

        f.pack();

        // Close dialog
        f.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent evt) {
                f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            }
        }
        );

        f.setVisible(true);
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

}
