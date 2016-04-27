//import java.util.ArrayList;
//
//import org.jnetpcap.Pcap;
//import org.jnetpcap.nio.JMemory;
//import org.jnetpcap.packet.JScanner;
//import org.jnetpcap.packet.PcapPacket;
//import org.jnetpcap.protocol.network.Ip4;
//import org.jnetpcap.protocol.tcpip.Tcp;
//import org.jnetpcap.protocol.tcpip.Udp;
//
//public class MainClass {
//	private static MyFile myfile = new MyFile();
//	private static ArrayList<MyFlow> flows = new ArrayList<>();
//	private static long initialTime = 0;
//	private static boolean isStart = true;
//	private static int sumTCPPayloadLength;
//	private static int tcpPacketNo;
//	private static int sumUDPPayloadLength;
//	private static int udpPacketNo;
//	private static int sumUDPLength;
//	private static int sumTCPLength;
//	
//	public static void main(String[] args) {
//		String FILENAME;
//		FILENAME = "src/QUIC1.pcap"; 
////		FILENAME = "src/HTTP_SampleA.pcap";
//		
//		readPcapFile(FILENAME);
//	}
//	
//	private static void readPcapFile(String FILENAME){
//		final StringBuilder errbuf = new StringBuilder();
//
//		final Pcap pcap = Pcap.openOffline(FILENAME, errbuf);
//		if (pcap == null) {
//			System.err.println(errbuf);
//			return;
//		}
//		JScanner.getThreadLocal().setFrameNumber(0);
//
//		PcapPacket packet = new PcapPacket(JMemory.POINTER);
//
//		myfile.openFile();
//		while (pcap.nextEx(packet) == Pcap.NEXT_EX_OK) {
//			if (isStart == true) {
//				initialTime = packet.getCaptureHeader().timestampInMicros();
//				isStart = false;
//			}
//			analyzePacketHeader(packet);
//		}
//
////		analyzeFlows();
//////		printFlows();
//		String outStr = "Average TCP Payload Length: " + (sumTCPPayloadLength*1.0/tcpPacketNo) + "\n" + 
//						"Average UDP Payload Length: " + (sumUDPPayloadLength*1.0/udpPacketNo);
//		myfile.writeInFile(outStr);
//		myfile.closeFile();
//		pcap.close();
//
//	}
//
//	private static void printCongestionWindow(MyFlow myFlow) {
//		System.out.println("Congestion WIndow Sizes:");
//		int cws[] = myFlow.computeCongestionWindowSize();
//		for (int i = 0; i < cws.length; i++) {
//			System.out.print(cws[i] + "   ");
//		}
//	}
//
//	private static void analyzeFlows() {
//		for (int i = 0; i < flows.size(); i++) {
//			MyFlow tempFlow = flows.get(i);
//			ArrayList<MyPacket> flowPackets = tempFlow.getFlowPackets();
//			if (flowPackets.size() >= 3) {
//				tempFlow.checkFlow();
//			}
//			if (tempFlow.isFlow() == true)
//				tempFlow.addReqRes();
//		}
//	}
//
//	private static void printFlowsInFiles(MyPacket myPacket) {
//		String packetHeaderInfo = "   Source: " + myPacket.getSourcePort() + "    Destination: "
//				+ myPacket.getDestinationPort() + "    SequenceNo.: " + myPacket.getSequenceNumber()
//				+ "    Acknowledgement: " + myPacket.getAcknowledgementNumber() + "    Window: "
//				+ myPacket.getWindowSize() + "    URG: " + myPacket.getURG() + "   ACK: " + myPacket.getACK()
//				+ "   SYN: " + myPacket.getSYN() + "   FIN: " + myPacket.getFIN() + "  Total Length: "
//				+ myPacket.getTotalLength() + "\n";
//		myfile.writeInFile(packetHeaderInfo);
//	}
//
//	private static void printFlows() {
//		for (int i = 0; i < flows.size(); i++) {
//			MyFlow myFlow = flows.get(i);
//			if (myFlow.isFlow()) {
//				ArrayList<MyPacket> packetsArray = myFlow.getFlowPackets();
//				System.out.println("FLOW #" + i);
//				for (int j = 0; j < packetsArray.size(); j++) {
//					printPacketInfo(packetsArray.get(j));
//					printFlowsInFiles(packetsArray.get(j));
//				}
//				System.out.println("IW: " + flows.get(i).calculateInitialWindowSize());
//				System.out.println("AVERAGE ROUND TRIP TIME: " + myFlow.calculatAverageRoundTripTime());
//				myFlow.calculateThroughputGoodput();
//				System.out.println("THROUGHPUT: " + myFlow.getThroughput());
//				System.out.println("GOODPUT: " + myFlow.getGoodput());
//				printCongestionWindow(myFlow);
//				// double RTOs[] = computePartC_2(myFlow);
//				// for (int j = 0; j < RTOs.length; j++) {
//				// System.out.print(RTOs[j] + " ");
//				// }
//			}
//		}
//	}
//
//	private static void analyzePacketHeader(PcapPacket packet) {
//		int protocolNo = packet.getByte(23);
//		if (protocolNo == 6) {			
//			Ip4 ip = packet.getHeader(new Ip4());
//			Tcp tcp = packet.getHeader(new Tcp());
//			sumTCPLength = tcp.getLength();
//			sumTCPPayloadLength += tcp.getPayloadLength();
//			tcpPacketNo++;
////			int ipHeaderLength = calculateIpHeaderLength(packet) * 4;
////			int tcpHeaderLength = calculateTCPHeaderLength(packet, ipHeaderLength + 14) * 4;
//////			System.out.println("ipHeaderLength: " + ipHeaderLength);
//////			System.out.println("tcpHeaderLength: " + tcpHeaderLength);
////			byte[] ipHeaderBytes = packet.getByteArray(14, ipHeaderLength);
////			byte[] tcpHeaderBytes = packet.getByteArray(ipHeaderLength + 14, tcpHeaderLength);
////			MyPacket myPacket = new MyPacket(tcpHeaderBytes, ipHeaderBytes);
////			myPacket.setTime((packet.getCaptureHeader().timestampInMicros() - initialTime) / Math.pow(10, 6));
////			myPacket.setCompleteSize(packet.size());
////			myPacket.setIpHeaderLength(ipHeaderLength);
////			myPacket.setTcpHeaderLength(tcpHeaderLength);
////			myPacket.setFrameNumber(packet.getFrameNumber());
////			MyFlow myFlow;
////			if (myPacket.getSYN() == 1 && myPacket.getACK() == 0) {
////				myFlow = new MyFlow(myPacket.getSourceIP() + "-" + myPacket.getSourcePort(),
////						myPacket.getDestinationIP() + "-" + myPacket.getDestinationPort());
////				myFlow.addPacketToFlow(myPacket, true);
////				flows.add(myFlow);
////			} else if (myPacket.getACK() == 1) {
////				myFlow = getCurrentFlow(myPacket);
////				if (myFlow != null) {
////					if (checkRetransmition(myPacket, myFlow)) {
////						myFlow.addPacketToFlow(myPacket, false);
//////						System.out.println("Retransmition");
////					} else
////						myFlow.addPacketToFlow(myPacket, true);
////				}
////				// myFlow.addReqRes(myPacket);
////			}
//		}
//		else if(protocolNo == 17){
//			System.out.println("UDP");
//			Udp udp = packet.getHeader(new Udp());
//			sumUDPLength += udp.length();
//			sumUDPPayloadLength += udp.getPayloadLength();
//			udpPacketNo++;
//		}
//	}
//
//	private static void printPacketInfo(MyPacket myPacket) {
//		System.out.printf(
//				"SourceIP: %s    DestinationIP: %s    SourcePort: %d    DestinationPort: %d   SequenceNo.: %s   Acknowledgement: %d   Window:%d   Total Length: %d%n",
//				myPacket.getSourceIP(), myPacket.getDestinationIP(), myPacket.getSourcePort(),
//				myPacket.getDestinationPort(), myPacket.getSequenceNumber(), myPacket.getAcknowledgementNumber(),
//				myPacket.getWindowSize(), myPacket.getTotalLength());
//		System.out.printf("URG: %d   ACK: %d   SYN: %d   FIN: %d%n", myPacket.getURG(), myPacket.getACK(),
//				myPacket.getSYN(), myPacket.getFIN());
//		System.out.println("Size:  " + myPacket.getCompleteSize());
//		System.out.println("Time:  " + myPacket.getTime());
//		System.out.println("-------------------------------------------------------------------------------");
//	}
//	
//	private static boolean checkRetransmition(MyPacket mypacket, MyFlow myFlow) {
//		ArrayList<MyPacket> packets = myFlow.getFlowPackets();
//		boolean flag = false;
//		for (int i = 0; i < packets.size(); i++) {
//			MyPacket temp = packets.get(i);
//			if (temp.equals(mypacket)) {
//				myFlow.getValidityOfFlowPackets().set(i, false);
//				packets.get(i).setRetransmitted(true);
//				flag = true;
//			}
//		}
//		return flag;
//	}
//
//	private static MyFlow getCurrentFlow(MyPacket myPacket) {
//		MyFlow currentFlow = null;
//		for (int i = 0; i < flows.size(); i++) {
//			currentFlow = flows.get(i);
//			String srcInfo = myPacket.getSourceIP() + "-" + myPacket.getSourcePort();
//			String destInfo = myPacket.getDestinationIP() + "-" + myPacket.getDestinationPort();
//			if ((currentFlow.getClient().equals(srcInfo) && currentFlow.getServer().equals(destInfo))
//					|| (currentFlow.getClient().equals(destInfo) && currentFlow.getServer().equals(srcInfo))) {
//				return currentFlow;
//			}
//		}
//		return null;
//	}
//}
