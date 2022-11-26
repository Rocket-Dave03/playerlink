package io.github.rockedave.link;

import io.github.rockedave.Main;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerLink {
	private float health = 20f;
	private final ArrayList<UUID> players;
	private final ArrayList<UUID> deadPlayers;
	protected MinecraftServer server;
	
	public PlayerLink(MinecraftServer server) {
		players = new ArrayList<>();
		deadPlayers = new ArrayList<>();
		this.server = server;
	}
	
	public boolean inLink(ServerPlayerEntity p) {
		return players.contains(p.getUuid()) || deadPlayers.contains(p.getUuid());
	}
	public boolean isActive(ServerPlayerEntity e) {
		return players.contains(e.getUuid());
	}
	
	protected void addPlayer(ServerPlayerEntity p) {
		if (!inLink(p)) {
			players.add(p.getUuid());
		}
	}
	
	public float getHealth(ServerPlayerEntity p) {
		if (this.deadPlayers.contains(p.getUuid())) {
			return 0.0f;
		} else {
			return this.health;
		}
	}
	
	public void setHealth(ServerPlayerEntity p, float h) {
		if (!deadPlayers.contains(p.getUuid())) {
			this.health = h;
		}
	}
	
	protected void remove(ServerPlayerEntity player) {
		players.remove(player.getUuid());
		deadPlayers.remove(player.getUuid());
	}
	

	public String getPlayers(MinecraftServer server) {
		return String.format("Alive: %d\n", players.size()) + players.stream().map(uuid -> {
			try {
				String name = Objects.requireNonNull(server.getPlayerManager().getPlayer(uuid)).getName().getString();
				return String.format("%s: %s\n", name, uuid.toString());
			} catch (NullPointerException e) {
				return String.format("Disconnected: %s\n", uuid.toString());
			}
		}).collect(Collectors.joining()) + String.format("Dead: %d\n", deadPlayers.size()) +
		deadPlayers.stream().map(uuid -> {
			try {
				String name = Objects.requireNonNull(server.getPlayerManager().getPlayer(uuid)).getName().getString();
				return String.format("%s: %s\n", name, uuid.toString());
			} catch (NullPointerException e) {
				return String.format("Disconnected: %s\n", uuid.toString());
			}
		}).collect(Collectors.joining());
	}
	
	protected void respawnPlayer(ServerPlayerEntity p) {
		if (inLink(p)) {
//			p.sendMessage(Text.of("Respawned in link"));
			Main.LOGGER.info(String.format("Respawning %s", p.getName().getString()));
			deadPlayers.remove(p.getUuid());
			if (!players.contains(p.getUuid())) {
				players.add(p.getUuid());
			}
			this.health = p.getMaxHealth();
			p.dataTracker.set(LivingEntity.HEALTH, p.getMaxHealth());
		}
	}
	
	protected void killAll(ServerPlayerEntity current) {
		this.health = 0.0f;
		deadPlayers.add(current.getUuid());
		players.remove(current.getUuid());
		Iterator<UUID> playerIter = players.iterator();
		while(playerIter.hasNext()) {
			UUID player = playerIter.next();
			try {
				ServerPlayerEntity sp = server.getPlayerManager().getPlayer(player);
				if (sp == null) {
					throw new NullPointerException();
				}
				sp.dataTracker.set(LivingEntity.HEALTH, 0.0f);
				sp.dropInventory();
				sp.sendMessage(Text.of("Died"));
			} catch (NullPointerException e) {
				Main.LOGGER.error(Arrays.toString(e.getStackTrace()));
			}
			deadPlayers.add(player);
			playerIter.remove();
		}
	}
	
	public int numPlayers() {
		return players.size() + deadPlayers.size();
	}
}
