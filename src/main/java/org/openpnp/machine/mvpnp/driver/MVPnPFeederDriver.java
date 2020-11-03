package org.openpnp.machine.mvpnp.driver;

import org.jdesktop.beansbinding.Converter;
import org.openpnp.gui.MainFrame;
import org.openpnp.gui.support.BooleanConverter;
import org.openpnp.gui.support.Icons;
import org.openpnp.gui.support.IntegerConverter;
import org.openpnp.gui.support.PropertySheetWizardAdapter;
import org.openpnp.machine.mvpnp.MVPnPDriver;
import org.openpnp.machine.mvpnp.driver.wizards.MVPnPFeederSettings;
import org.openpnp.machine.reference.ReferenceActuator;
import org.openpnp.machine.reference.ReferenceHead;
import org.openpnp.machine.reference.ReferenceHeadMountable;
import org.openpnp.machine.reference.ReferenceMachine;
import org.openpnp.machine.reference.driver.AbstractReferenceDriver;
import org.openpnp.model.Configuration;
import org.openpnp.model.Location;
import org.openpnp.spi.Movable;
import org.pmw.tinylog.Logger;
import org.simpleframework.xml.Attribute;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;

public class MVPnPFeederDriver extends AbstractReferenceDriver {

    private static final String QUERY_DELIMITER = "\n";
    private static final String QUERY_SEPARATOR = "=";
    private static final String REPLY_TERMINATION = "\r";
    private static final String REPLY_SEPARATOR = ":";
    private static final String REPLY_OK = "OK";

    @Attribute(required = false)
    protected String name = "MVPnPFeederDriver";

    private MVPnPDriver parent = null;
    private char currentFeeder = 0;
    private boolean connected;
    private List<ReferenceActuator> actuatorList = new LinkedList<ReferenceActuator>();

    public enum Actuator {
        ACT_MVPNP_FEEDER_SELECT("Select", '\0', new Converter<String, String>() {
            @Override
            public String convertForward(String o) {
                return o;
            }

            @Override
            public String convertReverse(String o) {
                return o;
            }
        }),
        ACT_MVPNP_FEEDER_NAME("Name", 'N', new Converter<String, String>() {
            @Override
            public String convertForward(String o) {
                return o;
            }

            @Override
            public String convertReverse(String o) {
                return o;
            }
        }),
        ACT_MVPNP_FEEDER_ADVANCE("Advance", 'A', new IntegerConverter() {
            @Override
            public String convertForward(Integer arg0) {
                if (arg0 == null) { // Forward null values
                    return null;
                }
                return super.convertForward(arg0);
            }

            @Override
            public Integer convertReverse(String arg0) {
                if (arg0 == null) { // Forward null values
                    return null;
                }
                return super.convertReverse(arg0);
            }
        }),
        ACT_MVPNP_FEEDER_ENABLE("Enable", 'E', new BooleanConverter(new String[]{"1"}, new String[]{"0"}, true)),
        ACT_MVPNP_FEEDER_POSITION("Position", 'P', new IntegerConverter()),
        ACT_MVPNP_FEEDER_ORIGIN("Origin", 'O', new IntegerConverter()),
        ACT_MVPNP_FEEDER_HALF("Half", 'H', new IntegerConverter()),
        ACT_MVPNP_FEEDER_FULL("Full", 'F', new IntegerConverter()),
        ACT_MVPNP_FEEDER_LENGTH("Length", 'L', new IntegerConverter()),
        ACT_MVPNP_FEEDER_TIME("Time", 'T', new IntegerConverter()),
        ACT_MVPNP_FEEDER_SAVE("Save", 'S', new BooleanConverter(new String[]{"1"}, new String[]{"0"}, true)),
        ACT_MVPNP_FEEDER_RESET("Reset", 'R', new BooleanConverter(new String[]{"1"}, new String[]{"0"}, true));

        private final String name;
        private final char cmd;
        private final Converter<?, String> converter;

        Actuator(String name, char cmd, Converter<?, String> converter) {
            this.name = name;
            this.cmd = cmd;
            this.converter = converter;
        }

        public String getName() {
            return name;
        }

        public char getCmd() {
            return cmd;
        }

        public Converter<?, String> getConverter() {
            return converter;
        }
    }

    public MVPnPFeederDriver() {
    }

    public synchronized void connect() throws Exception {
        getCommunications().connect();
        connected = true;
    }

    @Override
    public void createDefaults() {
        // Make sure required objects exist
        ReferenceMachine machine = ((ReferenceMachine) Configuration.get().getMachine());

        for (ReferenceActuator actuator : actuatorList) {
            machine.removeActuator(actuator);
        }

        for (Actuator value : Actuator.values()) {
            ReferenceActuator a = new ReferenceActuator();
            a.setName(getActuatorFullname(value.getName()));
            actuatorList.add(a);
            try {
                machine.addActuator(a);
            } catch (Exception e) {
                Logger.error("Can't add actuator: " + a.getName());
            }
        }
    }

