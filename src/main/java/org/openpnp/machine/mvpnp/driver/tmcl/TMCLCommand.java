package org.openpnp.machine.mvpnp.driver.tmcl;

public enum TMCLCommand {
    ROR(1),
    ROL(2),
    MST(3),
    MVP(4),
    SAP(5),
    GAP(6),
    STAP(7),
    RSAP(8),
    SGP(9),
    GGP(10),
    STGP(11),
    RSGP(12),
    RFS(13),
    SIO(14),
    GIO(15),
    CALC(19),
    COMP(20),
    JC(21),
    JA(22),
    CSUB(23),
    RSUB(24),
    EI(25),
    DI(26),
    WAIT(27),
    STOP(28),
    SCO(30),
    GCO(31),
    CCO(32),
    CALCX(33),
    AAP(34),
    AGP(35),
    CLE(36),
    VECT(37),
    RETI(38),
    ACO(39),
    GFV(136);

    private final byte id;

    TMCLCommand(int id) {
        this.id = (byte)id;
    }

    public byte getByte() {
        return id;
    }
}
