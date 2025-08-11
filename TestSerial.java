
import com.fazecast.jSerialComm.SerialPort;

public class TestSerial {
    public static void main(String[] args) {
        SerialPort[] ports = SerialPort.getCommPorts();
        System.out.println("Found " + ports.length + " serial ports:");
        for (SerialPort port : ports) {
            System.out.println(" - " + port.getSystemPortName());
        }
    }
}
