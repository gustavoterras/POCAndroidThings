package com.infoterras.androidthings.model;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;

/**
 * Created by gustavo.filho on 17/09/17.
 */

public class Led {

    private Gpio gpio;
    private String pin;
    private boolean status;

    public Led(String pin) {
        this.setPin(pin);

        PeripheralManagerService service = new PeripheralManagerService();
        try {
            gpio = service.openGpio(pin);
            gpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        try {
            gpio.setValue(status);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.status = status;
    }

    public void onDestroy() {
        try {
            gpio.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            gpio = null;
        }
    }
}
