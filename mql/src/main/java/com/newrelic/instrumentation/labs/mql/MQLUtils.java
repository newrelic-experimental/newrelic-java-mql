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

	private static final String MQL_REPORTING = "MQL.Reporting.enabled";
	private static final String MQL_REPORTING_TYPE = "MQL.Reporting.type";
	
	private enum REPORTINGTYPE  {
		OFF, 
		RAW, 
		OBFUSCATED
	};
	
	private static REPORTINGTYPE MQL_reportType = REPORTINGTYPE.OBFUSCATED;
	private static REPORTINGTYPE SQL_reportType = REPORTINGTYPE.OBFUSCATED;
	private static boolean enabled = false;
	
	static {
		Config config = NewRelic.getAgent().getConfig();
		if(config instanceof AgentConfig) {
			AgentConfig aConfig = (AgentConfig)config;
			
			String recordSQL = aConfig.getTransactionTracerConfig().getRecordSql();
			SQL_reportType = REPORTINGTYPE.valueOf(recordSQL.toUpperCase());
		} else {
			SQL_reportType = REPORTINGTYPE.OBFUSCATED;
		}
		NewRelic.getAgent().getLogger().log(Level.INFO, "MQL queries will be reported as {0} in db.statement Span", SQL_reportType);
		ConfigService configService = ServiceFactory.getConfigService();
		if(configService != null) {
			configService.addIAgentConfigListener(new MQLUtils());
			NewRelic.getAgent().getLogger().log(Level.INFO, "Will listen for changes to the agent configuration");
		}
		Boolean b = config.getValue(MQL_REPORTING, Boolean.FALSE);
		if(b != enabled) {
			enabled = b;
		}
		NewRelic.getAgent().getLogger().log(Level.INFO, "MQL Reporting is {0}", enabled ? "enabled" : "disabled");
		
		// need get as Object because off gets parsed as a Boolean
		Object typeObject = config.getValue(MQL_REPORTING_TYPE);
		String typeString;
		if(typeObject instanceof Boolean) {
			// set to off
			typeString = "off";
		} else {
			typeString = typeObject.toString();
		}
		if(typeString != null) {
			MQL_reportType = REPORTINGTYPE.valueOf(typeString.toUpperCase());
		} else if(MQL_reportType != REPORTINGTYPE.OBFUSCATED) {
			MQL_reportType = REPORTINGTYPE.OBFUSCATED;
		}
		NewRelic.getAgent().getLogger().log(Level.INFO, "MQL Reporting type is {0}", MQL_reportType);
	}

	public static String getCommandToReport(String command) {
		/*
		 * will return null if report_sql is off and MQL reporting is not enabled or enabled and set to off
		 */
		if(SQL_reportType == REPORTINGTYPE.OFF) {
			if(enabled && MQL_reportType == REPORTINGTYPE.OFF) {
				return null;
			} else if(!enabled) {
				return null;
			}
		}
		
		return command;
	}
	
	public static String getCommandWithArgs(String command, String[] args) {
		
		if(command == null) return command;
		
		if(SQL_reportType == REPORTINGTYPE.OBFUSCATED || SQL_reportType == REPORTINGTYPE.OFF) {
			// if record_sql is set to off or obfuscated then only report if MQL reporting is enabled and set to raw
			if(enabled) {
				if(MQL_reportType != REPORTINGTYPE.RAW) {
					return null;
				}
			} else {
				return null;
			}
		}
		
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
		if(recordSQL != null) {
			SQL_reportType = REPORTINGTYPE.valueOf(recordSQL.toUpperCase());
		} else {
			SQL_reportType = REPORTINGTYPE.OBFUSCATED;
		}
		NewRelic.getAgent().getLogger().log(Level.INFO, "MQL queries will be reported as {0} in db.statement Span", SQL_reportType);

		Boolean b = agentConfig.getValue(MQL_REPORTING, Boolean.FALSE);
		if(b != enabled) {
			enabled = b;
		}
		NewRelic.getAgent().getLogger().log(Level.INFO, "MQL Reporting is {0}", enabled ? "enabled" : "disabled");
		
		// need get as Object because off gets parsed as a Boolean
		Object typeObject = agentConfig.getValue(MQL_REPORTING_TYPE);
		String typeString;
		if(typeObject instanceof Boolean) {
			// set to off
			typeString = "off";
		} else {
			typeString = typeObject.toString();
		}
		if(typeString != null) {
			MQL_reportType = REPORTINGTYPE.valueOf(typeString.toUpperCase());
		} else if(MQL_reportType != REPORTINGTYPE.OBFUSCATED) {
			MQL_reportType = REPORTINGTYPE.OBFUSCATED;
		}
		NewRelic.getAgent().getLogger().log(Level.INFO, "MQL Reporting type is {0}", MQL_reportType);
	}

	
	
}
