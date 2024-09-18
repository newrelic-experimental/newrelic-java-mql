package matrix.db;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TracedMethod;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave
public abstract class MQLCommand {

	@Trace(dispatcher = true)
	public boolean executeCommand(Context context, String command, String[] args) {
		TracedMethod traced = NewRelic.getAgent().getTracedMethod();
		
		traced.addCustomAttribute("MQL-Command", command);
		
		boolean result = Weaver.callOriginal();
		traced.addCustomAttribute("Execute-Result", result);
		
		return result;
		
	}
	
	@Trace
	public boolean executeCommand(Context context,boolean b1, boolean b2, boolean b3, String command, String[] args) {
		TracedMethod traced = NewRelic.getAgent().getTracedMethod();
		
		traced.addCustomAttribute("MQL-Command", command);
		
		boolean result = Weaver.callOriginal();
		traced.addCustomAttribute("Execute-Result", result);
		
		return result;
		
	}

}
