# Java UDP Alternating Byte Protocol (ABP)

**University Project - Data Communication Practical (SoSe 2026)**

This repository contains a Java-based implementation of a client-server architecture communicating via User Datagram Protocol (UDP). The project implements an Alternating Byte Protocol (ABP) to ensure reliable message delivery over an unreliable network, complete with a Man-in-the-Middle proxy server to simulate packet loss.

## Project Overview


### 1. Client-Server Communication (Alternating Byte Protocol)
* **The Client** sends the string `"TheQuickBrownRabbit"` character by character.
* **The Server** receives the characters and responds with `"JumpsOverTheLazyFox"`.
* **Message Format:** Every message is 2 bytes long. The first byte (B₀) contains the ASCII character, and the second byte (B₁) is a control byte that alternates between `0x00` and `0xFF` to track state.
* The applications communicate over the loopback interface (`localhost`). The Client uses port `1338` and the Server uses port `1337`.
* All sent and received messages are logged to the console.

### 2. The Proxy (Man-in-the-Middle)
To test the robustness of the protocol, a Proxy application sits between the Client and Server.
* The proxy intercepts traffic utilizing Java 21 Virtual Threads (`Thread.startVirtualThread`) to handle blocking network calls efficiently.
* It forwards packets from port `3887` to `1337` (Server) and from port `3888` to `1338` (Client).

### 3. Packet Loss and Resend Logic
Because UDP does not guarantee delivery, this project implements custom reliability mechanisms:
* **Packet Dropping:** The Proxy is intentionally configured to drop 20% of the packets traveling from the Client to the Server to simulate a faulty network connection.
* **Timeout and Retransmission:** The Client is configured with a 500ms socket timeout (`socket.setSoTimeout(500)`). If the Client does not receive an acknowledgment from the Server within 500ms, it automatically resends the last packet.
* Dropped messages and timeout events are clearly logged in the console.

## How to Run

1. **Compile the Java files:** Ensure you have Java 21 or higher installed to support Virtual Threads.
2. **Start the Server:** Run `UdpServer.java` first so it begins listening on port `1337`.
3. **Start the Proxy:** Run `UdpProxy.java` to initialize the middleman routing.
4. **Start the Client:** Finally, run `UdpClient.java` on port `1338` to initiate the message exchange.
5. **Observe the Console:** Watch the console outputs across all three applications to see the Alternating Byte Protocol in action, including successful deliveries, simulated 20% packet drops, and automatic 500ms client retransmissions.

## Technologies Used

* **Language:** Java 21 (leveraging Virtual Threads and the standard `java.net` DatagramSocket API)
* **Testing & Debugging:** Wireshark (for UDP packet analysis and protocol verification)
* **Environment:** IntelliJ IDEA / macOS
