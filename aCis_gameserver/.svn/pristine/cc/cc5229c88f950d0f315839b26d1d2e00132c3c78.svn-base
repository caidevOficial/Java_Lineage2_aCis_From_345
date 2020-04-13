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
package net.sf.l2j.gameserver.handler.admincommandhandlers;

import java.util.StringTokenizer;

import net.sf.l2j.commons.lang.StringUtil;
import net.sf.l2j.gameserver.datatables.BookmarkTable;
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.L2Bookmark;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * This class handles bookmarks (stored locations for GMs use).<br>
 * A bookmark is registered using //bk name. The book itself is called with //bk without parameter.
 * @author Tryskell
 */
public class AdminBookmark implements IAdminCommandHandler
{
	private final static int PAGE_LIMIT = 15;
	
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_bkpage",
		"admin_bk",
		"admin_delbk"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		if (command.startsWith("admin_bkpage"))
		{
			StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken(); // skip command
			
			int page = 1;
			if (st.hasMoreTokens())
				page = Integer.parseInt(st.nextToken());
			
			showBookmarks(activeChar, page);
		}
		else if (command.startsWith("admin_bk"))
		{
			StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken(); // skip command
			
			// Save the bookmark on SQL, and call the HTM.
			if (st.hasMoreTokens())
			{
				final String name = st.nextToken();
				
				if (name.length() > 15)
				{
					activeChar.sendMessage("The location name is too long.");
					return true;
				}
				
				if (BookmarkTable.getInstance().isExisting(name, activeChar.getObjectId()))
				{
					activeChar.sendMessage("That location is already existing.");
					return true;
				}
				
				BookmarkTable.getInstance().saveBookmark(name, activeChar);
			}
			
			// Show the HTM.
			showBookmarks(activeChar, 1);
		}
		else if (command.startsWith("admin_delbk"))
		{
			StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken(); // skip command
			
			if (st.hasMoreTokens())
			{
				final String name = st.nextToken();
				final int objId = activeChar.getObjectId();
				
				if (!BookmarkTable.getInstance().isExisting(name, objId))
				{
					activeChar.sendMessage("That location doesn't exist.");
					return true;
				}
				BookmarkTable.getInstance().deleteBookmark(name, objId);
			}
			else
				activeChar.sendMessage("The command delbk must be followed by a valid name.");
			
			showBookmarks(activeChar, 1);
		}
		return true;
	}
	
	/**
	 * Show the basic HTM fed with generated data.
	 * @param activeChar The player to make checks on.
	 * @param page The page id to show.
	 */
	private static void showBookmarks(L2PcInstance activeChar, int page)
	{
		final int objId = activeChar.getObjectId();
		final L2Bookmark[] bookmarks = BookmarkTable.getInstance().getBookmarks(objId);
		
		// Load static Htm.
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/admin/bk.htm");
		
		if (bookmarks == null)
		{
			html.replace("%locs%", "<tr><td>No bookmarks are currently registered.</td></tr>");
			activeChar.sendPacket(html);
			return;
		}
		
		if (page > bookmarks.length / PAGE_LIMIT + 1 || page < 1)
			return;
		
		int max = bookmarks.length / PAGE_LIMIT;
		if (bookmarks.length > PAGE_LIMIT * max)
			max++;
		
		int start = ((page - 1) * PAGE_LIMIT);
		int end = Math.min(((page - 1) * PAGE_LIMIT) + PAGE_LIMIT, bookmarks.length);
		
		// Generate data.
		final StringBuilder sb = new StringBuilder(2000);
		
		for (int i = start; i < end; i++)
		{
			final L2Bookmark bk = bookmarks[i];
			if (bk != null)
			{
				final String name = bk.getName();
				final int x = bk.getX();
				final int y = bk.getY();
				final int z = bk.getZ();
				
				StringUtil.append(sb, "<tr><td><a action=\"bypass -h admin_move_to ", x, " ", y, " ", z, "\">", name, " (", x, " ", y, " ", z, ")", "</a></td><td><a action=\"bypass -h admin_delbk ", name, "\">Remove</a></td></tr>");
			}
		}
		
		// End of table, open a new table for pages system.
		sb.append("</table><br><table width=270 bgcolor=444444><tr><td>Page: ");
		for (int x1 = 0; x1 < max; x1++)
		{
			int pagenr = x1 + 1;
			if (page == pagenr)
				StringUtil.append(sb, pagenr, "|");
			else
				StringUtil.append(sb, "<a action=\"bypass -h admin_bkpage ", x1 + 1, "\">", pagenr, "</a>|");
		}
		sb.append("</td></tr>");
		
		html.replace("%locs%", sb.toString());
		activeChar.sendPacket(html);
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}