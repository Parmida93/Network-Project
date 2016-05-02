import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;

public class NewPcapAnalyzer {

	
	public MyPacket createTCPPacket(PcapPacket packet,MyPacket myPacket, int IPHeaderStartPoint, int counter,  MyPcapAnalyzer myPcapAnalyzer, long initTime){
    	int last4Bit = 0x000F;
    	int temp = packet.getByte(IPHeaderStartPoint) & last4Bit;
    	int IPHeaderSize = temp * 4;
    	int TCPHeaderStartPoint = IPHeaderStartPoint + IPHeaderSize;
    	
//    	myPacket.setID(counter);
    	
    	myPacket.setSourcePort(packet.getByteArray(TCPHeaderStartPoint , 2) );
    	myPacket.setDestPort(packet.getByteArray(TCPHeaderStartPoint + 2 , 2) );
    	myPacket.setSeqNum(packet.getByteArray(TCPHeaderStartPoint + 4 , 4) );
    	myPacket.setAckNum(packet.getByteArray(TCPHeaderStartPoint + 8 , 4) );
    	myPacket.setWindowSize(packet.getByteArray(TCPHeaderStartPoint + 14 , 2) );
    	
    	int first4Bit = 0x00F0;
    	temp = packet.getByte(TCPHeaderStartPoint + 12);
    	temp = (temp & first4Bit) >> 4;
    	int TCPHeaderSize = temp * 4;
    	myPacket.setDataLen(packet.getByteArray(IPHeaderStartPoint + 2 , 2), IPHeaderSize, TCPHeaderSize);
    	Tcp tcp = new Tcp();
		packet.hasHeader(tcp);
		myPacket.setTotalSize(tcp.getLength());
		myPacket.setDataLen(tcp.getLength() - tcp.getHeaderLength());
		
    	
    	byte typeByte = packet.getByte(TCPHeaderStartPoint + 13);
    	Integer[] typeIndexes = myPcapAnalyzer.typeDetector(typeByte);
    	myPacket.setType(typeIndexes);
    	
    	
    	if( (typeIndexes[0] == 6) || (typeIndexes.length > 1 && typeIndexes[1] == 6 ) ){
    		byte[] MSS = packet.getByteArray(TCPHeaderStartPoint + 22 , 2);
    		myPacket.setMMS(Integer.parseInt(myPacket.toDecimal(MSS)));
    	}
    	
		return myPacket;
	}
	
	public MyPacket createUDPPacket(PcapPacket packet,MyPacket myPacket, long initTime){
		Udp udp = new Udp();
		if(packet.hasHeader(udp)){
			myPacket.setSourcePort(udp.source());
			myPacket.setDestPort(udp.destination());
			myPacket.setDataLen(udp.length() - udp.getHeaderLength());
			myPacket.setTotalSize(udp.length());
//			myPacket.setHe
//			JNetPcapFormatter.
//			udp.getHeader();
		}
		
		return myPacket;
	}
	
	public MyPacket setIPLayer(PcapPacket packet,MyPacket myPacket, long initTime){
		Ip4 ip = new Ip4();
		if (packet.hasHeader(ip)) {
			myPacket.setSourceIP(ip.source());
			myPacket.setDestIP(ip.destination());
		}
		long currentTime = packet.getCaptureHeader().timestampInMicros();
    	myPacket.setAbsoluteTime(currentTime);
    	myPacket.setTime(currentTime - initTime);
    	Integer[] arr = {8};
    	myPacket.setType(arr);
		
		return myPacket;
	}
}
