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

package org.openpnp.machine.reference;

import java.io.Closeable;

import org.openpnp.spi.Driver;
import org.openpnp.spi.PropertySheetHolder;
import org.openpnp.spi.WizardConfigurable;

/**
 * Defines the interface for a simple driver that the ReferenceMachine can drive. All methods result
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
public interface ReferenceDriver extends WizardConfigurable, PropertySheetHolder, Driver, Closeable {
}
