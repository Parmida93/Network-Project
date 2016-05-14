import java.util.ArrayList;

public class Statistics {
	
	ArrayList<MyPacket> myPackets;
	
	private double totalAve = -1, dataAve = -1, headerAve = -1, throughput = -1;
	
	public double getTotalAve(){
		if( totalAve == -1)
			this.setAverages();
		return totalAve;
	}
	
	public double getDataAve(){
		if( dataAve == -1)
			this.setAverages();
		return dataAve;
	}
	public double getHeaderAve() {
		if( headerAve == -1)
			this.setAverages();
		return headerAve;
	}
	
	public double getThroughput(){
		setThroughput();
		return this.throughput;
	}
	
	public Statistics(ArrayList<MyPacket> pS){
		this.myPackets = pS;
	}
	private void setAverages(){
		long total_sum = 0, data_sum = 0, header_sum = 0;
//		int total_count = 0, data_count =0;
		
		for (MyPacket myPacket : myPackets) {
			total_sum += myPacket.getTotalSize();
			data_sum += myPacket.getDataLen();
			header_sum += myPacket.getHeaderSize();
		}
		this.totalAve = total_sum / myPackets.size();
		this.dataAve = data_sum / myPackets.size();
		this.headerAve = header_sum / myPackets.size();
		
	}
	
	private void setThroughput(){
		System.out.println(myPackets.get(0).getTime());
		System.out.println(myPackets.get(myPackets.size()-1).getTime() - myPackets.get(0).getTime());
		this.throughput = Math.pow(10.0, 6) * (myPackets.size()+0.0) / (myPackets.get(myPackets.size()-1).getTime() - myPackets.get(0).getTime());
	}

}
