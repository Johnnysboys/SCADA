package scada;

import RMIComms.*;
import java.rmi.RemoteException;

/**
 *
 * @author Jakob
 */
public class Scada_Controller implements IController {

    private FXMLDocumentController guiCon;
    private Client rmiClient;
    private final DeployHandler deployHandler = new DeployHandler();
    private final OrderHandler orderHandler = new OrderHandler();

    /**
     * SCADA_Controller constructor.
     */
    public Scada_Controller() {

    }

    @Override
    public DeployHandler getDeployHandler() {
        return deployHandler;
    }

    @Override
    public OrderHandler getOrderHandler() {
        return orderHandler;
    }

    @Override
    public Client getRmiClient() {
        return rmiClient;
    }

    /**
     * Attempts to establish a connection to a MES-server.
     *
     * @param ip - IP of the MES-server.
     * @throws RemoteException
     */
    @Override
    public void createRMIClient(String ip) throws RemoteException {
        rmiClient = new Client(ip, deployHandler.getCapacity());
        (new Thread(rmiClient)).start();
        rmiClient.setScadCon(this);
    }

    /**
     * Adds a deployable to the SCADA-controller with a socket port, and an
     * IP-address.
     *
     * @param port - The port of the newly added deployable.
     * @param ip - The IP-address of the newly added deployable.
     * @return - An integer containing the number of deployables currently
     * added.
     */
    @Override
    public int addDeployable(int port, String ip) {
        int size = deployHandler.addDeployable(port, ip);
        if (rmiClient != null) {
            rmiClient.increaseCapacity();
        }
        return size;
    }

    /**
     * Returns the FXMLDocumentController attached to this Scada_Controller
     *
     * @return - FXMLDocumentController object attached to this Scada_Controller
     */
    public FXMLDocumentController getGUICon() {
        return this.guiCon;
    }

    /**
     * Attaches the GUI-Controller to this object, so calls can be made to it.
     *
     * @param con - The controller to attach to the SCADA-object.
     */
    public void setGUICon(FXMLDocumentController con) {
        this.guiCon = con;
    }
}
