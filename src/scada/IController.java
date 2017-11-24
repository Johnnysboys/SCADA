package scada;

import RMIComms.Client;
import java.rmi.RemoteException;

/**
 *
 * @author Jakob
 */
public interface IController {

    /**
     * Fetches the DeployHandler of the Controller
     *
     * @return DeployHandler
     */
    public DeployHandler getDeployHandler();

    /**
     * Fetches the OrderHandler of the Controller.
     *
     * @return OrderHandler
     */
    public OrderHandler getOrderHandler();

    /**
     * Fetches the Client of the Controller.
     *
     * @return Client
     */
    public Client getRmiClient();

    /**
     * Adds a deployable to the SCADA
     *
     * @param port - The port of the added deployable.
     * @param ip - The port of the added deployable.
     * @return - Integer containing the amount of deployables currently added.
     */
    public int addDeployable(int port, String ip);

    /**
     * Creates a connection to the MES-server for the Controller.
     *
     * @param ip - The IP of the MES-server to connect to.
     * @throws RemoteException
     */
    public void createRMIClient(String ip) throws RemoteException;

}
