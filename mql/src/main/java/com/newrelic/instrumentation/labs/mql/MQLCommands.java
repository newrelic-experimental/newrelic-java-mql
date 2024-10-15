package com.newrelic.instrumentation.labs.mql;

import com.newrelic.api.agent.DatastoreParameters;

public class MQLCommands {

	private static DatastoreParameters getParameters(String command, String collection, String query, MQLQueryConverter converter) {
		DatastoreParameters params = DatastoreParameters.product("MQL").collection(collection).operation(command).noInstance().noDatabaseName().slowQuery(query, converter).build();
		
		return params;
	}
	
	public static DatastoreParameters getDatastoreParams(String query, String[] args) {
		
		String[] splits = query.split(" ");
		MQLQueryConverter converter = new MQLQueryConverter(query, args);
		
		String command = splits[0];
		int paramsToUse = getParamsToUse(command);

		if(paramsToUse == 1) {
			boolean useArg = splits[1].startsWith("$");
			if(useArg) {
				return getParameters(command, args[0], query, converter);
			}
			return getParameters(command, splits[1], query, converter);
		}
		if(paramsToUse == 2) {
			return getParameters(command, splits[1] + " " + splits[2], query, converter);
		}
		
		
		
		return null;
	}
	
	private static int getParamsToUse(String command) {
		if(command.equals("check") || command.equals("temp") || command.equals("temporary")) return 2;
		return 1;
	}
}
