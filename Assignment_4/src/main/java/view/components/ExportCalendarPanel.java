package view.components;

import controller.EventController;
import view.utils.CommandGenerator;
import view.utils.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Map;

/**
 * Panel for exporting a calendar to a file
 */
public class ExportCalendarPanel extends AbstractCalendarPanel {

  private CommandGenerator commandGenerator;
  private JTextField filenameField;
  private JTextField pathField;
  private JComboBox<String> formatCombo;

  public ExportCalendarPanel(EventController controller, JTextArea commandPreviewArea,
                             CommandGenerator commandGenerator) {
    super(controller, commandPreviewArea);
    this.commandGenerator = commandGenerator;
  }

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

  @Override
  protected String getButtonText() {
    return "Export Calendar";
  }
}