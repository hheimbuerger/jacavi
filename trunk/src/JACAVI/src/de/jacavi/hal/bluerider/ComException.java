/* froth modified this in the way that there is no more GUI in here.
 * On problems and qustions or for the original code apply to daniel@d-urbanietz.de or monagel@gmx.net 
 */

package de.jacavi.hal.bluerider;

public class ComException extends Exception {
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public static int FAILSAFE = 0;

    private int reason;

    public ComException(int reason) {
        this.reason = reason;

    }

    public int getReason() {
        return reason;
    }
}
