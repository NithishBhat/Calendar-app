package view.componentsgui;

import controller.EventController;
import view.utilsgui.CommandGenerator;
import view.utilsgui.UIUtils;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;
import java.util.Map;

/**
 * Panel for importing a calendar from a file.
 * Provides UI components for selecting a file to import and the format
 * of the file. Extends AbstractCalendarPanel to inherit common panel functionality.
 */
public class ImportCalendarPanel extends AbstractCalendarPanel {

  /** Generator for creating formatted command strings. */
  private CommandGenerator commandGenerator;

  /** Text field for displaying the selected file path. */
  private JTextField filenameField;

  /** Dropdown for selecting the import file format. */
  private JComboBox<String> formatCombo;

  /** Button to initiate the import action. */
  private JButton importButton;

  /**
   * Constructor for the import calendar panel.
   *
   * @param controller The event controller that processes commands
   * @param commandPreviewArea The shared text area for displaying command output
   * @param commandGenerator The generator for creating formatted command strings
   */
  public ImportCalendarPanel(EventController controller, JTextArea commandPreviewArea,
                             CommandGenerator commandGenerator) {
    super(controller, commandPreviewArea);
    this.commandGenerator = commandGenerator;
  }

  /**
   * Initializes the UI components for this panel.
   * Creates and arranges fields for file selection, format options,
   * and buttons for browsing and importing.
   */
  @Override
  protected void initializeUI() {
    // Import file section
    JPanel detailsPanel = createSectionPanel("Import Details", 2, 1);

    // Filename field
    filenameField = new JTextField("calendar.csv");
    applySize(filenameField, UIUtils.getFieldSize());
    addFormField("Filename", filenameField);
    detailsPanel.add(createRow("Filename:", filenameField));

    // File format combo
    JPanel formatPanel = createSectionPanel("Format Options", 1, 1);

    formatCombo = new JComboBox<>(new String[]{"CSV (.csv)"});
    applySize(formatCombo, UIUtils.getFieldSize());
    addFormField("Format", formatCombo);
    formatPanel.add(createRow("Import format:", formatCombo));

    add(formatPanel);
    add(Box.createRigidArea(new Dimension(0, 10)));

    // Import button
    importButton = new JButton("Import Calendar");
    applySize(importButton, UIUtils.getButtonSize());
    importButton.addActionListener(e -> executeAction());

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

    // Add "Browse" button to allow file selection
    JButton browseButton = new JButton("Browse");
    applySize(browseButton, UIUtils.getButtonSize());
    browseButton.addActionListener(e -> openFileChooser());
    buttonPanel.add(browseButton);

    buttonPanel.add(importButton);
    add(buttonPanel);
  }

  /**
   * Opens a file chooser dialog to select a file for import.
   * Sets the selected file path in the filename field.
   * The file chooser is configured to filter for CSV files.
   */
  private void openFileChooser() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setFileFilter(new FileNameExtensionFilter("CSV files", "csv"));
    int returnVal = fileChooser.showOpenDialog(this);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File selectedFile = fileChooser.getSelectedFile();
      filenameField.setText(selectedFile.getAbsolutePath());  // Set the filename in the text field
    }
  }

  /**
   * Generates a command string for importing a calendar based on the current field values.
   * Extracts just the filename (without the path) from the selected file
   * and uses the command generator to format it into a valid command.
   *
   * @return A formatted command string for importing a calendar from the specified file
   */
  @Override
  protected String generateCommand() {
    Map<String, String> values = getFormValues();

    // Get filename from the text field
    String filename = filenameField.getText();

    // Extract just the filename (without the path)
    String extractedFilename = new File(filename).getName();

    // Generate command using just the extracted filename
    values.put("Filename:", extractedFilename);

    return commandGenerator.importCalendarCommand(values);
  }

  /**
   * Returns the text to display on this panel's action button.
   *
   * @return The button text ("Import Calendar")
   */
  @Override
  protected String getButtonText() {
    return "Import Calendar";
  }
}