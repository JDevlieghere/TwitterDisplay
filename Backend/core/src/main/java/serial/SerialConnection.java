package serial;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Enumeration;

public class SerialConnection implements SerialPortEventListener {

    private final Logger log = LoggerFactory.getLogger(SerialConnection.class);

    private static final int TIME_OUT = 2000;
    private static final int DATA_RATE = 9600;

    private SerialPort serialPort;
    private BufferedReader input;
    private PrintWriter output;

    public void initialize(String port) {

        CommPortIdentifier portId = null;
        Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

        while (portEnum.hasMoreElements()) {
            CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
            if(currPortId.getName().equals(port)){
                portId = currPortId;
                break;
            }
        }
        if (portId == null) {
            throw new IllegalArgumentException("Could not find COM port.");
        }

        try {
            // Open serial port, and use class name for the appName.
            serialPort = (SerialPort) portId.open(this.getClass().getName(), TIME_OUT);

            // Set port parameters
            serialPort.setSerialPortParams(DATA_RATE,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);

            // Open the streams
            input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
            output = new PrintWriter(new OutputStreamWriter(serialPort.getOutputStream()));

            // Add event listeners
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
        } catch (Exception e) {
           log.error(e.toString());
        }

        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
            log.info("Shutting Down");
            close();
            }
        });
    }

    public void write(String s) {
        if (output != null){
            output.print(s);
            output.flush();
        }else {
            throw new IllegalStateException("Cannot write when not connected");
        }
    }

    public synchronized void close() {
        if (serialPort != null) {
            serialPort.removeEventListener();
            serialPort.close();
        }
    }

    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        if (serialPortEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            try {
                if(input.ready()){
                    String inputLine = input.readLine();
                    if(inputLine.length() > 0)
                        log.info("Received from COM: " + inputLine);
                }
            } catch (Exception e) {
                log.warn(e.toString());
            }
        }
    }
}
