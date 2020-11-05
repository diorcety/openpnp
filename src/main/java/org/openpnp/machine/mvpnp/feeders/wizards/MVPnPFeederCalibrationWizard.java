package org.openpnp.machine.mvpnp.feeders.wizards;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import org.openpnp.gui.MainFrame;
import org.openpnp.gui.support.AbstractConfigurationWizard;
import org.openpnp.gui.support.ApplyResetBindingListener;
import org.openpnp.gui.support.IntegerConverter;
import org.openpnp.gui.support.Wizard;
import org.openpnp.gui.support.WizardContainer;
import org.openpnp.machine.mvpnp.driver.MVPnPFeederDriver;
import org.openpnp.machine.mvpnp.feeders.MVPnPAutoFeeder;
import org.openpnp.machine.reference.ReferenceActuator;
import org.openpnp.model.Configuration;
import org.openpnp.util.UiUtils;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MVPnPFeederCalibrationWizard extends JPanel implements Wizard {
    private final MVPnPAutoFeeder feeder;
    private WizardContainer wizardContainer;

    public MVPnPFeederCalibrationWizard(MVPnPAutoFeeder feeder) {
        this.feeder = feeder;
        IntegerConverter integerConverter = new IntegerConverter();

        setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(contentPanel);

        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        JPanel panelSettings = new JPanel();
        panelSettings.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null),
                "Settings", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
        contentPanel.add(panelSettings);
        panelSettings.setLayout(new FormLayout(new ColumnSpec[]{
                FormSpecs.RELATED_GAP_COLSPEC,
                FormSpecs.DEFAULT_COLSPEC,
                FormSpecs.RELATED_GAP_COLSPEC,
                FormSpecs.DEFAULT_COLSPEC,
                FormSpecs.RELATED_GAP_COLSPEC,
                FormSpecs.DEFAULT_COLSPEC,
                FormSpecs.RELATED_GAP_COLSPEC,
                FormSpecs.DEFAULT_COLSPEC,
                FormSpecs.RELATED_GAP_COLSPEC,
                ColumnSpec.decode("default:grow"),},
                new RowSpec[]{
                        FormSpecs.RELATED_GAP_ROWSPEC,
                        FormSpecs.DEFAULT_ROWSPEC,
                        FormSpecs.NARROW_LINE_GAP_ROWSPEC,
                        FormSpecs.DEFAULT_ROWSPEC,
                        FormSpecs.UNRELATED_GAP_ROWSPEC,
                        FormSpecs.DEFAULT_ROWSPEC,
                        FormSpecs.RELATED_GAP_ROWSPEC,
                        FormSpecs.DEFAULT_ROWSPEC,
                        FormSpecs.RELATED_GAP_ROWSPEC,
                        FormSpecs.DEFAULT_ROWSPEC,
                        FormSpecs.PARAGRAPH_GAP_ROWSPEC,
                        FormSpecs.DEFAULT_ROWSPEC,
                        FormSpecs.RELATED_GAP_ROWSPEC,
                        FormSpecs.DEFAULT_ROWSPEC,
                        FormSpecs.RELATED_GAP_ROWSPEC,}));

        JLabel lblCurrent = new JLabel("Current");
        panelSettings.add(lblCurrent, "2, 2, right, default");

        JTextField currentValue = new JTextField();
        panelSettings.add(currentValue, "4, 2");
        currentValue.setColumns(5);

        JButton btnReadCurrent = new JButton(new AbstractAction("Read") {
            @Override
            public void actionPerformed(ActionEvent e) {
                UiUtils.messageBoxOnException(() -> {
                    MVPnPAutoFeeder.getActuator(feeder.getFeederDriver(), MVPnPFeederDriver.MVPnPActuator.ACT_MVPNP_FEEDER_SELECT).actuate(feeder.getLetter());
                    currentValue.setText(integerConverter.convertForward((Integer) MVPnPAutoFeeder.getActuator(feeder.getFeederDriver(), MVPnPFeederDriver.MVPnPActuator.ACT_MVPNP_FEEDER_POSITION).read()));
                });
            }
        });
        panelSettings.add(btnReadCurrent, "6, 2");

        JButton btnWriteCurrent = new JButton(new AbstractAction("Write") {
            @Override
            public void actionPerformed(ActionEvent e) {
                UiUtils.messageBoxOnException(() -> {
                    MVPnPAutoFeeder.getActuator(feeder.getFeederDriver(), MVPnPFeederDriver.MVPnPActuator.ACT_MVPNP_FEEDER_SELECT).actuate(feeder.getLetter());
                    MVPnPAutoFeeder.getActuator(feeder.getFeederDriver(), MVPnPFeederDriver.MVPnPActuator.ACT_MVPNP_FEEDER_POSITION).actuate(integerConverter.convertReverse(currentValue.getText()));
                });
            }
        });
        panelSettings.add(btnWriteCurrent, "8, 2");

        JLabel lblIncremental = new JLabel("Incremental");
        panelSettings.add(lblIncremental, "2, 4, right, default");

        JTextField incrementalValue = new JTextField("100");
        panelSettings.add(incrementalValue, "4, 4");
        incrementalValue.setColumns(5);

        JButton btnReadIncremental = new JButton(new AbstractAction("+") {
            @Override
            public void actionPerformed(ActionEvent e) {
                UiUtils.messageBoxOnException(() -> {
                    MVPnPAutoFeeder.getActuator(feeder.getFeederDriver(), MVPnPFeederDriver.MVPnPActuator.ACT_MVPNP_FEEDER_SELECT).actuate(feeder.getLetter());
                    int position = (Integer) MVPnPAutoFeeder.getActuator(feeder.getFeederDriver(), MVPnPFeederDriver.MVPnPActuator.ACT_MVPNP_FEEDER_POSITION).read();
                    position += integerConverter.convertReverse(incrementalValue.getText());
                    MVPnPAutoFeeder.getActuator(feeder.getFeederDriver(), MVPnPFeederDriver.MVPnPActuator.ACT_MVPNP_FEEDER_POSITION).actuate(position);
                });
            }
        });
        panelSettings.add(btnReadIncremental, "6, 4");

        JButton btnWriteIncremental = new JButton(new AbstractAction("-") {
            @Override
            public void actionPerformed(ActionEvent e) {
                UiUtils.messageBoxOnException(() -> {
                    MVPnPAutoFeeder.getActuator(feeder.getFeederDriver(), MVPnPFeederDriver.MVPnPActuator.ACT_MVPNP_FEEDER_SELECT).actuate(feeder.getLetter());
                    int position = (Integer) MVPnPAutoFeeder.getActuator(feeder.getFeederDriver(), MVPnPFeederDriver.MVPnPActuator.ACT_MVPNP_FEEDER_POSITION).read();
                    position -= integerConverter.convertReverse(incrementalValue.getText());
                    MVPnPAutoFeeder.getActuator(feeder.getFeederDriver(), MVPnPFeederDriver.MVPnPActuator.ACT_MVPNP_FEEDER_POSITION).actuate(position);
                });
            }
        });
        panelSettings.add(btnWriteIncremental, "8, 4");

        JLabel lblAOrigin = new JLabel("Origin");
        panelSettings.add(lblAOrigin, "2, 6, right, default");

        JTextField originValue = new JTextField();
        panelSettings.add(originValue, "4, 6");
        originValue.setColumns(5);

        JButton btnReadOrigin = new JButton(new AbstractAction("Read") {
            @Override
            public void actionPerformed(ActionEvent e) {
                UiUtils.messageBoxOnException(() -> {
                    MVPnPAutoFeeder.getActuator(feeder.getFeederDriver(), MVPnPFeederDriver.MVPnPActuator.ACT_MVPNP_FEEDER_SELECT).actuate(feeder.getLetter());
                    originValue.setText(integerConverter.convertForward((Integer) MVPnPAutoFeeder.getActuator(feeder.getFeederDriver(), MVPnPFeederDriver.MVPnPActuator.ACT_MVPNP_FEEDER_ORIGIN).read()));
                });
            }
        });
        panelSettings.add(btnReadOrigin, "6, 6");

        JButton btnWriteOrigin = new JButton(new AbstractAction("Write") {
            @Override
            public void actionPerformed(ActionEvent e) {
                UiUtils.messageBoxOnException(() -> {
                    MVPnPAutoFeeder.getActuator(feeder.getFeederDriver(), MVPnPFeederDriver.MVPnPActuator.ACT_MVPNP_FEEDER_SELECT).actuate(feeder.getLetter());
                    MVPnPAutoFeeder.getActuator(feeder.getFeederDriver(), MVPnPFeederDriver.MVPnPActuator.ACT_MVPNP_FEEDER_ORIGIN).actuate(integerConverter.convertReverse(originValue.getText()));
                });
            }
        });
        panelSettings.add(btnWriteOrigin, "8, 6");

        JLabel lblHalf = new JLabel("Half");
        panelSettings.add(lblHalf, "2, 8, right, default");

        JTextField halfValue = new JTextField();
        panelSettings.add(halfValue, "4, 8");
        halfValue.setColumns(5);

        JButton btnReadHalf = new JButton(new AbstractAction("Read") {
            @Override
            public void actionPerformed(ActionEvent e) {
                UiUtils.messageBoxOnException(() -> {
                    MVPnPAutoFeeder.getActuator(feeder.getFeederDriver(), MVPnPFeederDriver.MVPnPActuator.ACT_MVPNP_FEEDER_SELECT).actuate(feeder.getLetter());
                    halfValue.setText(integerConverter.convertForward((Integer) MVPnPAutoFeeder.getActuator(feeder.getFeederDriver(), MVPnPFeederDriver.MVPnPActuator.ACT_MVPNP_FEEDER_HALF).read()));
                });
            }
        });
        panelSettings.add(btnReadHalf, "6, 8");

        JButton btnWriteHalf = new JButton(new AbstractAction("Write") {
            @Override
            public void actionPerformed(ActionEvent e) {
                UiUtils.messageBoxOnException(() -> {
                    MVPnPAutoFeeder.getActuator(feeder.getFeederDriver(), MVPnPFeederDriver.MVPnPActuator.ACT_MVPNP_FEEDER_SELECT).actuate(feeder.getLetter());
                    MVPnPAutoFeeder.getActuator(feeder.getFeederDriver(), MVPnPFeederDriver.MVPnPActuator.ACT_MVPNP_FEEDER_HALF).actuate(integerConverter.convertReverse(halfValue.getText()));
                });
            }
        });
        panelSettings.add(btnWriteHalf, "8, 8");

        JLabel lblFull = new JLabel("Full");
        panelSettings.add(lblFull, "2, 10, right, default");

        JTextField fullValue = new JTextField();
        panelSettings.add(fullValue, "4, 10");
        fullValue.setColumns(5);

        JButton btnReadFull = new JButton(new AbstractAction("Read") {
            @Override
            public void actionPerformed(ActionEvent e) {
                UiUtils.messageBoxOnException(() -> {
                    MVPnPAutoFeeder.getActuator(feeder.getFeederDriver(), MVPnPFeederDriver.MVPnPActuator.ACT_MVPNP_FEEDER_SELECT).actuate(feeder.getLetter());
                    fullValue.setText(integerConverter.convertForward((Integer) MVPnPAutoFeeder.getActuator(feeder.getFeederDriver(), MVPnPFeederDriver.MVPnPActuator.ACT_MVPNP_FEEDER_FULL).read()));
                });
            }
        });
        panelSettings.add(btnReadFull, "6, 10");

        JButton btnWriteFull = new JButton(new AbstractAction("Write") {
            @Override
            public void actionPerformed(ActionEvent e) {
                UiUtils.messageBoxOnException(() -> {
                    MVPnPAutoFeeder.getActuator(feeder.getFeederDriver(), MVPnPFeederDriver.MVPnPActuator.ACT_MVPNP_FEEDER_SELECT).actuate(feeder.getLetter());
                    MVPnPAutoFeeder.getActuator(feeder.getFeederDriver(), MVPnPFeederDriver.MVPnPActuator.ACT_MVPNP_FEEDER_FULL).actuate(integerConverter.convertReverse(fullValue.getText()));
                });
            }
        });
        panelSettings.add(btnWriteFull, "8, 10");


        JLabel lblLength = new JLabel("Length");
        panelSettings.add(lblLength, "2, 12, right, default");

        JTextField lengthValue = new JTextField();
        panelSettings.add(lengthValue, "4, 12");
        lengthValue.setColumns(5);

        JButton btnReadLength = new JButton(new AbstractAction("Read") {
            @Override
            public void actionPerformed(ActionEvent e) {
                UiUtils.messageBoxOnException(() -> {
                    MVPnPAutoFeeder.getActuator(feeder.getFeederDriver(), MVPnPFeederDriver.MVPnPActuator.ACT_MVPNP_FEEDER_SELECT).actuate(feeder.getLetter());
                    lengthValue.setText(integerConverter.convertForward((Integer) MVPnPAutoFeeder.getActuator(feeder.getFeederDriver(), MVPnPFeederDriver.MVPnPActuator.ACT_MVPNP_FEEDER_LENGTH).read()));
                });
            }
        });
        panelSettings.add(btnReadLength, "6, 12");

        JButton btnWriteLength = new JButton(new AbstractAction("Write") {
            @Override
            public void actionPerformed(ActionEvent e) {
                UiUtils.messageBoxOnException(() -> {
                    MVPnPAutoFeeder.getActuator(feeder.getFeederDriver(), MVPnPFeederDriver.MVPnPActuator.ACT_MVPNP_FEEDER_SELECT).actuate(feeder.getLetter());
                    MVPnPAutoFeeder.getActuator(feeder.getFeederDriver(), MVPnPFeederDriver.MVPnPActuator.ACT_MVPNP_FEEDER_LENGTH).actuate(integerConverter.convertReverse(lengthValue.getText()));
                });
            }
        });
        panelSettings.add(btnWriteLength, "8, 12");

        JLabel lblTime = new JLabel("Time");
        panelSettings.add(lblTime, "2, 14, right, default");

        JTextField timeValue = new JTextField();
        panelSettings.add(timeValue, "4, 14");
        timeValue.setColumns(5);

        JButton btnReadTime = new JButton(new AbstractAction("Read") {
            @Override
            public void actionPerformed(ActionEvent e) {
                UiUtils.messageBoxOnException(() -> {
                    MVPnPAutoFeeder.getActuator(feeder.getFeederDriver(), MVPnPFeederDriver.MVPnPActuator.ACT_MVPNP_FEEDER_SELECT).actuate(feeder.getLetter());
                    timeValue.setText(integerConverter.convertForward((Integer) MVPnPAutoFeeder.getActuator(feeder.getFeederDriver(), MVPnPFeederDriver.MVPnPActuator.ACT_MVPNP_FEEDER_TIME).read()));
                });
            }
        });
        panelSettings.add(btnReadTime, "6, 14");

        JButton btnWriteTime = new JButton(new AbstractAction("Write") {
            @Override
            public void actionPerformed(ActionEvent e) {
                UiUtils.messageBoxOnException(() -> {
                    MVPnPAutoFeeder.getActuator(feeder.getFeederDriver(), MVPnPFeederDriver.MVPnPActuator.ACT_MVPNP_FEEDER_SELECT).actuate(feeder.getLetter());
                    MVPnPAutoFeeder.getActuator(feeder.getFeederDriver(), MVPnPFeederDriver.MVPnPActuator.ACT_MVPNP_FEEDER_TIME).actuate(integerConverter.convertReverse(timeValue.getText()));
                });
            }
        });
        panelSettings.add(btnWriteTime, "8, 14");

        JPanel panelActions = new JPanel();
        panelActions.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null),
                "Actions", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
        contentPanel.add(panelActions);
        panelActions.setLayout(new FormLayout(new ColumnSpec[]{
                FormSpecs.RELATED_GAP_COLSPEC,
                FormSpecs.DEFAULT_COLSPEC,
                FormSpecs.RELATED_GAP_COLSPEC,
                FormSpecs.DEFAULT_COLSPEC,
                FormSpecs.RELATED_GAP_COLSPEC,
                FormSpecs.DEFAULT_COLSPEC,
                FormSpecs.RELATED_GAP_COLSPEC,
                FormSpecs.DEFAULT_COLSPEC,
                FormSpecs.RELATED_GAP_COLSPEC,
                FormSpecs.DEFAULT_COLSPEC,
                FormSpecs.RELATED_GAP_COLSPEC,
                ColumnSpec.decode("default:grow"),},
                new RowSpec[]{
                        FormSpecs.RELATED_GAP_ROWSPEC,
                        FormSpecs.DEFAULT_ROWSPEC,
                        FormSpecs.RELATED_GAP_ROWSPEC,}));

        JButton btnEnable = new JButton(new AbstractAction("Enable") {
            @Override
            public void actionPerformed(ActionEvent e) {
                UiUtils.messageBoxOnException(() -> {
                    MVPnPAutoFeeder.getActuator(feeder.getFeederDriver(), MVPnPFeederDriver.MVPnPActuator.ACT_MVPNP_FEEDER_SELECT).actuate(feeder.getLetter());
                    MVPnPAutoFeeder.getActuator(feeder.getFeederDriver(), MVPnPFeederDriver.MVPnPActuator.ACT_MVPNP_FEEDER_ENABLE).actuate(true);
                });
            }
        });
        panelActions.add(btnEnable, "2, 2");

        JButton btnDisable = new JButton(new AbstractAction("Disable") {
            @Override
            public void actionPerformed(ActionEvent e) {
                UiUtils.messageBoxOnException(() -> {
                    MVPnPAutoFeeder.getActuator(feeder.getFeederDriver(), MVPnPFeederDriver.MVPnPActuator.ACT_MVPNP_FEEDER_SELECT).actuate(feeder.getLetter());
                    MVPnPAutoFeeder.getActuator(feeder.getFeederDriver(), MVPnPFeederDriver.MVPnPActuator.ACT_MVPNP_FEEDER_ENABLE).actuate(false);
                });
            }
        });
        panelActions.add(btnDisable, "4, 2");

        JButton btnRename = new JButton(new AbstractAction("Rename") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object nameRet = JOptionPane.showInputDialog(MainFrame.get(),
                        "Choose the feeder new letter", "Feeder Letter", JOptionPane.QUESTION_MESSAGE,
                        null, null, feeder.getLetter());
                if (!(nameRet instanceof String)) {
                    return;
                }
                String newLetter = (String) nameRet;
                if (newLetter.isEmpty() || newLetter.equals(feeder.getLetter())) {
                    return;
                }
                int ret = JOptionPane.showConfirmDialog(MainFrame.get(),
                        "Are you sure you want to change the feeder \"" + feeder.getLetter() + "\"letter to \"" + newLetter + "\"?",
                        "Rename Feeder?", JOptionPane.YES_NO_OPTION);
                if (ret == JOptionPane.YES_OPTION) {
                    UiUtils.messageBoxOnException(() -> {
                        MVPnPAutoFeeder.getActuator(feeder.getFeederDriver(), MVPnPFeederDriver.MVPnPActuator.ACT_MVPNP_FEEDER_SELECT).actuate(feeder.getLetter());
                        MVPnPAutoFeeder.getActuator(feeder.getFeederDriver(), MVPnPFeederDriver.MVPnPActuator.ACT_MVPNP_FEEDER_ENABLE).actuate(false);
                    });
                }
            }
        });
        panelActions.add(btnRename, "6, 2");

        JButton btnSave = new JButton(new AbstractAction("Save") {
            @Override
            public void actionPerformed(ActionEvent e) {
                int ret = JOptionPane.showConfirmDialog(MainFrame.get(),
                        "Are you sure you want to save the feeder's settings?",
                        "Save Feeder Settings?", JOptionPane.YES_NO_OPTION);
                if (ret == JOptionPane.YES_OPTION) {
                    UiUtils.messageBoxOnException(() -> {
                        MVPnPAutoFeeder.getActuator(feeder.getFeederDriver(), MVPnPFeederDriver.MVPnPActuator.ACT_MVPNP_FEEDER_SELECT).actuate(feeder.getLetter());
                        MVPnPAutoFeeder.getActuator(feeder.getFeederDriver(), MVPnPFeederDriver.MVPnPActuator.ACT_MVPNP_FEEDER_SAVE).actuate(true);
                    });
                }
            }
        });
        panelActions.add(btnSave, "8, 2");

        JButton btnReset = new JButton(new AbstractAction("Reset") {
            @Override
            public void actionPerformed(ActionEvent e) {
                int ret = JOptionPane.showConfirmDialog(MainFrame.get(),
                        "Are you sure you want to reset the feeder's settings?",
                        "Reset Feeder Settings?", JOptionPane.YES_NO_OPTION);
                if (ret == JOptionPane.YES_OPTION) {
                    UiUtils.messageBoxOnException(() -> {
                        MVPnPAutoFeeder.getActuator(feeder.getFeederDriver(), MVPnPFeederDriver.MVPnPActuator.ACT_MVPNP_FEEDER_SELECT).actuate(feeder.getLetter());
                        MVPnPAutoFeeder.getActuator(feeder.getFeederDriver(), MVPnPFeederDriver.MVPnPActuator.ACT_MVPNP_FEEDER_RESET).actuate(true);
                    });
                }
            }
        });
        panelActions.add(btnReset, "10, 2");
    }

    @Override
    public void setWizardContainer(WizardContainer wizardContainer) {
        this.wizardContainer = wizardContainer;
    }

    @Override
    public JPanel getWizardPanel() {
        return this;
    }

    @Override
    public String getWizardName() {
        return null;
    }
}
