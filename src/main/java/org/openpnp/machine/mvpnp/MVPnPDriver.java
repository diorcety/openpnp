package org.openpnp.machine.mvpnp;

import org.openpnp.gui.support.Icons;
import org.openpnp.gui.support.Wizard;
import org.openpnp.machine.mvpnp.driver.MVPnPFeederDriver;
import org.openpnp.machine.mvpnp.driver.MVPnPMotorDriver;
import org.openpnp.machine.reference.ReferenceActuator;
import org.openpnp.machine.reference.ReferenceDriver;
import org.openpnp.machine.reference.ReferenceHead;
import org.openpnp.machine.reference.ReferenceHeadMountable;
import org.openpnp.model.AbstractModelObject;
import org.openpnp.model.LengthUnit;
import org.openpnp.model.Location;
import org.openpnp.spi.Actuator;
import org.openpnp.spi.Head;
import org.openpnp.spi.HeadMountable;
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

    @ElementList(required = false)
    protected List<MVPnPMotorDriver> motorDrivers = new ArrayList<>();

    public MVPnPDriver() {
    }

    @Commit
    public void commit() {
        for (MVPnPMotorDriver driver : motorDrivers) {
            driver.setParent(this);
        }
        for (MVPnPFeederDriver driver : feederDrivers) {
            driver.setParent(this);
        }
    }

    @Override
    public void home(Head head) throws Exception {
        for (ReferenceDriver driver : motorDrivers) {
            driver.home(head);
        }
        for (ReferenceDriver driver : feederDrivers) {
            driver.home(head);
        }
    }

    @Override
    public void moveTo(HeadMountable hm, Location location, double speed, Movable.MoveToOption... options) throws Exception {
        for (ReferenceDriver driver : motorDrivers) {
            driver.moveTo(hm, location,speed, options);
        }
        for (ReferenceDriver driver : feederDrivers) {
            driver.moveTo(hm, location,speed, options);
        }
    }

    @Override
    public Location getLocation(HeadMountable hm) {
        for (ReferenceDriver driver : motorDrivers) {
            Location location = driver.getLocation(hm);
            if (location != null) {
                return location;
            }
        }
        for (ReferenceDriver driver : feederDrivers) {
            Location location = driver.getLocation(hm);
            if (location != null) {
                return location;
            }
        }
        return new Location(LengthUnit.Millimeters, Double.NaN, Double.NaN, Double.NaN, Double.NaN);
    }

    @Override
    public void actuate(Actuator actuator, Object value) throws Exception {
        for (ReferenceDriver driver : motorDrivers) {
            driver.actuate(actuator, value);
        }
        for (ReferenceDriver driver : feederDrivers) {
            driver.actuate(actuator, value);
        }
    }

    @Override
    public Object actuatorRead(Actuator actuator, Object parameter) throws Exception {
        for (ReferenceDriver driver : motorDrivers) {
            Object s = driver.actuatorRead(actuator, parameter);
            if (s != null) {
                return s;
            }
        }
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
        for (ReferenceDriver driver : motorDrivers) {
            driver.setEnabled(enabled);
        }
        for (ReferenceDriver driver : feederDrivers) {
            driver.setEnabled(enabled);
        }
    }

    @Override
    public void close() throws IOException {
        for (ReferenceDriver driver : motorDrivers) {
            driver.close();
        }
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
        children.add(new SimplePropertySheetHolder("Motor Drivers", motorDrivers) {
            @Override
            public Action[] getPropertySheetHolderActions() {
                return new Action[] {addMotorDriverAction};
            }
        });
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

    public Action addMotorDriverAction = new AbstractAction() {
        {
            putValue(SMALL_ICON, Icons.add);
            putValue(NAME, "Add Motor Driver...");
            putValue(SHORT_DESCRIPTION, "Add a new motor driver.");
        }

        @Override
        public void actionPerformed(ActionEvent arg0) {
            addMotorDriver(new MVPnPMotorDriver());
        }
    };

    public Action addFeederDriverAction = new AbstractAction() {
        {
            putValue(SMALL_ICON, Icons.add);
            putValue(NAME, "Add Feeder Driver...");
            putValue(SHORT_DESCRIPTION, "Add a new feeder driver.");
        }

        @Override
        public void actionPerformed(ActionEvent arg0) {
            addFeederDriver(new MVPnPFeederDriver());
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

    public void addMotorDriver(MVPnPMotorDriver motorDriver) {
        motorDriver.setParent(this);
        motorDrivers.add(motorDriver);
        fireIndexedPropertyChange("feederDrivers", motorDrivers.size() - 1, null, motorDriver);

    }

    public void removeMotorDriver(MVPnPMotorDriver motorDriver) {
        motorDrivers.remove(motorDriver);
        fireIndexedPropertyChange("feederDrivers", motorDrivers.size(), motorDriver, null);
    }

    public List<MVPnPMotorDriver> getMotorDrivers() {
        return motorDrivers;
    }

    public MVPnPMotorDriver getMotorDriverByName(String name) {
        for (MVPnPMotorDriver motorDriver : motorDrivers) {
            if(motorDriver.getName().equals(name)) {
                return motorDriver;
            }
        }
        return null;
    }

    public void addFeederDriver(MVPnPFeederDriver feederDriver) {
        feederDriver.setParent(this);
        feederDrivers.add(feederDriver);
        fireIndexedPropertyChange("feederDrivers", feederDrivers.size() - 1, null, feederDriver);

    }

    public void removeFeederDriver(MVPnPFeederDriver feederDriver) {
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
