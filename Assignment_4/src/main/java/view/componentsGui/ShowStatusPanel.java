package view.componentsGui;

import controller.EventController;
import view.utilsGui.CommandGenerator;
import view.utilsGui.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.time.LocalDate;
import java.util.Map;

/**
 * Panel for showing calendar status at a particular date/time
 */
public class ShowStatusPanel extends AbstractCalendarPanel {

  private CommandGenerator commandGenerator;
  private JTextField dateField;
  private JTextField timeField;

  public ShowStatusPanel(EventController controller, JTextArea commandPreviewArea,
                         CommandGenerator commandGenerator) {
    super(controller, commandPreviewArea);
    this.commandGenerator = commandGenerator;
  }

  @Override
  protected void initializeUI() {
    // Date and time section
    JPanel dateTimePanel = createSectionPanel("Status Date & Time", 2, 1);

    // Date field with format hint
    JPanel dateRow = new JPanel();
    dateRow.setLayout(new BoxLayout(dateRow, BoxLayout.X_AXIS));

    JLabel dateLabel = new JLabel("Date:");
    applySize(dateLabel, UIUtils.getLabelSize());
    dateRow.add(dateLabel);

    dateField = new JTextField(LocalDate.now().toString());
    applySize(dateField, new Dimension(160, 25));
    addFormField("StatusDate", dateField);
    dateRow.add(dateField);

    JLabel dateFormatHint = new JLabel("  (YYYY-MM-DD)");
    dateFormatHint.setFont(new Font(dateFormatHint.getFont().getName(), Font.ITALIC, 12));
    dateRow.add(dateFormatHint);

    dateRow.add(Box.createHorizontalGlue());
    dateRow.setAlignmentX(Component.LEFT_ALIGNMENT);
    dateRow.setBorder(new EmptyBorder(5, 0, 5, 0));

    dateTimePanel.add(dateRow);

    // Time field with format hint
    JPanel timeRow = new JPanel();
    timeRow.setLayout(new BoxLayout(timeRow, BoxLayout.X_AXIS));

    JLabel timeLabel = new JLabel("Time:");
    applySize(timeLabel, UIUtils.getLabelSize());
    timeRow.add(timeLabel);

    timeField = new JTextField("09:00");
    applySize(timeField, new Dimension(160, 25));
    addFormField("StatusTime", timeField);
    timeRow.add(timeField);

    JLabel timeFormatHint = new JLabel("  (HH:MM, 24-hour)");
    timeFormatHint.setFont(new Font(timeFormatHint.getFont().getName(), Font.ITALIC, 12));
    timeRow.add(timeFormatHint);

    timeRow.add(Box.createHorizontalGlue());
    timeRow.setAlignmentX(Component.LEFT_ALIGNMENT);
    timeRow.setBorder(new EmptyBorder(5, 0, 5, 0));

    dateTimePanel.add(timeRow);

    add(dateTimePanel);
    add(Box.createRigidArea(new Dimension(0, 15)));

    // Check status button
    JButton checkButton = new JButton(getButtonText());
    applySize(checkButton, UIUtils.getButtonSize());
    checkButton.addActionListener(e -> executeAction());

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonPanel.add(checkButton);
    add(buttonPanel);
  }

  @Override
  protected String generateCommand() {
    Map<String, String> values = getFormValues();

    values.put("Status Date:", dateField.getText());
    values.put("Status Time:", timeField.getText());

    return commandGenerator.showStatusCommand(values);
  }

  @Override
  protected String getButtonText() {
    return "Check Status";
  }
}