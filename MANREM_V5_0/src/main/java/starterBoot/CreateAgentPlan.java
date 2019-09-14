package starterBoot;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.SUtil;
import jadex.commons.future.ITuple2Future;

@Plan
public class CreateAgentPlan {
	 
	private IComponentManagementService cms;
	public String[] params;
	public String agentName;
	public String className;
	
	  public CreateAgentPlan(IComponentManagementService cms, String[] params)
	  {
		  this.cms = cms;
		  this.params = params;
		  this.agentName = params[0];
		  this.className = params[1];
	  }

	  @PlanBody
	  public void createAgent()
	  {
	    	/** Passing arguments to the agent **/
	    	CreationInfo parameters = new CreationInfo(SUtil.createHashMap(new String[]{"agentName", "className"}, new Object[]{agentName, className}));
	    	
	    	/** Starting the component **/
	    	ITuple2Future<IComponentIdentifier,java.util.Map<java.lang.String,java.lang.Object>> iTupleFut = this.cms.createComponent(agentName, "Trader.TraderBDI.class", parameters);
	    	IComponentIdentifier cid = iTupleFut.getFirstResult().getParent();
	    	System.out.println("Started component: " + cid + " - " + params[0]);
	  }
	
}
