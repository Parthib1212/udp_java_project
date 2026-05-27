import java.io.IOException;
import java.net.*;



public class UdpClient {



    enum state{
        C0,
        C1,
        C2;
    }



    public static void main(String[] args) {
        byte[] buffer = new byte[2];
        String data = "TheQuickBrownRabbit";
        //receive teil
        byte[] receiveBuffer = new byte[2];
        DatagramPacket receiveSegment = new DatagramPacket(receiveBuffer, 2);

        try(DatagramSocket serverSocket = new DatagramSocket(1338)){
            //timeout
            serverSocket.setSoTimeout(5000);
            DatagramPacket sendSegment = new DatagramPacket(buffer, 2, InetAddress.getByName("localhost"), 3887);

            var currentState = state.C0;
            int  dataPointer = 0;
            byte KB_0 = 0x00;
            byte KB_1 = (byte) 0xFF;

            while(true){

                switch (currentState){

                    case C0:
                        sendSegment.getData()[0] = (byte) data.charAt(dataPointer);
                        sendSegment.getData()[1] = (byte) KB_0;
                        sendSegment.setPort(3887);
                        serverSocket.send(sendSegment);
                        System.out.println("Sent in C0 (M0): " + (char)sendSegment.getData()[0] + " | KB: " + sendSegment.getData()[1]);

                        dataPointer++;

                        currentState = state.C1;
                        break;
                    case C1:
                        //sendSegment.setPort(3887); //hardcoding wegen fehler
                        try {


                            serverSocket.receive(receiveSegment);
                            byte receivedL = receiveSegment.getData()[0];
                            byte receivedC1 = receiveSegment.getData()[1];
                            System.out.println("Empfangen (A0): '" + (char) receivedL + "' (KB: " + receivedC1 + ")");
                            //lesen

                            if (receivedC1 == KB_0) {
                                dataPointer++;
                                if (dataPointer >= data.length()) {
                                    System.out.println("Finish");
                                    dataPointer = 0;
                                }
                                //ändern und senden
                                sendSegment.getData()[0] = (byte) data.charAt(dataPointer);
                                sendSegment.getData()[1] = KB_1;
                                sendSegment.setPort(3887);
                                serverSocket.send(sendSegment);
                                System.out.println("Gesendet  (M1): '" + (char) sendSegment.getData()[0] + "' (KB: " + KB_1 + ")");

                                currentState = state.C2;

                            } else {

                                System.out.println("Falsch");
                            }

                        } catch (SocketTimeoutException e){
                            System.out.println("nochmal schicken wegen timeout");
                            //nochmal schicken
                            sendSegment.getData()[0] = (byte) data.charAt(dataPointer);
                            sendSegment.getData()[1] = (byte) KB_0;
                            sendSegment.setPort(3887);
                            serverSocket.send(sendSegment);
                        }break;
                    case C2:
                        try {
                            serverSocket.receive(receiveSegment);
                            byte receivedL_C2 = receiveSegment.getData()[0];
                            byte receivedC2 = receiveSegment.getData()[1];
                            System.out.println("Empfangen (A1): '" + (char) receivedL_C2 + "' (KB: " + receivedC2 + ")");
                            if (receivedC2 == KB_1) {
                                dataPointer++;
                                if (dataPointer >= data.length()) {
                                    System.out.println("Finish");
                                    dataPointer = 0;
                                }
                                //ändern und senden
                                sendSegment.getData()[0] = (byte) data.charAt(dataPointer);
                                sendSegment.getData()[1] = KB_0;
                                sendSegment.setPort(3887);
                                serverSocket.send(sendSegment);
                                System.out.println("Gesendet  (M0): '" + (char) sendSegment.getData()[0] + "' (KB: " + KB_0 + ")");

                                currentState = state.C1;

                            } else {

                                System.out.println("Falsch");
                            }
                        }catch (SocketTimeoutException e){
                            System.out.println("nochmal schicken wegen timeout");
                            //nochmal schicken
                            sendSegment.getData()[0] = (byte) data.charAt(dataPointer);
                            sendSegment.getData()[1] = KB_1;
                            sendSegment.setPort(3887);
                            serverSocket.send(sendSegment);

                        }
                        break;


                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
