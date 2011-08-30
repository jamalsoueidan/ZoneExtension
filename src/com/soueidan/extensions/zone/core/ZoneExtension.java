package com.soueidan.extensions.zone.core;

import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.extensions.*;
import com.soueidan.extensions.zone.events.*;
import com.soueidan.extensions.zone.requests.*;

public class ZoneExtension extends SFSExtension {

	
	public static final String ROOM_NAME = "room";
	public static final String USER_ID = "user_id";
	public static final String USER_REGISTERED = "user_registered";
	public static final String USER_SESSION = "session";
	public static final String USER_VIP = "vip";
	
	@Override
	public void init() {
		
		trace("ZoneExtension init");
		
		addEventHandler(SFSEventType.USER_LOGIN, UserLoginEventHandler.class);
		addEventHandler(SFSEventType.USER_JOIN_ZONE, UserZoneJoinEventHandler.class);

		addRequestHandler(CreateRoomRequestHandler.CREATE_ROOM, CreateRoomRequestHandler.class);	
	}
	
	@Override
	public void destroy()
	{
	    super.destroy();
	    trace("ZoneExtension destroy");
	}
}
