package io.github.rockedave;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.rockedave.link.LinkManager;
import io.github.rockedave.link.PlayerLink;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class Main implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("linkplayers");
	private static LinkManager linkManager;
	
	
	public static PlayerLink getLink(ServerPlayerEntity p) {
		return linkManager.getLink(p);
	}
	
	public static void killInLink(ServerPlayerEntity serverPlayerEntity) {
		linkManager.killInLink(serverPlayerEntity);
	}
	
	public static void respawnPlayer(ServerPlayerEntity serverPlayerEntity) {
		linkManager.respawnPlayer(serverPlayerEntity);
	}
	
	public static boolean isAlive(ServerPlayerEntity e) {
		PlayerLink link = linkManager.getLink(e);
		if (link != null) {
			return link.isActive(e);
		} else {
			return false;
		}
	}
	
	@Override
	public void onInitialize() {
		linkManager = new LinkManager();
		CommandRegistrationCallback.EVENT.register(
			(dispatcher, registryAccess, environment) -> {
			dispatcher.register(
				literal("link")
					.requires(source -> source.hasPermissionLevel(4))
					.then(
						argument("player1", EntityArgumentType.player()).then(
							argument("player2", EntityArgumentType.player())
								.executes(this::linkPlayerPair)
						)
					)
					.then(
						argument("players", EntityArgumentType.players())
							.executes(this::linkPlayers)
					)
			);
			dispatcher.register(
				literal("unlink")
					.requires(source -> source.hasPermissionLevel(4))
					.then(
						argument("player", EntityArgumentType.player())
							.executes(this::unlinkPlayers)
					)
			);
			dispatcher.register(
				literal("links")
					.requires(source -> source.hasPermissionLevel(4))
					.executes(this::listLinks)
			);
			}
		);
	}
	
	private int listLinks(CommandContext<ServerCommandSource> ctx) {
		ctx.getSource().sendMessage(Text.of(String.format("# of Links: %d", linkManager.getLinks().size())));
		for (PlayerLink l : linkManager.getLinks()) {
			ctx.getSource().sendMessage(Text.of(l.getPlayers(ctx.getSource().getServer())));
		}
		return 0;
	}
	
	public static boolean isLinked(ServerPlayerEntity p) {
		return linkManager.isLinked(p);
	}
	
	private int linkPlayerPair(CommandContext<ServerCommandSource> ctx) {
		ServerPlayerEntity p1;
		ServerPlayerEntity p2;
		try {
			p1 = EntityArgumentType.getPlayer(ctx, "player1");
			p2 = EntityArgumentType.getPlayer(ctx, "player2");
		} catch (CommandSyntaxException e) {
			ctx.getSource().sendError(Text.of(e.getMessage()));
			return -1;
		}
		
		return linkManager.linkPlayer(p1, p2) ? 0 : 1;
	}
	private int linkPlayers(CommandContext<ServerCommandSource> ctx) {
		try {
			List<ServerPlayerEntity> players = (List<ServerPlayerEntity>) EntityArgumentType.getPlayers(ctx, "players");
			ServerPlayerEntity p1 = players.remove(players.size() - 1);
			players.forEach(
				p -> linkManager.linkPlayer(p1, p)
			);
		} catch (CommandSyntaxException e) {
			ctx.getSource().sendError(Text.of(e.getMessage()));
			return -1;
		}
		return 0;
	}
	public int unlinkPlayers(CommandContext<ServerCommandSource> ctx) {
		try {
			ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
			linkManager.unlinkPlayer(player);
			return 0;
		} catch (CommandSyntaxException e) {
			ctx.getSource().sendError(Text.of(e.getMessage()));
			return -1;
		}
	}
	
}
