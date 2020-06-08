package de.hhu.ba.yoshikoWrapper.core;

/**
 * Simple Exception that should be thrown when the CyNetwork combined with the given parameters can not be parsed in a meaningful WCE instance
 * @author Philipp Spohr, Dec 12, 2017
 *
 */
@SuppressWarnings("serial")
public class NetworkParsingException extends Exception {

    private String msg;

    public NetworkParsingException(String msg) {
        this.msg = msg;
    }

    @Override
    public String getMessage() {
        return LocalizationManager.get("networkParsingException")+" "+msg;
    }

}
