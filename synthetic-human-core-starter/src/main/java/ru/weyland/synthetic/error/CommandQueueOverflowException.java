package ru.weyland.synthetic.error;

public class CommandQueueOverflowException extends RuntimeException {

    public CommandQueueOverflowException(String message, Throwable cause) {
        super(message, cause);
    }
}
