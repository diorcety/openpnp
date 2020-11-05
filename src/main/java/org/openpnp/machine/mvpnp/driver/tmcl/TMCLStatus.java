package org.openpnp.machine.mvpnp.driver.tmcl;

public enum TMCLStatus {
    NO_ERROR(100),
    LOADED_IN_EEPROM(101),
    WRONG_CHECKSUM(1),
    INVALID_COMMAND(2),
    WRONG_TYPE(3),
    INVALID_VALUE(4),
    EEPROM_LOCKED(5),
    COMMAND_NOT_AVAILABLE(6);

    private final byte id;

    TMCLStatus(int id) {
        this.id = (byte)id;
    }

    public byte getByte() {
        return id;
    }
}
