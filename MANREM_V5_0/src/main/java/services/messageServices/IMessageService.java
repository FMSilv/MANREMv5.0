package services.messageServices;

import java.util.ArrayList;

import jadex.commons.future.IFuture;
import wholesalemarket_SMP.AgentData;

public interface IMessageService {

	  public IFuture<String> sendMessage(String sender, String receiver, String messageContent, String ontology, String protocol, String performative);
	  
	  public IFuture<String> sendMessage(String sender, String receiver, String messageContent);
	
	  public IFuture<String> sendSimulationInfo(String sender, String receiver, String simulationFlag, String[] buyerNames, String[] sellerNames, ArrayList<AgentData> buyers, ArrayList<AgentData> sellers);
	  
}
