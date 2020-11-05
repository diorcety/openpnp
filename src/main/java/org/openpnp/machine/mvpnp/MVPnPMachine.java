package org.openpnp.machine.mvpnp;

import org.openpnp.machine.mvpnp.feeders.MVPnPAutoFeeder;
import org.openpnp.machine.reference.ReferenceMachine;
import org.openpnp.spi.Actuator;
import org.openpnp.spi.Driver;
import org.openpnp.spi.Feeder;

import java.util.List;

public class MVPnPMachine extends ReferenceMachine {

    @Override
    public List<Class<? extends Driver>> getCompatibleDriverClasses() {
        List<Class<? extends Driver>> classes = super.getCompatibleDriverClasses();
        classes.add(MVPnPDriver.class);
        return classes;
    }

    @Override
    public List<Class<? extends Feeder>> getCompatibleFeederClasses() {
        List<Class<? extends Feeder>> classes = super.getCompatibleFeederClasses();
        classes.add(MVPnPAutoFeeder.class);
        return classes;
    }

    @Override
    public List<Class<? extends Actuator>> getCompatibleActuatorClasses() {
        List<Class<? extends Actuator>> classes = super.getCompatibleActuatorClasses();
        classes.add(0, MVPnPIOActuator.class);
        return classes;
    }
}
