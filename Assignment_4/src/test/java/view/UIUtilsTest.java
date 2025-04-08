package view;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;

import view.utils.UIUtils;

public class UIUtilsTest {

  private JTextField testField;
  private JLabel testLabel;
  private JButton testButton;

  @Before
  public void setUp() {
    testField = new JTextField();
    testLabel = new JLabel("Test Label");
    testButton = new JButton("Test Button");
  }

  @Test
  public void createRow_WithLabelAndField_ShouldCreateProperRow() {
    // When
    JPanel row = UIUtils.createRow("Test:", testField);

    // Then
    assertEquals(BoxLayout.X_AXIS, ((BoxLayout)row.getLayout()).getAxis());
    assertEquals(2, countVisibleComponents(row)); // Label + field (excluding rigid areas and glue)
    assertTrue(row.getComponent(0) instanceof JLabel);
    assertEquals("Test:", ((JLabel)row.getComponent(0)).getText());
    assertEquals(Component.LEFT_ALIGNMENT, row.getAlignmentX(), 0.001);
    assertTrue(row.getBorder() instanceof EmptyBorder);
  }

  @Test
  public void createRow_WithNoLabel_ShouldCreateRowWithSpacerInstead() {
    // When
    JPanel row = UIUtils.createRow("", testField);

    // Then
    assertTrue("First component should be a rigid area",
            row.getComponent(0) instanceof Box.Filler);
    assertEquals(1, countVisibleComponents(row)); // Only counting the field, not the rigid area
  }

  @Test
  public void createRow_WithMultipleFields_ShouldAddAllFields() {
    // When
    JPanel row = UIUtils.createRow("Fields:", testField, testButton);

    // Then
    assertEquals(3, countVisibleComponents(row)); // Label + 2 fields
    assertTrue(row.getComponent(0) instanceof JLabel);
    assertTrue(row.getComponent(1) instanceof JTextField);
    assertTrue(row.getComponent(3) instanceof JButton); // Index 3 due to rigid area at index 2
  }

  @Test
  public void applySize_ShouldSetAllSizeDimensions() {
    // Given
    Dimension testSize = new Dimension(200, 50);

    // When
    UIUtils.applySize(testField, testSize);

    // Then
    assertEquals(testSize, testField.getPreferredSize());
    assertEquals(testSize, testField.getMinimumSize());
    assertEquals(testSize, testField.getMaximumSize());
  }

  @Test
  public void wrapPanel_ShouldCreateBorderedPanel() {
    // Given
    JPanel content = new JPanel();

    // When
    JPanel wrapped = UIUtils.wrapPanel(content);

    // Then
    assertEquals(BorderLayout.class, wrapped.getLayout().getClass());
    assertTrue(wrapped.getBorder() instanceof EmptyBorder);
    assertEquals(content, wrapped.getComponent(0));
    assertEquals(BorderLayout.NORTH, ((BorderLayout)wrapped.getLayout()).getConstraints(content));
  }

  @Test
  public void createSectionPanel_ShouldCreatePanelWithTitleAndGrid() {
    // When
    JPanel section = UIUtils.createSectionPanel("Test Section", 3, 2);

    // Then
    assertTrue(section.getBorder() instanceof TitledBorder);
    assertEquals("Test Section", ((TitledBorder)section.getBorder()).getTitle());
    assertTrue(section.getLayout() instanceof GridLayout);
    GridLayout layout = (GridLayout)section.getLayout();
    assertEquals(3, layout.getRows());
    assertEquals(2, layout.getColumns());
  }

  @Test
  public void getStandardSizes_ShouldReturnNonNullDimensions() {
    // Then
    assertNotNull(UIUtils.getLabelSize());
    assertNotNull(UIUtils.getFieldSize());
    assertNotNull(UIUtils.getButtonSize());
    assertNotNull(UIUtils.getSmallButtonSize());
  }

  @Test
  public void getAvailableTimeZones_ShouldReturnSortedNonEmptyArray() {
    // When
    String[] timeZones = UIUtils.getAvailableTimeZones();

    // Then
    assertNotNull(timeZones);
    assertTrue(timeZones.length > 0);

    // Verify it's sorted
    String[] sortedCopy = Arrays.copyOf(timeZones, timeZones.length);
    Arrays.sort(sortedCopy);
    assertArrayEquals(sortedCopy, timeZones);
  }

  @Test
  public void createTimeZoneComboBox_ShouldCreateEditableComboBox() {
    // When
    JComboBox<String> comboBox = UIUtils.createTimeZoneComboBox();

    // Then
    assertTrue(comboBox.isEditable());
    assertNotNull(comboBox.getModel());
    assertTrue(comboBox.getItemCount() > 0);
    assertEquals("America/New_York", comboBox.getSelectedItem());

    // Verify the editor component has a KeyListener for filtering
    JTextField editor = (JTextField) comboBox.getEditor().getEditorComponent();
    boolean hasKeyListener = false;
    for (KeyListener listener : editor.getKeyListeners()) {
      if (listener instanceof java.awt.event.KeyAdapter) {
        hasKeyListener = true;
        break;
      }
    }
    assertTrue("Editor should have a KeyListener", hasKeyListener);
  }

