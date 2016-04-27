import java.math.BigInteger;

public class MyPacket {
	byte[] tcpHeaderBytes;
	byte[] ipHeaderBytes;
	
	private int sourcePort;
	private int destinationPort;
	private long sequenceNumber;
	private long acknowledgementNumber;
	private int windowSize;
	
	private String sourceIP;
	private String destinationIP;
	private int totalLength;
	
	private int ipHeaderLength = 20;
	private int tcpHeaderLength = 20;
	
	private int index = 0;
	
	private int completeSize;
	private double time;
	
	//Flags
	private int URG;
	private int ACK;
	private int PSH;
	private int RST;
	private int SYN;
	private int FIN;
	
	//Checksum
	private int checkSum;
	
	private long frameNumber;
	
	private boolean isRetransmitted = false;
	
	public MyPacket(byte[] tcpHeaderBytes, byte[] ipHeaderBytes) {
		this.tcpHeaderBytes = tcpHeaderBytes;
		this.ipHeaderBytes = ipHeaderBytes;
		decodePacket();
	}
	
	public boolean isTCPProtocol(){
		String res = getBits(ipHeaderBytes[9]);
		int resInt = Integer.parseInt(res, 2);
		if(resInt == 6)
			return true;
		return false;
	}
	
	public void decodePacket(){
		//Source
		String res1 = getBits(tcpHeaderBytes[0]) + getBits(tcpHeaderBytes[1]);
		this.sourcePort = Integer.parseInt(res1, 2);
		
		//Destination
		String res2 = getBits(tcpHeaderBytes[2]) + getBits(tcpHeaderBytes[3]);
		this.destinationPort = Integer.parseInt(res2, 2);
		
		//Sequence Number
		String res3 = getBits(tcpHeaderBytes[4]) + getBits(tcpHeaderBytes[5]) + getBits(tcpHeaderBytes[6]) + getBits(tcpHeaderBytes[7]);
		this.sequenceNumber = Long.parseLong(new BigInteger(res3, 2).toString(10));
		
		//Acknowledgement Number
		String res4 = getBits(tcpHeaderBytes[8]) + getBits(tcpHeaderBytes[9]) + getBits(tcpHeaderBytes[10]) + getBits(tcpHeaderBytes[11]);
		this.acknowledgementNumber = Long.parseLong(new BigInteger(res4, 2).toString(10));
		
		//Window Size
		String res5 = getBits(tcpHeaderBytes[14+index]) + getBits(tcpHeaderBytes[15+index]);
		this.windowSize = Integer.parseInt(res5, 2);
		
		//Flags
		String res6 = getFlagsBits(tcpHeaderBytes[13+index]);
		setFlags(res6);
		
		//Checksum
		String res7 = getBits(tcpHeaderBytes[16+index]) + getBits(tcpHeaderBytes[17+index]);
		this.checkSum = Integer.parseInt(res7, 2);
		
		//SourceIP
		this.sourceIP = Integer.parseInt(getBits(ipHeaderBytes[12]), 2) + "." + Integer.parseInt(getBits(ipHeaderBytes[13]), 2) + "." + Integer.parseInt(getBits(ipHeaderBytes[14]), 2) + "." + Integer.parseInt(getBits(ipHeaderBytes[15]), 2);
		
		//DestinationIP
		this.destinationIP = Integer.parseInt(getBits(ipHeaderBytes[16]), 2) + "." + Integer.parseInt(getBits(ipHeaderBytes[17]), 2) + "." + Integer.parseInt(getBits(ipHeaderBytes[18]), 2) + "." + Integer.parseInt(getBits(ipHeaderBytes[19]), 2);
		
		//Total Length
		String res8 = getBits(ipHeaderBytes[2]) + getBits(ipHeaderBytes[3]);
		this.totalLength = Integer.parseInt(res8, 2);
//		System.out.println("totalLength: " + totalLength);
	}
	
	private void setFlags(String flagsStr){
		this.URG = Character.getNumericValue(flagsStr.charAt(0));
		this.ACK = Character.getNumericValue(flagsStr.charAt(1));
		this.PSH = Character.getNumericValue(flagsStr.charAt(2));
		this.RST = Character.getNumericValue(flagsStr.charAt(3));
		this.SYN = Character.getNumericValue(flagsStr.charAt(4));
		this.FIN = Character.getNumericValue(flagsStr.charAt(5));
	}
	
