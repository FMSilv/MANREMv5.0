package services.messageServices;

import jadex.commons.future.IFuture;

public interface IMessageService {

	  public IFuture<String> sendMessage(String sender, String receiver, String messageContent, String ontology, String protocol, String performative);
	  
	  public IFuture<String> sendMessage(String sender, String receiver, String messageContent);
	
}
