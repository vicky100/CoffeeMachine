package main.java.exceptions;

public class MachineConfigurationException extends Exception {

    public MachineConfigurationException(final String msg) {
        super(msg);
    }

    public MachineConfigurationException(final String msg, final Throwable err) {
        super(msg, err);
    }
}
