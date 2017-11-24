package RMIComms;

import dto.OrderINFO;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.atomic.AtomicInteger;
import mes.IMESServer;
import mes.RMI_Constants;
import scada.ISCADAObserver;
import scada.Scada_Controller;

/**
 *
 * @author Jakob
 */
public class Client extends UnicastRemoteObject implements ISCADAObserver, Runnable {

    private String hostname;
    private IMESServer server;
    private Registry registry;
    private Scada_Controller scadCon;
    private AtomicInteger currentCapacity = new AtomicInteger();

    /**
     * Client constructor
     *
     * @param ip - The IP of the MES-server.
     * @param capa - The current capacity of this SCADA-unit.
     * @throws RemoteException
     */
    public Client(String ip, AtomicInteger capa) throws RemoteException {
        super();
        hostname = ip;
        currentCapacity = capa;
    }

    /**
     * Increases the observable capacity by one.
     */
    public void increaseCapacity() {
        currentCapacity.getAndIncrement();
    }

    /**
     * Decreases the observable capacity by one.
     */
    public void decreaseCapactiy() {
        currentCapacity.getAndDecrement();
    }

    /**
     * Notifies the MES-server of a harvest.
     *
     * @param orderNo - String containing the order number connected to the
     * harvest.
     * @throws RemoteException
     */
    public void notifyHarvest(String orderNo) throws RemoteException {
        server.alertHarvest(orderNo);
    }

    /**
     * Notifies the MES-server of a discard.
     *
     * @param orderNo - String containing the order number connected to the
     * discard.
     * @throws RemoteException
     */
    public void notifyDiscard(String orderNo) throws RemoteException {
        server.alertDiscarded(orderNo);
    }

    /**
     * Notifies the MES-server of a plant.
     *
     * @param orderNo - String containing the order number connected to the
     * plant.
     * @throws RemoteException
     */
    public void notifyPlant(String orderNo) throws RemoteException {
        server.alertPlanted(orderNo);
    }

    /**
     * Saves a reference to the Scada_Controller
     *
     * @param scad - The Scada_Controller to save.
     */
    public void setScadCon(Scada_Controller scad) {
        this.scadCon = scad;
    }

    /**
     * Establishes a connection to the MES-server.
     */
    @Override
    public void run() {
        try {
            registry = LocateRegistry.getRegistry(hostname, RMI_Constants.MES_PORT);
            // Get proxy to remote object from the registry server
            server = (IMESServer) registry.lookup(RMI_Constants.MES_OBJECTNAME);
            server.addObserver(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getCapacity() throws RemoteException {
        return currentCapacity.get();
    }

    @Override
    public void postOrder(OrderINFO oinfo) throws RemoteException {
        scadCon.getOrderHandler().addOrder(oinfo);
    }
}
