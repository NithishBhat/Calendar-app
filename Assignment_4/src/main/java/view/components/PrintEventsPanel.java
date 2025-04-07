package view.components;

import controller.EventController;
import view.utils.CommandGenerator;
import view.utils.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.time.LocalDate;
import java.util.Map;

/**
 * Panel for printing events by date or in a date range
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
  private JPanel dateTimeFieldsPanel;

  public PrintEventsPanel(EventController controller, JTextArea commandPreviewArea,
                          CommandGenerator commandGenerator) {
    super(controller, commandPreviewArea);
    this.commandGenerator = commandGenerator;
  }

  @Override
  protected void initializeUI() {
    // Print Mode Selection
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

    // Date Selection with CardLayout
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

    // From Row
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

    // To Row
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

    // Format hint
    JLabel formatHint = new JLabel("Format: Date (YYYY-MM-DD), Time (HH:MM, 24-hour)");
    formatHint.setFont(new Font(formatHint.getFont().getName(), Font.ITALIC, 12));
    formatHint.setAlignmentX(Component.LEFT_ALIGNMENT);
    add(formatHint);

    // Toggle card layout
    CardLayout cardLayout = (CardLayout) datePanel.getLayout();
    printOnDateRB.addActionListener(e -> cardLayout.show(datePanel, "onDate"));
    printIntervalRB.addActionListener(e -> cardLayout.show(datePanel, "range"));

    // Add space before button
    add(Box.createRigidArea(new Dimension(0, 15)));

    // Run Button
    JButton runBtn = new JButton(getButtonText());
    applySize(runBtn, UIUtils.getButtonSize());
    runBtn.addActionListener(e -> executeAction());

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonPanel.add(runBtn);
    add(buttonPanel);
  }

  @Override
  protected String generateCommand() {
    Map<String, String> values = getFormValues();

    // Prepare values based on selected mode
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

  @Override
  protected String getButtonText() {
    return "Print Events";
  }
}