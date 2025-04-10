package view.componentsGui;

import controller.EventController;
import view.utilsGui.UIUtils;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Base class for all calendar UI panels.
 * Provides common functionality and enforces consistent structure.
 */
public abstract class AbstractCalendarPanel extends JPanel {

  /** List of form fields contained in this panel. */
  protected List<FormField> formFields;

  /** Text area for displaying command previews. */
  protected JTextArea commandPreviewArea;

  /** Reference to the event controller for executing commands. */
  protected EventController controller;

  /**
   * Constructor for the abstract calendar panel.
   *
   * @param controller The event controller that will process generated commands
   * @param commandPreviewArea The shared command preview area for displaying commands
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
   * Must be implemented by subclasses to create their specific UI elements.
   */
  protected abstract void initializeUI();

  /**
   * Generate the command string for this panel's action.
   * Must be implemented by subclasses to create the appropriate command syntax.
   *
   * @return The command string to be executed
   */
  protected abstract String generateCommand();

  /**
   * Get the button text for this panel.
   * Can be overridden by subclasses if needed for custom button labels.
   *
   * @return The button text to display
   */
  protected String getButtonText() {
    return "Run Command";
  }

  /**
   * Add a form field to this panel.
   * Creates a FormField object and adds it to the collection of fields.
   *
   * @param label The field label to display
   * @param component The UI component for input
   * @return The created FormField object
   */
  protected FormField addFormField(String label, JComponent component) {
    FormField field = new FormField(label, component);
    formFields.add(field);
    return field;
  }

  /**
   * Create a section panel with title border.
   * Delegates to UIUtils to maintain consistent styling.
   *
   * @param title The section title to display
   * @param rows The number of rows in the grid
   * @param cols The number of columns in the grid
   * @return The created section panel
   */
  protected JPanel createSectionPanel(String title, int rows, int cols) {
    return UIUtils.createSectionPanel(title, rows, cols);
  }

  /**
   * Create a row with label and components.
   * Delegates to UIUtils to maintain consistent styling.
   *
   * @param label The row label to display
   * @param components The UI components to add to the row
   * @return The created row panel
   */
  protected JPanel createRow(String label, Component... components) {
    return UIUtils.createRow(label, components);
  }

  /**
   * Create the action button for this panel.
   * Sets up the button with appropriate styling and action listener.
   *
   * @return The configured action button
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
   * Can be overridden by subclasses for custom action behavior.
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
   * Collects values from all form fields into a map for processing.
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
   * Delegates to UIUtils to maintain consistent styling.
   *
   * @param component The component to size
   * @param size The dimension to apply
   */
  protected void applySize(Component component, Dimension size) {
    UIUtils.applySize(component, size);
  }
}