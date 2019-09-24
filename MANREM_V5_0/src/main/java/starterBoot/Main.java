package starterBoot;

import jadex.base.PlatformConfiguration;
import jadex.base.RootComponentConfiguration;
import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.SUtil;
import jadex.commons.future.IFuture;
import jadex.commons.future.ITuple2Future;

public class Main {

    public static void main(String[] args) {
        
    	/** Plataform configuring **/
    	PlatformConfiguration platformConfig;
    	if(args[0].equals("1"))
    	{
    		platformConfig = PlatformConfiguration.getDefault();
    	}
    	else
    	{
    		platformConfig = PlatformConfiguration.getDefaultNoGui();
    	}
    	platformConfig.setDebugFutures(true); // enables stacktraces of exceptions
    	platformConfig.setPlatformName("LocalJadexPlatform"); // set plataform name
    	RootComponentConfiguration rootConfig = platformConfig.getRootConfig();
    	if(args[0].equals("1"))
    	{
    		rootConfig.setGui(true); 		// run with/without GUI
    	}
    	rootConfig.setCli(true); 		// run with/without CLI
    	rootConfig.setLogging(true); 	// enables the printing of info and warning messages in addition to severe messages.
    	rootConfig.setWelcome(false); 	// do not print welcome message on startup
    	rootConfig.setPrintPass(false); // do not print password message on startup
    	// set available component kernels
    	rootConfig.setKernels(RootComponentConfiguration.KERNEL.micro,
                RootComponentConfiguration.KERNEL.component,
                RootComponentConfiguration.KERNEL.v3);
    	rootConfig.setAwareness(false); // disable plataform awareness
    	// set awareness mechanisms
    	rootConfig.setAwaMechanisms(RootComponentConfiguration.AWAMECHANISM.broadcast, 
    		    RootComponentConfiguration.AWAMECHANISM.relay);
    	rootConfig.setUsePass(false); // disable password protection (Caution!)
    	
    	IExternalAccess platform = Starter.createPlatform(platformConfig).get();
//    	Starter.createPlatform(platformConfig).get();
    	
    	/** Obtaining the CMS **/
    	IFuture<IComponentManagementService> fut = SServiceProvider.getService(platform, IComponentManagementService.class);
    	IComponentManagementService cms = fut.get();
        
    	/** Passing arguments to the agent **/
    	CreationInfo parameters = new CreationInfo(SUtil.createHashMap(new String[]{"chatOn"}, new Object[]{args[1]}));
    	
    	/** Starting the components **/
    	// ExternalAssistantBDI
    	ITuple2Future<IComponentIdentifier,java.util.Map<java.lang.String,java.lang.Object>> iTupleFutA1 = cms.createComponent("ExternalAssistantBDIAgent", "externalassistant.ExternalAssistantBDI.class", parameters);
    	IComponentIdentifier cidA1 = iTupleFutA1.getFirstResult();
    	System.out.println("Started component: " + cidA1);
    	// PersonalAssistant
    	ITuple2Future<IComponentIdentifier,java.util.Map<java.lang.String,java.lang.Object>> iTupleFutA2 = cms.createComponent("PersonalAssistantBDIAgent", "personalassistant.PersonalAssistantBDI.class", parameters);
    	IComponentIdentifier cidA2 = iTupleFutA2.getFirstResult();
    	System.out.println("Started component: " + cidA2);
    	// MarketOperator
    	ITuple2Future<IComponentIdentifier,java.util.Map<java.lang.String,java.lang.Object>> iTupleFutA3 = cms.createComponent("MarketOperatorBDIAgent", "marketoperator.MarketOperatorBDI.class", parameters);
    	IComponentIdentifier cidA3 = iTupleFutA3.getFirstResult();
    	System.out.println("Started component: " + cidA3);
    	// DayaheadinterfaceBDI
//    	ITuple2Future<IComponentIdentifier,java.util.Map<java.lang.String,java.lang.Object>> iTupleFutA4 = cms.createComponent("DayaheadinterfaceBDIAgent", "DayAheadInterface.DayaheadinterfaceBDI.class", parameters);
//    	IComponentIdentifier cidA4 = iTupleFutA4.getFirstResult();
//    	System.out.println("Started component: " + cidA4);
    	// DataStorageAssistantBDI
    	CreationInfo parametersDataStorageAgent = new CreationInfo(SUtil.createHashMap(new String[]{"chatOn"}, new Object[]{"0"}));
    	ITuple2Future<IComponentIdentifier,java.util.Map<java.lang.String,java.lang.Object>> iTupleFutA5 = cms.createComponent("DataStorageAssistantBDIAgent", "dataStorageAssistant.DataStorageAssistantBDI.class", parametersDataStorageAgent);
    	IComponentIdentifier cid5 = iTupleFutA5.getFirstResult();
    	System.out.println("Started component: " + cid5);
    	
    	/** Call this to destroy components **/
//    	Map<String,Object> results = cms.destroyComponent(cid).get();	
    }
	
}
