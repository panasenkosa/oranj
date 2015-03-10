package org.oranj.exceptions;

/**
 * Created by panasenko on 10.03.2015.
 */
public class ParseXMLException extends InitConfigurationException {
    public ParseXMLException(String message, Throwable e) {
        super(message, e);
    }

    public ParseXMLException(String message) {
        super(message);
    }

    public ParseXMLException(Throwable e) {
        super(e);
    }
}
