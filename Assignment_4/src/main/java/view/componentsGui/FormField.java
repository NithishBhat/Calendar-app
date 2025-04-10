package view.componentsGui;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.util.Map;

/**
 * Encapsulates a form field with its label and component.
 * This class provides a unified interface for working with different types of
 * input components in forms, handling value extraction from various Swing components.
 * It simplifies the process of gathering user input from forms with multiple field types.
 */
public class FormField {
  /** The label or name of the form field. Used as the key in value maps. */
  private String label;

  /** The Swing component that provides the input functionality. */
  private JComponent inputComp;

  /**
   * Constructor for creating a new form field.
   *
   * @param label The label or name for this field, used as identifier in value maps
   * @param inputComp The Swing component that provides the user input functionality
   */
  public FormField(String label, JComponent inputComp) {
    this.label = label;
    this.inputComp = inputComp;
  }

  /**
   * Gets the label of this form field.
   *
   * @return The label or name of this field
   */
  public String getLabel() {
    return label;
  }

  /**
   * Gets the input component of this form field.
   *
   * @return The Swing component used for input
   */
  public JComponent getComponent() {
    return inputComp;
  }

  /**
   * Returns the user input as a String, handling different component types.
   * Currently supports:
   * <ul>
   *   <li>JTextField - Returns the text content</li>
   *   <li>JComboBox - Returns the selected item's string representation</li>
   *   <li>JCheckBox - Returns "true" or "false" based on selection state</li>
   *   <li>JTextArea - Returns the text content</li>
   *   <li>JRadioButton - Returns "true" or "false" based on selection state</li>
   * </ul>
   *
   * @return A string representation of the user input, or an empty string if the component type is not supported
   */
  public String getValue() {
    if (inputComp instanceof JTextField) {
      return ((JTextField) inputComp).getText();
    } else if (inputComp instanceof JComboBox) {
      Object selected = ((JComboBox<?>) inputComp).getSelectedItem();
      return selected != null ? selected.toString() : "";
    } else if (inputComp instanceof JCheckBox) {
      return Boolean.toString(((JCheckBox) inputComp).isSelected());
    } else if (inputComp instanceof JTextArea) {
      return ((JTextArea) inputComp).getText();
    } else if (inputComp instanceof JRadioButton) {
      return Boolean.toString(((JRadioButton) inputComp).isSelected());
    }
    // fallback if the component isn't one of the above
    return "";
  }

  /**
   * Adds this field's value to a values map with the label as the key.
   * This is a convenient method for collecting all form values into a single map.
   *
   * @param values The map to add this field's value to
   */
  public void addToValuesMap(Map<String, String> values) {
    values.put(label, getValue());
  }
}