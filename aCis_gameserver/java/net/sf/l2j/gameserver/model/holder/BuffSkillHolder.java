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
package net.sf.l2j.gameserver.model.holder;

/**
 * @author Tryskell
 */
public final class BuffSkillHolder
{
	private final int _skillId;
	private final int _price;
	
	private final String _groupType;
	
	public BuffSkillHolder(int skillId, int price, String groupType)
	{
		_skillId = skillId;
		_price = price;
		_groupType = groupType;
	}
	
	public final int getSkillId()
	{
		return _skillId;
	}
	
	public final int getPrice()
	{
		return _price;
	}
	
	public final String getGroupType()
	{
		return _groupType;
	}
}