package com.soueidan.extensions.zone.events;

import java.util.Arrays;

import com.smartfoxserver.bitswarm.sessions.ISession;
import com.smartfoxserver.v2.api.CreateRoomSettings;
import com.smartfoxserver.v2.api.CreateRoomSettings.RoomExtensionSettings;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.SFSRoomRemoveMode;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.variables.SFSUserVariable;
import com.smartfoxserver.v2.entities.variables.UserVariable;
import com.smartfoxserver.v2.exceptions.SFSCreateRoomException;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;
import com.soueidan.extensions.zone.core.ZoneExtension;

public class UserZoneJoinEventHandler extends BaseServerEventHandler {

	private User user;
	
	@Override
	public void handleServerEvent(ISFSEvent event) throws SFSException {
		user = (User) event.getParameter(SFSEventParam.USER);
		ISession session = user.getSession();
		
		UserVariable userId = new SFSUserVariable(ZoneExtension.USER_ID, session.getProperty(ZoneExtension.USER_ID));
		userId.setHidden(true);
		
		UserVariable userVip = new SFSUserVariable(ZoneExtension.USER_VIP, session.getProperty(ZoneExtension.USER_VIP));
		userVip.setHidden(false);
		
		UserVariable userAvatar = new SFSUserVariable(ZoneExtension.USER_AVATAR, session.getProperty(ZoneExtension.USER_AVATAR));
		userAvatar.setHidden(false);
		
		java.util.List<UserVariable> vars = Arrays.asList(userId, userVip, userAvatar);
		getApi().setUserVariables(user, vars);
		
		String roomName = session.getProperty(ZoneExtension.ROOM_NAME).toString();
		
		Room room = getParentExtension().getParentZone().getRoomByName(roomName);
		
		if (room == null) {
			room = createRoom(roomName);
		}
		
		getApi().joinRoom(user, room);
	}

	private Room createRoom(String roomName) {
		// TODO ONLY ALLOW users in the params.getSFSArray("users") to access this 
		// createdRoom, so save the users id in the room table.
		
		trace("Create room name:", roomName);
		
		RoomExtensionSettings extension = new RoomExtensionSettings("TawleExtension", "com.soueidan.games.tawle.core.TawleExtension");
		
		CreateRoomSettings setting = new CreateRoomSettings();
		setting.setGroupId("games");
		setting.setGame(true);
		setting.setMaxUsers(2);
		setting.setDynamic(true);
		setting.setAutoRemoveMode(SFSRoomRemoveMode.WHEN_EMPTY);
		setting.setUseWordsFilter(true);
		setting.setName(roomName);
		setting.setHidden(false);
		setting.setExtension(extension);
		
		try {
			trace("Room created complete");
			return getApi().createRoom(getParentExtension().getParentZone(), setting, user, false, null);
		} catch ( SFSCreateRoomException err ) {
			trace(err.getMessage());
		}
		return null;
	}
	
	
}
