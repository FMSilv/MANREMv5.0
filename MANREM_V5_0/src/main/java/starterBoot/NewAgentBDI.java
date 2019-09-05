package starterBoot;

import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;

@Agent
@Arguments
(value={
	@Argument(name="argumentTest", description="componentBDI.argumentTest", clazz=String.class, defaultvalue="\"defaultValue1\""), 
	@Argument(name="secondArgumentTest", description="componentBDI.secondArgumentTest", clazz=String.class, defaultvalue="\"defaultValue2\"")
})
public class NewAgentBDI {
	
	@AgentArgument
	protected String argumentTest;
	
	@AgentArgument
	protected String secondArgumentTest;
	
	@AgentCreated
	public void init() {
		System.out.println("argumentTest: "+argumentTest);
		System.out.println("secondArgumentTest: "+secondArgumentTest);
	}
	
}
