import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.util.Random;

public class UdpProxy {

public static void main(String[] args)throws Exception{
    System.out.println("Proxy running ...");

            // client -> proxy -> server
    Thread threadA = Thread.startVirtualThread( () ->  {
        try(DatagramSocket socketIn = new DatagramSocket(3887)){
            byte[] buffer = new byte[2];
            DatagramPacket sendSegment = new DatagramPacket(buffer, 2);
            InetAddress serverAdress = InetAddress.getByName("localhost");
            while(true){
                socketIn.receive(sendSegment); // block und wartet auf client
                System.out.println("Client -> Server '" + (char)sendSegment.getData()[0] + "' | KB: " + sendSegment.getData()[1]);
                if(Math.random() < .20){
                    System.out.println("Nachricht verworfen. Must Resend");
                }
                else {
                    sendSegment.setAddress(serverAdress); // IP localhost laden
                    sendSegment.setPort(1337); // port auf server

                    socketIn.send(sendSegment); //schicke zu server
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    });
               // server -> proxy -> client
    Thread threadB = Thread.startVirtualThread( () ->  {
        try(DatagramSocket socketIn = new DatagramSocket(3888)){
            byte[] buffer = new byte[2];
            DatagramPacket sendSegment = new DatagramPacket(buffer, 2);
            InetAddress serverAdress = InetAddress.getByName("localhost");
            while(true){
                socketIn.receive(sendSegment);  // block und wartet auf server
                System.out.println("Server -> Client '" + (char)sendSegment.getData()[0] + " ' | KB: " + sendSegment.getData()[1]);
                sendSegment.setAddress(serverAdress); // IP localhost laden
                sendSegment.setPort(1338); //port andern zu client
                socketIn.send(sendSegment); // zum Client schicken
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    });

    try{
        System.in.read();
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
    System.out.println("Proxy ended");
}


}
