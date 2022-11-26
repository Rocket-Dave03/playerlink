package io.github.rockedave.link;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;

public class LinkManager {
	ArrayList<PlayerLink> links;
	
	public LinkManager() {
		links = new ArrayList<>();
	}
	
	public boolean isLinked(ServerPlayerEntity player) {
		return links.stream().anyMatch(link -> link.inLink(player));
	}
	
	public PlayerLink getLink(ServerPlayerEntity player) {
		for (PlayerLink link : links) {
			if(link.inLink(player)) {
				return link;
			}
		}
		return null;
	}
	
	public boolean linkPlayer(ServerPlayerEntity player1, ServerPlayerEntity player2) {
		if (isLinked(player1) && isLinked(player2)) {
			if (!getLink(player1).equals(getLink(player2))) {
				mergeLink(getLink(player1),getLink(player2));
			}
			return true;
		} else if (isLinked(player1) && !isLinked(player2)) {
			getLink(player1).addPlayer(player2);
			return true;
		} else if (!isLinked(player1) && isLinked(player2)) {
			getLink(player2).addPlayer(player1);
			return true;
		} else {
			PlayerLink l = new PlayerLink(player1.getServer());
			l.addPlayer(player1);
			l.addPlayer(player2);
			links.add(l);
			return true;
		}
	}
	
	
	public List<PlayerLink> getLinks() {
		return this.links;
	}
	// TODO: Implement link merging
	private void mergeLink(PlayerLink a, PlayerLink b) {
		PlayerLink link = new PlayerLink(a.server);
		throw new RuntimeException("Unimplemented");
	}
	
	public void unlinkPlayer(ServerPlayerEntity player) {
		PlayerLink link = this.getLink(player);
		link.remove(player);
		if (link.numPlayers() == 0) {
			links.remove(link);
		}
	}

	public void respawnPlayer(ServerPlayerEntity player) {
		getLink(player).respawnPlayer(player);
	}
	public void killInLink(ServerPlayerEntity serverPlayerEntity) {
		getLink(serverPlayerEntity).killAll(serverPlayerEntity);
	}
}
