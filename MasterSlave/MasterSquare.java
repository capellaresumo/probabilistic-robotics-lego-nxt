import lejos.pc.comm.*;
import java.util.Scanner;
import java.io.*;
/**
 * Master: Sends commands to NXT Slave application
 * @author Denis Maua'
 * @since 2016-06-05
 * Modified by @pksm
 */
public class Master {
	private static final byte SQUARE = 0;
	private static final byte BATTERY = 1;
	private static final byte STOP = 2;
	
	private NXTComm nxtComm;
	private DataOutputStream dos;
	private DataInputStream dis;	
	
	private static final String NXT_ID = "NXT8"; // NXT BRICK ID
	
	/**
	 * Send command to the robot
	 * @param command specifies command
	 * @param param argument
	 * @return value returned by the robot (float)
	 */
	private float sendCommand(byte command, float param) {
		try {
			dos.writeByte(command);
			dos.writeFloat(param);
			dos.flush();
			return dis.readFloat();
		} catch (IOException ioe) {
			System.err.println("IO Exception");
			System.exit(1);
			return -1f;
		}
	}
	
	/**
	 * Connect to the NXT
	 *
	 */
	private void connect() {
		try {
			NXTComm nxtComm = NXTCommFactory.createNXTComm(NXTCommFactory.USB);
			/* Uncomment next line for Bluetooth communication */
			//NXTComm nxtComm = NXTCommFactory.createNXTComm(NXTCommFactory.BLUETOOTH);			
			NXTInfo[] nxtInfo = nxtComm.search(Master.NXT_ID);
			
			if (nxtInfo.length == 0) {
				System.err.println("NO NXT found");
				System.exit(1);
			}
			
			if (!nxtComm.open(nxtInfo[0])) {
				System.err.println("Failed to open NXT");
				System.exit(1);
			}
			
			dis = new DataInputStream(nxtComm.getInputStream());
			dos = new DataOutputStream(nxtComm.getOutputStream());
			
		} catch (NXTCommException e) {
			System.err.println("NXTComm Exception: "  + e.getMessage());
			System.exit(1);
		}
	}		
	/**
	 * Terminate the program and send stop command to the robot
	 *
	 */
	private void close() {
		try {
			dos.writeByte(STOP);
			dos.writeFloat(0f);
			dos.flush();
			Thread.sleep(200);
			System.exit(0);
		} catch (Exception ioe) {
			System.err.println("IO Exception");
		}
	}	
	
	public static void main(String[] args) {
		byte cmd = 0; float param = 0f; float ret=0f; 
		Master master = new Master();
		master.connect();
	    Scanner scan = new Scanner( System.in );	    
	    while(true) {
	    	System.out.print("Enter command [0:ROTATE 1:ROTATETO 2:SQUARE 3:STOP]: ");
	    	cmd = (byte) scan.nextFloat(); 
	    	if (cmd < 2) {
	    	 System.out.print("Enter param [float]: ");
	    	 param = scan.nextFloat();
	    	} else {
	    		param = 0;	    		
	    	}
	    	ret = master.sendCommand(cmd, param);
	    	System.out.println("cmd: " + cmd + " param: " + param + " return: " + ret);
	    }
	}

}
