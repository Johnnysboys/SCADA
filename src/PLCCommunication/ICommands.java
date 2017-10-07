/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package PLCCommunication;

/**
 * List of PLC commands
 * @author Steffen Skov
 */
public interface ICommands 
{
    final byte NO_CMD = 0;
    final byte TEMP_SETPOINT = 1;
    final byte MOIST_SETPOINT = 2;
    final byte REDLIGHT_SETPOINT = 3;
    final byte BLUELIGHT_SETPOINT = 4;
    final byte START_WATER_PUMP = 5;//Not implemented
    final byte ADDWATER = 6; 
    final byte ADDFERTILISER = 7; // Not implemented
    final byte ADDCO2 = 8; // Not implemented
    final byte READ_GREENHOUSE_TEMP = 9;
    final byte READ_OUTDOOR_TEMP = 10;
    final byte READ_MOISTURE = 11;
    final byte READ_PLANT_HEIGHT = 12;
    final byte READ_ALL_ALARMS = 13;
    final byte RESET_ALARMS = 14;
    final byte GET_STATUS = 15;   
    final byte SET_FAN_SPEED = 16;
    final byte READ_WATER_LEVEL = 17;
    
    // Acknowledge/answer to commands
    // PLC add a bit to the command: command + 64 (~0x40) (~0100 0000)
    final byte TEMP_SETPOINT_ACK = TEMP_SETPOINT + 0x40;
    final byte MOIST_SETPOINT_ACK= 66;
    final byte REDLIGHT_SETPOINT_ACK = 67;
    final byte BLUELIGHT_SETPOINT_ACK = 68;
    final byte START_WATER_PUMP_ACK = 69;
    final byte ADDWATER_ACK = 70;
    final byte ADDFERTILISER_ACK = 71;
    final byte ADDCO2_ACK = 72;
    final byte READ_GREENHOUSE_TEMP_ACK = 73;
    final byte READ_OUTDOOR_TEMP_ACK = 74;
    final byte READ_MOISTURE_ACK = 75;
    final byte READ_PLANT_HEIGHT_ACK = 76;
    final byte READ_ALL_ALARMS_ACK = 77;
    final byte RESET_ALARMS_ACK = 78;
    final byte GET_STATUS_ACK = 79;   
    final byte SET_FAN_SPEED_ACK = 80;
    final byte READ_WATER_LEVEL_ACK = 81;
    
    
}
