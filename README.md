# Java UDP Alternating Byte Protocol (ABP)

[cite_start]**University Project - Data Communication Practical (SoSe 2026)** [cite: 1]

[cite_start]This repository contains a Java-based implementation of a client-server architecture communicating via User Datagram Protocol (UDP)[cite: 13, 64]. [cite_start]The project implements an Alternating Byte Protocol (ABP) to ensure reliable message delivery over an unreliable network [cite: 28][cite_start], complete with a Man-in-the-Middle proxy server to simulate packet loss[cite: 81, 113].

## Project Overview

[cite_start]This project is divided into three core components, fulfilling the requirements up to Task 3 of the practical[cite: 14, 81, 112]:

### 1. Client-Server Communication (Alternating Byte Protocol)
* [cite_start]**The Client** sends the string `"TheQuickBrownRabbit"` character by character[cite: 30].
* [cite_start]**The Server** receives the characters and responds with `"JumpsOver The LazyFox"`[cite: 31].
* [cite_start]**Message Format:** Every message is 2 bytes long[cite: 59]. [cite_start]The first byte (B₀) contains the ASCII character [cite: 60][cite_start], and the second byte (B₁) is a control byte that alternates between `0x00` and `0xFF` to track state[cite: 32, 61].
* [cite_start]The applications communicate over the loopback interface (`localhost`)[cite: 65]. [cite_start]The Client uses port `1338` [cite: 67] [cite_start]and the Server uses port `1337`[cite: 66].
* [cite_start]All sent and received messages are logged to the console[cite: 16, 18].

### 2. The Proxy (Man-in-the-Middle)
[cite_start]To test the robustness of the protocol, a Proxy application sits between the Client and Server[cite: 82].
* [cite_start]The proxy intercepts traffic utilizing Java 21 Virtual Threads (`Thread.startVirtualThread`) to handle blocking network calls efficiently[cite: 106, 107].
* [cite_start]It forwards packets from port `3887` to `1337` (Server) [cite: 101] [cite_start]and from port `3888` to `1338` (Client)[cite: 102].

### 3. Packet Loss and Resend Logic
Because UDP does not guarantee delivery, this project implements custom reliability mechanisms:
* [cite_start]**Packet Dropping:** The Proxy is intentionally configured to drop 20% of the packets traveling from the Client to the Server to simulate a faulty network connection[cite: 113].
* [cite_start]**Timeout and Retransmission:** The Client is configured with a 500ms socket timeout (`socket.setSoTimeout(500)`)[cite: 115, 144]. [cite_start]If the Client does not receive an acknowledgment from the Server within 500ms, it automatically resends the last packet[cite: 115].
* [cite_start]Dropped messages and timeout events are clearly logged in the console[cite: 114, 116].

## How to Run

1. [cite_start]**Compile the Java files:** Ensure you have Java 21 or higher installed to support Virtual Threads[cite: 106].
2. [cite_start]**Start the Server:** Run `UdpServer.java` first so it begins listening on port `1337`[cite: 66].
3. [cite_start]**Start the Proxy:** Run `UdpProxy.java` to initialize the middleman routing[cite: 101, 102].
4. [cite_start]**Start the Client:** Finally, run `UdpClient.java` on port `1338` to initiate the message exchange[cite: 67].
5. [cite_start]**Observe the Console:** Watch the console outputs across all three applications to see the Alternating Byte Protocol in action, including successful deliveries, simulated 20% packet drops, and automatic 500ms client retransmissions[cite: 16, 18, 114, 116].
