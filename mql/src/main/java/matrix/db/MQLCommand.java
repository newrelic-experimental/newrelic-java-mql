package matrix.db;

import com.newrelic.api.agent.DatastoreParameters;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TracedMethod;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.instrumentation.labs.mql.MQLCommands;
import com.newrelic.instrumentation.labs.mql.MQLUtils;

@Weave
public abstract class MQLCommand {

	@Trace(dispatcher = true)
	public boolean executeCommand(Context context, String command, String[] args) {
		TracedMethod traced = NewRelic.getAgent().getTracedMethod();
		
		String withArgs;
		if(MQLUtils.recordRaw) {
			withArgs = MQLUtils.getCommandWithArgs(command, args);
		} else {
			withArgs = "Cannot populate since record_sql is not set to raw";
		}
		traced.addCustomAttribute("MQL-CommandWithArgs", withArgs != null ? withArgs : "Unable to populate");
		
		
		traced.addCustomAttribute("MQL-Command", command);
		DatastoreParameters params = MQLCommands.getDatastoreParams(command, args);
		if(params != null) {
			NewRelic.getAgent().getTracedMethod().reportAsExternal(params);
		}
		
		
		boolean result = Weaver.callOriginal();
		traced.addCustomAttribute("Execute-Result", result);
		
		return result;
		
	}
	
	@Trace
	public boolean executeCommand(Context context,boolean b1, boolean b2, boolean b3, String command, String[] args) {
		TracedMethod traced = NewRelic.getAgent().getTracedMethod();

		String withArgs;
		if(MQLUtils.recordRaw) {
			withArgs = MQLUtils.getCommandWithArgs(command, args);
		} else {
			withArgs = "Cannot populate since record_sql is not set to raw";
		}
		traced.addCustomAttribute("MQL-CommandWithArgs", withArgs != null ? withArgs : "Unable to populate");
		traced.addCustomAttribute("MQL-Command", command);
		
		DatastoreParameters params = MQLCommands.getDatastoreParams(command, args);
		if(params != null) {
			NewRelic.getAgent().getTracedMethod().reportAsExternal(params);
		}
		
		boolean result = Weaver.callOriginal();
		traced.addCustomAttribute("Execute-Result", result);
		
		return result;
		
	}

}
