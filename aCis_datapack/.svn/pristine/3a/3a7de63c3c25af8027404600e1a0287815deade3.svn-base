/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package quests.Q020_BringUpWithLove;

import net.sf.l2j.gameserver.model.actor.L2Npc;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.quest.Quest;
import net.sf.l2j.gameserver.model.quest.QuestState;

public class Q020_BringUpWithLove extends Quest
{
	public final static String qn = "Q020_BringUpWithLove";
	
	// Item
	private static final int JEWEL_OF_INNOCENCE = 7185;
	
	public Q020_BringUpWithLove()
	{
		super(20, qn, "Bring Up With Love");
		
		setItemsIds(JEWEL_OF_INNOCENCE);
		
		addStartNpc(31537); // Tunatun
		addTalkId(31537);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("31537-09.htm"))
		{
			st.setState(STATE_STARTED);
			st.set("cond", "1");
			st.playSound(QuestState.SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("31537-12.htm"))
		{
			st.takeItems(JEWEL_OF_INNOCENCE, -1);
			st.rewardItems(57, 68500);
			st.playSound(QuestState.SOUND_FINISH);
			st.exitQuest(false);
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = getNoQuestMsg();
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		switch (st.getState())
		{
			case STATE_CREATED:
				htmltext = (player.getLevel() < 65) ? "31537-02.htm" : "31537-01.htm";
				break;
			
			case STATE_STARTED:
				if (st.getInt("cond") == 2)
					htmltext = "31537-11.htm";
				else
					htmltext = "31537-10.htm";
				break;
			
			case STATE_COMPLETED:
				htmltext = getAlreadyCompletedMsg();
				break;
		}
		
		return htmltext;
	}
	
	public static void main(String[] args)
	{
		new Q020_BringUpWithLove();
	}
}