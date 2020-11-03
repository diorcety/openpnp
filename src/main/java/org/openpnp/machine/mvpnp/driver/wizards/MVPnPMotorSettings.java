package org.openpnp.machine.mvpnp.driver.wizards;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import org.openpnp.gui.support.AbstractConfigurationWizard;
import org.openpnp.machine.mvpnp.driver.MVPnPMotorDriver;

import javax.swing.*;
import javax.swing.border.TitledBorder;

public class MVPnPMotorSettings extends AbstractConfigurationWizard {

    private final MVPnPMotorDriver motorDriver;
    private JLabel lblName;
    private JTextField nameTf;

    public MVPnPMotorSettings(MVPnPMotorDriver motorDriver) {
        this.motorDriver = motorDriver;
        JPanel panel = new JPanel();
        panel.setBorder(new TitledBorder(null, "Properties", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        contentPanel.add(panel);
        panel.setLayout(new FormLayout(new ColumnSpec[] {
                FormSpecs.RELATED_GAP_COLSPEC,
                FormSpecs.DEFAULT_COLSPEC,
                FormSpecs.RELATED_GAP_COLSPEC,
                FormSpecs.DEFAULT_COLSPEC,},
                new RowSpec[] {
                        FormSpecs.RELATED_GAP_ROWSPEC,
                        FormSpecs.DEFAULT_ROWSPEC,
                        FormSpecs.RELATED_GAP_ROWSPEC,
                        FormSpecs.DEFAULT_ROWSPEC,}));

        lblName = new JLabel("Name");
        panel.add(lblName, "2, 2, right, default");

        nameTf = new JTextField();
        panel.add(nameTf, "4, 2");
        nameTf.setColumns(20);
    }

    @Override
    public void createBindings() {
        addWrappedBinding(motorDriver, "name", nameTf, "text");
    }
}
