/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.l2j.gameserver.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.gameserver.templates.StatsSet;

public class ServerVariables
{
	private static final Logger _log = Logger.getLogger(ServerVariables.class.getName());
	
	private static final String SELECT = "SELECT * FROM server_variables";
	private static final String DELETE = "DELETE FROM server_variables WHERE name = ?";
	private static final String REPLACE = "REPLACE INTO server_variables (name, value) VALUES (?,?)";
	
	private static StatsSet server_vars;
	
	private static StatsSet getVars()
	{
		if (server_vars == null)
		{
			server_vars = new StatsSet();
			loadFromDB();
		}
		return server_vars;
	}
	
	private static void loadFromDB()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement(SELECT);
			ResultSet rs = statement.executeQuery();
			while (rs.next())
				server_vars.set(rs.getString("name"), rs.getString("value"));
			
			rs.close();
			statement.close();
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "ServerVariables: an exception occured at loading: " + e.getMessage(), e);
		}
	}
	
	private static void saveToDB(String name)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = null;
			
			String value = getVars().getString(name, "");
			if (value.isEmpty())
			{
				statement = con.prepareStatement(DELETE);
				statement.setString(1, name);
			}
			else
			{
				statement = con.prepareStatement(REPLACE);
				statement.setString(1, name);
				statement.setString(2, value);
			}
			statement.execute();
			statement.close();
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "ServerVariables: an exception occured during save: " + e.getMessage(), e);
		}
	}
	
	public static boolean getBool(String name)
	{
		return getVars().getBool(name);
	}
	
	public static boolean getBool(String name, boolean defult)
	{
		return getVars().getBool(name, defult);
	}
	
	public static int getInt(String name)
	{
		return getVars().getInteger(name);
	}
	
	public static int getInt(String name, int defult)
	{
		return getVars().getInteger(name, defult);
	}
	
	public static long getLong(String name)
	{
		return getVars().getLong(name);
	}
	
	public static long getLong(String name, long defult)
	{
		return getVars().getLong(name, defult);
	}
	
	public static double getDouble(String name)
	{
		return getVars().getDouble(name);
	}
	
	public static double getDouble(String name, double defult)
	{
		return getVars().getDouble(name, defult);
	}
	
	public static String getString(String name)
	{
		return getVars().getString(name);
	}
	
	public static String getString(String name, String defult)
	{
		return getVars().getString(name, defult);
	}
	
	public static void set(String name, boolean value)
	{
		getVars().set(name, value);
		saveToDB(name);
	}
	
	public static void set(String name, int value)
	{
		getVars().set(name, value);
		saveToDB(name);
	}
	
	public static void set(String name, long value)
	{
		getVars().set(name, value);
		saveToDB(name);
	}
	
	public static void set(String name, double value)
	{
		getVars().set(name, value);
		saveToDB(name);
	}
	
	public static void set(String name, String value)
	{
		getVars().set(name, value);
		saveToDB(name);
	}
	
	public static void unset(String name)
	{
		getVars().unset(name);
		saveToDB(name);
	}
}