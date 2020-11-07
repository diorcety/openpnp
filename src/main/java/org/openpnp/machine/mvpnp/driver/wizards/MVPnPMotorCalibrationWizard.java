package org.openpnp.machine.mvpnp.driver.wizards;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import org.jdesktop.swingx.combobox.EnumComboBoxModel;
import org.openpnp.gui.MainFrame;
import org.openpnp.gui.support.IntegerConverter;
import org.openpnp.gui.support.MessageBoxes;
import org.openpnp.gui.support.Wizard;
import org.openpnp.gui.support.WizardContainer;
import org.openpnp.machine.mvpnp.driver.MVPnPMotorDriver;
import org.openpnp.machine.mvpnp.driver.tmcl.*;
import org.openpnp.util.UiUtils;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MVPnPMotorCalibrationWizard extends JPanel implements Wizard {
    private final MVPnPMotorDriver driver;
    private WizardContainer wizardContainer;

    public MVPnPMotorCalibrationWizard(MVPnPMotorDriver driver) {
        this.driver = driver;
        IntegerConverter integerConverter = new IntegerConverter();

        setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(contentPanel);

        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        //GP
        {
            JPanel globalParameters = new JPanel();
            globalParameters.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null),
                    "Global parameters", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
            contentPanel.add(globalParameters);
            globalParameters.setLayout(new FormLayout(new ColumnSpec[]{
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
                            FormSpecs.DEFAULT_ROWSPEC,}));

            JLabel lblModule = new JLabel("Module");
            globalParameters.add(lblModule, "2, 4, right, default");

            JSpinner moduleSp = new JSpinner(new SpinnerNumberModel(0, 0, 255, 1));
            globalParameters.add(moduleSp, "4, 4, fill, default");

            JButton btnID = new JButton(new AbstractAction("Get ID") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    UiUtils.messageBoxOnException(() -> {
                        byte module = ((Integer) moduleSp.getValue()).byteValue();
                        TMCLRequest tmclRequest = new TMCLRequest(module, TMCLCommand.GFV.getByte(), (byte)1, (byte)0, 0);
                        TMCLReply tmclReply = driver.sendCommand(tmclRequest);
                        int value = tmclReply.getValue();
                        String message = String.format("ID: %02x%02x" + "\nVersion: %02x%02x", value >> 24 & 0xff, value >> 16 & 0xff, value >> 8 & 0xff, value >> 0 & 0xff);
                        MessageBoxes.infoBox("TMCL Board ID", message);
                    });
                }
            });
            globalParameters.add(btnID, "6, 4");

            JLabel lbMotor = new JLabel("Motor");
            globalParameters.add(lbMotor, "2, 6, right, default");

            JSpinner motorSp = new JSpinner(new SpinnerNumberModel(0, 0, 255, 1));
            globalParameters.add(motorSp, "4, 6, fill, default");

            JLabel lblType = new JLabel("Type");
            globalParameters.add(lblType, "2, 8, right, default");

            JComboBox<TMCLType.GP> typeCb = new JComboBox<TMCLType.GP>();
            typeCb.setModel(new EnumComboBoxModel<TMCLType.GP>(TMCLType.GP.class));
            globalParameters.add(typeCb, "4, 8, right, default");

            JButton btnLoadEEPROM = new JButton(new AbstractAction("Load from EEPROM") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    TMCLType.GP type = (TMCLType.GP) typeCb.getSelectedItem();
                    int ret = JOptionPane.showConfirmDialog(MainFrame.get(),
                            "Are you sure you want to load " + type.name() + " from EEPROM?",
                            "Save Feeder Settings?", JOptionPane.YES_NO_OPTION);
                    if (ret == JOptionPane.YES_OPTION) {
                        UiUtils.messageBoxOnException(() -> {
                            byte module = ((Integer) moduleSp.getValue()).byteValue();
                            byte typeByte = type.getByte();
                            byte motor = ((Integer) motorSp.getValue()).byteValue();
                            TMCLRequest tmclRequest = new TMCLRequest(module, TMCLCommand.RSGP.getByte(), typeByte, motor, 0);
                            driver.sendCommand(tmclRequest);
                        });
                    }
                }
            });
            globalParameters.add(btnLoadEEPROM, "6, 8");

            JButton btnSaveEEPROM = new JButton(new AbstractAction("Save to EEPROM") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    TMCLType.GP type = (TMCLType.GP) typeCb.getSelectedItem();
                    int ret = JOptionPane.showConfirmDialog(MainFrame.get(),
                            "Are you sure you want to save " + type.name() + " in EEPROM?",
                            "Save Feeder Settings?", JOptionPane.YES_NO_OPTION);
                    if (ret == JOptionPane.YES_OPTION) {
                        UiUtils.messageBoxOnException(() -> {
                            byte module = ((Integer) moduleSp.getValue()).byteValue();
                            byte typeByte = type.getByte();
                            byte motor = ((Integer) motorSp.getValue()).byteValue();
                            TMCLRequest tmclRequest = new TMCLRequest(module, TMCLCommand.STGP.getByte(), typeByte, motor, 0);
                            driver.sendCommand(tmclRequest);
                        });
                    }
                }
            });
            globalParameters.add(btnSaveEEPROM, "8, 8");

            JLabel lblValue = new JLabel("Value");
            globalParameters.add(lblValue, "2, 10, right, default");

            JTextField valueTf = new JTextField();
            globalParameters.add(valueTf, "4, 10");
            valueTf.setColumns(5);

            JButton btnRead = new JButton(new AbstractAction("Read") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    UiUtils.messageBoxOnException(() -> {
                        byte module = ((Integer) moduleSp.getValue()).byteValue();
                        byte type = ((TMCLType.GP) typeCb.getSelectedItem()).getByte();
                        byte motor = ((Integer) motorSp.getValue()).byteValue();
                        TMCLRequest tmclRequest = new TMCLRequest(module, TMCLCommand.GGP.getByte(), type, motor, 0);
                        TMCLReply tmclReply = driver.sendCommand(tmclRequest);
                        valueTf.setText(integerConverter.convertForward(tmclReply.getValue()));
                    });
                }
            });
            globalParameters.add(btnRead, "6, 10");

            JButton btnWrite = new JButton(new AbstractAction("Write") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    UiUtils.messageBoxOnException(() -> {
                        byte module = ((Integer) moduleSp.getValue()).byteValue();
                        byte type = ((TMCLType.GP) typeCb.getSelectedItem()).getByte();
                        byte motor = ((Integer) motorSp.getValue()).byteValue();
                        TMCLRequest tmclRequest = new TMCLRequest(module, TMCLCommand.SGP.getByte(), type, motor, integerConverter.convertReverse(valueTf.getText()));
                        driver.sendCommand(tmclRequest);
                    });
                }
            });
            globalParameters.add(btnWrite, "8, 10");
        }

        //AP
        {
            JPanel axisParameters = new JPanel();
            axisParameters.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null),
                    "Axis parameters", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
            contentPanel.add(axisParameters);
            axisParameters.setLayout(new FormLayout(new ColumnSpec[]{
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
                            FormSpecs.DEFAULT_ROWSPEC,}));

            JTextField valueTf = new JTextField();
            JSpinner motorSp = new JSpinner(new SpinnerNumberModel(0, 0, 255, 1));

            JLabel lblModule = new JLabel("Module");
            axisParameters.add(lblModule, "2, 4, right, default");

            JSpinner moduleSp = new JSpinner(new SpinnerNumberModel(0, 0, 255, 1));
            axisParameters.add(moduleSp, "4, 4, fill, default");

            JButton btnMoveTo = new JButton(new AbstractAction("Move to") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    UiUtils.messageBoxOnException(() -> {
                        byte module = ((Integer) moduleSp.getValue()).byteValue();
                        byte motor = ((Integer) motorSp.getValue()).byteValue();
                        byte type = TMCLType.MVP.ABS.getByte();
                        TMCLRequest tmclRequest = new TMCLRequest(module, TMCLCommand.MVP.getByte(), type, motor, integerConverter.convertReverse(valueTf.getText()));
                        driver.sendCommand(tmclRequest);
                    });
                }
            });
            axisParameters.add(btnMoveTo, "6, 4");

            JButton btnStop = new JButton(new AbstractAction("Stop") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    UiUtils.messageBoxOnException(() -> {
                        byte module = ((Integer) moduleSp.getValue()).byteValue();
                        byte motor = ((Integer) motorSp.getValue()).byteValue();
                        byte type = 0;
                        TMCLRequest tmclRequest = new TMCLRequest(module, TMCLCommand.MST.getByte(), type, motor,0);
                        driver.sendCommand(tmclRequest);
                    });
                }
            });
            axisParameters.add(btnStop, "8, 4");

            JLabel lbMotor = new JLabel("Motor");
            axisParameters.add(lbMotor, "2, 6, right, default");

            axisParameters.add(motorSp, "4, 6, fill, default");

            JButton btnRotateLeft = new JButton(new AbstractAction("Rotate Left") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    UiUtils.messageBoxOnException(() -> {
                        byte module = ((Integer) moduleSp.getValue()).byteValue();
                        byte motor = ((Integer) motorSp.getValue()).byteValue();
                        byte type = 0;
                        TMCLRequest tmclRequest = new TMCLRequest(module, TMCLCommand.ROL.getByte(), type, motor, integerConverter.convertReverse(valueTf.getText()));
                        driver.sendCommand(tmclRequest);
                    });
                }
            });
            axisParameters.add(btnRotateLeft, "6, 6");

            JButton btnRotateRight = new JButton(new AbstractAction("Rotate Right") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    UiUtils.messageBoxOnException(() -> {
                        byte module = ((Integer) moduleSp.getValue()).byteValue();
                        byte motor = ((Integer) motorSp.getValue()).byteValue();
                        byte type = 0;
                        TMCLRequest tmclRequest = new TMCLRequest(module, TMCLCommand.ROR.getByte(), type, motor, integerConverter.convertReverse(valueTf.getText()));
                        driver.sendCommand(tmclRequest);
                    });
                }
            });
            axisParameters.add(btnRotateRight, "8, 6");

            JLabel lblType = new JLabel("Type");
            axisParameters.add(lblType, "2, 8, right, default");

            JComboBox<TMCLType.AP> typeCb = new JComboBox<TMCLType.AP>();
            typeCb.setModel(new EnumComboBoxModel<TMCLType.AP>(TMCLType.AP.class));
            axisParameters.add(typeCb, "4, 8, right, default");

            JButton btnLoadEEPROM = new JButton(new AbstractAction("Load from EEPROM") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    TMCLType.AP type = (TMCLType.AP) typeCb.getSelectedItem();
                    int ret = JOptionPane.showConfirmDialog(MainFrame.get(),
                            "Are you sure you want to load " + type.name() + " from EEPROM?",
                            "Save Feeder Settings?", JOptionPane.YES_NO_OPTION);
                    if (ret == JOptionPane.YES_OPTION) {
                        UiUtils.messageBoxOnException(() -> {
                            byte module = ((Integer) moduleSp.getValue()).byteValue();
                            byte typeByte = type.getByte();
                            byte motor = ((Integer) motorSp.getValue()).byteValue();
                            TMCLRequest tmclRequest = new TMCLRequest(module, TMCLCommand.RSAP.getByte(), typeByte, motor, 0);
                            driver.sendCommand(tmclRequest);
                        });
                    }
                }
            });
            axisParameters.add(btnLoadEEPROM, "6, 8");

            JButton btnSaveEEPROM = new JButton(new AbstractAction("Save to EEPROM") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    TMCLType.AP type = (TMCLType.AP) typeCb.getSelectedItem();
                    int ret = JOptionPane.showConfirmDialog(MainFrame.get(),
                            "Are you sure you want to save " + type.name() + " in EEPROM?",
                            "Save Feeder Settings?", JOptionPane.YES_NO_OPTION);
                    if (ret == JOptionPane.YES_OPTION) {
                        UiUtils.messageBoxOnException(() -> {
                            byte module = ((Integer) moduleSp.getValue()).byteValue();
                            byte typeByte = type.getByte();
                            byte motor = ((Integer) motorSp.getValue()).byteValue();
                            TMCLRequest tmclRequest = new TMCLRequest(module, TMCLCommand.STAP.getByte(), typeByte, motor, 0);
                            driver.sendCommand(tmclRequest);
                        });
                    }
                }
            });
            axisParameters.add(btnSaveEEPROM, "8, 8");

            JLabel lblValue = new JLabel("Value");
            axisParameters.add(lblValue, "2, 10, right, default");

            axisParameters.add(valueTf, "4, 10");
            valueTf.setColumns(5);

            JButton btnRead = new JButton(new AbstractAction("Read") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    UiUtils.messageBoxOnException(() -> {
                        byte module = ((Integer) moduleSp.getValue()).byteValue();
                        byte type = ((TMCLType.AP) typeCb.getSelectedItem()).getByte();
                        byte motor = ((Integer) motorSp.getValue()).byteValue();
                        TMCLRequest tmclRequest = new TMCLRequest(module, TMCLCommand.GAP.getByte(), type, motor, 0);
                        TMCLReply tmclReply = driver.sendCommand(tmclRequest);
                        valueTf.setText(integerConverter.convertForward(tmclReply.getValue()));
                    });
                }
            });
            axisParameters.add(btnRead, "6, 10");

            JButton btnWrite = new JButton(new AbstractAction("Write") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    UiUtils.messageBoxOnException(() -> {
                        byte module = ((Integer) moduleSp.getValue()).byteValue();
                        byte type = ((TMCLType.AP) typeCb.getSelectedItem()).getByte();
                        byte motor = ((Integer) motorSp.getValue()).byteValue();
                        TMCLRequest tmclRequest = new TMCLRequest(module, TMCLCommand.SAP.getByte(), type, motor, integerConverter.convertReverse(valueTf.getText()));
                        driver.sendCommand(tmclRequest);
                    });
                }
            });
            axisParameters.add(btnWrite, "8, 10");
        }
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
