package scada;

import GreenhouseAPI.Greenhouse;
import PLCCommunication.PLCConnection;
import PLCCommunication.UDPConnection;
import RMIComms.*;
import dto.OrderINFO;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author Jakob
 */
public class Scada_Controller {

    private OrderINFO currentOrder = null;
    private int maxCapacity = 0;
    private int currentCapacity = 0;
    private String error = "";

    private FXMLDocumentController guiCon;
    private Client rmiClient;

    private ArrayList<IDeployable> deployArray = new ArrayList<IDeployable>();
    private ArrayList<OrderINFO> orderArray = new ArrayList<OrderINFO>();

    private ArrayList<String> discardArray = new ArrayList<String>();
    private ArrayList<String> harvestArray = new ArrayList<String>();
    private ArrayList<String> plantArray = new ArrayList<String>();

    /**
     * SCADA_Controller constructor.
     */
    public Scada_Controller() {

    }

    /**
     * Increases the current capacity by one.
     */
    public void increaseCurrentCapacity() {
        currentCapacity++;
        if (rmiClient != null) {
            rmiClient.decreaseCapactiy();
        }
    }

    /**
     * Decreases the current capacity by one.
     */
    public void decreaseCurrentCapacity() {
        currentCapacity--;
        if (rmiClient != null) {
            rmiClient.decreaseCapactiy();
        }
    }

    /**
     * Set a String to display as an error in the GUI.
     *
     * @param e
     */
    public void setError(String e) {
        this.error = e;
    }

    /**
     * Returns any error-Strings added.
     *
     * @return
     */
    public String getError() {
        return error;
    }

    /**
     * Fetches the currently active order object. If no order is currently
     * active, null is returned.
     *
     * @return OrderINFO object.
     */
    public OrderINFO getCurrentOrder() {
        return currentOrder;
    }

    /**
     * Returns the array of orders currently available.
     *
     * @return
     */
    public ArrayList<OrderINFO> getOrders() {
        return this.orderArray;
    }

    /**
     * Sets a specific order as the currently active order.
     *
     * @param currentOrder - The order to set as active.
     */
    public void setCurrentOrder(OrderINFO currentOrder) {
        this.currentOrder = currentOrder;
    }

    /**
     * Adds a new order the orderArray for future processing.
     *
     * @param order - The order object to add.
     */
    public void addOrder(OrderINFO order) {
        orderArray.add(order);
    }

    /**
     * Adds a greenhouse to the SCADA-controller.
     *
     * @param gh - The greenhouse to add.
     */
    public void addGreenHouse(Greenhouse gh) {
        deployArray.add(gh);
    }

    /**
     * Iterates through all connected GH's, requesting relevant data and
     * updating the Greenhouse-array.
     */
    public void updateAll() {
        // Calls to the GUI to update displayed information.
        guiCon.updateTable(deployArray);
        guiCon.updateDiscardDelay(discardArray);
        guiCon.updateHarvestDelay(harvestArray);
        guiCon.updatePlantDelay(plantArray);

        Date date = new Date();
        for (IDeployable deploy : deployArray) {
            new Thread(() -> {
                deploy.update(date);
            }).start();
        }

    }

    /**
     * Attempts to establish a connection to a MES-server.
     *
     * @param ip - IP of the MES-server.
     * @throws RemoteException
     */
    public void createConnector(String ip) throws RemoteException {
        rmiClient = new Client(ip, this.currentCapacity);
        (new Thread(rmiClient)).start();
        rmiClient.setScadCon(this);
    }

    /**
     * Registers a harvest from a deployable.
     *
     * @param id - ID of the deployable that is harvested from.
     * @throws java.rmi.RemoteException
     */
    public void harvest(int id) throws RemoteException {
        IDeployable deploy = this.deployArray.get(id);
        if (deploy.getStatus()) {
            String orderNo = deploy.emptyArticle();
            try {
                new Thread(() -> {
                    try {
                        rmiClient.notifyHarvest(orderNo);
                    } catch (RemoteException ex) {
                        System.out.println("No MES-Server connected - Harvest added to queue.");
                        harvestArray.add(orderNo);
                    }
                }).start();
                this.increaseCurrentCapacity();

            } catch (NullPointerException e) {
                System.out.println("No MES-Server connected - Harvest added to queue.");
                harvestArray.add(orderNo);
                guiCon.updateHarvestDelay(harvestArray);
            }
        } else {

            System.out.println("Tried harvesting an idle deployable.");

        }
    }

