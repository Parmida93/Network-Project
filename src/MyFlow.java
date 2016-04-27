//import java.util.ArrayList;
//
//public class MyFlow {
//	private ArrayList<MyPacket> flowPackets;
//	private ArrayList<Boolean> validityOfFlowPackets;
//
//	private String client;
//	private String server;
//	
//	private boolean isFlow;
//	
//	private boolean isFinished;
//	
//	private ArrayList<RequestResponsePackets> reqResPacketsServer;
//	
//	private ArrayList<RequestResponsePackets> reqResPacketsTotal;
//	
//	private double averageRoundTripTime;
//	
//	private double initialRTT;
//	
//	private double throughput;
//	private double goodput;
//	
//	private double windowSizePacketsNo;
//
//	private long initialWindowSize;
//	private long SMSS;
//	
//	private ArrayList<RequestResponsePackets> reqResPacketsClient;
//
//	public MyFlow(String client, String server) {
//		this.flowPackets = new ArrayList<>();
//		this.reqResPacketsServer = new ArrayList<>();
//		this.reqResPacketsClient = new ArrayList<>();
//		this.reqResPacketsTotal = new ArrayList<>();
//		this.validityOfFlowPackets = new ArrayList<>();
//		this.client = client;
//		this.server = server;
//		this.setFlow(false);
//	}
//	
//	public void addPacketToFlow(MyPacket mypacket, boolean isCounted){
//		this.flowPackets.add(mypacket);
//		this.validityOfFlowPackets.add(isCounted);
//	}
//	
//	public ArrayList<MyPacket> getFlowPackets(){
//		return flowPackets;
//	}
//
//	public String getClient() {
//		return client;
//	}
//
//	public void setClient(String client) {
//		this.client = client;
//	}
//
//	public String getServer() {
//		return server;
//	}
//
//	public void setServer(String server) {
//		this.server = server;
//	}
//	
//	public boolean isFlow() {
//		return isFlow;
//	}
//
//	public void setFlow(boolean isFlow) {
//		this.isFlow = isFlow;
//	}
//
//	public boolean isFinished() {
//		return isFinished;
//	}
//
//	public void setFinished(boolean isFinished) {
//		this.isFinished = isFinished;
//	}
//
//	public ArrayList<RequestResponsePackets> getReqResPackets() {
//		return reqResPacketsServer;
//	}
//
//	public void setReqResPackets(ArrayList<RequestResponsePackets> reqResPackets) {
//		this.reqResPacketsServer = reqResPackets;
//	}
//
//	public void addReqRes() {
//		ArrayList<Boolean> copy = validityOfFlowPackets;
//		for (int i = 3; i < flowPackets.size(); i++) {
//			MyPacket myPacket = flowPackets.get(i);
//			boolean flag = false;
//			for (int j = 3; j < i; j++) {
//				MyPacket temp = flowPackets.get(j);
//				if(temp.getSourceIP().equals(server.split("-")[0])){					
//					if(temp.isRetransmitted() == true){
//						RequestResponsePackets reqres = new RequestResponsePackets(temp, null);
//						reqres.setDropped(true);
//						reqResPacketsServer.add(reqres);
//						reqResPacketsTotal.add(reqres);
//					}
//					if(validityOfFlowPackets.get(j) == true && (myPacket.getAcknowledgementNumber() == (temp.getSequenceNumber() + temp.getDataLength()))){
//						if(myPacket.getSourceIP().equals(temp.getSourceIP())){
//							flag = true;
//							break;
//						}
//						RequestResponsePackets reqres = new RequestResponsePackets(temp, myPacket);
//						reqres.setRoundTripTime(myPacket.getTime() - temp.getTime());
//						reqResPacketsServer.add(reqres);
//						reqResPacketsTotal.add(reqres);
//						validityOfFlowPackets.set(j, false);
//						validityOfFlowPackets.set(i, false);
//						flag = true;
//						break;
//					}
//				}
//				else{
//					if(temp.isRetransmitted() == true){
//						RequestResponsePackets reqres = new RequestResponsePackets(temp, null);
//						reqres.setDropped(true);
//						reqResPacketsClient.add(reqres);
//						reqResPacketsTotal.add(reqres);
//					}
//					if(validityOfFlowPackets.get(j) == true && (myPacket.getAcknowledgementNumber() == (temp.getSequenceNumber() + temp.getDataLength()))){
//						if(myPacket.getSourceIP().equals(temp.getSourceIP())){
//							flag = true;
//							break;
//						}
//						RequestResponsePackets reqres = new RequestResponsePackets(temp, myPacket);
//						reqres.setRoundTripTime(myPacket.getTime() - temp.getTime());
//						reqResPacketsClient.add(reqres);
//						reqResPacketsTotal.add(reqres);
//						validityOfFlowPackets.set(j, false);
//						validityOfFlowPackets.set(i, false);
//						flag = true;
//						break;
//					}
//				}
//			}
////			if(flag == false){
////				for (int j = 3; j < i; j++) {
////					MyPacket temp = flowPackets.get(j);
////					if(temp.getSourceIP().equals(server.split("-")[0])){					
////						if(temp.isRetransmitted() == true){
////							RequestResponsePackets reqres = new RequestResponsePackets(temp, null);
////							reqres.setDropped(true);
////							reqResPacketsServer.add(reqres);
////							reqResPacketsTotal.add(reqres);
////						}
////						if(validityOfFlowPackets.get(j) == true && (myPacket.getAcknowledgementNumber() >= (temp.getSequenceNumber() + temp.getDataLength()))){
////							if(myPacket.getSourceIP().equals(temp.getSourceIP()))
////								break;
////							RequestResponsePackets reqres = new RequestResponsePackets(temp, myPacket);
////							reqres.setRoundTripTime(myPacket.getTime() - temp.getTime());
////							reqResPacketsServer.add(reqres);
////							reqResPacketsTotal.add(reqres);
////							validityOfFlowPackets.set(j, false);
////							validityOfFlowPackets.set(i, false);
////						}
////					}
////					else{
////						if(temp.isRetransmitted() == true){
////							RequestResponsePackets reqres = new RequestResponsePackets(temp, null);
////							reqres.setDropped(true);
////							reqResPacketsClient.add(reqres);
////							reqResPacketsTotal.add(reqres);
////						}
////						if(validityOfFlowPackets.get(j) == true && (myPacket.getAcknowledgementNumber() >= (temp.getSequenceNumber() + temp.getDataLength()))){
////							if(myPacket.getSourceIP().equals(temp.getSourceIP()))
////								break;
////							RequestResponsePackets reqres = new RequestResponsePackets(temp, myPacket);
////							reqres.setRoundTripTime(myPacket.getTime() - temp.getTime());
////							reqResPacketsClient.add(reqres);
////							reqResPacketsTotal.add(reqres);
////							validityOfFlowPackets.set(j, false);
////							validityOfFlowPackets.set(i, false);
////						}
////					}
////				}
////			}
//		}
//		validityOfFlowPackets = copy;
//	}
//	
////	public void totalReqRes() {
////		ArrayList<Boolean> copy = validityOfFlowPackets;
////		for (int i = 3; i < flowPackets.size(); i++) {
////			MyPacket myPacket = flowPackets.get(i);
////			for (int j = 3; j < i; j++) {
////				MyPacket temp = flowPackets.get(j);
////				if(temp.isRetransmitted() == true){
////					RequestResponsePackets reqres = new RequestResponsePackets(temp, null);
////					reqres.setDropped(true);
////					reqResPacketsTotal.add(reqres);
////				}
////				if(validityOfFlowPackets.get(j) == true && (myPacket.getAcknowledgementNumber() == (temp.getSequenceNumber() + temp.getDataLength()))){
////					if(myPacket.getSourceIP() == temp.getSourceIP())
////						break;
////					RequestResponsePackets reqres = new RequestResponsePackets(temp, myPacket);
////					reqres.setRoundTripTime(myPacket.getTime() - temp.getTime());
////					reqResPacketsTotal.add(reqres);
////					validityOfFlowPackets.set(j, false);
////					validityOfFlowPackets.set(i, false);
////					break;
////				}
////			}
////		}
////
////		validityOfFlowPackets = copy;
////	}
//
//
//	public void checkFlow() {
//		for (int i = 0; i < flowPackets.size(); i++) {
//			if(flowPackets.get(1).getSYN() == 1 && flowPackets.get(1).getACK() == 1
//					&& flowPackets.get(2).getSequenceNumber() == flowPackets.get(1).getAcknowledgementNumber()){
//				isFlow = true;
//				setInitialRTT(flowPackets.get(1).getTime() - flowPackets.get(0).getTime());
//			}
//		}
//	}
//
//	public double calculatAverageRoundTripTime() {
//		double sum = 0;
//		double size = 0;
//		for (int i = 0; i < reqResPacketsServer.size(); i++) {
//			if(reqResPacketsServer.get(i).getMyPacket2() != null){
//				sum += reqResPacketsServer.get(i).getRoundTripTime();
//				size++;
//			}
//		}
//		this.averageRoundTripTime = (sum / size);
//		return this.averageRoundTripTime;
//	}
//	
//	
//	public void calculateThroughputGoodput(){
//		int sum = 0;
//		int sum2 = 0;
//		for (int i = 0; i < flowPackets.size(); i++) {
//			MyPacket temp = flowPackets.get(i);
//			if(!temp.isRetransmitted()){
//				sum2 += temp.getTotalLength() + 14;
//			}
//			sum += temp.getTotalLength() + 14;
//		}
//		
//		this.throughput = sum / (flowPackets.get(flowPackets.size() - 1).getTime() - flowPackets.get(0).getTime());
//		this.goodput = sum2 / (flowPackets.get(flowPackets.size() - 1).getTime() - flowPackets.get(0).getTime());
//	}
//	
//	public long calculateInitialWindowSize(){
//		long synMSS = flowPackets.get(0).getMSS();
//		System.out.println("SYNMSS: " + synMSS);
//		long synAckMSS = flowPackets.get(1).getMSS();
//		System.out.println("SYNACKMSS: " + synAckMSS);
//		SMSS = Math.min(synMSS, synAckMSS);
//		initialWindowSize = SMSS;
//		if (SMSS > 2190){
//			initialWindowSize = 2 * SMSS;
//			windowSizePacketsNo = 2;
//		}
//		else if(SMSS > 1095){
//			initialWindowSize = 3 * SMSS;
//			windowSizePacketsNo = 3;
//		}
//		else if(SMSS <= 1095){
//			initialWindowSize = 4 * SMSS;
//			windowSizePacketsNo = 4;
//		}
//		return initialWindowSize;
////			IW = 2 * SMSS bytes and MUST NOT be more than 2 segments
////			If (SMSS > 1095 bytes) and (SMSS <= 2190 bytes):
////			IW = 3 * SMSS bytes and MUST NOT be more than 3 segments
////			If SMSS <= 1095 bytes:
////			IW = 4 * SMSS bytes and MUST NOT be more than 4 segments
//	}
//	
////	public void computeCongestionWindowSize(){
////		int i = 0;
////		boolean flag = false;
////		System.out.println("SMSS: " + SMSS);
////		System.out.println(i + "  " + windowSizePacketsNo);
////		int counter = 0;
////		while(counter <= 20 && i < reqResPackets.size()) {
////			RequestResponsePackets reqRes = reqResPackets.get(i);
////			String t1 = server.split("-")[0].trim();
////			String t2 = reqRes.getMyPacket1().getSourceIP().trim();
////			i++;
////			if(t1.equals(t2)){
////				if(reqRes.isDropped() == true){
////					windowSizePacketsNo /= 2;
////					flag = true;
////				}
////				else{
////					counter++;
////					if(windowSizePacketsNo * SMSS >= 26000 || flag == true){
////						windowSizePacketsNo += (1 / windowSizePacketsNo);
////					}
////					else{
////						windowSizePacketsNo++;				
////					}
////				}
////				System.out.println(i + "  " + windowSizePacketsNo);				
////			}
////			
////		}
////	}
//	
//	public int[] computeCongestionWindowSize(){
//		int j = 1;
//		int count[] = new int[20];
//		int k;
//		for (k = 0; k < flowPackets.size(); k++) {
//			MyPacket temp = flowPackets.get(k);
//			if(temp.getSourceIP().equals(server.split("-")[0]) && temp.getDataLength() != 0){
//				break;
//			}
//		}
//		this.averageRoundTripTime = calculatAverageRoundTripTime();
//		double time = flowPackets.get(k).getTime() + averageRoundTripTime;
//		while(j < 20 && k < flowPackets.size()) {
//			MyPacket temp = flowPackets.get(k);
//			if(temp.getTime() <= time){
//				if(temp.getSourceIP().equals(server.split("-")[0]) && temp.getDataLength() != 0){
//					count[j]++;
//				}
//			}
//			else{
//				time = time + averageRoundTripTime;
//				j++;
//			}
//			k++;
//		}
//		return count;
//	}
//	
//	public double getWindowSizePacketsNo() {
//		return windowSizePacketsNo;
//	}
//
//	public void setWindowSizePacketsNo(double windowSizePacketsNo) {
//		this.windowSizePacketsNo = windowSizePacketsNo;
//	}
//	
//	public double getAverageRoundTripTime() {
//		return averageRoundTripTime;
//	}
//
//	public void setAverageRoundTripTime(double averageRoundTripTime) {
//		this.averageRoundTripTime = averageRoundTripTime;
//	}
//	
//	public ArrayList<Boolean> getValidityOfFlowPackets() {
//		return validityOfFlowPackets;
//	}
//
//	public double getThroughput() {
//		return throughput;
//	}
//
//	public void setThroughput(double throughput) {
//		this.throughput = throughput;
//	}
//
//	public double getGoodput() {
//		return goodput;
//	}
//
//	public void setGoodput(double goodput) {
//		this.goodput = goodput;
//	}
//
//	public double getInitialRTT() {
//		this.initialRTT = flowPackets.get(1).getTime() - flowPackets.get(0).getTime();
//		return initialRTT;
//	}
//
//	public void setInitialRTT(double initialRTT) {
//		this.initialRTT = initialRTT;
//	}
//	
//	
//	public ArrayList<RequestResponsePackets> getReqResPacketsClient() {
//		return reqResPacketsClient;
//	}
//
//	public void setReqResPacketsClient(ArrayList<RequestResponsePackets> reqResPacketsClient) {
//		this.reqResPacketsClient = reqResPacketsClient;
//	}
//
//	public ArrayList<RequestResponsePackets> getReqResPacketsTotal() {
//		return reqResPacketsTotal;
//	}
//
//	public void setReqResPacketsTotal(ArrayList<RequestResponsePackets> reqResPacketsTotal) {
//		this.reqResPacketsTotal = reqResPacketsTotal;
//	}
//
//}
