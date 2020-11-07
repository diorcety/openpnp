package org.openpnp.machine.mvpnp.driver.tmcl;

public interface TMCLType {

    byte getByte();

    enum MVP implements TMCLType {
        ABS(0),
        REL(1),
        COORD(2);

        private byte id;

        MVP(int id) {
            this.id = (byte) id;
        }

        @Override
        public byte getByte() {
            return id;
        }
    }

    enum AP implements TMCLType {
        TargetPosition(0),
        ActualPosition(1),
        TargetSpeed(2),
        ActualSpeed(3),
        MaximumPositioningSpeed(4),
        MaximumAcceleration(5),
        MaximumCurrent(6),
        StandbyCurrent(7),
        PositionReachedFlag(8),
        HomeSwitchState(9),
        RightLimitSwitchState(10),
        LeftLimitSwitchState(11),
        RightLimitSwitchDisable(12),
        LeftLimitSwitchDisable(13),
        SwapLimitSwitches(14),
        Acceleration(15),
        Velocity(16),
        MaximumDeceleration(17),
        Deceleration(18),
        VStart(19),
        VStop(20),
        RampWaitTime(21),
        SpeedThresholdForCoolStep(22),
        MinimumSpeedForDcStep(23),
        RightLimitSwitchPolarity(24),
        LeftLimitSwitchPolarity(25),
        SoftStopEnable(26),
        HighSpeedChopperMode(27),
        HighSpeedFullstepMode(28),
        MeasuredSpeed(29),
        PowerDownRamp(31),
        DcStepTime(32),
        DcStepStallGuard(33),
        EEPROMMagic(64),
        RelativePositioningOption(127),
        MicrostepResolution(140),
        ChopperBlankTime(162),
        ConstantTOffMode(163),
        DisableFastDecayComparator(164),
        ChopperHysteresisEnd(165),
        ChopperHysteresisStart(166),
        ChopperOffTime(167),
        SmartEnergyCurrentMinimum(168),
        SmartEnergyCurrentDownStep(169),
        SmartEnergyHysteresis(170),
        SmartEnergyCurrentUpStep(171),
        SmartEnergyHysteresisStart(172),
        StallGuard2FilterEnable(173),
        StallGuard2Threshold(174),
        ShortToGndDisable(177),
        VSense(179),
        SmartEnergyActualCurrent(180),
        SmartEnergyStallVelocity(181),
        SmartEnergyThresholdSpeed(182),
        RandomTOffMode(184),
        ChopperSynchronization(185),
        //PWMThresholdSpeed(186),
        PWMGradient(187),
        PWMAmplitude(188),
        //PWMScale(189),
        //PWMMode(190),
        PWMFrequency(191),
        PWMAutomaticScale(192),
        ReferenceSearchMode(193),
        ReferenceSearchSpeed(194),
        ReferenceSwitchSpeed(195),
        EndSwitchDistance(196),
        LastReferencePosition(197),
        LatchedActualPosition(198),
        LatchedEncoderPosition(199),
        EncoderMode(201),
        MotorFullStepResolution(202),
        FreewheelingMode(204),
        ActualLoadValue(206),
        ExtendedErrorFlags(207),
        MotorDriverErrorFlags(208),
        EncoderPosition(209),
        EncoderResolution(210),
        MaximumEncoderDeviation(212),
        GroupIndex(213),
        PowerDownDelay(214),
        ReverseShaft(251),
        UnitMode(255);

        private byte id;

        AP(int id) {
            this.id = (byte) id;
        }

        @Override
        public byte getByte() {
            return id;
        }
    }

    enum GP implements TMCLType {
        EEPROMMagic(64),
        SerialBaudRate(65),
        SerialAddress(66),
        SerialHostAddress(76),
        SerialSecondaryAddress(87),
        GaugePressureOffset(111);

        private byte id;

        GP(int id) {
            this.id = (byte) id;
        }

        @Override
        public byte getByte() {
            return id;
        }
    }
}
