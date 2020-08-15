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

package org.openpnp.gui.support;

import org.jdesktop.beansbinding.Converter;

public class BooleanConverter extends Converter<Boolean, String> {
    private final String[] _true;
    private final String[] _false;
    private final boolean ignoreCase;

    public BooleanConverter() {
        this(new String[]{Boolean.TRUE.toString(), "1", "on"}, new String[]{Boolean.FALSE.toString(), "0", "off"}, true);
    }

    public BooleanConverter(String[] _true, String[] _false, boolean ignoreCase) {
        this._true = _true;
        this._false = _false;
        this.ignoreCase = ignoreCase;
    }

    @Override
    public String convertForward(Boolean arg0) {
        if (arg0 == null) {
            return null;
        }
        return arg0? _true[0] : _false[0];
    }

    @Override
    public Boolean convertReverse(String arg0) {
        if (arg0 == null) {
            return null;
        }
        if (ignoreCase) {
            arg0 = arg0.toLowerCase();
        }
        for (String s : _true) {
            String rs = ignoreCase? s.toLowerCase() : s;
            if (rs.equals(arg0)) {
                return true;
            }
        }
        for (String s : _false) {
            String rs = ignoreCase? s.toLowerCase() : s;
            if (rs.equals(arg0)) {
                return false;
            }
        }
        throw new IllegalArgumentException("Invalid string boolean value: " + arg0);
    }
}
