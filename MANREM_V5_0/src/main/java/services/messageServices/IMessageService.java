package services.messageServices;

import jadex.commons.future.IFuture;

public interface IMessageService {

	  public IFuture<String> sendMessage(String sender, String receiver, String message);
	
}
