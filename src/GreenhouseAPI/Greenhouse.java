/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GreenhouseAPI;

import PLCCommunication.*;
import java.util.ArrayList;
import java.util.BitSet;
import scada.Article;
import scada.IDeployable;

/**
 * API to communicate to the PLC
 *
 * @author Steffen Skov
 */
public class Greenhouse implements IGreenhouse, ICommands, IDeployable {

    private PLCConnection conn;
    private Message mess;

    private String ghNumber;
    private String ghTemp;
    private String ghWater = "700";
    private String ghSetTemp = "0";
    private String idle = "Idle";

    private boolean inUse = false;

    private Article currentArt;
    private String orderNo;

    /**
     * Create greenhouse API
     */
    public Greenhouse() {

    }

    public String getIdle() {
        return idle;
    }

    public void setIdle(String idle) {
        this.idle = idle;
    }

    @Override
    public void deployArticle(Article art, String orderNo) {
        this.currentArt = art;
        this.orderNo = orderNo;
        this.SetTemperature(this.currentArt.getPrefTemp()+273);
        this.ghWater = Integer.toString(this.currentArt.getPrefWat());
        
        this.flipInUse();

    }
    
    @Override
    public String emptyArticle(){
        String discardedOrder = this.orderNo;
        this.orderNo = null;
        this.currentArt = null;
        this.flipInUse();
        
        return discardedOrder;
    }

    @Override
    public ArrayList<String> getColumnNames() {
        ArrayList<String> columnNameArray = new ArrayList<String>();
        columnNameArray.add("Greenhouse Number");
        columnNameArray.add("Temperature");
        columnNameArray.add("Temperature Setpoint");
        columnNameArray.add("Water Level");
        columnNameArray.add("Status");

        return columnNameArray;
    }

    @Override
    public ArrayList<String> getColumnAttributes() {
        ArrayList<String> columnAttributeArray = new ArrayList<String>();
        columnAttributeArray.add("ghNumber");
        columnAttributeArray.add("ghTemp");
        columnAttributeArray.add("ghSetTemp");
        columnAttributeArray.add("ghWater");
        columnAttributeArray.add("idle");

        return columnAttributeArray;
    }

    @Override
    public ArrayList<Article> getArticles() {
        Article Salad = new Article(1, "Salad", 23, 500);
        Article Cress = new Article(2, "Cress", 20, 500);
        Article Potato = new Article(3, "Potato", 17, 400);
        ArrayList<Article> articleArray = new ArrayList<Article>();
        articleArray.add(Salad);
        articleArray.add(Cress);
        articleArray.add(Potato);
        return articleArray;
    }

    @Override
    public void setDeployNumber(int i) {
        this.ghNumber = Integer.toString(i);
    }

    public PLCConnection getConn() {
        return conn;
    }

    public void setConn(PLCConnection conn) {
        this.conn = conn;
    }

    public Message getMess() {
        return mess;
    }

    public void setMess(Message mess) {
        this.mess = mess;
    }

    @Override
    public Article getCurrentArt() {
        return currentArt;
    }

    public void setCurrentArt(Article currentArt) {
        this.currentArt = currentArt;
    }

    public String getGhNumber() {
        return ghNumber;
    }

    public void setGhNumber(String ghNumber) {
        this.ghNumber = ghNumber;
    }

    public String getGhTemp() {
        return ghTemp;
    }

    public void setGhTemp(String ghTemp) {
        this.ghTemp = ghTemp;
    }

    public String getGhWater() {
        return ghWater;
    }

    public void setGhWater(String ghWater) {
        this.ghWater = ghWater;
    }

    public String getGhSetTemp() {
        return ghSetTemp;
    }

    public void setGhSetTemp(String ghSetTemp) {
        this.ghSetTemp = ghSetTemp;
    }

    @Override
    public void update() {
        double temp = this.ReadTemp1();
        double wLevel = this.ReadWaterLevel();

        // If the temperature is higher than the SetPoint, the fan is started as to bring down the temperature. If the temperature is lower than the SetPoint the heater automatically starts. 
        if (temp > Double.parseDouble(this.getGhSetTemp()) + 273) {
            this.SetFanSpeed(1);
        } else {
            this.SetFanSpeed(0);
        }
        if (wLevel < Double.parseDouble(this.ghWater)) {      // Set to needed waterlevel - Around 650 is base.
            this.AddWater(5); //Amount of seconds to pump water.
        }
    }
    
