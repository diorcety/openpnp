package org.openpnp.machine.mvpnp.driver.tmcl;

import java.io.IOException;

public class TMCLReply {
    private final byte replyAddress;
    private final byte moduleAddress;
    private final byte status;
    private final byte command;
    private final int value;
    private final byte checksum;

    public TMCLReply(byte[] data) throws IOException {
        this.replyAddress = data[0];
        this.moduleAddress = data[1];
        this.status = data[2];
        this.command = data[3];
        this.value = ((data[4] & 0xff) << 24) | ((data[5] & 0xff) << 16) | ((data[6] & 0xff) << 8) | (data[7] & 0xff);
        this.checksum = data[8];
        checkCheckSum();
    }

    public byte getReplyAddress() {
        return replyAddress;
    }

    public byte getModuleAddress() {
        return moduleAddress;
    }

    public byte getStatus() {
        return status;
    }

    public byte getCommand() {
        return command;
    }

    public int getValue() {
        return value;
    }

    public byte getChecksum() {
        return checksum;
    }

    private void checkCheckSum() throws IOException {
        int temp = this.value;
        byte checksum= (byte) (this.replyAddress + this.moduleAddress + this.status + this.command +
                (byte) ((temp >> 24) & 0xff) + (byte) ((temp >> 16) & 0xff) +
                (byte) ((temp >> 8) & 0xff) + (byte) (temp & 0xff));
        if(checksum != this.checksum) {
            throw new IOException("Invalid TMCL checksum");
        }
    }


    @Override
    public String toString() {
        return "TMCLReplyCommand{" +
                "replyAddress=" + replyAddress +
                ", moduleAddress=" + moduleAddress +
                ", status=" + status +
                ", command=" + command +
                ", value=" + value +
                ", checksum=" + checksum +
                '}';
    }
}