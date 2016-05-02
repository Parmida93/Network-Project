
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.jnetpcap.Pcap;
import org.jnetpcap.nio.JMemory;
import org.jnetpcap.packet.PcapPacket;  
  


public class MyPcapAnalyzer {  
  
	
	public static String[] types = {"CWR", "ECE", "URG", "ACK", "PSH", "RST", "SYN", "FIN", "UDP"};
	
	public Integer[] typeDetector(byte b){
		ArrayList<Integer> indexes = new ArrayList<>(); 
		String binary = Integer.toBinaryString(b);
		while (binary.length() < 8) {
			binary = "0" + binary;
		}
		for (int i = 0; i < binary.length(); i++) {
			if(binary.charAt(i) == '1')
				indexes.add(i);
		}
		Integer[] ans = new Integer[indexes.size()]; 
		indexes.toArray(ans);
		return ans;
	}
	
	public String[] keyGen(byte[] srcIP, byte[] destIP, int srcPort, int destPort){
		String[] ans = new String[2];
		byte[][] merged = keyGenByteMerger(srcIP, destIP, srcPort, destPort);
		for (int i = 0; i < merged.length; i++) {
			ans[i] = "";
			for (int j = 0; j < merged[i].length; j++) {
				ans[i] += merged[i][j];
			}
		}
		return ans;
	}

	private byte[][] keyGenByteMerger(byte[] srcIP, byte[] destIP, int srcPort, int destPort) {
		byte[][] ans = new byte[2][];
		ans[0] = byteMerger(srcIP, srcPort, destIP, destPort);
		ans[1] = byteMerger(destIP, destPort, srcIP, srcPort);
		return ans;
	}

	private byte[] byteMerger(byte[] a, int srcPortNum, byte[] c, int destPortNum) {
		byte[] srcPort = new String(srcPortNum+"").getBytes();
		byte[] destPort = new String(destPortNum+"").getBytes();
		byte[] ans = new byte[a.length + srcPort.length + c.length + destPort.length];
		for (int i = 0; i < ans.length; i++) {
			if(i < a.length)
				ans[i] = a[i];
			else if (i < a.length + srcPort.length)
				ans[i] = srcPort[i - a.length];
			else if (i < a.length + srcPort.length + c.length)
				ans[i] = c[i - a.length - srcPort.length];
			else
				ans[i] = destPort[i - a.length - srcPort.length - c.length];
		}
		return ans;
	}

	public ArrayList<MyPacket> run(String file ) {  

		MyPcapAnalyzer myPcapAnalyzer = new MyPcapAnalyzer();
		
		final StringBuilder errbuf = new StringBuilder();   
		
//        String file = "tests/http_first_sample.pcap";
//        final String file = "tests/HTTP_SampleA.pcap";
//        final String file = "tests/HTTP_SampleB.pcap"; 
//        String file = "tests/HTTP_Sample_Big_Packet.pcap";
        
        System.out.printf("Opening file for reading: %s%n", file);  
  
        Pcap pcap = Pcap.openOffline(file, errbuf);  
  
        if (pcap == null) {  
            System.err.printf("Error while opening device for capture: "  
                + errbuf.toString());  
        }
        
        PcapPacket packetPointer = new PcapPacket(JMemory.POINTER);
        NewPcapAnalyzer newPcap = new NewPcapAnalyzer();
        
        int IPHeaderStartPoint = 14;
        
        int counter = 1;
        boolean first = true;
        long initTime = 0 ;
        
        HashMap<String, MyFlow> flows = new HashMap<>();
        ArrayList<MyPacket> packetsss = new ArrayList<>();
        
        while (pcap.nextEx(packetPointer) == Pcap.NEXT_EX_OK) {
        	
        	PcapPacket packet = new PcapPacket(packetPointer);
        	MyPacket myPacket = new MyPacket();
        	myPacket.setID(counter);
        	counter ++ ;
        	
        	byte tcpUDP = packet.getByte(IPHeaderStartPoint + 9);
        	
        	if(first){
        		initTime = packet.getCaptureHeader().timestampInMicros();
        		first = false;
        	}
        	
        	myPacket = newPcap.setIPLayer(packet, myPacket, initTime);
        	if(tcpUDP == 6)
        		myPacket = newPcap.createTCPPacket(packet, myPacket, IPHeaderStartPoint, counter, myPcapAnalyzer, initTime);
        	else if ( tcpUDP == 17)
        		myPacket = newPcap.createUDPPacket(packet, myPacket, initTime);
        	else{
        		System.err.println("UNKNOWN transmission protocol: " + tcpUDP);
        		continue;
        	}
        	packetsss.add(myPacket);

        	String[] keys = myPcapAnalyzer.keyGen(myPacket.getSourceIP(), myPacket.getDestIP(), myPacket.getSourcePort(), myPacket.getDestPort());
        	boolean newFlow = true;
        	for (int i = 0; i < keys.length; i++) {
    			if(flows.containsKey(keys[i])){
    				flows.get(keys[i]).addPacket(myPacket);
    				newFlow = false;
    				break;
    			}
    		}

        	if(newFlow){
        		MyFlow newMyFlow = new MyFlow(myPacket.getSourceIP(), myPacket.getDestIP());
        		newMyFlow.addPacket(myPacket);
        		flows.put(keys[0], newMyFlow);
        	}
		}
        System.out.println();
        System.out.println("Flows start here:");
        System.out.println();
        int flowNumber = 1;
        Set<String> keys = flows.keySet();
        for (String key : keys) {
        	System.out.println("Flow number " + flowNumber);
        	flowNumber++;
        	MyFlow tempFlow = flows.get(key);
        	if(!tempFlow.isValid())
        		continue;
        	ArrayList<MyPacket> temp = tempFlow.getFlow();
        	for (MyPacket myPacket : temp) {
				System.out.println(myPacket);
			}
//        	System.err.println(tempFlow.isValid());
//        	if( howToRun[1]){
//        		for (MyPacket myPacket2 : temp) {
//        			System.out.println(myPacket2);
//        			System.out.println("########################");
//        		}
//        	}
//        	if( howToRun[2]){
//        		System.out.println("RTT is: " + tempFlow.getAveRTT()/1000.0 + " ms");
//        		System.out.println("Throughput is: " + tempFlow.getThroughput(false));
//        		System.out.println("Good Throughput is: " + tempFlow.getThroughput(true));
//        		System.out.println("Initial Window: " + tempFlow.getIW());
//        		
//        	}
        	System.out.println("////////////////////////////////////////////////");
        	System.out.println("\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\");
 
        	
//        	if( howToRun[0]){
//        		File f = new File("A.txt");
//        		FileWriter fw;
//        		try {
//        			fw = new FileWriter(f);
//        			fw.write(tempFlow.fileOutput());
//        			fw.flush();
//        			fw.close();
//        		} catch (IOException e) {
//        			// TODO Auto-generated catch block
//        			e.printStackTrace();
//        		}
//        	}
//        	if(howToRun[3]){
//        		C c = new C();
//        		c.C_A_B(tempFlow);
//        	}
//        	if(howToRun[4]){
//	        	double[] RTOs = tempFlow.getRetransmissionValue();
//	        	for (double d : RTOs) {
//					System.out.println(d);
//				}
//        	}
        	
		}
        
        for (MyPacket myPacket : packetsss) {
//			System.out.println(myPacket.toString());
		}
        return packetsss;
    }  
}  