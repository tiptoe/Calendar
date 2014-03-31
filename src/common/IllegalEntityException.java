/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

/**
 *
 * @author Jan Smerda
 */
public class IllegalEntityException extends RuntimeException {
    
    public IllegalEntityException(String msg) {
        super(msg);
    }

    public IllegalEntityException(Throwable cause) {
        super(cause);
    }

    public IllegalEntityException(String message, Throwable cause) {
        super(message, cause);
    }
}
