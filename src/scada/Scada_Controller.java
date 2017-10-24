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

/**
 *
 * @author Jakob
 */
public class Scada_Controller {

    private FXMLDocumentController guiCon;
    private ArrayList<IDeployable> deployArray = new ArrayList<IDeployable>();

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

    /**
     * WORK IN PROGRESS
     *
     * @param id - ID of edited deployable.
     */
    public void harvest(int id) {
        IDeployable deploy = this.deployArray.get(id);
        if (deploy.getStats()) {
            Article art = deploy.getCurrentArt();

            deploy.emptyArticle();

            // CONTACT MES, 1 x art has been harvested belonging to order ?
        } else {

            System.out.println("Tried harvesting an idle deployable.");

        }
    }

    /**
     * WORK IN PROGRESS
     *
     * @param id - ID of edited deployable.
     */
    public void discard(int id) {
        IDeployable deploy = this.deployArray.get(id);
        if (deploy.getStats()) {

            Article art = deploy.getCurrentArt();

            String orderNo = deploy.emptyArticle();

            // CONTACT MES 1 x art has been discarded, belonging to order ? 
        } else {
            System.out.println("Tried discarding an idle deployable.");
        }

    }

    /**
     * WORK IN PROGRESS
     *
     * @param id - ID of edited deployable.
     * @param art - The article to add to the deployable.
     * @param orderNo - The order to associate with the article in production.
     */
    public void plant(int id, Article art, String orderNo) {
        IDeployable deploy = this.deployArray.get(id);
        if (!deploy.getStats()) {
            deploy.deployArticle(art, orderNo);

        } else {
            System.out.println("Tried planting a non-idle deployable.");
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
