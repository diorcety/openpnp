package org.openpnp.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Objects;

import javax.swing.*;

import org.jdesktop.beansbinding.Converter;
import org.jdesktop.swingx.combobox.ListComboBoxModel;
import org.openpnp.gui.support.BooleanConverter;
import org.openpnp.gui.support.DoubleConverter;
import org.openpnp.gui.support.IntegerConverter;
import org.openpnp.gui.support.LongConverter;
import org.openpnp.spi.Actuator;
import org.openpnp.util.UiUtils;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

public class ActuatorControlDialog extends JDialog {
    private JTextField valueTf;
    private JTextField readTf;
    private LabeledStringConverter<?> converters[] = new LabeledStringConverter[] {
            new LabeledStringConverter<Object>("None", new Converter() {
                @Override
                public Object convertForward(Object o) {
                    return null;
                }

                @Override
                public Object convertReverse(Object o) {
                    return null;
                }
            }),
            new LabeledStringConverter<Boolean>("Boolean", new BooleanConverter()),
            new LabeledStringConverter<Integer>("Integer", new IntegerConverter()),
            new LabeledStringConverter<Long>("Long", new LongConverter()),
            new LabeledStringConverter<Double>("Double", new DoubleConverter()),
            new LabeledStringConverter<String>("String", new Converter() {
                @Override
                public Object convertForward(Object o) {
                    return o;
                }

                @Override
                public Object convertReverse(Object o) {
                    return o;
                }
            }),
    };

    private static class LabeledStringConverter<T> {
        private final String label;
        private final Converter<T, String> converter;

        public LabeledStringConverter(String label, Converter<T, String> converter) {
            this.label = label;
            this.converter = converter;
        }

        public String getLabel() {
            return label;
        }

        public Converter<T, String> getConverter() {
            return converter;
        }
    }

    public ActuatorControlDialog(Actuator actuator) {
        super(MainFrame.get(), actuator.getHead() == null ? actuator.getName()
                : actuator.getHead().getName() + ":" + actuator.getName(), true);
        getContentPane().setLayout(new FormLayout(new ColumnSpec[] {
                FormSpecs.RELATED_GAP_COLSPEC,
                FormSpecs.DEFAULT_COLSPEC,
                FormSpecs.RELATED_GAP_COLSPEC,
                ColumnSpec.decode("default:grow"),
                FormSpecs.RELATED_GAP_COLSPEC,
                FormSpecs.DEFAULT_COLSPEC,
                FormSpecs.RELATED_GAP_COLSPEC,
                FormSpecs.DEFAULT_COLSPEC,},
            new RowSpec[] {
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC,
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC,
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC,
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC,
                FormSpecs.RELATED_GAP_ROWSPEC,}));
        
        setModalityType(JDialog.ModalityType.MODELESS);

        JPanel panel = new JPanel();
        FlowLayout flowLayout = (FlowLayout) panel.getLayout();
        flowLayout.setAlignment(FlowLayout.LEFT);
        getContentPane().add(panel, "4, 2, 3, 1");

        JLabel lblValue = new JLabel("Value type");
        getContentPane().add(lblValue, "2, 2");

        JComboBox<LabeledStringConverter<?>> comboBoxType = new JComboBox<LabeledStringConverter<?>>();
        comboBoxType.setModel(new ListComboBoxModel<LabeledStringConverter<?>>(Arrays.asList(converters)));
        comboBoxType.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                ((JLabel) component).setText(((LabeledStringConverter) value).getLabel());
                return component;
            }
        });
        getContentPane().add(comboBoxType, "4, 2");
        
        JLabel lblSet = new JLabel("Set Value");
        getContentPane().add(lblSet, "2, 4, right, default");
        
        valueTf = new JTextField();
        getContentPane().add(valueTf, "4, 4");
        valueTf.setColumns(10);
        
        JButton setBtn = new JButton("Set");
        setBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                UiUtils.submitUiMachineTask(() -> {
                    LabeledStringConverter<?> converter = (LabeledStringConverter<?>) comboBoxType.getSelectedItem();
                    Object value = converter.getConverter().convertReverse(valueTf.getText());
                    actuator.actuate(value);
                });
            }
        });
        getContentPane().add(setBtn, "6, 4");
        
        JLabel lblRead = new JLabel("Read Value");
        getContentPane().add(lblRead, "2, 6, right, default");
        
        readTf = new JTextField();
        getContentPane().add(readTf, "4, 6");
        readTf.setColumns(10);
        
        JButton readBtn = new JButton("Read");
        readBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                UiUtils.submitUiMachineTask(() -> {
                    LabeledStringConverter<?> converter = (LabeledStringConverter<?>) comboBoxType.getSelectedItem();
                    Object value = converter.getConverter().convertReverse(valueTf.getText());
                    Object s = actuator.read(value);
                    readTf.setText(Objects.toString(s, ""));
                });
            }
        });
        getContentPane().add(readBtn, "6, 6");
        
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        getContentPane().add(closeBtn, "6, 8");
    }
}
