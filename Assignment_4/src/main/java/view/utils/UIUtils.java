package view.utils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.ZoneId;
import java.util.*;
import java.util.List;

/**
 * Shared utility methods for UI creation and management
 */
public class UIUtils {

  // Common dimensions
  private static final Dimension LABEL_SIZE = new Dimension(100, 25);
  private static final Dimension FIELD_SIZE = new Dimension(300, 25);
  private static final Dimension BUTTON_SIZE = new Dimension(150, 30);
  private static final Dimension SMALL_BUTTON_SIZE = new Dimension(120, 30);

  /**
   * Creates a form row with label and components
   */
  public static JPanel createRow(String labelText, Component... fields) {
    JPanel p = new JPanel();
    p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));

    if (labelText != null && !labelText.isEmpty()) {
      JLabel label = new JLabel(labelText);
      applySize(label, LABEL_SIZE);
      p.add(label);
    } else {
      p.add(Box.createRigidArea(LABEL_SIZE));
    }

    for (Component field : fields) {
      if (!(field instanceof JScrollPane)) {
        applySize(field, FIELD_SIZE);
      }
      p.add(field);
      p.add(Box.createRigidArea(new Dimension(10, 0)));
    }

    p.add(Box.createHorizontalGlue());
    p.setAlignmentX(Component.LEFT_ALIGNMENT);
    p.setBorder(new EmptyBorder(5, 0, 5, 0));
    return p;
  }

  /**
   * Sets preferred, minimum and maximum size of a component
   */
  public static void applySize(Component c, Dimension size) {
    c.setPreferredSize(size);
    c.setMinimumSize(size);
    c.setMaximumSize(size);
  }

  /**
   * Wraps a panel in a scrollable container with border
   */
  public static JPanel wrapPanel(JPanel content) {
    JPanel wrapper = new JPanel(new BorderLayout());
    wrapper.setBorder(new EmptyBorder(10, 10, 10, 10));
    wrapper.add(content, BorderLayout.NORTH);
    return wrapper;
  }

  /**
   * Creates a section panel with title border and grid layout
   */
  public static JPanel createSectionPanel(String title, int rows, int cols) {
    JPanel panel = new JPanel();
    panel.setBorder(BorderFactory.createTitledBorder(title));
    panel.setLayout(new GridLayout(rows, cols, 0, 2));
    return panel;
  }

  /**
   * Returns standard label size
   */
  public static Dimension getLabelSize() {
    return LABEL_SIZE;
  }

  /**
   * Returns standard field size
   */
  public static Dimension getFieldSize() {
    return FIELD_SIZE;
  }

  /**
   * Returns standard button size
   */
  public static Dimension getButtonSize() {
    return BUTTON_SIZE;
  }

  /**
   * Returns small button size
   */
  public static Dimension getSmallButtonSize() {
    return SMALL_BUTTON_SIZE;
  }

  /**
   * Gets all available time zones as a sorted array
   */
  public static String[] getAvailableTimeZones() {
    // Get all available zone IDs
    Set<String> zoneIds = ZoneId.getAvailableZoneIds();

    // Sort them alphabetically
    List<String> zoneList = new ArrayList<>(zoneIds);
    Collections.sort(zoneList);

    return zoneList.toArray(new String[0]);
  }

  /**
   * Creates a timezone combo box with filtering capability
   */
  public static JComboBox<String> createTimeZoneComboBox() {
    String[] timeZones = getAvailableTimeZones();
    JComboBox<String> timeZoneCombo = new JComboBox<>(timeZones);
    timeZoneCombo.setEditable(true);
    timeZoneCombo.setSelectedItem("America/New_York");

    // Add a key listener to the editor component for filtering
    JTextField textField = (JTextField) timeZoneCombo.getEditor().getEditorComponent();
    textField.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyReleased(java.awt.event.KeyEvent evt) {
        String text = textField.getText();
        // Don't filter if empty
        if (text.length() == 0) {
          return;
        }

        // Start a SwingWorker to handle the filtering
        new javax.swing.SwingWorker<Void, Void>() {
          @Override
          protected Void doInBackground() {
            return null;
          }

          @Override
          protected void done() {
            // Create a new filtered model
            DefaultComboBoxModel<String> filteredModel = new DefaultComboBoxModel<>();
            for (String zone : timeZones) {
              if (zone.toLowerCase().contains(text.toLowerCase())) {
                filteredModel.addElement(zone);
              }
            }

            // Only update if we found matches
            if (filteredModel.getSize() > 0) {
              // Save current selection
              String currentText = textField.getText();
              int selectionStart = textField.getSelectionStart();
              int selectionEnd = textField.getSelectionEnd();

              // Update model
              timeZoneCombo.setModel(filteredModel);

              // Restore text and selection
              timeZoneCombo.getEditor().setItem(currentText);
              textField.setSelectionStart(selectionStart);
              textField.setSelectionEnd(selectionEnd);

              // Show popup if not visible
              if (!timeZoneCombo.isPopupVisible()) {
                timeZoneCombo.showPopup();
              }
            }
          }
        }.execute();
      }
    });

    return timeZoneCombo;
  }
}