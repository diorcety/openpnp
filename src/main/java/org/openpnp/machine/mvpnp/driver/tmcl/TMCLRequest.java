package org.openpnp.machine.mvpnp.driver.tmcl;

public class TMCLRequest {
    private final byte moduleAddress;
    private final byte command;
    private final byte type;
    private final byte motor;
    private final int value;
    private final byte checksum;

    public TMCLRequest(byte moduleAddress, byte command, byte type, byte motor, int value) {
        this.moduleAddress = moduleAddress;
        this.command = command;
        this.type = type;
        this.motor = motor;
        this.value = value;
        this.checksum = computeCheckSum();
    }

    public byte getModuleAddress() {
        return moduleAddress;
    }

    public byte getCommand() {
        return command;
    }

    public byte getType() {
        return type;
    }

    public byte getMotor() {
        return motor;
    }

    public int getValue() {
        return value;
    }

    public byte getChecksum() {
        return checksum;
    }

    private byte computeCheckSum() {
        int temp = this.value;
        return (byte) (this.moduleAddress + this.command + this.type + this.motor +
                (byte) ((temp >> 24) & 0xff) + (byte) ((temp >> 16) & 0xff) +
                (byte) ((temp >> 8) & 0xff) + (byte) (temp & 0xff));
    }

    @Override
    public String toString() {
        return "TMCLRequestCommand{" +
                "moduleAddress=" + moduleAddress +
                ", command=" + command +
                ", type=" + type +
                ", motor=" + motor +
                ", value=" + value +
                ", checksum=" + checksum +
                '}';
    }

    public byte[] getData() {
        byte[] data = new byte[9];
        data[0] = this.moduleAddress;
        data[1] = this.command;
        data[2] = this.type;
        data[3] = this.motor;
        data[4] = (byte) ((this.value >> 24) & 0xff);
        data[5] = (byte) ((this.value >> 16) & 0xff);
        data[6] = (byte) ((this.value >> 8) & 0xff);
        data[7] = (byte) (this.value & 0xff);
        data[8] = this.checksum;
        return data;
    }
}