package org.openpnp.spi;

import org.openpnp.model.Location;

import java.io.Closeable;

/**
 * Defines the interface for a simple driver. All methods result
 * in machine operations and all methods should block until they are complete or throw an error.
 *
 * This Driver interface is intended to model a machine with one or more Heads, and each Head having
 * one or more Nozzles and zero or more Cameras and Actuators.
 *
 * In OpenPnP, the Head does not move on it's own. It is moved by the moving of attached objects:
 * Nozzles, Cameras, Actuators. For this reason, all movements on the driver are specified as
 * movements by one of these objects. This allows the driver to make decisions as to what axes
 * should be moved to accomplish a specific task.
 */
public interface Driver {
    /**
     * Performing the hardware homing operation for the given Head. When this call completes the
     * Head should be at it's 0,0,0,0 position.
     *
     * @throws Exception
     */
    public void home(Head head) throws Exception;

    /**
     * Moves the specified HeadMountable to the given location at a speed defined by (maximum feed
     * rate * speed) where speed is greater than 0 and typically less than or equal to 1. A speed of
     * 0 means to move at the minimum possible speed.
     *
     * HeadMountable object types include Nozzle, Camera and Actuator.
     *
     * @param hm
     * @param location destination
     * @param speed relative speed (0-1) of the move
     * @param options zero to n options from the MoveToOptions enum.
     * @throws Exception
     */
    public void moveTo(HeadMountable hm, Location location, double speed, Movable.MoveToOption... options) throws Exception;

    /**
     * Returns a clone of the HeadMountable's current location. It's important that the returned
     * object is a clone, since the caller may modify the returned Location.
     *
     * @param hm
     * @return
     */
    public Location getLocation(HeadMountable hm);

    /**
     * Actuates a machine defined object.
     *
     * @param actuator
     * @param value
     * @throws Exception
     */
    public void actuate(Actuator actuator, Object value) throws Exception;

    /**
     * Read a value from the given Actuator.
     *
     * @param actuator
     * @return
     * @throws Exception
     */
    public default Object actuatorRead(Actuator actuator, Object parameter) throws Exception {
        return null;
    }

    /**
     * Attempts to enable the Driver, turning on all outputs.
     *
     * @param enabled
     * @throws Exception
     */
    public void setEnabled(boolean enabled) throws Exception;

    public default void createDefaults() {};
}
