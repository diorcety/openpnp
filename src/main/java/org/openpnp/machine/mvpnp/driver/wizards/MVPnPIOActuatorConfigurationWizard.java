/*
 * Copyright (C) 2011 Jason von Nieda <jason@vonnieda.org>
 *
 * This file is part of OpenPnP.
 *
 * OpenPnP is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * OpenPnP is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with OpenPnP. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * For more information about OpenPnP visit http://openpnp.org
 */

package org.openpnp.machine.mvpnp.driver.wizards;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.swingx.combobox.EnumComboBoxModel;
import org.openpnp.gui.components.ComponentDecorators;
import org.openpnp.gui.support.AbstractConfigurationWizard;
import org.openpnp.gui.support.IntegerConverter;
import org.openpnp.gui.support.LengthConverter;
import org.openpnp.gui.support.MutableLocationProxy;
import org.openpnp.machine.mvpnp.driver.MVPnPIOActuator;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

@SuppressWarnings("serial")
public class MVPnPIOActuatorConfigurationWizard extends AbstractConfigurationWizard {
    private final MVPnPIOActuator actuator;

    private JTextField locationX;
    private JTextField locationY;
    private JTextField locationZ;
    private JPanel panelOffsets;
    private JPanel panelSafeZ;
    private JLabel lblSafeZ;
    private JTextField textFieldSafeZ;
    private JPanel headMountablePanel;
    private JPanel generalPanel;
    private JLabel lblIndex;
    private JTextField indexTextField;
    private JPanel panelProperties;
    private JLabel lblName;
    private JTextField nameTf;
    private JLabel lblModule;
    private JSpinner moduleSp;
    private JLabel lblBank;
    private JSpinner bankSp;
    private JLabel lblPort;
    private JSpinner portSp;
    private JLabel lblType;
    private JComboBox<MVPnPIOActuator.Type> typeCb;

