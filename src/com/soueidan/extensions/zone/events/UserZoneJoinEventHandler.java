package com.soueidan.extensions.zone.events;

import java.util.Arrays;

import com.smartfoxserver.bitswarm.sessions.ISession;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.variables.SFSUserVariable;
import com.smartfoxserver.v2.entities.variables.UserVariable;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;
import com.soueidan.extensions.zone.core.ZoneExtension;

public class UserZoneJoinEventHandler extends BaseServerEventHandler {

	@Override
	public void handleServerEvent(ISFSEvent event) throws SFSException {
		User user = (User) event.getParameter(SFSEventParam.USER);
		ISession session = user.getSession();
		
		UserVariable userId = new SFSUserVariable("dbid", session.getProperty(ZoneExtension.USER_ID));
		userId.setHidden(true);
		
		UserVariable userVip = new SFSUserVariable(ZoneExtension.USER_VIP, session.getProperty(ZoneExtension.USER_VIP));
		userVip.setHidden(false);
		
		java.util.List<UserVariable> vars = Arrays.asList(userId, userVip);
		getApi().setUserVariables(user, vars);
		
		String roomName = session.getProperty(ZoneExtension.ROOM_NAME).toString();
		
		Room lobby = getParentExtension().getParentZone().getRoomByName(roomName);
		
		if (lobby == null) {
			throw new SFSException(lobby + " room was not found!");
		}
		
		getApi().joinRoom(user, lobby);
	}
}
