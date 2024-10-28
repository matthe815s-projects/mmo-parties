package dev.matthe815.mmoparties.common.stats;

import net.minecraft.world.entity.player.Player;

public class PartyMember {
    /**
     * The player name used to determine data target.
     */
    private String playerName;

    private Party party;

    /**
     * The data for this member, including current synched stats and other values.
     */
    private PartyMemberData data;

    /**
     * The moderator status of the member.
     */
    private boolean moderator;

    public PartyMember(Player player, Party party)
    {
        playerName = player.getName().getString();
        this.party = party;
    }

    /**
     * Returns if this user is the leader of the party.
     * @return
     */
    public boolean IsLeader()
    {
        return party.leader == party.leader;
    }

    /**
     * Returns if this user is a moderator of the party.
     * @return
     */
    public boolean IsModerator()
    {
        return moderator;
    }

    public PartyMemberData GetData()
    {
        return data;
    }

}
