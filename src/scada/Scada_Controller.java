/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scada;

import GreenhouseAPI.Greenhouse;
import PLCCommunication.PLCConnection;
import PLCCommunication.UDPConnection;
import RMIComms.*;
import dto.OrderINFO;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 *
 * @author Jakob
 */
public class Scada_Controller {

    private OrderINFO currentOrder = null;
    private int maxCapacity = 0;

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public int getCurrentCapacity() {
        return currentCapacity;
    }

    public void setCurrentCapacity(int currentCapacity) {
        this.currentCapacity = currentCapacity;
    }
    private int currentCapacity = 0;

    private FXMLDocumentController guiCon;
    private Client rmiClient;
    
    private ArrayList<IDeployable> deployArray = new ArrayList<IDeployable>();
    private ArrayList<OrderINFO> orderArray = new ArrayList<OrderINFO>();

    private ArrayList<String> discardArray = new ArrayList<String>();
    private ArrayList<String> harvestArray = new ArrayList<String>();

    /**
     * SCADA_Controller constructor.
     */
    public Scada_Controller() {

    }

    public OrderINFO getCurrentOrder() {
        return currentOrder;
    }

    public void setCurrentOrder(OrderINFO currentOrder) {
        this.currentOrder = currentOrder;
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
        for (IDeployable deploy : deployArray) {
            deploy.update();
        }
        guiCon.updateTable(deployArray);
    }

    public void createConnector(String ip) throws RemoteException {
        rmiClient = new Client(ip);

        (new Thread(rmiClient)).start();

        rmiClient.setScadCon(this);
    }

    /**
     * Adds a new order the orderArray for future processing.
     *
     * @param order - The order object to add.
     */
    public void addOrder(OrderINFO order) {
        orderArray.add(order);
        guiCon.updateOrderView(orderArray);

    }

    /**
     * WORK IN PROGRESS
     *
     * @param id - ID of edited deployable.
     * @throws java.rmi.RemoteException
     */
    public void harvest(int id) throws RemoteException {
        IDeployable deploy = this.deployArray.get(id);
        if (deploy.getStats()) {
            String orderNo = deploy.emptyArticle();

            try {
                rmiClient.notifyHarvest(orderNo);

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
     * WORK IN PROGRESS
     *
     * @param id - ID of edited deployable.
     * @throws java.rmi.RemoteException
     */
    public void discard(int id) throws RemoteException {
        IDeployable deploy = this.deployArray.get(id);
        if (deploy.getStats()) {
            String orderNo = deploy.emptyArticle();
            try {
                rmiClient.notifyDiscard(orderNo);
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
     * @param id - ID of edited deployable.
     * @param art - The article to add to the deployable.
     * @throws java.rmi.RemoteException
     */
    public void plant(int id, Article art) throws RemoteException {
        IDeployable deploy = this.deployArray.get(id);

        if (this.currentOrder == null) {
            System.out.println("No order selected as active.");
        } else {
            if (!deploy.getStats()) {
                try {
                    rmiClient.notifyPlant(this.currentOrder.getOrderID());
                    deploy.deployArticle(art, this.currentOrder.getOrderID());
                    this.currentOrder.setQuantity(this.currentOrder.getQuantity() - 1);
                    // If the correct amount of articles have been deployed remove the order from the overview. 
                    if (this.currentOrder.getQuantity() == 0) {
                        orderArray.remove(this.currentOrder);
                        this.currentOrder = null;
                    }
                    guiCon.updateOrderView(orderArray);
                } catch (NullPointerException e) {
                    System.out.println("No MES-Server connected - Plant aborted.");
                }

            } else {
                System.out.println("Tried planting a non-idle deployable.");
            }
        }
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

    /**
     * Changes the temperature set-point for a greenhouse.
     *
     * @param dNumber - The id of the deployable to change set-point for.
     * @param newPoint - The value of the new set-point.
     */
//    public void changeSetPoint(String dNumber, int newPoint) {
//        try {
//            int deployNumber = Integer.parseInt(dNumber);
//            deployNumber--;
//            if (deployNumber > deployArray.size() && deployNumber >= 0) {
//                System.out.println("Attempted to modify a too high deployable.");
//            } else {
//                deployArray.get(deployNumber).SetTemperature(newPoint);
//            }
//
//        } catch (NumberFormatException e) {
//            System.out.println("Deploy-number is not an Integer.");
//        }
//
//    }

}
