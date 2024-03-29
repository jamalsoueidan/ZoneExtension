package com.soueidan.extensions.zone.events;

import java.sql.SQLException;

import java.sql.*;
import com.smartfoxserver.bitswarm.sessions.ISession;
import com.smartfoxserver.v2.core.*;
import com.smartfoxserver.v2.db.IDBManager;
import com.smartfoxserver.v2.entities.data.*;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.exceptions.SFSLoginException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;
import com.smartfoxserver.v2.extensions.ExtensionLogLevel;
import com.smartfoxserver.v2.security.DefaultPermissionProfile;
import com.soueidan.extensions.zone.core.ZoneExtension;

public class UserLoginEventHandler extends BaseServerEventHandler {
	
	private SFSObject _data;
	private ISession _session;
	
	@Override
	public void handleServerEvent(ISFSEvent event) throws SFSException {
		_session = (ISession) event.getParameter(SFSEventParam.SESSION);
		
		_data = (SFSObject) event.getParameter(SFSEventParam.LOGIN_IN_DATA);
		
		String session = _data.getUtfString(ZoneExtension.USER_SESSION);
		if ( session.isEmpty() ) {
			throw new SFSException("No session key specified");
		}
	
		IDBManager dbManager = getParentExtension().getParentZone().getDBManager();
        
        try {
        	Connection connection = dbManager.getConnection();
        	
        	PreparedStatement stmt = connection.prepareStatement("SELECT id, nickname, status, vip, avatar_url FROM users where session=? limit 1");
    		stmt.setString(1, session);
    		
        	ResultSet res = stmt.executeQuery();

        	if (!res.first()) {
        		throw new SFSLoginException("Login failed for user");
			}
        		
            String nickname = res.getString("nickname");
            Integer status = res.getInt("status");
            Boolean vip = res.getBoolean("vip");
            String avatar = res.getString("avatar_url");
            
            ISFSObject outData = (ISFSObject) event.getParameter(SFSEventParam.LOGIN_OUT_DATA);
            outData.putUtfString(SFSConstants.NEW_LOGIN_NAME, nickname);
            
            _session.setProperty(ZoneExtension.USER_ID, res.getInt("id"));
            
            DefaultPermissionProfile permission = DefaultPermissionProfile.GUEST;
            if ( status == 3 ) {
            	permission = DefaultPermissionProfile.ADMINISTRATOR;
            } else if ( status == 2 ) {
            	permission = DefaultPermissionProfile.MODERATOR; 
            } else {
            	permission = DefaultPermissionProfile.STANDARD;
            }
            
            _session.setProperty("$permission", permission);
            _session.setProperty(ZoneExtension.USER_VIP, vip);
            _session.setProperty(ZoneExtension.ROOM_NAME, _data.getUtfString(ZoneExtension.ROOM_NAME));
            _session.setProperty(ZoneExtension.USER_AVATAR, avatar);
            
            int gameId = _data.getInt("game_id");
            
            trace("GameId", gameId);
            
            stmt = connection.prepareStatement("SELECT win,loss,points FROM stats WHERE user_id=? AND game_id=? limit 1");
    		stmt.setInt(1, res.getInt("id"));
    		stmt.setInt(2, gameId);
            
    		res = stmt.executeQuery();
    		
    		Integer win = 0;
            Integer loss = 0;
            Integer points = 0;
             
    		if (res.first()) {
        		win = res.getInt("win");
        		loss = res.getInt("loss");
        		points = res.getInt("points");
			}
            
    		_session.setProperty(ZoneExtension.USER_WIN, win);
    		_session.setProperty(ZoneExtension.USER_LOSS, loss);
    		_session.setProperty(ZoneExtension.USER_POINTS, points);
    		
    		_session.setProperty(ZoneExtension.GAME_ID, gameId);
    		
			connection.close();
        }
        catch (SQLException e)
        {
            trace(ExtensionLogLevel.WARN, "SQL Failed: " + e.toString());
        }
	}
}
