package view.components;


import controller.EventController;
import view.models.FormField;
import view.utils.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Base class for all calendar UI panels.
 * Provides common functionality and enforces consistent structure.
 */
public abstract class AbstractCalendarPanel extends JPanel {

  // Common components
  protected List<FormField> formFields;
  protected JTextArea commandPreviewArea;
  protected EventController controller;

  /**
   * Constructor
   *
   * @param controller The event controller
   * @param commandPreviewArea The shared command preview area
   */
  public AbstractCalendarPanel(EventController controller, JTextArea commandPreviewArea) {
    this.controller = controller;
    this.commandPreviewArea = commandPreviewArea;
    this.formFields = new ArrayList<>();

    // Set up panel
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    setBorder(new EmptyBorder(10, 10, 10, 10));

    // Initialize UI components
    initializeUI();
  }

  /**
   * Initialize the panel UI components.
   * Must be implemented by subclasses.
   */
  protected abstract void initializeUI();

  /**
   * Generate the command string for this panel's action.
   * Must be implemented by subclasses.
   *
   * @return The command string
   */
  protected abstract String generateCommand();

  /**
   * Get the button text for this panel.
   * Can be overridden by subclasses if needed.
   *
   * @return The button text
   */
  protected String getButtonText() {
    return "Run Command";
  }

  /**
   * Add a form field to this panel.
   *
   * @param label The field label
   * @param component The UI component
   * @return The created FormField object
   */
  protected FormField addFormField(String label, JComponent component) {
    FormField field = new FormField(label, component);
    formFields.add(field);
    return field;
  }

  /**
   * Create a section panel with title border.
   *
   * @param title The section title
   * @param rows The number of rows in the grid
   * @param cols The number of columns in the grid
   * @return The created section panel
   */
  protected JPanel createSectionPanel(String title, int rows, int cols) {
    return UIUtils.createSectionPanel(title, rows, cols);
  }

  /**
   * Create a row with label and components.
   *
   * @param label The row label
   * @param components The UI components to add
   * @return The created row panel
   */
  protected JPanel createRow(String label, Component... components) {
    return UIUtils.createRow(label, components);
  }

  /**
   * Create the action button for this panel.
   *
   * @return The action button
   */
  protected JButton createActionButton() {
    JButton button = new JButton(getButtonText());
    UIUtils.applySize(button, UIUtils.getButtonSize());
    button.addActionListener(e -> executeAction());

    // Create a panel to hold the button
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonPanel.add(button);
    return button;
  }

  /**
   * Execute the panel's action.
   * Default implementation generates and executes the command.
   */
  protected void executeAction() {
    // Generate the command
    String command = generateCommand();

    // Execute the command without showing it in the preview area
    if (controller != null) {
      controller.filterCommand(command);
    }
  }
  /**
   * Get values from all form fields.
   *
   * @return A map of field labels to values
   */
  protected Map<String, String> getFormValues() {
    Map<String, String> values = new LinkedHashMap<>();

    for (FormField field : formFields) {
      field.addToValuesMap(values);
    }

    return values;
  }

  /**
   * Apply a consistent size to a component.
   *
   * @param component The component to size
   * @param size The dimension to apply
   */
  protected void applySize(Component component, Dimension size) {
    UIUtils.applySize(component, size);
  }
}