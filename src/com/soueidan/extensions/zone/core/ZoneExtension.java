package com.soueidan.extensions.zone.core;

import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.extensions.*;
import com.soueidan.extensions.zone.events.UserLoginEventHandler;
import com.soueidan.extensions.zone.events.UserZoneJoinEventHandler;

public class ZoneExtension extends SFSExtension {

	
	public static final String ROOM_NAME = "room";
	public static final String USER_ID = "user_id";
	public static final String USER_REGISTERED = "user_registered";
	public static final String USER_STATUS = "status";
	public static final String USER_SESSION = "session";
	public static final String USER_VIP = "vip";
	
	@Override
	public void init() {
		
		trace("ZoneExtension init");
		
		addEventHandler(SFSEventType.USER_LOGIN, UserLoginEventHandler.class);
		addEventHandler(SFSEventType.USER_JOIN_ZONE, UserZoneJoinEventHandler.class);

	}
	
	@Override
	public void destroy()
	{
	    super.destroy();
	    trace("ZoneExtension destroy");
	}
}