    /**
     * Registers a discard from a deployable.
     *
     * @param id - ID of the deployable that is discarded from.
     * @throws java.rmi.RemoteException
     */
    public void discard(int id) throws RemoteException {
        IDeployable deploy = this.deployArray.get(id);
        if (deploy.getStatus()) {
            String orderNo = deploy.emptyArticle();
            try {
                new Thread(() -> {
                    try {
                        rmiClient.notifyDiscard(orderNo);
                    } catch (RemoteException ex) {
                        System.out.println("No MES-Server connected - Discard added to queue.");
                        discardArray.add(orderNo);
                    }
                }).start();
                this.increaseCurrentCapacity();
            } catch (NullPointerException e) {
                System.out.println("No MES-Server connected - Discard added to queue.");
                discardArray.add(orderNo);
                guiCon.updateDiscardDelay(discardArray);
            }
        } else {
            System.out.println("Tried discarding an idle deployable.");
        }
    }

    /**
     * Registers the planting of an article in a specific greenhouse.
     *
     * @param id - ID of the deployable to plant in.
     * @param art - The article to add to the deployable.
     * @throws java.rmi.RemoteException
     */
    public void plant(int id, Article art) throws RemoteException {
        IDeployable deploy = this.deployArray.get(id);

        if (this.currentOrder == null) {
            System.out.println("No order selected as active.");
        } else {
            if (!deploy.getStatus()) {
                String orderNo = this.currentOrder.getOrderID();
                try {
                    this.decreaseCurrentCapacity();
                    deploy.deployArticle(art, this.currentOrder.getOrderID());
                    this.currentOrder.setQuantity(this.currentOrder.getQuantity() - 1);
                    // If the correct amount of articles have been deployed, remove the order from the overview. 
                    if (this.currentOrder.getQuantity() == 0) {
                        orderArray.remove(this.currentOrder);
                        this.currentOrder = null;
                    }
                    new Thread(() -> {
                        try {
                            rmiClient.notifyPlant(orderNo);
                        } catch (RemoteException ex) {
                            System.out.println("No MES-Server connected - Plant added to queue.");
                            plantArray.add(orderNo);
                        }
                    }).start();
                } catch (NullPointerException e) {
                    System.out.println("No MES-Server connected - Plant added to queue.");
                    plantArray.add(orderNo);
                }
            } else {
                System.out.println("Tried planting a non-idle deployable.");
            }
        }
        guiCon.updateOrderView(orderArray);
    }

    /**
     * Takes all delayed notifications and tries to re-send them to the
     * MES-server.
     *
     * @throws RemoteException
     */
    public void notifyDelayed() throws RemoteException {
        for (String e : plantArray) {
            rmiClient.notifyPlant(e);
            plantArray.remove(e);
        }
        for (String e : harvestArray) {
            rmiClient.notifyHarvest(e);
            harvestArray.remove(e);
        }
        for (String e : discardArray) {
            rmiClient.notifyDiscard(e);
            discardArray.remove(e);
        }
        guiCon.updateDiscardDelay(plantArray);
        guiCon.updateHarvestDelay(plantArray);
        guiCon.updatePlantDelay(plantArray);
    }

    /**
     * Adds a deployable to the SCADA-controller with a socket port, and an
     * IP-address. MODIFY CONSTRUCTOR IN THIS METHOD TO CHANGE TYPE OF
     * DEPLOYABLE
     *
     * @param port - The port of the newly added deployable.
     * @param ip - The IP-address of the newly added deployable.
     */
    public void addPortIp(int port, String ip) {
        PLCConnection con = new UDPConnection(port, ip);

        IDeployable deploy = new Greenhouse(con);
        deployArray.add(deploy);
        deploy.setDeployNumber(deployArray.size());

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

    /**
     * Returns the list of deployables currently connected to the SCADA.
     *
     * @return List of IDeployable-objects.
     */
    public ArrayList<IDeployable> getDeployList() {
        return this.deployArray;
    }
}
