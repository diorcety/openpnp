package org.openpnp.machine.mvpnp;

import org.openpnp.gui.support.Icons;
import org.openpnp.gui.support.Wizard;
import org.openpnp.machine.mvpnp.driver.MVPnPFeederDriver;
import org.openpnp.machine.reference.ReferenceActuator;
import org.openpnp.machine.reference.ReferenceDriver;
import org.openpnp.machine.reference.ReferenceHead;
import org.openpnp.machine.reference.ReferenceHeadMountable;
import org.openpnp.model.AbstractModelObject;
import org.openpnp.model.LengthUnit;
import org.openpnp.model.Location;
import org.openpnp.spi.Movable;
import org.openpnp.spi.PropertySheetHolder;
import org.openpnp.spi.base.SimplePropertySheetHolder;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.core.Commit;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Root
public class MVPnPDriver extends AbstractModelObject implements ReferenceDriver, Closeable {

    @Attribute(required = false)
    protected String name = "MVPnPDriver";

    @ElementList(required = false)
    protected List<MVPnPFeederDriver> feederDrivers = new ArrayList<>();

    public MVPnPDriver() {
    }

    @Commit
    public void commit() {
        for (MVPnPFeederDriver driver : feederDrivers) {
            driver.setParent(this);
        }
    }

    @Override
    public void home(ReferenceHead head) throws Exception {

    }

    @Override
    public void moveTo(ReferenceHeadMountable hm, Location location, double speed, Movable.MoveToOption... options) throws Exception {

    }

    @Override
    public Location getLocation(ReferenceHeadMountable hm) {
        return new Location(LengthUnit.Millimeters, 0, 0, 0, 0);
    }

    @Override
    public void actuate(ReferenceActuator actuator, Object value) throws Exception {
        for (ReferenceDriver driver : feederDrivers) {
            driver.actuate(actuator, value);
        }
    }

    @Override
    public Object actuatorRead(ReferenceActuator actuator, Object parameter) throws Exception {
        for (ReferenceDriver driver : feederDrivers) {
            Object s = driver.actuatorRead(actuator, parameter);
            if (s != null) {
                return s;
            }
        }
        return null;
    }

    @Override
    public void setEnabled(boolean enabled) throws Exception {
        for (ReferenceDriver driver : feederDrivers) {
            driver.setEnabled(enabled);
        }
    }

    @Override
    public void close() throws IOException {
        for (ReferenceDriver driver : feederDrivers) {
            driver.close();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        firePropertyChange("name", null, getName());
    }

    @Override
    public String getPropertySheetHolderTitle() {
        return getName() == null ? "MVPnPDriver" : getName();
    }

    @Override
    public PropertySheetHolder[] getChildPropertySheetHolders() {
        ArrayList<PropertySheetHolder> children = new ArrayList<>();
        children.add(new SimplePropertySheetHolder("Feeder Drivers", feederDrivers) {
            @Override
            public Action[] getPropertySheetHolderActions() {
                return new Action[] {addFeederDriverAction};
            }
        });
        return children.toArray(new PropertySheetHolder[] {});
    }

    @Override
    public PropertySheet[] getPropertySheets() {
        return new PropertySheet[0];
    }

    @Override
    public Action[] getPropertySheetHolderActions() {
        return null;
    }

    public Action addFeederDriverAction = new AbstractAction() {
        {
            putValue(SMALL_ICON, Icons.add);
            putValue(NAME, "Add Feeder Driver...");
            putValue(SHORT_DESCRIPTION, "Add a new feeder driver.");
        }

        @Override
        public void actionPerformed(ActionEvent arg0) {
            addFeederDrivers(new MVPnPFeederDriver());
        }
    };

    @Override
    public Icon getPropertySheetHolderIcon() {
        return null;
    }

    @Override
    public Wizard getConfigurationWizard() {
        return null;
    }

    public void addFeederDrivers(MVPnPFeederDriver feederDriver) {
        feederDriver.setParent(this);
        feederDrivers.add(feederDriver);
        fireIndexedPropertyChange("feederDrivers", feederDrivers.size() - 1, null, feederDriver);

    }

    public void removeFeederDrivers(MVPnPFeederDriver feederDriver) {
        feederDrivers.remove(feederDriver);
        fireIndexedPropertyChange("feederDrivers", feederDrivers.size(), feederDriver, null);
    }

    public List<MVPnPFeederDriver> getFeederDrivers() {
        return feederDrivers;
    }

    public MVPnPFeederDriver getFeederDriverByName(String name) {
        for (MVPnPFeederDriver feederDriver : feederDrivers) {
            if(feederDriver.getName().equals(name)) {
                return feederDriver;
            }
        }
        return null;
    }
}
