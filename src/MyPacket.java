public class MyPacket {
	private Integer[] type;
	private byte[] seqNum;
	private byte[] ackNum;
	private byte[] windowSize;
	
	private int sourcePort;
	private int destPort;
	
	private int ID;
	
	private byte[] sourceIP;
	private byte[] destIP;
	
	private long time;
	private long absoluteTime;
	
	public long getAbsoluteTime() {
		return absoluteTime;
	}
	public void setAbsoluteTime(long absoluteTime) {
		this.absoluteTime = absoluteTime;
	}

	private boolean hasData;
	private int dataLen;
	private int totalSize;
	
	private int MMS;
	private boolean retransmission = false;
	private boolean willBeRetransmitted = false;
	
	public boolean isWillBeRetransmitted() {
		return willBeRetransmitted;
	}
	public void setWillBeRetransmitted(boolean willBeRetransmitted) {
		this.willBeRetransmitted = willBeRetransmitted;
	}
	public int getMMS() {
		return MMS;
	}
	public void setMMS(int mMS) {
		MMS = mMS;
	}
	public boolean isRetransmission() {
		return retransmission;
	}
	public void setRetransmission(boolean retransmission) {
		this.retransmission = retransmission;
	}
	public int getTotalSize() {
		return totalSize;
	}

	
	public int getDataLen() {
		return dataLen;
	}
	public void setDataLen(byte[] dataLen, int iPHeaderSize, int TCPHeaderSize) {
		int value = Integer.parseInt(this.toDecimal(dataLen)) - iPHeaderSize - TCPHeaderSize;
		this.totalSize = 14 + value + iPHeaderSize + TCPHeaderSize;
		if(value > 0)
			this.hasData = true;
		this.dataLen = value;
	}
	public void setDataLen(int size){
		this.dataLen = size;
	}
	
	public boolean hasData() {
		return hasData;
	}
	public void setHasData(boolean hasData) {
		this.hasData = hasData;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public byte[] getWindowSize() {
		return windowSize;
	}
	public void setWindowSize(byte[] windowSize) {
		this.windowSize = windowSize;
	}
	public Integer[] getType() {
		return type;
	}
	public void setType(Integer[] typeIndexes) {
		this.type = typeIndexes;
	}
	public byte[] getSeqNum() {
		return seqNum;
	}
	public void setSeqNum(byte[] seqNum) {
		this.seqNum = seqNum;
	}
	public byte[] getAckNum() {
		return ackNum;
	}
	public void setAckNum(byte[] ackNum) {
		this.ackNum = ackNum;
	}
	
	public int getSourcePort() {
		return sourcePort;
	}
	public void setSourcePort(byte[] sourcePort) {
		this.sourcePort = Integer.parseInt(this.toDecimal(sourcePort));
	}
	public void setSourcePort(int sourcePort) {
		this.sourcePort = sourcePort;
	}
	public int getDestPort() {
		return destPort;
	}
	public void setDestPort(byte[] destPort) {
		this.destPort = Integer.parseInt(this.toDecimal(destPort));
	}
	public void setDestPort(int destPort) {
		this.destPort = (destPort);
	}
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public byte[] getSourceIP() {
		return sourceIP;
	}
	public void setSourceIP(byte[] sourceIP) {
		this.sourceIP = sourceIP;
	}
	public byte[] getDestIP() {
		return destIP;
	}
	public void setDestIP(byte[] destIP) {
		this.destIP = destIP;
	}
	@Override
	public String toString() {
		
		String ans = "ID: " + this.ID + "\n";
		ans += "Type: ";
		for (int i = 0; i < this.type.length; i++) {
			ans += MyPcapAnalyzer.types[this.type[i]] + " ";
		}
		ans += "\n";
		ans +=  "SourceIP: " + this.toHex(sourceIP) + "\t" + this.IPtoDecimal(sourceIP)  + "\n" ;
		ans +=  "SourcePort: " + "\t" + this.sourcePort  + "\n" ;
		ans +=  "DestIP  : " + this.toHex(destIP) + "\t" + this.IPtoDecimal(destIP)  + "\n" ;
		ans +=  "DestPort  : " + "\t" + this.destPort  + "\n\n";
		ans +=  "SeqNum: " + this.toHex(seqNum) + "\t" + this.toDecimal(seqNum)  + "\n"; 
		ans +=  "AckNum: " + this.toHex(ackNum) + "\t" + this.toDecimal(ackNum)  + "\n";
		ans +=  "windowSize: " + this.toHex(windowSize) + "\t" + this.toDecimal(windowSize)  + "\n\n";
		ans +=  "DataSize: " + this.dataLen  + "\n";
				
		return ans;
	}
	
	public String getSeqAckWinSize(){
		String ans = "";
		ans += "SeqNum: " + this.toDecimal(seqNum);
		ans += "  AckNum: " + this.toDecimal(ackNum);
		ans += "  Window size: " + this.toDecimal(windowSize);
		return ans;
		
	}
	
	private String toHex(byte[] arr){
		String ans = "";
		for (int i = 0; i < arr.length; i++) {
			String temp = Integer.toHexString(arr[i]);
			if(temp.length() < 2)
				temp = "0" + temp;
			ans += temp.substring(temp.length() - 2, temp.length()) + " ";
		}
		return ans;
	}
	
	private String IPtoDecimal(byte[] arr){
		String ans = "";
		for (int i = 0; i < arr.length; i++) {
			int temp = arr[i];
			if(temp < 0)
				temp += 256;
			ans += temp + ".";
		}
		return ans.substring(0, ans.length()-1);
	}
	
	public String toDecimal(byte[] arr){
		long temp = arr[0];
		if(arr[0] < 0)
			temp += 256;
		for (int i = 1; i < arr.length; i++) {
			temp = temp << 8;
			temp += arr[i];
			if(arr[i] < 0 )
				temp += 256;
		}
		return temp + "";
	}
	
	@Override
	public boolean equals(Object obj) {
		MyPacket p = (MyPacket) obj;
		if(byteArrCompare(this.sourceIP, p.sourceIP) && this.sourcePort == p.sourcePort)
			if (byteArrCompare(this.destIP, p.destIP) && this.destPort == p.destPort)
				if(byteArrCompare(this.seqNum, p.seqNum))
					if(byteArrCompare(this.ackNum, p.ackNum))
						if( this.type.length == p.type.length )
							if( this.dataLen == p.dataLen)
								return true;
		return false;
	}
	
	public boolean byteArrCompare(byte[] arr1, byte[] arr2) {
		boolean ans = true;
		for (int i = 0; i < arr2.length; i++) {
			if (arr1[i] != arr2[i]) {
				ans = false;
				break;
			}
		}
		return ans;
	}
}
