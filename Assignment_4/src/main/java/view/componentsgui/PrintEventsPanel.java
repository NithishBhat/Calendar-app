package view.componentsgui;

import controller.EventController;
import view.utilsgui.CommandGenerator;
import view.utilsgui.UIUtils;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.ButtonGroup;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.border.EmptyBorder;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FlowLayout;

import java.time.LocalDate;
import java.util.Map;

/**
 * Panel for printing events either on a specific date or within a date-time range.
 * Provides radio button options to choose the print mode and inputs for date/time.
 * Uses a card layout to toggle between date-only and date-time range input modes.
 */
public class PrintEventsPanel extends AbstractCalendarPanel {

  private CommandGenerator commandGenerator;
  private JRadioButton printOnDateRB;
  private JRadioButton printIntervalRB;
  private JTextField printOnDateField;
  private JTextField fromDateField;
  private JTextField fromTimeField;
  private JTextField toDateField;
  private JTextField toTimeField;

  /**
   * Constructs a PrintEventsPanel with the provided controller, command preview area,
   * and command generator.
   *
   * @param controller           the EventController to handle commands
   * @param commandPreviewArea   the JTextArea to preview commands
   * @param commandGenerator     the command generator utility
   */
  public PrintEventsPanel(EventController controller, JTextArea commandPreviewArea,
                          CommandGenerator commandGenerator) {
    super(controller, commandPreviewArea);
    this.commandGenerator = commandGenerator;
  }

  /**
   * Initializes the UI components of the panel, including print mode selection,
   * date and time input fields, and the action button.
   */
  @Override
  protected void initializeUI() {
    JPanel modePanel = createSectionPanel("Print Mode", 2, 1);

    printOnDateRB = new JRadioButton("Print events on a specific day");
    printOnDateRB.setSelected(true);
    addFormField("PrintOnDateRB", printOnDateRB);

    printIntervalRB = new JRadioButton("Print events in a date-time range");
    addFormField("PrintIntervalRB", printIntervalRB);

    ButtonGroup radioGroup = new ButtonGroup();
    radioGroup.add(printOnDateRB);
    radioGroup.add(printIntervalRB);

    modePanel.add(printOnDateRB);
    modePanel.add(printIntervalRB);

    add(modePanel);
    add(Box.createRigidArea(new Dimension(0, 10)));

    JPanel datePanel = new JPanel(new CardLayout());
    datePanel.setBorder(BorderFactory.createTitledBorder("Date Selection"));

    // On Date Panel
    JPanel onDatePanel = new JPanel();
    onDatePanel.setLayout(new BoxLayout(onDatePanel, BoxLayout.X_AXIS));

    JLabel dateLabel = new JLabel("Date:");
    applySize(dateLabel, UIUtils.getLabelSize());
    onDatePanel.add(dateLabel);

    printOnDateField = new JTextField(LocalDate.now().toString());
    applySize(printOnDateField, new Dimension(180, 25));
    addFormField("PrintOnDate", printOnDateField);
    onDatePanel.add(printOnDateField);

    JLabel dateFormatHint = new JLabel("  (YYYY-MM-DD)");
    dateFormatHint.setFont(new Font(dateFormatHint.getFont().getName(), Font.ITALIC, 12));
    onDatePanel.add(dateFormatHint);

    onDatePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
    onDatePanel.setBorder(new EmptyBorder(5, 0, 5, 0));
    datePanel.add(onDatePanel, "onDate");

    // Interval Panel
    JPanel rangePanel = new JPanel();
    rangePanel.setLayout(new BoxLayout(rangePanel, BoxLayout.Y_AXIS));

    JPanel fromRow = new JPanel();
    fromRow.setLayout(new BoxLayout(fromRow, BoxLayout.X_AXIS));
    JLabel fromLabel = new JLabel("From:");
    applySize(fromLabel, UIUtils.getLabelSize());
    fromRow.add(fromLabel);

    fromDateField = new JTextField(LocalDate.now().toString());
    applySize(fromDateField, new Dimension(180, 25));
    addFormField("FromDate", fromDateField);
    fromRow.add(fromDateField);

    fromTimeField = new JTextField("09:00");
    applySize(fromTimeField, new Dimension(80, 25));
    addFormField("FromTime", fromTimeField);
    fromRow.add(fromTimeField);

    fromRow.setAlignmentX(Component.LEFT_ALIGNMENT);
    fromRow.setBorder(new EmptyBorder(5, 0, 5, 0));
    rangePanel.add(fromRow);

    JPanel toRow = new JPanel();
    toRow.setLayout(new BoxLayout(toRow, BoxLayout.X_AXIS));
    JLabel toLabel = new JLabel("To:");
    applySize(toLabel, UIUtils.getLabelSize());
    toRow.add(toLabel);

    toDateField = new JTextField(LocalDate.now().toString());
    applySize(toDateField, new Dimension(180, 25));
    addFormField("ToDate", toDateField);
    toRow.add(toDateField);

    toTimeField = new JTextField("17:00");
    applySize(toTimeField, new Dimension(80, 25));
    addFormField("ToTime", toTimeField);
    toRow.add(toTimeField);

    toRow.setAlignmentX(Component.LEFT_ALIGNMENT);
    toRow.setBorder(new EmptyBorder(5, 0, 5, 0));
    rangePanel.add(toRow);

    datePanel.add(rangePanel, "range");
    add(datePanel);
    add(Box.createRigidArea(new Dimension(0, 5)));

    JLabel formatHint = new JLabel("Format: Date (YYYY-MM-DD), Time (HH:MM, 24-hour)");
    formatHint.setFont(new Font(formatHint.getFont().getName(), Font.ITALIC, 12));
    formatHint.setAlignmentX(Component.LEFT_ALIGNMENT);
    add(formatHint);

    CardLayout cardLayout = (CardLayout) datePanel.getLayout();
    printOnDateRB.addActionListener(e -> cardLayout.show(datePanel, "onDate"));
    printIntervalRB.addActionListener(e -> cardLayout.show(datePanel, "range"));

    add(Box.createRigidArea(new Dimension(0, 15)));

    JButton runBtn = new JButton(getButtonText());
    applySize(runBtn, UIUtils.getButtonSize());
    runBtn.addActionListener(e -> executeAction());

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonPanel.add(runBtn);
    add(buttonPanel);
  }

  /**
   * Gathers form values and generates the command string based on selected print mode.
   *
   * @return the generated command string
   */
  @Override
  protected String generateCommand() {
    Map<String, String> values = getFormValues();

    if (printOnDateRB.isSelected()) {
      values.put("Print Events on:", printOnDateField.getText());
      values.put("Interval From Date:", "");
      values.put("Interval From Time:", "");
      values.put("Interval To Date:", "");
      values.put("Interval To Time:", "");
    } else {
      values.put("Print Events on:", "");
      values.put("Interval From Date:", fromDateField.getText());
      values.put("Interval From Time:", fromTimeField.getText());
      values.put("Interval To Date:", toDateField.getText());
      values.put("Interval To Time:", toTimeField.getText());
    }

    return commandGenerator.printEventsCommand(values);
  }

  /**
   * Returns the label for the execute button.
   *
   * @return the button label
   */
  @Override
  protected String getButtonText() {
    return "Print Events";
  }
}