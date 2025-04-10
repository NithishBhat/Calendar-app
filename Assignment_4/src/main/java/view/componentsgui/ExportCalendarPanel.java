package view.componentsgui;

import controller.EventController;
import view.utilsgui.CommandGenerator;
import view.utilsgui.UIUtils;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Map;

/**
 * Panel for exporting a calendar to a file in different formats.
 * Provides UI components for specifying export details including filename,
 * location, and format. Extends AbstractCalendarPanel to inherit common panel functionality.
 */
public class ExportCalendarPanel extends AbstractCalendarPanel {

  /** Generator for creating formatted command strings. */
  private CommandGenerator commandGenerator;

  /** Text field for entering the export filename. */
  private JTextField filenameField;

  /** Text field for entering the export file path. */
  private JTextField pathField;

  /** Dropdown for selecting the export file format. */
  private JComboBox<String> formatCombo;

  /**
   * Constructor for the export calendar panel.
   *
   * @param controller The event controller that processes commands
   * @param commandPreviewArea The shared text area for displaying command output
   * @param commandGenerator The generator for creating formatted command strings
   */
  public ExportCalendarPanel(EventController controller, JTextArea commandPreviewArea,
                             CommandGenerator commandGenerator) {
    super(controller, commandPreviewArea);
    this.commandGenerator = commandGenerator;
  }

  /**
   * Initializes the UI components for this panel.
   * Creates and arranges fields for export details including filename,
   * path selection with browse button, and format options.
   */
  @Override
  protected void initializeUI() {
    // File details section
    JPanel detailsPanel = createSectionPanel("Export Details", 2, 1);

    // Filename field
    filenameField = new JTextField("calendar.csv");
    applySize(filenameField, UIUtils.getFieldSize());
    addFormField("Filename", filenameField);
    detailsPanel.add(createRow("Filename:", filenameField));

    // Path field with browse button - don't use createRow here to avoid clipping
    JPanel pathRow = new JPanel();
    pathRow.setLayout(new BoxLayout(pathRow, BoxLayout.X_AXIS));
    JLabel pathLabel = new JLabel("Save to path:");
    applySize(pathLabel, UIUtils.getLabelSize());
    pathRow.add(pathLabel);

    pathField = new JTextField();
    addFormField("Path", pathField);

    JButton browseButton = new JButton("Browse...");
    applySize(browseButton, new Dimension(90, 25));

    // Use a panel for the combined field and button
    JPanel pathWithButtonPanel = new JPanel(new BorderLayout(5, 0));
    pathWithButtonPanel.add(pathField, BorderLayout.CENTER);
    pathWithButtonPanel.add(browseButton, BorderLayout.EAST);

    // Make sure the path panel doesn't get resized too small
    Dimension pathPanelSize = new Dimension(250, 25);
    pathWithButtonPanel.setMinimumSize(pathPanelSize);
    pathWithButtonPanel.setPreferredSize(pathPanelSize);

    pathRow.add(pathWithButtonPanel);
    pathRow.add(Box.createHorizontalGlue());

    // Set alignment and add border
    pathRow.setAlignmentX(Component.LEFT_ALIGNMENT);
    pathRow.setBorder(new EmptyBorder(5, 0, 5, 0));

    detailsPanel.add(pathRow);

    // Add browse button action
    browseButton.addActionListener(e -> {
      JFileChooser fileChooser = new JFileChooser();
      fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      int returnVal = fileChooser.showOpenDialog(this);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        pathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
      }
    });

    add(detailsPanel);
    add(Box.createRigidArea(new Dimension(0, 10)));

    // Format options section
    JPanel formatPanel = createSectionPanel("Format Options", 1, 1);

    // File format combo
    formatCombo = new JComboBox<>(new String[]{"CSV (.csv)", "iCalendar (.ics)"});
    applySize(formatCombo, UIUtils.getFieldSize());
    addFormField("Format", formatCombo);
    formatPanel.add(createRow("Export format:", formatCombo));

    add(formatPanel);
    add(Box.createRigidArea(new Dimension(0, 10)));

    // Export button
    JButton exportButton = new JButton(getButtonText());
    applySize(exportButton, UIUtils.getButtonSize());
    exportButton.addActionListener(e -> executeAction());

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonPanel.add(exportButton);
    add(buttonPanel);
  }

  /**
   * Generates a command string for exporting a calendar based on the current field values.
   * Handles file extension validation and path combination, then uses the command
   * generator to format the values into a valid command.
   *
   * @return A formatted command string for exporting a calendar to the specified file
   */
  @Override
  protected String generateCommand() {
    Map<String, String> values = getFormValues();

    // Get filename with proper extension based on selected format
    String filename = filenameField.getText();
    String format = formatCombo.getSelectedItem().toString();

    // Add extension if not already present
    if (format.contains("CSV") && !filename.toLowerCase().endsWith(".csv")) {
      filename += ".csv";
    } else if (format.contains("iCalendar") && !filename.toLowerCase().endsWith(".ics")) {
      filename += ".ics";
    }

    // Get path, use empty string if not specified
    String path = pathField.getText();

    // Create complete path
    String fullPath = path.isEmpty() ? filename : path + "/" + filename;

    // Generate command using just the filename (path handling is internal)
    values.put("Filename:", fullPath);

    return commandGenerator.exportCalendarCommand(values);
  }

  /**
   * Returns the text to display on this panel's action button.
   *
   * @return The button text ("Export Calendar")
   */
  @Override
  protected String getButtonText() {
    return "Export Calendar";
  }
}