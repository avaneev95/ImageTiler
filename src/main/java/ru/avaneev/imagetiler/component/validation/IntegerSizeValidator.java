package ru.avaneev.imagetiler.component.validation;

import com.jfoenix.validation.base.ValidatorBase;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.TextInputControl;

/**
 * @author Andrey Vaneev
 * Creation date: 07.09.2018
 */
public class IntegerSizeValidator extends ValidatorBase {

    public IntegerSizeValidator() {
        this.max.set(Integer.MAX_VALUE);
        this.min.set(0);
    }

    @Override
    protected void eval() {
        if (srcControl.get() instanceof TextInputControl) {
            TextInputControl textField = (TextInputControl) srcControl.get();
            String text = textField.getText();
            try {
                hasErrors.set(false);
                if (!text.isEmpty()) {
                    int value = Integer.parseInt(text);
                    if (value > getMax() || value < getMin()) {
                        hasErrors.set(true);
                    }
                }
            } catch (Exception e) {
                hasErrors.set(true);
            }
        }
    }

    protected SimpleIntegerProperty max = new SimpleIntegerProperty();

    protected SimpleIntegerProperty min = new SimpleIntegerProperty();

    public void setMax(int max) {
        this.max.set(max);
    }

    public int getMax() {
        return this.max.get();
    }

    public void setMin(int min) {
        this.min.set(min);
    }

    public int getMin() {
        return this.min.get();
    }
}
