/* froth modified this in the way that there is no more GUI in here.
 * On problems and qustions or for the original code apply to daniel@d-urbanietz.de or monagel@gmx.net 
 */

package de.jacavi.hal.bluerider;

public interface ComListener {
    void msgReceived(Message m, int index);

    void fixDataReceived(byte b, int index);
}
