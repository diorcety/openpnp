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

package org.openpnp.machine.mvpnp.feeders.wizards;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import org.openpnp.gui.support.DoubleConverter;
import org.openpnp.gui.support.IntegerConverter;
import org.openpnp.machine.mvpnp.MVPnPDriver;
import org.openpnp.machine.mvpnp.driver.MVPnPFeederDriver;
import org.openpnp.machine.mvpnp.feeders.MVPnPAutoFeeder;
import org.openpnp.machine.reference.ReferenceDriver;
import org.openpnp.machine.reference.ReferenceMachine;
import org.openpnp.machine.reference.feeder.wizards.AbstractReferenceFeederConfigurationWizard;
import org.openpnp.model.AbstractModelObject;
import org.openpnp.model.Configuration;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class MVPnPFeederConfigurationWizard
        extends AbstractReferenceFeederConfigurationWizard {
    private final MVPnPAutoFeeder feeder;
    private JComboBox comboBoxDriver;
    private JTextField letterValue;
    private JTextField advanceValue;
    private JCheckBox ckBoxMoveBeforeFeed;


    public MVPnPFeederConfigurationWizard(MVPnPAutoFeeder feeder) {
        super(feeder);
        this.feeder = feeder;

        JPanel panelActuator = new JPanel();
        panelActuator.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null),
                "Configuration", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
        contentPanel.add(panelActuator);
        panelActuator.setLayout(new FormLayout(new ColumnSpec[] {
                FormSpecs.RELATED_GAP_COLSPEC,
                FormSpecs.DEFAULT_COLSPEC,
                FormSpecs.RELATED_GAP_COLSPEC,
                FormSpecs.DEFAULT_COLSPEC,
                FormSpecs.RELATED_GAP_COLSPEC,
                ColumnSpec.decode("default:grow"),},
            new RowSpec[] {
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC,
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC,
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC,
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC,}));

        JLabel lblFeed = new JLabel("Driver");
        panelActuator.add(lblFeed, "2, 2, right, default");

        comboBoxDriver = new JComboBox();
        comboBoxDriver.setModel(new FeederDriversComboBoxModel(Configuration.get().getMachine()));
        panelActuator.add(comboBoxDriver, "4, 2, fill, default");

        JLabel lblLetter = new JLabel("Letter");
        panelActuator.add(lblLetter, "2, 4, right, default");

        letterValue = new JTextField();
        panelActuator.add(letterValue, "4, 4");
        letterValue.setColumns(1);

        JLabel lblAdvance = new JLabel("Advance");
        panelActuator.add(lblAdvance, "2, 6, right, default");

        advanceValue = new JTextField();
        panelActuator.add(advanceValue, "4, 6");
        advanceValue.setColumns(3);

        JLabel lblMoveBeforeFeed = new JLabel("Move before feed");
        panelActuator.add(lblMoveBeforeFeed, "2, 8, right, default");
        lblMoveBeforeFeed.setToolTipText("Move nozzle to pick location before actuating feed actuator");
        
        ckBoxMoveBeforeFeed = new JCheckBox();
        panelActuator.add(ckBoxMoveBeforeFeed, "4, 8, left, default");
    }

    @Override
    public void createBindings() {
        super.createBindings();

        addWrappedBinding(feeder, "driver", comboBoxDriver, "selectedItem");
        addWrappedBinding(feeder, "letter", letterValue, "text");
        addWrappedBinding(feeder, "advance", advanceValue, "text", new IntegerConverter() {
            @Override
            public String convertForward(Integer arg0) {
                if (arg0 == null) {
                    return "";
                }
                return super.convertForward(arg0);
            }

            @Override
            public Integer convertReverse(String arg0) {
                if (arg0 == null || arg0.isEmpty()) {
                    return null;
                }
                return super.convertReverse(arg0);
            }
        });
        addWrappedBinding(feeder, "moveBeforeFeed", ckBoxMoveBeforeFeed, "selected");
    }

    @SuppressWarnings("serial")
    public static class FeederDriversComboBoxModel extends DefaultComboBoxModel implements PropertyChangeListener {
        private Comparator<MVPnPFeederDriver> comparator = new Comparator<MVPnPFeederDriver>() {
            @Override
            public int compare(MVPnPFeederDriver o1, MVPnPFeederDriver o2) {
                return o1.getName().compareTo(o2.getName());
            }
        };

        private AbstractModelObject machineBase;
        private final List<AbstractModelObject> listenedModelObjects = new LinkedList<AbstractModelObject>();

        public FeederDriversComboBoxModel(Object object) {
            this.machineBase = (AbstractModelObject)object;
            addAllElements();
        }

        private void addAllElements() {
            addElement(new String());
            if (!(machineBase instanceof ReferenceMachine)) {
                return;
            }
            addPropertyChangeListener(((ReferenceMachine) machineBase), "driver");
            ReferenceDriver driver = ((ReferenceMachine) machineBase).getDriver();
            if (!(driver instanceof MVPnPDriver)) {
                return;
            }
            addPropertyChangeListener(((MVPnPDriver) driver), "feederDrivers");
            List<MVPnPFeederDriver> feederDrivers = ((MVPnPDriver) driver).getFeederDrivers();
            Collections.sort(feederDrivers, comparator);
            for (MVPnPFeederDriver feederDriver : feederDrivers) {
                addElement(feederDriver.getName());
            }
        }

        public void addPropertyChangeListener(AbstractModelObject modelObject, String propertyName) {
            modelObject.addPropertyChangeListener(propertyName, this);
            listenedModelObjects.add(modelObject);
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            removeAllElements();
            while(!listenedModelObjects.isEmpty()) {
                listenedModelObjects.remove(0).removePropertyChangeListener(this);
            }
            addAllElements();
        }
    }
}
