package org.openpnp.machine.mvpnp.driver;

import org.openpnp.gui.support.Wizard;
import org.openpnp.machine.mvpnp.driver.wizards.MVPnPIOActuatorConfigurationWizard;
import org.openpnp.machine.reference.ReferenceActuator;
import org.simpleframework.xml.Attribute;

public class MVPnPIOActuator extends ReferenceActuator {
    @Attribute
    private int module;

    @Attribute
    private int bank;

    @Attribute
    private int port;

    @Attribute
    private Type type = Type.BOOLEAN;

    public int getModule() {
        return module;
    }

    public void setModule(int module) {
        this.module = module;
    }

    public int getBank() {
        return bank;
    }

    public void setBank(int bank) {
        this.bank = bank;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public Wizard getConfigurationWizard() {
        return new MVPnPIOActuatorConfigurationWizard(this);
    }

    public enum Type {
        BOOLEAN(new BooleanConverter()),
        INTEGER(new IntegerConverter()),
        FLOAT(new FloatConverter());

        private final Converter converter;

        Type(Converter converter) {
            this.converter = converter;
        }

        public <T> Converter<T> getConverter() {
            return converter;
        }
    }

    public interface Converter<S> {
        int convertForward(S var1);

        S convertReverse(int var1);
    }

    public static class BooleanConverter implements Converter<Boolean> {
        @Override
        public int convertForward(Boolean var1) {
            return Boolean.TRUE.equals(var1) ? 1 : 0;
        }

        @Override
        public Boolean convertReverse(int var1) {
            return var1 != 0 ? Boolean.TRUE : Boolean.FALSE;
        }
    }

    public static class IntegerConverter implements Converter<Integer> {
        @Override
        public int convertForward(Integer var1) {
            return var1;
        }

        @Override
        public Integer convertReverse(int var1) {
            return var1;
        }
    }

    public static class FloatConverter implements Converter<Float> {
        @Override
        public int convertForward(Float var1) {
            return Float.floatToIntBits(var1);
        }

        @Override
        public Float convertReverse(int var1) {
            return Float.intBitsToFloat(var1);
        }
    }
}