    public ReferenceActuator getActuatorByName(String name) {
        String fullname = getActuatorFullname(name);
        for (ReferenceActuator referenceActuator : actuatorList) {
            if (referenceActuator.getName().equals(fullname)) {
                return referenceActuator;
            }
        }
        return null;
    }

    private String getActuatorFullname(String name) {
        return getName() + "_" + name;
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
    public void home(ReferenceHead head) throws Exception {

    }

    @Override
    public void moveTo(ReferenceHeadMountable hm, Location location, double speed, Movable.MoveToOption... options) throws Exception {

    }

    @Override
    public Location getLocation(ReferenceHeadMountable hm) {
        return null;
    }

    @Override
    public void actuate(ReferenceActuator actuator, Object value) throws Exception {
        for (Actuator act : Actuator.values()) {
            if (getActuatorFullname(act.getName()).equals(actuator.getName())) {
                String strValue = ((Converter<Object, String>)act.getConverter()).convertForward(value);
                if (act == Actuator.ACT_MVPNP_FEEDER_SELECT) {
                    if (strValue.length() != 1) {
                        throw new IllegalArgumentException("Invalid feeder name");
                    }
                    currentFeeder = strValue.charAt(0);
                    return;
                } else {
                    writeValue(act.getCmd(), strValue);
                    return;
                }
            }
        }

        throw new IllegalArgumentException("Invalid actuate");
    }

    @Override
    public Object actuatorRead(ReferenceActuator actuator, Object value) throws Exception {
        for (Actuator act : Actuator.values()) {
            if (getActuatorFullname(act.getName()).equals(actuator.getName())) {
                if (act == Actuator.ACT_MVPNP_FEEDER_SELECT) {
                    return "" + currentFeeder;
                } else {
                    String strValue = readValue(act.getCmd());
                    return ((Converter<Object, String>)act.getConverter()).convertReverse(strValue);
                }
            }
        }

        throw new IllegalArgumentException("Invalid actuate");
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

    private String sendCommand(char feeder, char cmd, String parameter) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb = sb.append(QUERY_DELIMITER).append(feeder).append(cmd);
        if (parameter != null) {
            sb = sb.append(QUERY_SEPARATOR).append(parameter);
        }
        sb.append(QUERY_DELIMITER);
        String query = sb.toString();
        getCommunications().writeBytes(query.getBytes());
        String s = getCommunications().readUntil(REPLY_TERMINATION);
        String[] split = s.split(REPLY_SEPARATOR);
        if (split.length != 2 && split[0].length() != 1) {
            throw new IllegalStateException("Invalid reply: " + s);
        }
        if (split[0].charAt(0) != cmd) {
            throw new IllegalStateException("Reply for another command");
        }
        return split[1];
    }

    private void writeValue(char cmd, String parameter) throws Exception {
        if (currentFeeder == 0) {
            throw new IllegalArgumentException("No feeder selected");
        }
        String reply = sendCommand(currentFeeder, cmd, parameter);
        if (!REPLY_OK.equals(reply)) {
            throw new IllegalStateException("Can't execute action: " + reply);
        }
    }

    private String readValue(char cmd) throws Exception {
        if (currentFeeder == 0) {
            throw new IllegalArgumentException("No feeder selected");
        }
        return sendCommand(currentFeeder, cmd, null);
    }

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
        return getName() == null ? "MVPnPFeederDriver" : getName();
    }

    @Override
    public PropertySheet[] getPropertySheets() {
        return new PropertySheet[]{
                new PropertySheetWizardAdapter(new MVPnPFeederSettings(this), "General Settings"),
                new PropertySheetWizardAdapter(super.getConfigurationWizard(), "Communications")
        };
    }

    @Override
    public Action[] getPropertySheetHolderActions() {
        return new Action[]{deleteDriverAction};
    }

    public Action deleteDriverAction = new AbstractAction() {
        {
            putValue(SMALL_ICON, Icons.delete);
            putValue(NAME, "Delete Feeder Driver...");
            putValue(SHORT_DESCRIPTION, "Delete the selected feeder driver.");
        }

        @Override
        public void actionPerformed(ActionEvent arg0) {
            int ret = JOptionPane.showConfirmDialog(MainFrame.get(),
                    "Are you sure you want to delete the selected feeder driver?",
                    "Delete Feeder Driver?", JOptionPane.YES_NO_OPTION);
            if (ret == JOptionPane.YES_OPTION) {
                parent.removeFeederDriver(MVPnPFeederDriver.this);
            }
        }
    };
}
