package com.soueidan.extensions.zone.events;

import java.util.Arrays;
import java.util.List;

import com.smartfoxserver.bitswarm.sessions.ISession;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.SFSRoomRemoveMode;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.variables.SFSUserVariable;
import com.smartfoxserver.v2.entities.variables.UserVariable;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;
import com.soueidan.extensions.zone.core.ZoneExtension;

public class UserZoneJoinEventHandler extends BaseServerEventHandler {

	private User user;
	
	@Override
	public void handleServerEvent(ISFSEvent event) throws SFSException {
		user = (User) event.getParameter(SFSEventParam.USER);
		ISession session = user.getSession();
		
		UserVariable gameId = new SFSUserVariable(ZoneExtension.GAME_ID, session.getProperty(ZoneExtension.GAME_ID));
		gameId.setHidden(true);
		
		UserVariable id = new SFSUserVariable(ZoneExtension.USER_ID, session.getProperty(ZoneExtension.USER_ID));
		id.setHidden(true);
		
		UserVariable vip = new SFSUserVariable(ZoneExtension.USER_VIP, session.getProperty(ZoneExtension.USER_VIP));
		vip.setHidden(false);
		
		UserVariable avatar = new SFSUserVariable(ZoneExtension.USER_AVATAR, session.getProperty(ZoneExtension.USER_AVATAR));
		avatar.setHidden(false);
		
		// this is for ready to play or don't disturb
		// don't think this has something to do with admin, moderator or normal user.
		UserVariable status = new SFSUserVariable(ZoneExtension.USER_STATUS, 0);
		status.setHidden(false);
		
		UserVariable win = new SFSUserVariable(ZoneExtension.USER_WIN, session.getProperty(ZoneExtension.USER_WIN));
		win.setHidden(false);
		
		UserVariable loss = new SFSUserVariable(ZoneExtension.USER_LOSS, session.getProperty(ZoneExtension.USER_LOSS));
		loss.setHidden(false);
		
		UserVariable points = new SFSUserVariable(ZoneExtension.USER_POINTS, session.getProperty(ZoneExtension.USER_POINTS));
		points.setHidden(false);
		
		List<UserVariable> vars = Arrays.asList(gameId, id, vip, avatar, status, win, loss, points);
		getApi().setUserVariables(user, vars);
		
		String roomName = session.getProperty(ZoneExtension.ROOM_NAME).toString();
		
		Room room = getParentExtension().getParentZone().getRoomByName(roomName);
		
		if ( room.isGame() ) {
			room.setAutoRemoveMode(SFSRoomRemoveMode.WHEN_EMPTY);
		}
		
		getApi().joinRoom(user, room);
	}	
}
