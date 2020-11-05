package org.openpnp.machine.mvpnp.feeders;

import org.openpnp.gui.support.PropertySheetWizardAdapter;
import org.openpnp.gui.support.Wizard;
import org.openpnp.machine.mvpnp.MVPnPDriver;
import org.openpnp.machine.mvpnp.driver.MVPnPFeederDriver;
import org.openpnp.machine.mvpnp.feeders.wizards.MVPnPFeederCalibrationWizard;
import org.openpnp.machine.mvpnp.feeders.wizards.MVPnPFeederConfigurationWizard;
import org.openpnp.machine.reference.ReferenceActuator;
import org.openpnp.machine.reference.ReferenceDriver;
import org.openpnp.machine.reference.ReferenceFeeder;
import org.openpnp.machine.reference.ReferenceMachine;
import org.openpnp.model.Configuration;
import org.openpnp.model.Location;
import org.openpnp.spi.Actuator;
import org.openpnp.spi.Machine;
import org.openpnp.spi.Nozzle;
import org.openpnp.spi.PropertySheetHolder;
import org.openpnp.util.MovableUtils;
import org.openpnp.util.UiUtils;
import org.simpleframework.xml.Attribute;

import javax.swing.*;

public class MVPnPAutoFeeder extends ReferenceFeeder {

    @Attribute(required = false)
    protected String driver;

    @Attribute(required = false)
    protected String letter;

    @Attribute(required = false)
    protected Integer advance;

    @Attribute(required = false)
    protected boolean moveBeforeFeed;

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        String oldDriver = this.driver;
        this.driver = driver;
        firePropertyChange("driver", oldDriver, getDriver());
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        String oldLetter = this.letter;
        this.letter = letter;
        firePropertyChange("letter", oldLetter, getLetter());
    }

    public Integer getAdvance() {
        return advance;
    }

    public void setAdvance(Integer advance) {
        Integer oldAdvance = this.advance;
        this.advance = advance;
        firePropertyChange("advance", oldAdvance, getAdvance());
    }

    public boolean isMoveBeforeFeed() {
        return moveBeforeFeed;
    }

    public void setMoveBeforeFeed(boolean moveBeforeFeed) {
        boolean oldMoveBeforeFeed = this.moveBeforeFeed;
        this.moveBeforeFeed = moveBeforeFeed;
        firePropertyChange("moveBeforeFeed", oldMoveBeforeFeed, isMoveBeforeFeed());
    }

    @Override
    public Location getPickLocation() throws Exception {
        return location;
    }

    @Override
    public void feed(Nozzle nozzle) throws Exception {
        MVPnPFeederDriver feederDriver = getFeederDriver();
        if (isMoveBeforeFeed()) {
            MovableUtils.moveToLocationAtSafeZ(nozzle, getPickLocation().derive(null, null, Double.NaN, null));
        }
        getActuator(feederDriver, MVPnPFeederDriver.MVPnPActuator.ACT_MVPNP_FEEDER_SELECT).actuate(letter);
        getActuator(feederDriver, MVPnPFeederDriver.MVPnPActuator.ACT_MVPNP_FEEDER_ADVANCE).actuate(advance);
    }

    public static Actuator getActuator(MVPnPFeederDriver feederDriver, MVPnPFeederDriver.MVPnPActuator feederActuator) throws Exception {
        Actuator actuator = feederDriver.getActuatorByName(feederActuator.getName());
        if (actuator == null) {
            throw new Exception("Actuator \"" + feederActuator.getName() + "\" not found on feeder driver \"" + feederDriver.getName() + "\"");
        }
        return actuator;
    }

    public MVPnPFeederDriver getFeederDriver() throws Exception {
        Machine machine = Configuration.get().getMachine();
        if (!(machine instanceof ReferenceMachine)) {
            throw new Exception("Unsupported machine");
        }
        ReferenceDriver referenceDriver = ((ReferenceMachine) machine).getDriver();
        if (!(referenceDriver instanceof MVPnPDriver)) {
            throw new Exception("Unsupported driver");
        }
        MVPnPFeederDriver feederDriver = ((MVPnPDriver) referenceDriver).getFeederDriverByName(driver);
        if (feederDriver == null) {
            throw new Exception("Feed failed. Unable to find an feeder driver named \"" + driver + "\"");
        }
        return feederDriver;
    }

    @Override
    public String getPropertySheetHolderTitle() {
        return getClass().getSimpleName() + " " + getName();
    }

    @Override
    public PropertySheetHolder[] getChildPropertySheetHolders() {
        return new PropertySheetHolder[0];
    }

    @Override
    public Action[] getPropertySheetHolderActions() {
        return new Action[0];
    }

    @Override
    public Wizard getConfigurationWizard() {
        return new MVPnPFeederConfigurationWizard(this);
    }

    @Override
    public PropertySheet[] getPropertySheets() {
        return new PropertySheet[] {
                new PropertySheetWizardAdapter(getConfigurationWizard(), "Configuration"),
                new PropertySheetWizardAdapter(new MVPnPFeederCalibrationWizard(this), "Calibration")
        };
    }
}
