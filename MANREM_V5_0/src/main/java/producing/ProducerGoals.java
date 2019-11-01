package producing;

import java.util.Map;

import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalCreationCondition;
import jadex.bdiv3.annotation.GoalParameter;
import jadex.bdiv3.annotation.GoalResult;

@Goal
public class ProducerGoals {

	@GoalParameter
	protected Map<String, String> paramHashmap;

	@GoalResult
	protected Map<String, String> resultHashmap;
	
	@GoalCreationCondition(beliefs="hashmap")
	public ProducerGoals(Map<String, String> paramHashmap) {
		this.paramHashmap = paramHashmap;
	}
	
}
