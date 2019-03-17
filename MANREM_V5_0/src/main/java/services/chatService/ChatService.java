package services.chatService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.SwingUtilities;

import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceShutdown;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.future.SwingExceptionDelegationResultListener;

@Service
public class ChatService implements IChatService{

	@ServiceComponent
	IInternalAccess agent;
	@ServiceComponent
	IRequiredServicesFeature requiredServicesFeature;
	
	IClockService clock;
	DateFormat format;
	
	ChatGui gui;
	
	public void message(String sender, String text) {
//		agent.getComponentIdentifier().getName() -> Chat1@DESKTOP-7B836GA_40b
//		agent.getComponentIdentifier().getLocalName() -> Chat1
//		System.out.println(agent.getComponentIdentifier().getLocalName()+" received at \""+new Date(clock.getTime())+"\" from: " +sender+" message: "+text);

		if(sender.equals(agent.getComponentIdentifier().getLocalName()))
		{
			gui.addMessage(agent.getComponentIdentifier().getLocalName()+" <received> at \""+new Date(clock.getTime())+"\" <from>: " +sender+" <message>: "+text, true);
		}
		else
		{
			gui.addMessage(agent.getComponentIdentifier().getLocalName()+" <received> at \""+new Date(clock.getTime())+"\" <from>: " +sender+" <message>: "+text, false);
		}
	}
	
	@ServiceStart
	public IFuture<Void> startService() {
		final IExternalAccess exta = agent.getExternalAccess();
		format = new SimpleDateFormat("hh:mm:ss");
		final Future<Void> ret = new Future<Void>();
		IFuture<IClockService> clockservice = requiredServicesFeature.getRequiredService("clockservice");
		clockservice.addResultListener(new SwingExceptionDelegationResultListener<IClockService, Void>(ret)
		{
		    public void customResultAvailable(IClockService result)
		    {
		        clock = result;
		        if(exta.getArguments().get().get("chatOn").equals("1"))
		        {
			        gui = createGui(exta);
		        }
		        ret.setResult(null);
		    }
		});
		return ret;
	}

	protected ChatGui createGui(IExternalAccess agent) {
		return new ChatGui(agent);
	}
	
	@ServiceShutdown
	public void shutdownService () {
		Runnable doWorkRunnable = new Runnable() {
		    public void run()
		    {
		    	gui.dispose();
		    }
		};
		SwingUtilities.invokeLater(doWorkRunnable);
	}
	
}
