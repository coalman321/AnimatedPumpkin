package main;

import java.io.IOException;
import java.net.*;


public class UDPServer {

    protected DatagramSocket socket;
    private InetAddress server, client;
    private int clientPort;
    private boolean isMulticast = false;


    /**
     * a UDP server useful for sending data to a known host
     * the host port is assigned via the system
     * @param clientAddr client address can either be an IP or a hostname
     * @param isMulticast is the server operating on multicast
     * @throws IOException
     * @throws UnknownHostException
     */
    public UDPServer(String clientAddr, int clientPort, boolean isMulticast) throws IOException, UnknownHostException {
        server = InetAddress.getLocalHost();
        if(!isMulticast) {
            socket = new DatagramSocket();
            client = InetAddress.getByName(clientAddr);
            this.clientPort = clientPort;
        }
        else{
            this.isMulticast = true;
            socket = new MulticastSocket();
            client = InetAddress.getByName(clientAddr);
            ((MulticastSocket) socket).joinGroup(client);
        }
    }

    /**
     * a UDP server useful for sending and receiving from a known host
     * @param clientAddr client address can either be an IP or a hostname
     * @param serverPort specify the server host port opened
     * @param isMulticast is the server operating on multicast
     * @throws IOException
     * @throws UnknownHostException
     */
    public UDPServer(String clientAddr, int clientPort, int serverPort, boolean isMulticast) throws IOException, UnknownHostException {
        server = InetAddress.getLocalHost();
        if(!isMulticast) {
            socket = new DatagramSocket(serverPort);
            client = InetAddress.getByName(clientAddr);
            this.clientPort = clientPort;
        }
        else{
            this.isMulticast = true;
            socket = new MulticastSocket(serverPort);
            client = InetAddress.getByName(clientAddr);
            ((MulticastSocket) socket).joinGroup(client);
        }
    }

    public String getHostAddress(){
        return server.getHostAddress();
    }

    public void sendMsg(String msg) throws IOException {
        byte[] buffer = msg.getBytes();
        DatagramPacket toSend = new DatagramPacket(buffer, buffer.length, client, clientPort);
        socket.send(toSend);
    }

    public String recieve()throws IOException{
        byte[] buffer = new byte[256];
        DatagramPacket toRecieve = new DatagramPacket(buffer, buffer.length);
        socket.receive(toRecieve);
        return new String(toRecieve.getData());
    }

    public void close() throws IOException{
        if(isMulticast) ((MulticastSocket)socket).leaveGroup(client);
        socket.close();
    }

}
