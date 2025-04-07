package view.models;

import javax.swing.*;
import java.util.Map;

/**
 * Encapsulates a form field with its label and component.
 * Handles getting values from different component types.
 */
public class FormField {
  private String label;
  private JComponent inputComp;

  public FormField(String label, JComponent inputComp) {
    this.label = label;
    this.inputComp = inputComp;
  }

  public String getLabel() {
    return label;
  }

  public JComponent getComponent() {
    return inputComp;
  }

  /**
   * Returns the user input as a String (handles JTextField, JComboBox, JCheckBox).
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
   * Adds this field's value to a values map with the label as the key
   */
  public void addToValuesMap(Map<String, String> values) {
    values.put(label, getValue());
  }
}