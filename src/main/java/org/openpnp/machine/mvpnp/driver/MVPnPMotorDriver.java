package org.openpnp.machine.mvpnp.driver;

import org.openpnp.gui.MainFrame;
import org.openpnp.gui.support.Icons;
import org.openpnp.gui.support.PropertySheetWizardAdapter;
import org.openpnp.machine.mvpnp.MVPnPDriver;
import org.openpnp.machine.mvpnp.MVPnPIOActuator;
import org.openpnp.machine.mvpnp.driver.tmcl.TMCLCommand;
import org.openpnp.machine.mvpnp.driver.tmcl.TMCLReply;
import org.openpnp.machine.mvpnp.driver.tmcl.TMCLRequest;
import org.openpnp.machine.mvpnp.driver.tmcl.TMCLType;
import org.openpnp.machine.mvpnp.driver.wizards.MVPnPMotorCalibrationWizard;
import org.openpnp.machine.mvpnp.driver.wizards.MVPnPMotorSettings;
import org.openpnp.machine.reference.ReferenceHeadMountable;
import org.openpnp.machine.reference.driver.AbstractReferenceDriver;
import org.openpnp.model.Configuration;
import org.openpnp.model.LengthUnit;
import org.openpnp.model.Location;
import org.openpnp.spi.Actuator;
import org.openpnp.spi.Head;
import org.openpnp.spi.HeadMountable;
import org.openpnp.spi.Movable;
import org.openpnp.spi.Nozzle;
import org.pmw.tinylog.Logger;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MVPnPMotorDriver extends AbstractReferenceDriver {

    private static final int TMCL_FRAME_SIZE = 9;

    @Attribute(required = false)
    protected String name = "MVPnPMotorDriver";

    @Attribute(required = false)
    protected LengthUnit units = LengthUnit.Millimeters;

    @ElementList(required = false)
    protected List<Axis> axes = new ArrayList<Axis>();

    private boolean connected;

    private MVPnPDriver parent = null;

    private Map<Byte, Byte> moduleStatus = new HashMap<Byte, Byte>();

    public void setParent(MVPnPDriver driver) {
        parent = driver;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        String oldName = this.name;
        this.name = name;
        firePropertyChange("name", oldName, getName());
    }

    @Override
    public String getPropertySheetHolderTitle() {
        return getName() == null ? "MVPnPMotorDriver" : getName();
    }

    @Override
    public PropertySheet[] getPropertySheets() {
        return new PropertySheet[]{
                new PropertySheetWizardAdapter(new MVPnPMotorSettings(this), "General Settings"),
                new PropertySheetWizardAdapter(super.getConfigurationWizard(), "Communications"),
                new PropertySheetWizardAdapter(new MVPnPMotorCalibrationWizard(this), "Calibration")
        };
    }

    @Override
    public Action[] getPropertySheetHolderActions() {
        return new Action[]{deleteDriverAction};
    }

    public Action deleteDriverAction = new AbstractAction() {
        {
            putValue(SMALL_ICON, Icons.delete);
            putValue(NAME, "Delete Motor Driver...");
            putValue(SHORT_DESCRIPTION, "Delete the selected motor driver.");
        }

        @Override
        public void actionPerformed(ActionEvent arg0) {
            int ret = JOptionPane.showConfirmDialog(MainFrame.get(),
                    "Are you sure you want to delete the selected motor driver?",
                    "Delete Motor Driver?", JOptionPane.YES_NO_OPTION);
            if (ret == JOptionPane.YES_OPTION) {
                parent.removeMotorDriver(MVPnPMotorDriver.this);
            }
        }
    };

    public synchronized void connect() throws Exception {
        getCommunications().connect();
        connected = true;
    }

    @Override
    public void disconnect() throws Exception {
        connected = false;
        try {
            getCommunications().disconnect();
        } catch (Exception e) {
            Logger.error("disconnect()", e);
        }
    }

    @Override
    public void createDefaults() {
        axes = new ArrayList<>();
        byte module = 0;
        axes.add(new Axis("x", Axis.Type.X, module, (byte) 0, 0, "*"));
        axes.add(new Axis("y", Axis.Type.Y, module, (byte) 1, 0, "*"));
        module++;
        try {
            List<Nozzle> nozzles = Configuration.get().getMachine().getDefaultHead().getNozzles();
            if (nozzles.size() < 1) {
                throw new Exception("No nozzles.");
            }
            for (Nozzle nozzle : nozzles) {
                axes.add(new Axis("z", Axis.Type.X, module, (byte) 0, 0, nozzle.getId()));
                axes.add(new Axis("rotation", Axis.Type.Y, module, (byte) 1, 0, nozzle.getId()));
                module++;
            }
        } catch (Exception e) {
            Logger.warn("Can't create nozzle axis", e);
        }
    }

    @Override
    public void home(Head head) throws Exception {

    }

    @Override
    public void moveTo(HeadMountable hm, Location location, double speed, Movable.MoveToOption... options) throws Exception {
        location = location.convertToUnits(units);
        if (hm instanceof ReferenceHeadMountable) {
            location = location.subtract(((ReferenceHeadMountable)hm).getHeadOffsets());
        }

        Axis xAxis = getAxis(hm, Axis.Type.X);
        Axis yAxis = getAxis(hm, Axis.Type.Y);
        Axis zAxis = getAxis(hm, Axis.Type.Z);
        Axis rotationAxis = getAxis(hm, Axis.Type.Rotation);

        move(hm, xAxis, location.getX(), speed);
        move(hm, yAxis, location.getY(), speed);
        move(hm, zAxis, location.getZ(), speed);
        move(hm, rotationAxis, location.getRotation(), speed);
    }

    @Override
    public Location getLocation(HeadMountable hm) {
        // according main driver
        Axis xAxis = getAxis(hm, Axis.Type.X);
        Axis yAxis = getAxis(hm, Axis.Type.Y);
        Axis zAxis = getAxis(hm, Axis.Type.Z);
        Axis rotationAxis = getAxis(hm, Axis.Type.Rotation);

        Location location =
                new Location(units, xAxis == null ? 0 : xAxis.getTransformedCoordinate(hm),
                        yAxis == null ? 0 : yAxis.getTransformedCoordinate(hm),
                        zAxis == null ? 0 : zAxis.getTransformedCoordinate(hm),
                        rotationAxis == null ? 0 : rotationAxis.getTransformedCoordinate(hm));
        if (hm instanceof ReferenceHeadMountable) {
            location = location.add(((ReferenceHeadMountable)hm).getHeadOffsets());
        }
        return location;
    }

    @Override
    public void actuate(Actuator actuator, Object value) throws Exception {
        if (!(actuator instanceof MVPnPIOActuator)) {
            return;
        }
        MVPnPIOActuator ioActuator = (MVPnPIOActuator) actuator;
        int intValue = ioActuator.getType().getConverter().convertForward(value);
        sendCommand(new TMCLRequest((byte)ioActuator.getModule(), TMCLCommand.SIO.getByte(), (byte)ioActuator.getPort(), (byte)ioActuator.getBank(), intValue));
    }

    @Override
    public Object actuatorRead(Actuator actuator, Object parameter) throws Exception {
        if (!(actuator instanceof MVPnPIOActuator)) {
            return null;
        }
        MVPnPIOActuator ioActuator = (MVPnPIOActuator) actuator;
        TMCLReply tmclReply = sendCommand(new TMCLRequest((byte)ioActuator.getModule(), TMCLCommand.SIO.getByte(), (byte)ioActuator.getPort(), (byte)ioActuator.getBank(), 0));
        return ioActuator.getType().getConverter().convertReverse(tmclReply.getValue());
    }

    @Override
    public void setEnabled(boolean enabled) throws Exception {
        if (enabled && !connected) {
            connect();
        }
        if (connected && !enabled) {
            if (!connectionKeepAlive) {
                disconnect();
            }
        }
    }

    private void move(HeadMountable hm, Axis axis, double position, double speed) throws Exception {
        if (Double.isNaN(position)) {
            return; // No move requested
        }
        int stepPosition;
        int stepSpeed;
        if (axis.getTransform() != null) {
            stepPosition = axis.getTransform().toRaw(axis, hm, position);
            stepSpeed = axis.getTransform().toRaw(axis, hm, speed);
        } else {
            stepPosition = (int) position;
            stepSpeed = (int) speed;
        }
        if (axis.getCoordinate() == stepPosition) {
            return; // Already at the position
        }
        setMoveSpeed(axis, stepSpeed);
        moveTo(axis, stepPosition);
    }

    private void moveTo(Axis axis, int position) throws Exception {
        sendCommand(axis, TMCLCommand.MVP, TMCLType.MVP.ABS, position);
    }

    private void setMoveSpeed(Axis axis, int speed) throws Exception {
        sendCommand(axis, TMCLCommand.SAP, TMCLType.AP.MaximumPositioningSpeed, speed);
    }

    public int sendCommand(Axis axis, TMCLCommand command, TMCLType type, int value) throws Exception {
        TMCLRequest tmclRequest = new TMCLRequest(axis.getModule(), command.getByte(), type.getByte(), axis.getMotor(), value);
        TMCLReply tmclReply = sendCommand(tmclRequest);
        return tmclReply.getValue();
    }

    public TMCLReply sendCommand(TMCLRequest tmclRequest) throws Exception {
        Logger.debug("sendCommand({})...", tmclRequest);
        getCommunications().writeBytes(tmclRequest.getData());
        byte[] bytes = getCommunications().readBytes(TMCL_FRAME_SIZE);
        if (bytes == null) {
            throw new IOException("Can't get TMCL reply");
        }
        TMCLReply tmclReply = new TMCLReply(bytes);
        Logger.debug("sendCommand({}) => {}", tmclRequest, tmclReply);
        moduleStatus.put(tmclRequest.getModuleAddress(), tmclReply.getStatus());
        return tmclReply;
    }

    private Axis getAxis(HeadMountable hm, Axis.Type type) {
        for (Axis axis : axes) {
            if (axis.getType() == type && (axis.getHeadMountableIds().contains("*")
                    || axis.getHeadMountableIds().contains(hm.getId()))) {
                return axis;
            }
        }
        return null;
    }

    public static class Axis {
        public enum Type {
            X,
            Y,
            Z,
            Rotation
        }

        @Attribute
        private String name;

        @Attribute
        private Byte module;

        @Attribute
        private Byte motor;

        @Attribute
        private Type type;

        @Attribute(required = false)
        private int homeCoordinate = 0;

        @ElementList(required = false)
        private Set<String> headMountableIds = new HashSet<String>();

        @Element(required = false)
        private AxisTransform transform;

        @Element(required = false, data = true)
        private String preMoveCommand;

        /**
         * Stores the current value for this axis.
         */
        private int coordinate = 0;

        public Axis() {

        }

        public Axis(String name, Type type, Byte module, Byte motor, int homeCoordinate, String... headMountableIds) {
            this.name = name;
            this.type = type;
            this.module = module;
            this.motor = motor;
            this.homeCoordinate = homeCoordinate;
            this.headMountableIds.addAll(Arrays.asList(headMountableIds));
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Byte getModule() {
            return module;
        }

        public void setModule(Byte module) {
            this.module = module;
        }

        public Byte getMotor() {
            return motor;
        }

        public void setMotor(Byte motor) {
            this.motor = motor;
        }

        public Type getType() {
            return type;
        }

        public void setType(Type type) {
            this.type = type;
        }

        public int getCoordinate() {
            return coordinate;
        }

        public void setCoordinate(int coordinate) {
            this.coordinate = coordinate;
        }

        public int getHomeCoordinate() {
            return homeCoordinate;
        }

        public void setHomeCoordinate(int homeCoordinate) {
            this.homeCoordinate = homeCoordinate;
        }

        public double getTransformedCoordinate(HeadMountable hm) {
            if (this.transform != null) {
                return transform.toTransformed(this, hm, this.coordinate);
            }
            return this.coordinate;
        }

        public Set<String> getHeadMountableIds() {
            return headMountableIds;
        }

        public void setHeadMountableIds(Set<String> headMountableIds) {
            this.headMountableIds = headMountableIds;
        }

        public AxisTransform getTransform() {
            return transform;
        }

        public void setTransform(AxisTransform transform) {
            this.transform = transform;
        }

        public String getPreMoveCommand() {
            return preMoveCommand;
        }

        public void setPreMoveCommand(String preMoveCommand) {
            this.preMoveCommand = preMoveCommand;
        }
    }

    public interface AxisTransform {
        /**
         * Transform the specified raw coordinate into it's corresponding transformed coordinate.
         * The transformed coordinate is what the user sees, while the raw coordinate is what the
         * motion controller sees.
         *
         * @param hm
         * @param rawCoordinate
         * @return
         */
        public double toTransformed(Axis axis, HeadMountable hm, int rawCoordinate);

        /**
         * Transform the specified transformed coordinate into it's corresponding raw coordinate.
         * The transformed coordinate is what the user sees, while the raw coordinate is what the
         * motion controller sees.
         *
         * @param hm
         * @param transformedCoordinate
         * @return
         */
        public int toRaw(Axis axis, HeadMountable hm, double transformedCoordinate);
    }
}
