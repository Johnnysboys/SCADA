package GreenhouseAPI;

import java.util.BitSet;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Greenhouse Java API
 * Synchronous mode
 * @version 2.00/17-9-2014
 * @author Steffen Skov
 */
public interface IGreenhouse 
{
    final byte ON = 1;
    final byte OFF = 0;
    
    final byte LOW = 1;
    final byte HIGH = 2;
    
    /**
     * Setpoint for temperature inside Greenhouse
     * CMD: 1
     * @param kelvin :temperature in kelvin
     * @return true if processed
     */
    boolean SetTemperature( int kelvin);
    
    /**
     * Setpoint for moisture inside Greenhouse
     * CMD:2
     * @param moist
     * @return true if processed
     */
    boolean SetMoisture(int moist);
    
    /**
     * Setpoint for red light inside Greenhouse
     * CMD:3
     * @param level in percent
     * @return true if processed
     */
    boolean SetRedLight(int level);
    
    /**
     * Setpoint for red light inside Greenhouse
     * CMD: 4
     * @param level in percent
     * @return true if processed
     */
    boolean SetBlueLight(int level);
    
    /**
     * Add water for some seconds. Pump is stopped if height of water is
     * exceeded
     * CMD: 5
     * @param sec : Secord to turn on the pump
     * @return true if processed
     */
    boolean AddWater(int sec);
    
    /**
     * NOT IMPLEMENTED
     * Add Fertiliser for some seconds. Pump is stopped if height of water is
     * exceeded
     * CMD: 6
     * @param sec : Secord to turn on the pump
     * @return true if processed
     */
    boolean AddFertiliser(int sec);
    
    /**
     * Add CO2 for some seconds. 
     * CMD: 7
     * @param sec : Second to turn on the valve
     * @return true if processed
     */
    boolean AddCO2(int sec);
    
    /**
     * NOT IMPLEMENTED
     * Read tempature inside the Greenhouse
     * CMD:8 
     * @return Temperature in kelvin
     */
    double ReadTemp1();
    
    /**
     * NOT IMPLEMENTED
     * Read tempature outside the Greenhouse
     * CMD: 9
     * @return Temperature in kelvin
     */
    double ReadTemp2();
    
    /**
     * Read relative moisture inside the Greenhouse
     * CMD: 10
     * @return Moisture in %
     */
    double ReadMoist();
    
    /**
     * Read level of water in the Greenhouse
     * CMD: 11
     * @return Level in millimeter
     */
    double ReadWaterLevel();
    
    /**
     * NOT IMPLEMENTED
     * Read higths of the plants
     * CMD: 12
     * @return Higths (cm?)
     */
    double ReadPlantHeight();
    
    /**
     * Read all alarms one bits pr. alarm.
     * CMD: 13
     * @return Alarms
     * Bit 0: Temperatur
     * Bit 1: Moisture
     * Bit 2: Waterlevel to high
     * Bit 3: Water pump error
     * ......
     */
    BitSet ReadErrors();
    
    
    
    /**
     * Reset one alarm
     * CMD: 14
     * @param errorNum
     * @return Done
     */
    boolean ResetError(int errorNum);
    
    /**
     * Get all values as a byte array
     * CMD: 15
     * @return All values
     * 1: temp inside greenhouse [celcius]
     * 2. temp outside greenhouse [celcius]
     * 3: moisture [0 - 100 %]
     * 4: waterlevel [0 - 25 cm]
     * 5: red ligth level [0 - 100]
     * 6: blue light level [0 - 100]
     * 7: fan level [0, 1, 2]
     * 8: water pump state [ON,OFF]
     * 9: Fertiliser pump state
     * 10: CO2 valve state
     * 11: Temperature setpoint
     * 12: Moisture setpoint
     * 13 - 20: NO
     * 21 - 24: All alarms
     * 25 - 99: AUX
     */             
    byte [] GetStatus();
    
    /**
     * Set fan speed
     * CMD 16
     * @param speed = {0,1,2} (OFF, LOW, HIGH)
     * @return true if processed
     */
    boolean SetFanSpeed(int speed);

}
