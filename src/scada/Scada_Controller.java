/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scada;

import GreenhouseAPI.Greenhouse;
import PLCCommunication.PLCConnection;
import PLCCommunication.UDPConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Jakob
 */
public class Scada_Controller {

    private FXMLDocumentController guiCon;
    private ArrayList<IDeployable> deployArray = new ArrayList<IDeployable>();
    private ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();

    /**
     * SCADA_Controller constructor.
     */
    public Scada_Controller() {

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
    
    public void harvest(int id){
    }
    
    public void discard(int id){
        
    }
    
    public void plant(int id, Article art){
        
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
     * @param ghNumber - The id of the greenhouse to change set-point for.
     * @param newPoint - The value of the new set-point.
     */
    public void changeSetPoint(String dNumber, int newPoint) {
        try {
            int deployNumber = Integer.parseInt(dNumber);
            deployNumber--;
            if (deployNumber > deployArray.size() && deployNumber >= 0) {
                System.out.println("Attempted to modify a too high deployable.");
            } else {
                deployArray.get(deployNumber).SetTemperature(newPoint);
            }

        } catch (NumberFormatException e) {
            System.out.println("Deploy-number is not an Integer.??");
        }

    }

}
