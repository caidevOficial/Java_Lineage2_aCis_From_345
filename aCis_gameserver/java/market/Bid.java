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
package market;

/**
 * @author CaiDev
 *
 */
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
 
public class Bid {
 
/**
* @author Elfocrash
*/
 
private final L2PcInstance bidder;
 
private final ItemInstance bidItem;
 
private final int costItemId;
 
private final int costItemCount;
 
private final int bidId;
 
private final MarketTaxType taxType;
 
public Bid(L2PcInstance bidder, int bidId, ItemInstance bidItem, int costItemId, int costItemCount, MarketTaxType taxType)
{
this.bidder = bidder;
this.bidId = bidId;
this.bidItem = bidItem;
this.costItemId = costItemId;
this.costItemCount = costItemCount;
this.taxType = taxType;
}
 
public L2PcInstance getBidder()
{
return bidder;
}
 
public int getBidId()
{
return bidId;
}
 
public ItemInstance getBidItem()
{
return bidItem;
}
 
public int getCostItemId()
{
return costItemId;
}
 
public int getCostItemCount()
{
return costItemCount;
}
 
public MarketTaxType getTaxType()
{
return taxType;
}
 
}
