package de.jacavi.appl.controller.agent.impl;

import java.util.List;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.RequestProcessorFactoryFactory;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.webserver.WebServer;

import de.jacavi.appl.controller.ControllerSignal;
import de.jacavi.appl.controller.agent.ExternalController;
import de.jacavi.appl.racelogic.Player;
import de.jacavi.appl.track.Track;
import de.jacavi.rcp.util.ExceptionHandler;



/**
 * Implements an XML-RPC based controller as a proof-of-concept for externally controlling a car.
 * <p>
 * Each controller is listening on exactly one port. The callable methods are always prefixed with "jacavi.", followed
 * by the names of the public methods in
 * {@link de.jacavi.appl.controller.agent.impl.XmlRpcController.XmlRpcRequestHandler}.
 * <h3>Example code:</h3><br>
 * After starting a race with a player that uses the XmlRpcController (assuming port 8080 here), the following example
 * code can be used in the Python console to control the car:
 * <p>
 * <code>
 * import xmlrpclib<br>
 * serverProxy = xmlrpclib.ServerProxy("http://localhost:8080/")<br>
 * serverProxy.jacavi.setSignal(50, True)
 * </code>
 */
public class XmlRpcController extends ExternalController {

    /**
     * Implements the actual XML-RPC methods (all public methods, determined via reflection).
     */
    public static class XmlRpcRequestHandler {
        private ControllerSignal controllerSignal;

        /**
         * Used by the RequestProcessorFactoryFactory to inject the reference of the controller signal to modify.
         */
        void setControllerSignal(ControllerSignal controllerSignal) {
            this.controllerSignal = controllerSignal;
        }

        public int setThrust(int thrust) throws Exception {
            if(thrust >= 0 && thrust <= 100) {
                this.controllerSignal.setThrust(thrust);
            } else {
                throw new Exception("Thrust must be between 0 and 100.");
            }
            return 0;
        }

        public int setTrigger(boolean trigger) {
            this.controllerSignal.setTrigger(trigger);
            return 0;
        }

        public int setSignal(int thrust, boolean trigger) throws Exception {
            setThrust(thrust);
            setTrigger(trigger);
            return 0;
        }
    }

    private final int port;

    private WebServer webServer;

    ControllerSignal controllerSignal;

    public XmlRpcController(String name, int port) {
        super(name);
        this.port = port;
    }

    @Override
    public void activate(Track track, List<Player> players) {
        assert this.webServer == null: "activate() was invoked, but there's still a reference to an existing webServer!";

        this.controllerSignal = new ControllerSignal();

        try {
            // create the internal web server that will be listening for incoming XML-RPC connections
            this.webServer = new WebServer(this.port);
            XmlRpcServer xmlRpcServer = this.webServer.getXmlRpcServer();

            // create the handler mapping that will tell the server which objects can be accessed via XML-RPC
            PropertyHandlerMapping mapping = new PropertyHandlerMapping();

            // our handler object (XmlRpcRequestHandler) will be recreated for each incoming call, but it also has some
            // state
            // (it needs to know which ControllerSignal to modify), so we need a custom initializer to pass it a
            // reference to that Controller Signal
            mapping.setRequestProcessorFactoryFactory(new RequestProcessorFactoryFactory.RequestSpecificProcessorFactoryFactory() {
                @Override
                protected Object getRequestProcessor(@SuppressWarnings("rawtypes") Class clazz, XmlRpcRequest request)
                        throws XmlRpcException {
                    XmlRpcRequestHandler requestProcessor = (XmlRpcRequestHandler) super.getRequestProcessor(clazz,
                            request);
                    requestProcessor.setControllerSignal(XmlRpcController.this.controllerSignal);
                    return requestProcessor;
                }
            });

            // alternative implementation, should work as well
            /*mapping
                    .setRequestProcessorFactoryFactory(new RequestProcessorFactoryFactory.StatelessProcessorFactoryFactory() {
                        @SuppressWarnings("unchecked")
                        @Override
                        protected Object getRequestProcessor(Class clazz) throws XmlRpcException {
                            XmlRpcRequestHandler requestProcessor = (XmlRpcRequestHandler) super
                                    .getRequestProcessor(clazz);
                            requestProcessor.setControllerSignal(controllerSignal);
                            return requestProcessor;
                        }
                    });*/

            // now we're adding a mapping for "jacavi" to the internal class
            mapping.addHandler("jacavi", XmlRpcRequestHandler.class);
            xmlRpcServer.setHandlerMapping(mapping);

            // configure the XML-RPC server (contentLengthOptional means it doesn't have to set the content-length
            // parameter in the HTTP response, which speeds up things a little, and the extensions are required for
            // contentLengthOptional to work)
            XmlRpcServerConfigImpl serverConfig = (XmlRpcServerConfigImpl) xmlRpcServer.getConfig();
            serverConfig.setEnabledForExtensions(true);
            serverConfig.setContentLengthOptional(false);

            // everything's configured, now we can start the web server
            this.webServer.start();
        } catch(Exception e) {
            ExceptionHandler.handleException(this, e, true);
        }
    }

    @Override
    public void deactivate() {
        if(this.webServer != null) {
            this.webServer.shutdown();
        }
        this.webServer = null;
        this.controllerSignal = null;
    }

    @Override
    public void reset() {
        this.controllerSignal = new ControllerSignal();
    }

    @Override
    public ControllerSignal poll() {
        return this.controllerSignal;
    }

    @Override
    public ControllerSignal getLastSignal() {
        return this.controllerSignal;
    }

}
