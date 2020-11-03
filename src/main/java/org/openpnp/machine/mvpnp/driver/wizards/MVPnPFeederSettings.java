package org.openpnp.machine.mvpnp.driver.wizards;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import org.openpnp.gui.support.AbstractConfigurationWizard;
import org.openpnp.machine.mvpnp.driver.MVPnPFeederDriver;

import javax.swing.*;
import javax.swing.border.TitledBorder;

public class MVPnPFeederSettings extends AbstractConfigurationWizard {

    private final MVPnPFeederDriver feederDriver;
    private JLabel lblName;
    private JTextField nameTf;

    public MVPnPFeederSettings(MVPnPFeederDriver feederDriver) {
        this.feederDriver = feederDriver;
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
        addWrappedBinding(feederDriver, "name", nameTf, "text");
        addWrappedBinding(feederDriver, "name", nameTf, "text");

    }
}
