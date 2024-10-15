package com.newrelic.instrumentation.labs.mql;

import java.util.Map;
import java.util.logging.Level;

import com.newrelic.agent.config.AgentConfig;
import com.newrelic.agent.config.AgentConfigListener;
import com.newrelic.agent.config.ConfigService;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.api.agent.Config;
import com.newrelic.api.agent.NewRelic;

import matrix.db.Context;
import matrix.util.MatrixException;

public class MQLUtils implements AgentConfigListener {

	public static boolean recordRaw;
	
	static {
		Config config = NewRelic.getAgent().getConfig();
		if(config instanceof AgentConfig) {
			AgentConfig aConfig = (AgentConfig)config;
			String recordSQL = aConfig.getTransactionTracerConfig().getRecordSql();
			if(recordSQL != null) {
				if(recordSQL.equalsIgnoreCase("raw")) {
					recordRaw = true;
				} else {
					recordRaw = false;
				}
			} else {
				recordRaw = false;
			}
		} else {
			recordRaw = false;
		}
		NewRelic.getAgent().getLogger().log(Level.INFO, "New Relic {0} collect raw MQL queries", recordRaw ? "will" : "will not");
		ConfigService configService = ServiceFactory.getConfigService();
		if(configService != null) {
			configService.addIAgentConfigListener(new MQLUtils());
			NewRelic.getAgent().getLogger().log(Level.INFO, "Will listen for changes to sql_record", recordRaw ? "will" : "will not");
		}
	}
	
	public static String getCommandWithArgs(String command, String[] args) {
		
		if(command == null) return command;
		
		int numOfArgs = args != null ? args.length : 0;
		
		if(numOfArgs == 0) return command;
		
		String tmp = command;
		
		for(int i = 0; i < numOfArgs; i++) {
			int j = i + 1;
			tmp = tmp.replace("$" + j, args[i]);
		}
		
		return tmp;
	}
	
	public static void addContext(Map<String,Object> attributes, Context context) {
		if(context != null) {
			addAttribute(attributes, "Context-Role", context.getRole());
			addAttribute(attributes, "Context-User", context.getUser());
			addAttribute(attributes, "Context-Protocol", context.getProtocol());

			try {
				addAttribute(attributes, "Context-Application", context.getApplication());
			} catch (MatrixException e) {
				NewRelic.recordMetric("MPLQuery/Context/Application/Error", 1.0F);
			}
			
		}
		
	}
	
	private static void addAttribute(Map<String, Object> attributes, String key, Object value) {
		if(attributes != null && key != null && !key.isEmpty() && value != null) {
			attributes.put(key, value);
		}
	}

	@Override
	public void configChanged(String appName, AgentConfig agentConfig) {
		String recordSQL = agentConfig.getTransactionTracerConfig().getRecordSql();
		boolean isRaw;
		if(recordSQL != null) {
			if(recordSQL.equalsIgnoreCase("raw")) {
				isRaw = true;
			} else {
				isRaw = false;
			}
		} else {
			isRaw = false;
		}
		if(isRaw != recordRaw) {
			NewRelic.getAgent().getLogger().log(Level.INFO, "Changing whether to collect raw MQL from {0} to {1}" , recordRaw, isRaw);
			recordRaw = isRaw;
		} else {
			NewRelic.getAgent().getLogger().log(Level.INFO, "No change needed for current value for collecting raw MQL: {0}" , recordRaw);
		}
	}

	
	
}