    public MVPnPIOActuatorConfigurationWizard(MVPnPIOActuator actuator) {
        this.actuator = actuator;

        panelProperties = new JPanel();
        panelProperties.setBorder(new TitledBorder(null, "Properties", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        contentPanel.add(panelProperties);
        panelProperties.setLayout(new FormLayout(new ColumnSpec[]{
                FormSpecs.RELATED_GAP_COLSPEC,
                FormSpecs.DEFAULT_COLSPEC,
                FormSpecs.RELATED_GAP_COLSPEC,
                FormSpecs.DEFAULT_COLSPEC,},
                new RowSpec[]{
                        FormSpecs.RELATED_GAP_ROWSPEC,
                        FormSpecs.DEFAULT_ROWSPEC,
                        FormSpecs.RELATED_GAP_ROWSPEC,
                        FormSpecs.DEFAULT_ROWSPEC,
                        FormSpecs.RELATED_GAP_ROWSPEC,
                        FormSpecs.DEFAULT_ROWSPEC,
                        FormSpecs.RELATED_GAP_ROWSPEC,
                        FormSpecs.DEFAULT_ROWSPEC,
                        FormSpecs.RELATED_GAP_ROWSPEC,
                        FormSpecs.DEFAULT_ROWSPEC,}));

        lblName = new JLabel("Name");
        panelProperties.add(lblName, "2, 2, right, default");

        nameTf = new JTextField();
        panelProperties.add(nameTf, "4, 2, fill, default");
        nameTf.setColumns(20);

        lblModule = new JLabel("Module");
        panelProperties.add(lblModule, "2, 4, right, default");

        moduleSp = new JSpinner(new SpinnerNumberModel(0, 0, 255, 1));
        panelProperties.add(moduleSp, "4, 4, fill, default");

        lblBank = new JLabel("Bank");
        panelProperties.add(lblBank, "2, 6, right, default");

        bankSp = new JSpinner(new SpinnerNumberModel(0, 0, 255, 1));
        panelProperties.add(bankSp, "4, 6, fill, default");

        lblPort = new JLabel("Port");
        panelProperties.add(lblPort, "2, 8, right, default");

        portSp = new JSpinner(new SpinnerNumberModel(0, 0, 255, 1));
        panelProperties.add(portSp, "4, 8, fill, default");

        lblType = new JLabel("Type");
        panelProperties.add(lblType, "2, 10, right, default");

        typeCb = new JComboBox<MVPnPIOActuator.Type>();
        typeCb.setModel(new EnumComboBoxModel<MVPnPIOActuator.Type>(MVPnPIOActuator.Type.class));
        panelProperties.add(typeCb, "4, 10, right, default");

        headMountablePanel = new JPanel();
        headMountablePanel.setLayout(new BoxLayout(headMountablePanel, BoxLayout.Y_AXIS));
        contentPanel.add(headMountablePanel);

        panelOffsets = new JPanel();
        headMountablePanel.add(panelOffsets);
        panelOffsets.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null),
                "Offsets", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
        panelOffsets.setLayout(new FormLayout(
                new ColumnSpec[]{FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.DEFAULT_COLSPEC,
                        FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.DEFAULT_COLSPEC,
                        FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.DEFAULT_COLSPEC,
                        FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.DEFAULT_COLSPEC,},
                new RowSpec[]{FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
                        FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,}));

        JLabel lblX = new JLabel("X");
        panelOffsets.add(lblX, "2, 2");

        JLabel lblY = new JLabel("Y");
        panelOffsets.add(lblY, "4, 2");

        JLabel lblZ = new JLabel("Z");
        panelOffsets.add(lblZ, "6, 2");

        locationX = new JTextField();
        panelOffsets.add(locationX, "2, 4");
        locationX.setColumns(5);

        locationY = new JTextField();
        panelOffsets.add(locationY, "4, 4");
        locationY.setColumns(5);

        locationZ = new JTextField();
        panelOffsets.add(locationZ, "6, 4");
        locationZ.setColumns(5);

        panelSafeZ = new JPanel();
        headMountablePanel.add(panelSafeZ);
        panelSafeZ.setBorder(new TitledBorder(null, "Safe Z", TitledBorder.LEADING,
                TitledBorder.TOP, null, null));
        panelSafeZ.setLayout(new FormLayout(
                new ColumnSpec[]{FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.DEFAULT_COLSPEC,
                        FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.DEFAULT_COLSPEC,},
                new RowSpec[]{FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,}));

        lblSafeZ = new JLabel("Safe Z");
        panelSafeZ.add(lblSafeZ, "2, 2, right, default");

        textFieldSafeZ = new JTextField();
        panelSafeZ.add(textFieldSafeZ, "4, 2, fill, default");
        textFieldSafeZ.setColumns(10);

        generalPanel = new JPanel();
        generalPanel.setBorder(new TitledBorder(null, "General", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        contentPanel.add(generalPanel);
        generalPanel.setLayout(new FormLayout(new ColumnSpec[]{
                FormSpecs.RELATED_GAP_COLSPEC,
                FormSpecs.DEFAULT_COLSPEC,
                FormSpecs.RELATED_GAP_COLSPEC,
                FormSpecs.DEFAULT_COLSPEC,},
                new RowSpec[]{
                        FormSpecs.RELATED_GAP_ROWSPEC,
                        FormSpecs.DEFAULT_ROWSPEC,}));

        lblIndex = new JLabel("Index");
        generalPanel.add(lblIndex, "2, 2, right, default");

        indexTextField = new JTextField();
        generalPanel.add(indexTextField, "4, 2, fill, default");
        indexTextField.setColumns(10);
        if (actuator.getHead() == null) {
            headMountablePanel.setVisible(false);
        }
    }

    @Override
    public void createBindings() {
        LengthConverter lengthConverter = new LengthConverter();
        IntegerConverter intConverter = new IntegerConverter();

        addWrappedBinding(actuator, "name", nameTf, "text");
        addWrappedBinding(actuator, "module", moduleSp, "value");
        addWrappedBinding(actuator, "bank", bankSp, "value");
        addWrappedBinding(actuator, "port", portSp, "value");
        addWrappedBinding(actuator, "type", typeCb, "selectedItem");
        
        MutableLocationProxy headOffsets = new MutableLocationProxy();
        bind(UpdateStrategy.READ_WRITE, actuator, "headOffsets", headOffsets, "location");
        addWrappedBinding(headOffsets, "lengthX", locationX, "text", lengthConverter);
        addWrappedBinding(headOffsets, "lengthY", locationY, "text", lengthConverter);
        addWrappedBinding(headOffsets, "lengthZ", locationZ, "text", lengthConverter);
        addWrappedBinding(actuator, "safeZ", textFieldSafeZ, "text", lengthConverter);
        addWrappedBinding(actuator, "index", indexTextField, "text", intConverter);

        ComponentDecorators.decorateWithAutoSelect(nameTf);
        ComponentDecorators.decorateWithAutoSelect(indexTextField);
        ComponentDecorators.decorateWithAutoSelectAndLengthConversion(locationX);
        ComponentDecorators.decorateWithAutoSelectAndLengthConversion(locationY);
        ComponentDecorators.decorateWithAutoSelectAndLengthConversion(locationZ);
        ComponentDecorators.decorateWithAutoSelectAndLengthConversion(textFieldSafeZ);
    }

    private static class LabeledType {
        private final String label;
        private final MVPnPIOActuator.Type type;

        public LabeledType(String label, MVPnPIOActuator.Type type) {
            this.label = label;
            this.type = type;
        }

        public String getLabel() {
            return label;
        }

        public MVPnPIOActuator.Type getType() {
            return type;
        }
    }
}
