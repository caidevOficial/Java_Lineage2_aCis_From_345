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
package net.sf.l2j.log;

/**
 * @version 0.1, 2005-06-06
 * @author Balancer
 */
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Log
{
	private static final Logger _log = Logger.getLogger(Log.class.getName());
	
	public static final void add(String text, String cat)
	{
		String date = (new SimpleDateFormat("yy.MM.dd H:mm:ss")).format(new Date());
		String curr = (new SimpleDateFormat("yyyy-MM-dd-")).format(new Date());
		new File("log/game").mkdirs();
		
		final File file = new File("log/game/" + (curr != null ? curr : "") + (cat != null ? cat : "unk") + ".txt");
		try (FileWriter save = new FileWriter(file, true))
		{
			String out = "[" + date + "] " + text + "\n";
			save.write(out);
		}
		catch (IOException e)
		{
			_log.log(Level.WARNING, "Error saving logfile: ", e);
		}
	}
}