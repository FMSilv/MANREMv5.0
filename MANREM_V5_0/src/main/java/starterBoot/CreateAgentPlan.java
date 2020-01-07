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
	public String isLoaded;
	
	  public CreateAgentPlan(IComponentManagementService cms, String[] params, String isLoaded)
	  {
		  this.cms = cms;
		  this.params = params;
		  this.agentName = params[0];
		  this.className = params[1];
		  this.isLoaded = isLoaded;
	  }

	  @PlanBody
	  public void createAgent()
	  {
	    	/** Passing arguments to the agent **/
	    	CreationInfo parameters = new CreationInfo(SUtil.createHashMap(new String[]{"agentName", "className", "isLoaded"}, new Object[]{agentName, className, isLoaded}));
	    	
	    	/** Starting the component **/
	    	ITuple2Future<IComponentIdentifier,java.util.Map<java.lang.String,java.lang.Object>> iTupleFut = this.cms.createComponent(agentName, className+".class", parameters);
	    	IComponentIdentifier cid = iTupleFut.getFirstResult().getParent();
	    	System.out.println("Started component: " + cid + " - " + params[0]);
	  }
	
}
