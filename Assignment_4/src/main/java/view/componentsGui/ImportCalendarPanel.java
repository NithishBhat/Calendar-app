package view.componentsGui;

import controller.EventController;
import view.utilsGui.CommandGenerator;
import view.utilsGui.UIUtils;

import javax.swing.*;

import java.awt.*;
import java.io.File;
import java.util.Map;

/**
 * Panel for importing a calendar from a file
 */
public class ImportCalendarPanel extends AbstractCalendarPanel {

  private CommandGenerator commandGenerator;
  private JTextField filenameField;
  private JComboBox<String> formatCombo;
  private JButton importButton;  // Declare the import button

  public ImportCalendarPanel(EventController controller, JTextArea commandPreviewArea,
                             CommandGenerator commandGenerator) {
    super(controller, commandPreviewArea);
    this.commandGenerator = commandGenerator;
  }

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
   * Opens a file chooser to select a file.
   */
  private void openFileChooser() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV files", "csv"));
    int returnVal = fileChooser.showOpenDialog(this);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File selectedFile = fileChooser.getSelectedFile();
      filenameField.setText(selectedFile.getAbsolutePath());  // Set the filename in the text field
    }
  }

  @Override
  protected String generateCommand() {
    Map<String, String> values = getFormValues();

    // Get filename from the text field
    String filename = filenameField.getText();

    System.out.println("Selected File: " + filename);

    // Extract just the filename (without the path)
    String extractedFilename = new File(filename).getName();

    // Generate command using just the extracted filename
    values.put("Filename:", extractedFilename);

    return commandGenerator.importCalendarCommand(values);
  }

  @Override
  protected String getButtonText() {
    return "Import Calendar";
  }
}