	private String getFlagsBits(byte flagsByte) {
		String result = Integer.toBinaryString(flagsByte);
		if(result.length() <= 6){
			String temp = "";
			for (int i = 0; i < 6 - result.length(); i++) {
				temp += '0';
			}
			result = temp + result;
		}
		if(result.length() > 6)
			result = result.substring(result.length() - 6, result.length());
		return result;
	}
	
	private String getBits(byte first) {
		String result = Integer.toBinaryString(first);
		if(result.length() < 8){
			String temp = "";
			for (int i = 0; i < 8 - result.length(); i++) {
				temp += '0';
			}
			result = temp + result;
		}
		if(result.length() > 8)
			result = result.substring(result.length() - 8, result.length());
//		System.out.println(first + "   " + result);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		MyPacket newPacket = (MyPacket) obj;
		if(newPacket.getSourceIP().equals(this.sourceIP) && newPacket.getDestinationIP().equals(this.destinationIP) 
				&& newPacket.sourcePort == this.sourcePort && newPacket.destinationPort == this.destinationPort 
				&& newPacket.getAcknowledgementNumber() == this.acknowledgementNumber && newPacket.getSequenceNumber() == this.sequenceNumber
				&& newPacket.getACK() == this.ACK && newPacket.getSYN() == this.SYN && newPacket.getFIN() == this.FIN
				&& newPacket.totalLength == this.totalLength)
			return true;
		return false;
	}
	
	public long getMSS(){
		String res4 = getBits(tcpHeaderBytes[22]) + getBits(tcpHeaderBytes[23]);
		return Long.parseLong(new BigInteger(res4, 2).toString(10));
	}
	
	public int getDataLength(){
		return (this.totalLength - (this.ipHeaderLength + this.tcpHeaderLength));
	}
	
	public int getSourcePort(){
		return this.sourcePort;
	}
	
	public int getDestinationPort(){
		return this.destinationPort;
	}
	
	public long getSequenceNumber(){
		return this.sequenceNumber;
	}
	
	public long getAcknowledgementNumber(){
		return acknowledgementNumber;
	}
	
	public int getWindowSize(){
		return this.windowSize;
	}
	
	public int getURG() {
		return URG;
	}

	public int getACK() {
		return ACK;
	}


	public int getPSH() {
		return PSH;
	}


	public int getRST() {
		return RST;
	}


	public int getSYN() {
		return SYN;
	}

	public int getFIN() {
		return FIN;
	}

	public int getTotalLength() {
		return totalLength;
	}

	public void setTotalLength(int totalLength) {
		this.totalLength = totalLength;
	}

	public String getSourceIP() {
		return sourceIP;
	}

	public void setSourceIP(String sourceIP) {
		this.sourceIP = sourceIP;
	}

	public String getDestinationIP() {
		return destinationIP;
	}

	public void setDestinationIP(String destinationIP) {
		this.destinationIP = destinationIP;
	}

	public int getCompleteSize() {
		return completeSize;
	}

	public void setCompleteSize(int completeSize) {
		this.completeSize = completeSize;
	}

	public double getTime() {
		return time;
	}

	public void setTime(double time) {
		this.time = time;
	}

	public int getIpHeaderLength() {
		return ipHeaderLength;
	}

	public void setIpHeaderLength(int ipHeaderLength) {
		this.ipHeaderLength = ipHeaderLength;
	}

	public int getTcpHeaderLength() {
		return tcpHeaderLength;
	}

	public void setTcpHeaderLength(int tcpHeaderLength) {
		this.tcpHeaderLength = tcpHeaderLength;
	}
	
	public byte[] getTcpHeaderBytes() {
		return tcpHeaderBytes;
	}

	public byte[] getIpHeaderBytes() {
		return ipHeaderBytes;
	}

	public boolean isRetransmitted() {
		return isRetransmitted;
	}

	public void setRetransmitted(boolean isRetransmitted) {
		this.isRetransmitted = isRetransmitted;
	}

	public long getFrameNumber() {
		return frameNumber;
	}

	public void setFrameNumber(long frameNumber) {
		this.frameNumber = frameNumber;
	}
}
