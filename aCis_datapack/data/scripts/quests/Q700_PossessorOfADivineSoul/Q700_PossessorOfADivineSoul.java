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
package quests.Q700_PossessorOfADivineSoul;

import net.sf.l2j.gameserver.model.actor.L2Npc;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.quest.Quest;
import net.sf.l2j.gameserver.model.quest.QuestState;
import net.sf.l2j.gameserver.network.serverpackets.SocialAction;

public class Q700_PossessorOfADivineSoul extends Quest
{
	private static final String qn = "Q700_PossessorOfADivineSoul";
	
	// NPCs
	private static final int ARNOLD = 60000;//31739; TALIEN - Arnold Van Clauss	Location: 89495 -153584 -1343
	private static final int DARKRA = 60001;//30753;GABRIELLE-Darkra van clauss	Location: 104957 -160835 -1794 Near Planderous Plains.
 	private static final int EMIL = 60002;//30754; GILMORE - emil van clauss	Location: 123661 -142074 -846 , in the top of sky wagon relic.
	private static final int IRIA = 60003;//31042; KANTABILION- IRIA VAN CLASS	Location: 74949 -101922 -970, inside caron's dungeon.
	private static final int DREIFUS = 60004;//30692;STEDMEL - Dreifus Van Hook Location: 85086 -144405 -1539 Schuttgart
	private static final int VLADIMIR = 60005;//31742;VIRGIL-vladimir van clauss	Location: 112973 -109277 -844 In Ice Merchant Cabin
	private static final int SANSA = 60006;//31744;OGMAR- sansa van clauss	Location: 113021 -109258 -845 In Ice Merchant Cabin
	private static final int JASMINE = 60007;//31336;RAHORAKTI - JASMINE VAN CLAUSS	Location: 86065 -141264 -1499 grocery store de schuttgart
	private static final int CATHERINE = 60008;//31743;KASSANDRA-catherine van clauss	Location: 113946 -109017 -851 In Ice Merchant Cabin
	private static final int SYLVANAS = 60009;//31740;CARADINE-Sylvanas van clauss Location 83954 -143342 -1542
	private static final int EUGEENE = 60010;//31272;NOEL-eugeene van clauss	Location:127759	-161369	-1157 in bandit strongold cave in schuttgart
	
	// Monsters
	private static final int DIABOLO = 60011;//27113; //quest monster Diabolo	Location: 127367 -147919 -3742 near sky wagon relic, in a lake
	private static final int Ascended_Silent_Seeker = 60017;// Location: 109340 -102765 -3382
	private static final int Ascended_Silent_Brother = 60018;
	//private static final int MALRUK_SUCCUBUS_2 = 20283;
	//private static final int MALRUK_SUCCUBUS_TUREN_2 = 20284;
	private static final int Ascended_Warrior = 60019;//Ascended Warrior Monk Location: 111853 -154874 -1489
	private static final int Judge_Of_Ascension = 60021; //Judge of Ascension Location: 120028 -156124 -1733
	private static final int Pilgrim_Of_Ascension = 60020;//pilgrim of ascension Location: 119337 -161167 -1148
	//private static final int SPLINTER_STAKATO_DRONE_1 = 21511;
	//private static final int SPLINTER_STAKATO_DRONE_2 = 21512;
		
	// Items
	private static final int LEGEND_OF_SEVENTEEN = 60028;//7587;
	private static final int Silent_Mask = 60031;//7597; //minor_demon_claw
	private static final int ECHO_CRYSTAL = 60030;//7589; //musical_crystal
	private static final int POETRY_BOOK = 60029;//7588; 	//Family_book
	private static final int Divine_Feathers = 60032;//7598;	//Ascended_Moss cambiar por Angel_Feather
	private static final int JASMINE_MEDICINE = 60033;//7599;	//buscar otro id
	private static final int PURE_SILVER = 6320;	
	private static final int NIGHTMARE_OIL = 6034;	
	private static final int VLADIMIR_LETTER = 7677;	//buscar otro, usado por el momento
		
		public Q700_PossessorOfADivineSoul()
		{
			super(700, qn, "Possessor of a Divine Soul - 1");
			
			setItemsIds(LEGEND_OF_SEVENTEEN, Silent_Mask, ECHO_CRYSTAL, POETRY_BOOK, Divine_Feathers, JASMINE_MEDICINE);
			
			addStartNpc(ARNOLD);
			addTalkId(ARNOLD, DARKRA, EMIL, IRIA, DREIFUS, VLADIMIR, SANSA, JASMINE, CATHERINE, SYLVANAS, EUGEENE);
			
			addKillId(DIABOLO, Ascended_Silent_Seeker,Ascended_Silent_Brother, Ascended_Warrior, Judge_Of_Ascension, Pilgrim_Of_Ascension);
		}
		