        /**
     * Flips the boolean that keeps track of whether or not the greenhouse is
     * occupied by a production.
     */
    public void flipInUse() {
        if (inUse) {
            inUse = false;
            idle = "Idle";
            System.out.println("BOOOOOOOOOOP BAD IDLESTATUS");
        } else {
            inUse = true;
            idle = currentArt.getName();
            System.out.println("WOOOOOOOP NEW IDLESTATUS");
        }
    }
    

    @Override
    public boolean getStats() {
        return inUse;
    }

    /**
     * Create greenhouse API
     *
     * @param c connection
     */
    public Greenhouse(PLCConnection c) {
        this.conn = c;
    }

    /**
     * Setpoint for temperature inside Greenhouse CMD: 1
     *
     * @param kelvin : temperature in kelvin (273 > T > 303)
     * @return true if processed
     */
    @Override
    public boolean SetTemperature(int kelvin) {
        mess = new Message(TEMP_SETPOINT);
        if (kelvin > 273 && kelvin < 303) // 0 - 30 grader celcius
        {
            System.out.println("Set temperatur setpoint to " + kelvin);
            mess.setData(kelvin - 273);
            conn.addMessage(mess);
            System.out.println("WOOOOOOOOOOOOOOOOOOOOOH");
            this.setGhSetTemp(Integer.toString(kelvin-273));
            if (conn.send()) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * Setpoint for moisture inside Greenhouse CMD:2
     *
     * @param moist in % ( 10 > M > 90 )
     * @return true if processed
     */
    public boolean SetMoisture(int moist) {
        mess = new Message(MOIST_SETPOINT);
        if (moist > 10 && moist < 90) {
            mess.setData(moist);
            conn.addMessage(mess);
            if (conn.send()) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * Setpoint for red light inside Greenhouse CMD:3
     *
     * @param level in percent
     * @return true if processed
     */
    public boolean SetRedLight(int level) {
        System.out.println("Set red light to " + level);
        mess = new Message(REDLIGHT_SETPOINT);
        if (level >= 0 && level <= 100) {
            mess.setData(level);
            conn.addMessage(mess);
            if (conn.send()) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * Setpoint for blue light inside Greenhouse CMD: 4
     *
     * @param level in percent
     * @return true if processed
     */
    public boolean SetBlueLight(int level) {
        mess = new Message(BLUELIGHT_SETPOINT);
        if (level >= 0 && level <= 100) {
            mess.setData(level);
            conn.addMessage(mess);
            if (conn.send()) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * Add water for some seconds. Pump is stopped if height of water is
     * exceeded CMD: 6
     *
     * @param sec : Second to turn on the pump {0 <= sec < 120}
     * @return true if processed
     */
    public boolean AddWater(int sec) {
        if (sec >= 0 && sec < 120) {
            mess = new Message(ADDWATER);
            mess.setData(sec);
            conn.addMessage(mess);
            if (conn.send()) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * NOT IMPLEMENTED Add Fertiliser for some seconds. Pump is stopped if
     * height of water is exceeded CMD: 7
     *
     * @param sec : Secord to turn on the pump
     * @return true if processed
     */
    public boolean AddFertiliser(int sec) {
        return true;
    }

    /**
     * NOT IMPLEMENTED Add CO2 for some seconds. Pump is stopped if height of
     * water is exceeded CMD: 8
     *
     * @param sec : Secord to turn on the valve
     * @return true if processed
     */
    public boolean AddCO2(int sec) {
        return true;
    }

    /**
     * Read tempature inside the Greenhouse CMD:9
     *
     * @return Temperature in kelvin
     */
    public double ReadTemp1() {
        System.out.println("Read greenhouse temperatur ");
        mess = new Message(READ_GREENHOUSE_TEMP);
        double temp = 0.0;
        mess.setData(); //None data
        conn.addMessage(mess);
        conn.addMessage(mess);
        if (conn.send()) {
            if (mess.getResultData() != null) {
                temp = (double) (mess.getResultData())[0];
            } else {
                temp = 19.99; // return a dummy value
            }
        }
        System.out.println("Temperature is: " + temp + "celcius");
        this.setGhTemp(Double.toString(temp));
        return temp + 273.0;
    }

    /**
     * Read tempature outside the Greenhouse CMD: 10
     *
     * @return Temperature in kelvin
     */
    public double ReadTemp2() {
        System.out.println("Read outdoor temperatur ");
        mess = new Message(READ_OUTDOOR_TEMP);
        double temp2 = 0.0;
        mess.setData(); //None data
        conn.addMessage(mess);
        if (conn.send()) {
            if (mess.getResultData() != null) {
                temp2 = (double) (mess.getResultData())[0];
            } else {
                temp2 = 19.99; // return a dummy value
            }
        }
        System.out.println("Temperature is: " + temp2);
        return temp2 + 273.0;

    }

    /**
     * Read relative moisture inside the Greenhouse CMD: 11
     *
     * @return Moisture in %
     */
    public double ReadMoist() {
        System.out.println("Read outdoor temperatur ");
        mess = new Message(READ_MOISTURE);
        double moist = 0.0;
        mess.setData(); //None data
        conn.addMessage(mess);
        if (conn.send()) {
            if (mess.getResultData() != null) {
                moist = (double) (mess.getResultData())[0];
            } else {
                moist = 1.0; // return a dummy value
            }                               // In real world moist will never be so low
        }
        System.out.println("Moisture is: " + moist + " %");
        return moist;
    }

    /**
     * Read level of water in the Greenhouse CMD: 17
     *
     * @return Level in millimeter [0 < level < 250]
     */
    public double ReadWaterLevel() {
        System.out.println("Read water level ");
        mess = new Message(READ_WATER_LEVEL);
        double level = 0.0; // level
        mess.setData(); //None data
        conn.addMessage(mess);
        if (conn.send()) {
            if (mess.getResultData() != null) {
                level = (mess.getResultData())[0] * 10.0;
            } else {
                level = 1000.0; // return a dummy value
            }
        }
        System.out.println("Water level is: " + level);
        this.setGhWater(Double.toString(level));
        return level;
    }

    /**
     * NOT IMPLEMENTED IN THE GREENHOUSE Read heights of the plants CMD: 12
     *
     * @return Heights (cm?)
     */
    public double ReadPlantHeight() {
        System.out.println("Read height of plants");
        mess = new Message(READ_PLANT_HEIGHT);
        double level = 0.0; // level
        mess.setData(); //None data
        conn.addMessage(mess);
        if (conn.send()) {
            if (mess.getResultData() != null) {
                level = (mess.getResultData())[0];
            } else {
                level = 1000.0; // return a dummy value
            }
        }
        System.out.println("Plant height is: " + level);
        return level;
    }

    /**
     * Read all alarms one bits pr. alarm. CMD: 13
     *
     * @return Alarms as BitSet
     */
    public BitSet ReadErrors() {
        System.out.println("Get all alarms ");
        mess = new Message(READ_ALL_ALARMS);
        BitSet alarms = new BitSet(32);

        mess.setData(); //None data
        conn.addMessage(mess);
        if (conn.send()) {
            alarms = fillBitSet(mess.getResultData());
        }
        System.out.println("Alarm state is: " + alarms);
        return alarms;
    }

    private BitSet fillBitSet(byte[] al) {
        BitSet alarms = new BitSet(32);
        if (true) {
            if (al != null && al.length == 4) {
                for (int i = 0; i < 4; i++) {
                    for (int b = 0; b < 8; b++) {
                        int ib = (al[i] >> b) & 0x1;
                        Boolean bit;
                        if (ib == 1) {
                            bit = true;
                        } else {
                            bit = false;
                        }
                        alarms.set(i * 8 + b, bit);
                    }
                }
            }
        }
        System.out.println("Alarms in set state: " + alarms);
        return alarms;
    }

    /**
     * Reset one alarm CMD: 14
     *
     * @param errorNum
     * @return Done
     */
    public boolean ResetError(int errorNum) {
        mess = new Message(RESET_ALARMS);
        errorNum--;
        if (errorNum >= 0 && errorNum < 64) // 0 - 30 grader celcius
        {
            System.out.println("Reset alarm " + errorNum + 1);
            mess.setData(errorNum);
            conn.addMessage(mess);
            if (conn.send()) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * Get all values as a byte array CMD: 15
     *
     * @return All values
     */
    public byte[] GetStatus() {
        byte[] result = new byte[100];
        System.out.println("Get status ");
        mess = new Message(GET_STATUS);
        mess.setData(); //None data
        conn.addMessage(mess);
        if (conn.send()) {
            result = mess.getResultData();
        }
        System.out.println("State is: " + result);
        return result;
    }

    /**
     * Set Fane speed CMD: 16
     *
     * @param speed : {OFF (0), LOW (1), HIGH(2)};
     * @return Done
     */
    public boolean SetFanSpeed(int speed) {
        System.out.println("Set fan speed " + speed);
        mess = new Message(SET_FAN_SPEED);
        if (speed >= 0 && speed <= 2) {
            mess.setData(speed);
            conn.addMessage(mess);
            if (conn.send()) {
                return true;
            } else {
                return false;
            }
        }
        return false;

    }


}
