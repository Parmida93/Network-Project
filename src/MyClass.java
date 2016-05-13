import java.util.ArrayList;

import org.jnetpcap.Pcap;
import org.jnetpcap.nio.JMemory;
import org.jnetpcap.packet.JScanner;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;

public class MyClass {
	
	private MyFile myfile = new MyFile("packets.txt");
	private int sumTCPPayloadLength;
	private int tcpPacketNo;
	private int sumUDPPayloadLength;
	private int udpPacketNo;
	private int sumUDPLength;
	private int sumTCPLength;
	public long initialTime = 0;
	public ArrayList<NewPacket> packets;
	public static void main(String[] args) {
		
		MyClass c = new MyClass();
		
		String FILENAME;
		for (int i = 1; i < 5; i++) {
			System.out.println(i);
			String name = "Video" + i + "_QUIC";
			FILENAME = "src/" + name + ".pcap"; 
//		FILENAME = "src/HTTP_SampleA.pcap";
			
			c.readPcapFile(FILENAME, name);
		}
	}
	
	public void readPcapFile(String FILENAME, String name){
		final StringBuilder errbuf = new StringBuilder();

		final Pcap pcap = Pcap.openOffline(FILENAME, errbuf);
		if (pcap == null) {
			System.err.println(errbuf);
			return;
		}
		JScanner.getThreadLocal().setFrameNumber(0);

		PcapPacket packet = new PcapPacket(JMemory.POINTER);

		myfile.openFile();
		MyFile traceFile = new MyFile("./Traces/" + name + ".txt");
		traceFile.openFile();
		boolean isStart = true;
		packets = new ArrayList<>();
		while(pcap.nextEx(packet) == Pcap.NEXT_EX_OK) {
//			System.out.println(packet);
			if(isStart){
				initialTime = packet.getCaptureHeader().timestampInMicros();
				isStart = false;
			}
			
			NewPacket p = new NewPacket(packet);
			packets.add(p);
//			writeTraceFile(packet, traceFile);
//			analyzePacketHeader(packet);
			
		}

//		analyzeFlows();
////		printFlows();
//		String outStr = "Average TCP Payload Length: " + (sumTCPPayloadLength*1.0/tcpPacketNo) + "\n" + 
//						"Average UDP Payload Length: " + (sumUDPPayloadLength*1.0/udpPacketNo) + "\n" +
//						"Average TCP Length: " + (sumTCPLength*1.0/tcpPacketNo) + "\n" + 
//						"Average UDP Length: " + (sumUDPLength*1.0/udpPacketNo);
//		myfile.writeInFile(outStr);
		myfile.closeFile();
		traceFile.closeFile();
		pcap.close();

	}
	
	
	public void writeTraceFile( MyFile traceFile, MyPacket packet, boolean isUDP) {
		String protocol = "TCP";
		if( isUDP)
			protocol = "UDP";
		String outStr = "";
        byte[] dIP = new byte[4], sIP = new byte[4];
		outStr += packet.getID() + " ";
		outStr += ((packet.getTime() ) / Math.pow(10, 6)) + " ";
		dIP = packet.getDestIP();
		sIP = packet.getSourceIP();
		String sourceIP = org.jnetpcap.packet.format.FormatUtils.ip(sIP);
		String destIP = org.jnetpcap.packet.format.FormatUtils.ip(dIP);
		outStr += sourceIP + " " + destIP + " ";
		outStr += protocol + " ";
		outStr += packet.getTotalSize() + "\n";
//		System.out.println(outStr);
		traceFile.writeInFile(outStr);
	}

	private String findProtocol(byte protocolNo) {
		String protocol = "";
		if(protocolNo == 6)
			protocol = "TCP";
		else
			protocol = "UDP";
		return protocol;
			
	}

	private void analyzePacketHeader(PcapPacket packet) {
		int protocolNo = packet.getByte(23);
		if (protocolNo == 6) {			
//			Ip4 ip = packet.getHeader(new Ip4());
			Tcp tcp = packet.getHeader(new Tcp());
			int length = tcp.getPayloadLength();
			String ans = length + "\n";
			myfile.writeInFile(ans);
			sumTCPLength += tcp.getPayloadLength() + tcp.getHeaderLength();
			sumTCPPayloadLength += tcp.getPayloadLength();
			tcpPacketNo++;
		}
		else if(protocolNo == 17){
			Udp udp = packet.getHeader(new Udp());
			Ip4 ip = new Ip4();
			if(packet.hasHeader(Ip4.ID)){
	            packet.getHeader(ip);
	            byte[] dIP = new byte[4], sIP = new byte[4];
				dIP = packet.getHeader(ip).destination();
				sIP = packet.getHeader(ip).source();
				String sourceIP = org.jnetpcap.packet.format.FormatUtils.ip(sIP);
				String destIP = org.jnetpcap.packet.format.FormatUtils.ip(dIP);
				String outStr = packet.getFrameNumber() + ": " + sourceIP + "    " + destIP;
				System.out.println(outStr);
			}
//			byte[] udpHeader = udp.get;
			System.out.print("Total: ");
			for (int i = 0; i < udp.getLength(); i++) {
				System.out.print(String.format("%02X ", udp.getByte(i)) + " ");
			}
			System.out.println();
			
			byte[] payload = udp.getPayload();
			System.out.print("payload: ");
			for (int i = 0; i < payload.length; i++) {
				System.out.print(String.format("%02X ", payload[i]) + " ");
			}
			System.out.println();
//			String ans = udp.getPayloadLength() + "\n";
//			myfile.writeInFile(ans);
			sumUDPLength += udp.length();
			sumUDPPayloadLength += udp.getPayloadLength();
			udpPacketNo++;
		}
	}

}