		@Override
		public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
		{
			String htmltext = event;
			QuestState st = player.getQuestState(qn);
			if (st == null)
				return htmltext;
			
			// ARNOLD
			if (event.equalsIgnoreCase("60000-03.htm"))
			{
				st.setState(STATE_STARTED);
				st.set("cond", "1");
				st.playSound(QuestState.SOUND_ACCEPT);
			}
			else if (event.equalsIgnoreCase("60000-07.htm"))
			{
				st.set("cond", "5");
				st.playSound(QuestState.SOUND_MIDDLE);
				st.takeItems(LEGEND_OF_SEVENTEEN, 1);
			}
			else if (event.equalsIgnoreCase("60000-10.htm"))
			{
				st.set("cond", "9");
				st.playSound(QuestState.SOUND_MIDDLE);
				st.takeItems(ECHO_CRYSTAL, 1);
			}
			else if (event.equalsIgnoreCase("60000-13.htm"))
			{
				st.set("cond", "11");
				st.playSound(QuestState.SOUND_MIDDLE);
				st.takeItems(POETRY_BOOK, 1);
			}
			// DARKRA
			else if (event.equalsIgnoreCase("60001-02.htm"))
			{
				st.set("cond", "2");
				st.playSound(QuestState.SOUND_MIDDLE);
			}
			// EMIL
			else if (event.equalsIgnoreCase("60002-02.htm"))
			{
				st.set("cond", "3");
				st.playSound(QuestState.SOUND_MIDDLE);
			}
			// IRIA
			else if (event.equalsIgnoreCase("60003-02.htm"))
			{
				st.set("cond", "6");
				st.playSound(QuestState.SOUND_MIDDLE);
			}
			else if (event.equalsIgnoreCase("60003-05.htm"))
			{
				st.set("cond", "8");
				st.playSound(QuestState.SOUND_MIDDLE);
				st.takeItems(Silent_Mask, -1);
				st.giveItems(ECHO_CRYSTAL, 1);
			}
			// DREIFUS
			else if (event.equalsIgnoreCase("60004-02.htm"))
			{
				st.set("cond", "10");
				st.playSound(QuestState.SOUND_MIDDLE);
				st.giveItems(POETRY_BOOK, 1);
			}
			// VLADIMIR
			else if (event.equalsIgnoreCase("60005-02.htm"))
			{
				st.set("cond", "12");
				st.playSound(QuestState.SOUND_MIDDLE);
			}
			else if (event.equalsIgnoreCase("60005-05.htm"))
			{
				st.set("cond", "18");
				st.playSound(QuestState.SOUND_MIDDLE);
			}
			// SANSA
			else if (event.equalsIgnoreCase("60006-02.htm"))
			{
				st.set("cond", "13");
				st.playSound(QuestState.SOUND_MIDDLE);
			}
			// JASMINE
			else if (event.equalsIgnoreCase("60007-02.htm"))
			{
				st.set("cond", "14");
				st.playSound(QuestState.SOUND_MIDDLE);
			}
			else if (event.equalsIgnoreCase("60007-05.htm"))
			{
				st.set("cond", "16");
				st.playSound(QuestState.SOUND_MIDDLE);
				st.takeItems(Divine_Feathers, -1);
				st.giveItems(JASMINE_MEDICINE, 1);
			}
			// CATHERINE
			else if (event.equalsIgnoreCase("60008-02.htm"))
			{
				st.set("cond", "17");
				st.playSound(QuestState.SOUND_MIDDLE);
				st.takeItems(JASMINE_MEDICINE, 1);
			}
			// SYLVANAS
			else if (event.equalsIgnoreCase("60009-02.htm"))
			{
				st.set("cond", "19");
				st.playSound(QuestState.SOUND_MIDDLE);
			}
			else if (event.equalsIgnoreCase("60009-05.htm"))
			{
				st.giveItems(VLADIMIR_LETTER, 1);
				st.rewardExpAndSp(263043, 0);
				player.broadcastPacket(new SocialAction(player, 3));
				st.playSound(QuestState.SOUND_FINISH);
				st.exitQuest(false);
			}
			// EUGEENE
			else if (event.equalsIgnoreCase("60010-02.htm"))
			{
				st.set("cond", "20");
				st.playSound(QuestState.SOUND_MIDDLE);
			}
			else if (event.equalsIgnoreCase("60010-05.htm"))
			{
				if (st.hasQuestItems(NIGHTMARE_OIL) && st.getQuestItemsCount(PURE_SILVER) >= 6)
				{
					st.set("cond", "21");
					st.playSound(QuestState.SOUND_MIDDLE);
					st.takeItems(PURE_SILVER, 5);
					st.takeItems(NIGHTMARE_OIL, 1);
				}
				else
					htmltext = "60010-07.htm";
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
					htmltext = (!player.isNoble() || player.getLevel() < 78) ? "60000-02.htm" : "60000-01.htm";
					break;
				
				case STATE_STARTED:
					if (!player.isSubClassActive())
						break;
					
					int cond = st.getInt("cond");
					switch (npc.getNpcId())
					{
						case ARNOLD:
							if (cond == 1)
								htmltext = "60000-04.htm";
							else if (cond == 2 || cond == 3)
								htmltext = "60000-05.htm";
							else if (cond == 4)
								htmltext = "60000-06.htm";
							else if (cond == 5)
								htmltext = "60000-08.htm";
							else if (cond == 8)
								htmltext = "60000-09.htm";
							else if (cond == 9)
								htmltext = "60000-11.htm";
							else if (cond == 10)
								htmltext = "60000-12.htm";
							else if (cond == 11)
								htmltext = "60000-14.htm";
							break;
						
						case DARKRA:
							if (cond == 1)
								htmltext = "60001-01.htm";
							else if (cond == 2)
								htmltext = "60001-03.htm";
							break;
						
						case EMIL:
							if (cond == 2)
								htmltext = "60002-01.htm";
							else if (cond == 3)
								htmltext = "60002-03.htm";
							break;
						
						case IRIA:
							if (cond == 5)
								htmltext = "60003-01.htm";
							else if (cond == 6)
								htmltext = "60003-03.htm";
							else if (cond == 7)
								htmltext = "60003-04.htm";
							else if (cond == 8)
								htmltext = "60003-06.htm";
							break;
						
						case DREIFUS:
							if (cond == 9)
								htmltext = "60004-01.htm";
							else if (cond == 10)
								htmltext = "60004-03.htm";
							break;
						
						case VLADIMIR:
							if (cond == 11)
								htmltext = "60005-01.htm";
							else if (cond == 12)
								htmltext = "60005-03.htm";
							else if (cond == 17)
								htmltext = "60005-04.htm";
							else if (cond == 18)
								htmltext = "60005-06.htm";
							break;
						
						case SANSA:
							if (cond == 12)
								htmltext = "60006-01.htm";
							else if (cond == 13)
								htmltext = "60006-03.htm";
							break;
						
						case JASMINE:
							if (cond == 13)
								htmltext = "60007-01.htm";
							else if (cond == 14)
								htmltext = "60007-03.htm";
							else if (cond == 15)
								htmltext = "60007-04.htm";
							else if (cond == 16)
								htmltext = "60007-06.htm";
							break;
						
						case CATHERINE:
							if (cond == 16)
								htmltext = "60008-01.htm";
							else if (cond == 17)
								htmltext = "60008-03.htm";
							break;
						
						case SYLVANAS:
							if (cond == 18)
								htmltext = "60009-01.htm";
							else if (cond == 19)
								htmltext = "60009-03.htm";
							else if (cond == 21)
								htmltext = "60009-04.htm";
							break;
						
						case EUGEENE:
							if (cond == 19)
								htmltext = "60010-01.htm";
							else if (cond == 20)
							{
								if (st.hasQuestItems(NIGHTMARE_OIL) && st.getQuestItemsCount(PURE_SILVER) >= 6)
									htmltext = "60010-04.htm";
								else
									htmltext = "60010-03.htm";
							}
							else if (cond == 21)
								htmltext = "60010-06.htm";
							break;
					}
					break;
				
				case STATE_COMPLETED:
					htmltext = getAlreadyCompletedMsg();
					break;
			}
			return htmltext;
		}
		
