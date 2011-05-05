package org.servalproject.dna;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class PeerConversation {
	static class Id{
		long transId;
		SocketAddress addr;
		Id(long transId, SocketAddress addr){
			this.transId=transId;
			this.addr=addr;
		}
		
		@Override
		public int hashCode() {
			return (int) transId ^ addr.hashCode();
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof Id){
				Id x = (Id)o;
				return x.transId==this.transId && x.addr.equals(this.addr);
			}
			return false;
		}
	}
	
	Id id;
	Packet packet;
	boolean responseReceived=false;
	boolean conversationComplete=false;
	int retryCount=0;
	OpVisitor vis;
	
	PeerConversation(Packet packet, InetAddress addr, OpVisitor vis){
		this(packet,new InetSocketAddress(addr,Packet.dnaPort),vis);
	}
	
	PeerConversation(Packet packet, SocketAddress addr, OpVisitor vis){
		this.id=new Id(packet.transactionId, addr);
		this.packet=packet;
		this.vis=vis;
	}
	
	void processResponse(Packet p){
		responseReceived=true;
		if (vis == null){
			conversationComplete=true;
			return;
		}
		for (Operation o:p.operations){
			if (o.visit(p, vis))
				conversationComplete=true;
		}
	}
}