  @Test
  public void testTimeZoneComboBoxFiltering() throws Exception {
    // Given
    JComboBox<String> comboBox = UIUtils.createTimeZoneComboBox();
    JTextField editor = (JTextField) comboBox.getEditor().getEditorComponent();
    int initialItemCount = comboBox.getItemCount();

    // When - set text and simulate key release
    editor.setText("London");

    // Simulate key event
    for (KeyListener listener : editor.getKeyListeners()) {
      listener.keyReleased(new KeyEvent(editor, KeyEvent.KEY_RELEASED,
              System.currentTimeMillis(), 0, KeyEvent.VK_N, 'n'));
    }

    // Give SwingWorker time to complete
    Thread.sleep(500);

    // Then
    // The filtered model should contain fewer items than the original
    // and all items should contain "London"
    int newItemCount = comboBox.getItemCount();
    assertTrue("Filtered model should have fewer items", newItemCount < initialItemCount);

    for (int i = 0; i < newItemCount; i++) {
      String item = comboBox.getItemAt(i);
      assertTrue("Item should contain 'London': " + item,
              item.toLowerCase().contains("london"));
    }
  }

  // Helper method to count non-spacer components
  private int countVisibleComponents(Container container) {
    int count = 0;
    for (Component comp : container.getComponents()) {
      // Skip spacers (Box.Filler) and glue
      if (!(comp instanceof Box.Filler)) {
        count++;
      }
    }
    return count;
  }
  // Add these tests to your UIUtilsTest.java class

  /**
   * Test to specifically cover the dropdown filtering and selection logic
   */
  @Test
  public void testTimeZoneComboBox_FilteringAndSelectionPreservation() throws Exception {
    // Create the combo box
    JComboBox<String> comboBox = UIUtils.createTimeZoneComboBox();
    JTextField editor = (JTextField) comboBox.getEditor().getEditorComponent();

    // Set text and selection
    editor.setText("America");
    editor.setSelectionStart(1);
    editor.setSelectionEnd(5);

    // Trigger filtering
    for (KeyListener listener : editor.getKeyListeners()) {
      listener.keyReleased(new KeyEvent(editor, KeyEvent.KEY_RELEASED,
              System.currentTimeMillis(), 0, KeyEvent.VK_A, 'a'));
    }

    // Give SwingWorker time to complete
    Thread.sleep(500);

    // Verify text and selection are preserved
    assertEquals("America", editor.getText());
    assertEquals(1, editor.getSelectionStart());
    assertEquals(5, editor.getSelectionEnd());

    // Verify popup is shown by checking visibility flag is changed
    // Note: We can't directly verify visibility in unit test
    comboBox.hidePopup(); // Ensure popup is hidden first

    // Trigger filtering again to force popup to show
    for (KeyListener listener : editor.getKeyListeners()) {
      listener.keyReleased(new KeyEvent(editor, KeyEvent.KEY_RELEASED,
              System.currentTimeMillis(), 0, KeyEvent.VK_M, 'm'));
    }

    // Cannot directly assert popup visibility in headless testing
    // But we can verify the code execution path
  }

  /**
   * Test to verify empty filter text behavior
   */
  @Test
  public void testTimeZoneComboBox_EmptyFilterTextDoesNothing() throws Exception {
    // Create the combo box
    JComboBox<String> comboBox = UIUtils.createTimeZoneComboBox();
    JTextField editor = (JTextField) comboBox.getEditor().getEditorComponent();
    int initialCount = comboBox.getItemCount();

    // Set empty text
    editor.setText("");

    // Trigger filtering
    for (KeyListener listener : editor.getKeyListeners()) {
      listener.keyReleased(new KeyEvent(editor, KeyEvent.KEY_RELEASED,
              System.currentTimeMillis(), 0, KeyEvent.VK_BACK_SPACE, '\b'));
    }

    // Give SwingWorker time to complete
    Thread.sleep(500);

    // Model should remain unchanged
    assertEquals(initialCount, comboBox.getItemCount());
  }

  /**
   * Test specifically targeting the JScrollPane handling in createRow
   */
  @Test
  public void createRow_WithScrollPane_ShouldNotApplySizeToScrollPane() {
    // Given
    JTextArea textArea = new JTextArea(5, 20);
    JScrollPane scrollPane = new JScrollPane(textArea);
    Dimension originalSize = scrollPane.getPreferredSize();

    // When
    JPanel row = UIUtils.createRow("Label:", scrollPane);

    // Then
    // Verify the scroll pane wasn't resized
    assertEquals(originalSize.width, scrollPane.getPreferredSize().width);
    assertEquals(originalSize.height, scrollPane.getPreferredSize().height);

    // Verify it was added to the panel
    boolean containsScrollPane = false;
    for (Component comp : row.getComponents()) {
      if (comp == scrollPane) {
        containsScrollPane = true;
        break;
      }
    }
    assertTrue("Row should contain the scroll pane", containsScrollPane);
  }

  /**
   * Test specifically targeting the sizing of regular components vs scroll panes
   */
  @Test
  public void createRow_WithMixedComponents_ShouldOnlyResizeNonScrollPanes() {
    // Given
    JTextField textField = new JTextField();
    JTextArea textArea = new JTextArea(5, 20);
    JScrollPane scrollPane = new JScrollPane(textArea);
    Dimension originalScrollPaneSize = scrollPane.getPreferredSize();

    // When
    JPanel row = UIUtils.createRow("Mixed:", textField, scrollPane);

    // Then
    // Verify the text field was resized to FIELD_SIZE
    assertEquals(UIUtils.getFieldSize(), textField.getPreferredSize());

    // Verify the scroll pane wasn't resized
    assertEquals(originalScrollPaneSize.width, scrollPane.getPreferredSize().width);
    assertEquals(originalScrollPaneSize.height, scrollPane.getPreferredSize().height);

    // Verify both components were added to the panel
    boolean containsTextField = false;
    boolean containsScrollPane = false;
    for (Component comp : row.getComponents()) {
      if (comp == textField) containsTextField = true;
      if (comp == scrollPane) containsScrollPane = true;
    }
    assertTrue("Row should contain the text field", containsTextField);
    assertTrue("Row should contain the scroll pane", containsScrollPane);
  }
}