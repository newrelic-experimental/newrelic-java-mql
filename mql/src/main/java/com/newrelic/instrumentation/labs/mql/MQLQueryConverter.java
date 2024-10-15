package com.newrelic.instrumentation.labs.mql;

import com.newrelic.api.agent.QueryConverter;

public class MQLQueryConverter implements QueryConverter<String> {
	
	private String rawQuery;
	private String query;
	
	public MQLQueryConverter(String command, String[] args) {
		query = command;
		rawQuery = MQLUtils.getCommandWithArgs(command, args);
	}

	@Override
	public String toRawQueryString(String rq) {
		return rawQuery;
	}

	@Override
	public String toObfuscatedQueryString(String rq) {
		return query;
	}

}
