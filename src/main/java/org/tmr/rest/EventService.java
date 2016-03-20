package org.tmr.rest;

import javax.ws.rs.core.Response;

public interface EventService {
	
	public Response getAllIcmEvents();
	
	public Response getAllIumEvents();
	
	public Response getAllIcmEventsByUrn( String id );
	
	public Response getAllIcmEventsByMsgId( String id );
	
	public Response getAllIumEventsByUrn( String id );
	
	public Response getAllIumEventsByMsgId( String id );
	
	public Response getAllEvents();
	
	public Response getEvents( String id );
	
	public Response mongoTest();
	
	public Response getCfServices();
	
	public Response amqpTest();
	
	public void resetData();

}
