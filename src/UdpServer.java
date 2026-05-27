import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UdpServer {




    enum state{
        S1, //warte auf A0 und sendet M0 mit KB 0
        S2; //mit KB 1
    }


    public static void main(String[] args) {
        String data = "JumpsOverTheLazyFox";
        int dataPointer = 0;
        byte KB_0 = 0x00;
        byte KB_01 = (byte) 0xFF;
        final byte[] buffer = new byte[2];

        state currentState = state.S1; //startet mit s1

        byte[] receivedBuffer = new byte[2];
        DatagramPacket receivedSegment = new DatagramPacket(receivedBuffer, 2);

        try (DatagramSocket serverSocket = new DatagramSocket(1337)) {     // client socket 1338 server 1337
            DatagramPacket sendSegment = new DatagramPacket(buffer, 2, InetAddress.getByName("localhost"), 3888); // packet
            System.out.println("Server waiting...");
            while (true) {

                serverSocket.receive(receivedSegment); // block wartet auf Nachricht

                //lesen
                byte receivedL = receivedSegment.getData()[0];
                byte receivedKB = receivedSegment.getData()[1];

                System.out.println("Text: " + (char)receivedL + " Kontrollbyte: " + receivedKB);

                switch (currentState){

                    case S1:

                        if(receivedKB == KB_0){
                            if (dataPointer >= data.length()){
                                System.out.println("Finish");
                                dataPointer = 0;
                            }
                            sendSegment.getData()[0] = (byte) data.charAt(dataPointer); //hole Buchstaben ab
                            sendSegment.getData()[1] = KB_0; // schreibe 0x00 in getData 1
                            sendSegment.setPort(3888); // hardgecoded weil error gab
                            serverSocket.send(sendSegment); //sende M0
                            System.out.println("Empfangen (A0): '" + (char)receivedL + "' (KB: " + receivedKB + ")");
                            System.out.println("Gesendet  (M0): '" + (char)sendSegment.getData()[0] + "' (KB: " + KB_0 + ")");
                            dataPointer++;
                            currentState = state.S2; // jetzt zum s2 wechseln
                        }
                        else {
                            System.out.println("Fehler Falsches Kontrollbyte");
                        }
                        break;
                    case S2:
                        if(receivedKB == KB_01){
                            if (dataPointer >= data.length()) {
                                dataPointer = 0;
                            }
                            sendSegment.getData()[0] = (byte) data.charAt(dataPointer);
                            sendSegment.getData()[1] = KB_01; // schreibe 0xFF in getData 1
                            sendSegment.setPort(3888);
                            serverSocket.send(sendSegment); //sende M1
                            System.out.println("Empfangen (A1): '" + (char)receivedL + "' (KB: " + receivedKB + ")");
                            System.out.println("Gesendet  (M1): '" + (char)sendSegment.getData()[0] + "' (KB: " + KB_01 + ")");
                            dataPointer++;
                            currentState = state.S1;
                        }
                        else {
                            System.out.println("Fehler Falsches Kontrollbyte");
                        }
                        break;




                }

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }



    }
}