		@Override
		public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
		{
			QuestState st = checkPlayerState(player, npc, STATE_STARTED);
			if (st == null || !player.isSubClassActive())
				return null;
			
			switch (npc.getNpcId())
			{
				case DIABOLO:
					if (st.getInt("cond") == 3)
					{
						st.set("cond", "4");
						st.giveItems(LEGEND_OF_SEVENTEEN, 1);
						st.playSound(QuestState.SOUND_MIDDLE);
					}
					break;
				
				case Ascended_Silent_Seeker:
					if (st.getInt("cond") == 6 && st.dropItems(Silent_Mask, 1, 100, 80000)) //8%
						st.set("cond", "7");
					break;
				
				case Ascended_Silent_Brother:
				//case MALRUK_SUCCUBUS_TUREN_2:
					if (st.getInt("cond") == 6 && st.dropItems(Silent_Mask, 1, 100, 90000)) //9%
						st.set("cond", "7");
					break;
				
				case Ascended_Warrior:
					if (st.getInt("cond") == 14 && st.dropItems(Divine_Feathers, 1, 50, 100000)) //10%
						st.set("cond", "15");
					break;
				
				case Judge_Of_Ascension:
					if (st.getInt("cond") == 14 && st.dropItems(Divine_Feathers, 1, 50, 150000)) //15%
						st.set("cond", "15");
					break;
				
				case Pilgrim_Of_Ascension:
					if (st.getInt("cond") == 14 && st.dropItems(Divine_Feathers, 1, 50, 150000)) //15%
						st.set("cond", "15");
					break;
			}
			return null;
		}
		
		public static void main(String[] args)
		{
			new Q700_PossessorOfADivineSoul();
		}
	}