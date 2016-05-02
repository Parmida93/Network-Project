import java.util.ArrayList;

public class MyFlow {

	private byte[] starter;
	private byte[] responder;
	private ArrayList<MyPacket> flow;
	private Boolean valid = null;
	private boolean detectRetransmissions = false;
	private boolean threeWayHandshakeDone = false;
	
	private int numberOfPackets = 0;
	private double tempNumberOfPackets = 0;

	public MyFlow(byte[] sourceIP, byte[] destIP) {
		this.starter = sourceIP;
		this.responder = destIP;
		this.flow = new ArrayList<>();
	}

	public void addPacket(MyPacket packet) {
		this.flow.add(packet);
	}

	public ArrayList<MyPacket> getFlow() {
		return flow;
	}

	public void setFlow(ArrayList<MyPacket> flow) {
		this.flow = flow;
	}

	public byte[] getStarter() {
		return starter;
	}

	public void setStarter(byte[] server) {
		this.starter = server;
	}

	public byte[] getResponder() {
		return responder;
	}

	public void setResponder(byte[] client) {
		this.responder = client;
	}

	public boolean isThreeWayHandshakeDone() {
		return threeWayHandshakeDone;
	}

	public void setThreeWayHandshakeDone(boolean threeWayHandshakeDone) {
		this.threeWayHandshakeDone = threeWayHandshakeDone;
	}

