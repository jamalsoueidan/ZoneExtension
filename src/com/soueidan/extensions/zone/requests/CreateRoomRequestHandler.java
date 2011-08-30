package com.soueidan.extensions.zone.requests;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.smartfoxserver.v2.db.IDBManager;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSDataWrapper;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;
import com.smartfoxserver.v2.extensions.ExtensionLogLevel;
import com.soueidan.extensions.zone.core.ZoneExtension;

public class CreateRoomRequestHandler extends BaseClientRequestHandler {
	
	public static final String CREATE_ROOM = "create_room";
	
	private int gameId;
	private User userInviter;
	
	@Override
	public void handleClientRequest(User user, ISFSObject params) {
		gameId = params.getInt("game_id");
		userInviter = user;	
		
		List<User> users = getUsers(params);
	
		String roomName = generateRandomWord();
	
		createRoomDB(roomName);
		
		ISFSObject data = new SFSObject();
		data.putUtfString("room", roomName);
		
		getParentExtension().send(CREATE_ROOM, data, users);
	}
	
	private List<User> getUsers(ISFSObject params) {
		ISFSArray usersArray = params.getSFSArray("users");
		Iterator<SFSDataWrapper> itr = usersArray.iterator();
		List<User> users = new ArrayList<User>();
		Room room = userInviter.getLastJoinedRoom();
		
		User currentUser;
		Integer userId;
		while(itr.hasNext()) {
			userId = (Integer) itr.next().getObject();
			currentUser = room.getUserById(userId);
			users.add(currentUser);
		}
		
		return users;
	}

	private void createRoomDB(String roomName) {
		int userId = userInviter.getVariable(ZoneExtension.USER_ID).getIntValue();
		
		IDBManager dbManager = getParentExtension().getParentZone().getDBManager();
        
        try {
        	Connection connection = dbManager.getConnection();
        	
        	Date dt = new Date();
        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        	
        	PreparedStatement stmt = connection.prepareStatement("INSERT INTO rooms (session, creator_id, game_id, started_date) VALUES(?, ?, ?, ?)");
    		stmt.setString(1, roomName);
    		stmt.setInt(2, userId);
    		stmt.setInt(3, gameId);
    		stmt.setString(4, sdf.format(dt));
        	stmt.execute();
            
			connection.close();
        }
        catch (SQLException e)
        {
            trace(ExtensionLogLevel.WARN, "SQL Failed: " + e.toString());
        }
	}

	private static String generateRandomWord()
	{
		int numberOfWords = 1;
	    String[] randomStrings = new String[numberOfWords];
	    Random random = new Random();
	    for(int i = 0; i < numberOfWords; i++)
	    {
	        char[] word = new char[random.nextInt(8)+3]; // words of length 3 through 10. (1 and 2 letter words are boring.)
	        for(int j = 0; j < word.length; j++)
	        {
	            word[j] = (char)('a' + random.nextInt(26));
	        }
	        randomStrings[i] = new String(word);
	    }
	    return randomStrings[0];
	}
}