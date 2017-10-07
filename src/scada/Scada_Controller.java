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
    private ArrayList<Greenhouse> greenHouseArray = new ArrayList<Greenhouse>();

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
        greenHouseArray.add(gh);
    }

    /**
     * Iterates through all connected GH's, requesting relevant data and
     * updating the Greenhouse-array.
     */
    public void updateAll() {
        for (Greenhouse gh : greenHouseArray) {
            double temp = gh.ReadTemp1();
            double wLevel = gh.ReadWaterLevel();

            // If the temperature is higher than the SetPoint, the fan is started as to bring down the temperature. If the temperature is lower than the SetPoint the heater automatically starts. 
            if (temp > Double.parseDouble(gh.getGhSetTemp()) + 273) {
                gh.SetFanSpeed(1);
            } else {
                gh.SetFanSpeed(0);
            }

            // Set to needed waterlevel
            if (wLevel < 400) {
                gh.AddWater(5);
            }
        }
        guiCon.updateTable(greenHouseArray);
    }

    /**
     * Adds a greenhouse to the SCADA-controller with a socket port, and an
     * IP-address.
     *
     * @param port - The port of the newly added greenhouse.
     * @param ip - The IP-address of the newly added greenhouse.
     */
    public void addGreenPortIp(int port, String ip) {
        PLCConnection con = new UDPConnection(port, ip);

        Greenhouse gh = new Greenhouse(con);
        greenHouseArray.add(gh);
        gh.setGhNumber(Integer.toString(greenHouseArray.size()));
    }

    /**
     * Adds a greenhouse to the SCADA-controller with a socket port.
     *
     * @param port - The port of the newly added greenhouse.
     */
    public void addGreenPort(int port) {
        System.out.println(port);

        PLCConnection con = new UDPConnection(port, "192.168.0.10");

        Greenhouse gh = new Greenhouse(con);
        greenHouseArray.add(gh);
        gh.setGhNumber(Integer.toString(greenHouseArray.size()));
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
     * Returns the list of greenhouses currently connected to the SCADA.
     *
     * @return List of Greenhouse-objects.
     */
    public ArrayList<Greenhouse> getGreenList() {
        return this.greenHouseArray;
    }

    /**
     * Changes the temperature set-point for a greenhouse.
     *
     * @param ghNumber - The id of the greenhouse to change set-point for.
     * @param newPoint - The value of the new set-point.
     */
    public void changeSetPoint(String ghNumber, int newPoint) {
        for (Greenhouse gh : greenHouseArray) {
            if (gh.getGhNumber().equals(ghNumber)) {
                gh.setGhSetTemp(Integer.toString(newPoint));
                gh.SetTemperature(newPoint + 273);
            } else {
            }
        }
    }
}