	public boolean isValid() {
		if (this.valid == null)
			this.checkValidity();
		return valid.booleanValue();
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public double getAveRTT() {
		long sum = 0;
		int counter = 0;
		this.findRetransmissions();
		
		for (int i = 3; i < this.flow.size(); i++) {
			MyPacket sender = this.flow.get(i);
			if(sender.byteArrCompare(sender.getSourceIP(), this.starter))
				continue;
			long senderNextAck = sender.getDataLen() + Long.parseLong(sender.toDecimal(sender.getSeqNum()));
			double RTT = -1;
			if (sender.hasData() && !sender.isRetransmission() && !sender.isWillBeRetransmitted()) {
				MyPacket rec = findRTT(i+1, sender, senderNextAck);
				if( rec != null )
					RTT = rec.getTime() - sender.getTime();
				
			}
			if(RTT != -1){
				sum += RTT;
				counter++;
			}
		}
		return (sum + 0.0) / counter;
	}

	public int getNumberOfPackets() {
		return numberOfPackets;
	}

//	public void setNumberOfPackets(int numberOfPackets) {
//		this.numberOfPackets = numberOfPackets;
//	}
	
	public void increaseNumberOfPackets() {
		this.numberOfPackets ++;
	}
	
	public void halfNumberOfPackets() {
		this.numberOfPackets = this.numberOfPackets / 2;
		this.tempNumberOfPackets = 0;
	}
	
	
	public void increaseNumberOfPacketsAfterThreshold() {
		this.tempNumberOfPackets += 1.0 / this.numberOfPackets;
		if( this.tempNumberOfPackets > 1){
			this.tempNumberOfPackets = 0;
			this.numberOfPackets ++;
		}
	}

	public MyPacket findRTT(int i, MyPacket sender, long senderNextAck) {
		MyPacket ans = null;
		boolean find = false;
		for (int j = i; j < flow.size(); j++) {
			MyPacket reciever = this.flow.get(j);
			if(reciever.byteArrCompare(reciever.getSourceIP(), sender.getSourceIP()))
				continue;
			long ack = Long.parseLong(reciever.toDecimal(reciever.getAckNum()));
			if (ack == senderNextAck) {
				ans = reciever;
				find = true;
//				System.out.println("(((((((((((((((((((((((((((((((((((((((((((");
//				System.out.println(sender.toString());
//				System.out.println(sender.getTime());
//				System.out.println(reciever.toString());
//				System.out.println(reciever.getTime());
//				System.out.println(")))))))))))))))))))))))))))))))))))))))))))");
				
//				RTT = reciever.getTime() - sender.getTime();
				// sum += reciever.getTime() - sender.getTime();
				break;
			}
		}
		
		if( find == false){
			for (int j = i; j < flow.size(); j++) {
				MyPacket reciever = this.flow.get(j);
				if(reciever.byteArrCompare(reciever.getSourceIP(), sender.getSourceIP()))
					continue;
				long ack = Long.parseLong(reciever.toDecimal(reciever.getAckNum()));
				if (ack >= senderNextAck) {
					ans = reciever;
					find = true;
					break;
				}
			}
		}
		return ans;
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

	private int getInitCWND() {
		int counter = 0;
		long initRTT = this.flow.get(1).getTime() - this.flow.get(0).getTime();
		long initTime = 0;

		int i;
		for (i = 3; i < responder.length; i++) {
			MyPacket temp = this.flow.get(i);
			if (temp.hasData()) {
				initTime = this.flow.get(i).getTime();
				break;
			}
		}

		for (; i < flow.size(); i++) {
			MyPacket temp = this.flow.get(i);
			if (temp.getTime() - initTime < initRTT) {
				counter++;
			}
		}
		return counter;
	}

	
	
	public long getIW() {
		long IW = 0;
		int MSS = this.getSMSS();

		if (MSS > 2190){
			IW = 2 * MSS;
		}
		else if (MSS > 1095){
			IW = 3 * MSS;
		}
		else{
			IW = 4 * MSS;
		}

		return IW;
		// If SMSS > 2190 bytes:
		// IW = 2 * SMSS bytes and MUST NOT be more than 2 segments
		// If (SMSS > 1095 bytes) and (SMSS <= 2190 bytes):
		// IW = 3 * SMSS bytes and MUST NOT be more than 3 segments
		// If SMSS <= 1095 bytes:
		// IW = 4 * SMSS bytes and MUST NOT be more than 4 segments
	}
	
	public void initNumberOfPackets(){
		int SMSS = this.getSMSS();
		if (SMSS > 2190){
			this.numberOfPackets = 2;
		}
		else if (SMSS > 1095){
			this.numberOfPackets = 3;
		}
		else{
			this.numberOfPackets = 4;
		}
	}

	private void checkValidity() {
		boolean ans = true;
//		if( this.flow.)
		if (this.flow.size() < 10) {
			ans = false;
		} 
		// For checking 3way handshake TCP
		/*else {
			MyPacket first = this.flow.get(0);
			int t1 =0;
			try{
				t1 = first.getType()[0];
			} catch( NullPointerException e){
				this.setValid(false);
				return;
			}
			if (first.getType().length != 1 || t1 != 6) {
				ans = false;
			} else {
				MyPacket second = this.flow.get(1);
				int t2_1 = second.getType()[0];
				int t2_2 = second.getType()[1];
				if ((t2_1 == 3 && t2_2 == 6) || (t2_1 == 6 && t2_2 == 3)) {
					MyPacket third = this.flow.get(2);
					int t3 = third.getType()[0];
					if (third.getType().length != 1 || t3 != 3)
						ans = false;
				} else {
					ans = false;
				}
			}
		}*/

		this.setValid(ans);
	}

	private void findRetransmissions() {
		for (int i = 0; i < this.flow.size(); i++) {
			MyPacket packet1 = this.flow.get(i);
			for (int j = i + 1; j < this.flow.size(); j++) {
				MyPacket packet2 = this.flow.get(j);
				if (packet1.equals(packet2)) {
					packet1.setWillBeRetransmitted(true);
					packet2.setRetransmission(true);
					break;
				}
			}
		}
		this.detectRetransmissions = true;
	}

	public double getThroughput(boolean good) {
		if (good)
			if (!this.detectRetransmissions)
				this.findRetransmissions();

		long sum = 0;
		for (MyPacket myPacket : flow) {
			sum += myPacket.getTotalSize();
			if( good && myPacket.isRetransmission())
				sum -= myPacket.getTotalSize();
//			if (!good)
//				sum += myPacket.getTotalSize();
//			else if (!myPacket.isRetransmission())
//				sum += myPacket.getDataLen();
		}
		double time = (this.flow.get(this.flow.size() - 1).getTime() - this.flow.get(0).getTime()) ;
		time = time / 1000000.0;

		return (sum + 0.0) / time;
	}

	public int getSMSS() {
		// first is SYN, second is SYN ACK, HARD CODED
		int MSS1 = this.flow.get(0).getMMS();
		int MSS2 = this.flow.get(1).getMMS();

		return Math.min(MSS1, MSS2);
	}

	public double[] getRetransmissionValue() {
		double[] ans = new double[3];
		double SRTT;
		double RTTVAR;
		double RTO = 1;
		double alpha = 0.125, beta = 0.25;
		int k = 4;
		double G = 1000000;
		int j = 3;
		
		// calculate first RTT
		
		double R = this.flow.get(1).getTime() - this.flow.get(0).getTime(); 
		System.out.println("R: " + R);
		SRTT = R;
		RTTVAR = R / 2;
		RTO = SRTT + Math.max(G, k * RTTVAR);
		System.out.println("SRTT: " + SRTT);
		System.out.println("RTTVAR: " + RTTVAR);
		System.out.println("RTO: " + RTO);
		
		for (int i = 0; i < 3; i++) {
			
			MyPacket sender = this.flow.get(j);
			while(!sender.byteArrCompare(sender.getSourceIP(), this.starter)){
				j++;
				sender = this.flow.get(j);
				
			}
			j++;
			long senderNextAck = sender.getDataLen() + Long.parseLong(sender.toDecimal(sender.getSeqNum()));
			
			R = findRTT(j, sender, senderNextAck).getTime() - sender.getTime();
			System.out.println("R: " + R);
			RTTVAR = (1 - beta) * RTTVAR + beta * Math.abs(SRTT - R);
			SRTT = (1 - alpha) * SRTT + alpha * R;
			System.out.println("SRTT: " + SRTT);
			System.out.println("RTTVAR: " + RTTVAR);
			RTO = SRTT + Math.max(G, k * RTTVAR);
			if (RTO < 1)
				RTO = 1;
			System.out.println("RTO: " + RTO);
			ans[i] = RTO;
		}

		return ans;
	}
	
	
	public String fileOutput(){
		String ans = "";
		
//		MyPacket first = this.flow.get(0);
//		ans += first.toDecimal(first.getSourceIP());
		for (MyPacket myPacket : flow) {
			ans += "|   " ;
			if(myPacket.byteArrCompare(myPacket.getSourceIP(), this.starter))
				ans += "------";
			else
				ans += "<-----";
			
			ans += "SeqNumber: " + myPacket.toDecimal(myPacket.getSeqNum());
			ans += "  ,AckNumber: " + myPacket.toDecimal(myPacket.getAckNum());
			ans += "  ,WindowSize: " + myPacket.toDecimal(myPacket.getWindowSize());
			
			if(myPacket.byteArrCompare(myPacket.getSourceIP(), this.starter))
				ans += "----->";
			else
				ans += "------";
			
			ans +="|\n";
		}
		
		return ans;
	}


}
