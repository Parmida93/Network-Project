import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Run {
	
	
	
	public static void main(String[] args) {
		Run run = new Run();
		String folderAddress = "Pcaps/"; 
		File f = new File(folderAddress);
		File[] pcaps = f.listFiles();
		for (File file : pcaps) {
			String name = file.getName();
			name = name.replace(".pcap", "");
			
			boolean isUDP = false;
			if(name.contains("QUIC"))
				isUDP = true;
			run.getStatistics(folderAddress, name, isUDP);
		}
		
	}
	
	public void getStatistics(String folderAddress, String fileName, boolean isUDP){
//		String folderAddress = "Pcaps/"; 
//		String fileaddress = "Video1_QUIC" ;
		MyPcapAnalyzer myPcap = new MyPcapAnalyzer();
		
		ArrayList<MyPacket> packets = myPcap.run(folderAddress + fileName + ".pcap");
		
		File file = new File("Results/" + fileName  + ".csv");
		try {
			FileWriter fw = new FileWriter(file);
			
			String[] list = { "ID", "Header Size" , "Payload Size" , "Packet Size" , "Average Packet Size", "Average Payload Size" , "Average Header Size"};
			String comma = ",";
			String nextLine = "\n";
			
			for (String string : list) {
				fw.write(string);
				fw.write(comma);
			}
			fw.write(nextLine);
			
			Statistics st = new Statistics(packets);
			MyClass traceBuilder = new MyClass();
			MyFile traceFile = new MyFile("./Traces/" + fileName + ".txt");
			traceFile.openFile();
			for (MyPacket myPacket : packets) {
				
				boolean packetType = myPacket.getType()[0] == 8;
				traceBuilder.writeTraceFile(traceFile, myPacket, packetType);
				if( (myPacket.getType()[0] != 8 && isUDP) || (myPacket.getType()[0] == 8 && !isUDP) )
					continue;
				fw.write(new String(myPacket.getID() + ""));
				fw.write(comma);
				fw.write(myPacket.getHeaderSize()+ comma + myPacket.getDataLen()  + comma + myPacket.getTotalSize()+ comma);
				
				fw.write(st.getTotalAve() + comma);
				fw.write(st.getDataAve() + comma);
				fw.write(st.getHeaderAve() + comma);
				fw.write(nextLine);
				
			}
			fw.close();
			traceFile.closeFile();
			packets = null;
			st = null;
			traceBuilder = null;
			traceFile = null;
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
