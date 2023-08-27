package dev.plex.command.impl;

import com.google.common.collect.ImmutableList;
import dev.plex.Plex;
import dev.plex.cache.DataUtils;
import dev.plex.command.PlexCommand;
import dev.plex.command.annotation.CommandParameters;
import dev.plex.command.annotation.CommandPermissions;
import dev.plex.command.exception.PlayerNotFoundException;
import dev.plex.command.source.RequiredCommandSource;
import dev.plex.player.PlexPlayer;
import dev.plex.punishment.Punishment;
import dev.plex.punishment.PunishmentType;
import dev.plex.rank.enums.Rank;
import dev.plex.util.BungeeUtil;
import dev.plex.util.PlexLog;
import dev.plex.util.PlexUtils;
import dev.plex.util.TimeUtils;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@CommandParameters(name = "gommeh", usage = "/<command> <player>", aliases = "gomban", description = "Gommeh's special command")
@CommandPermissions(level = Rank.ADMIN, permission = "plex.ban", source = RequiredCommandSource.IN_GAME)

public class GommehCMD extends PlexCommand
{
    @Override
    protected Component execute(@NotNull CommandSender sender, @Nullable Player playerSender, String[] args) throws InterruptedException {
        if (!playerSender.getName().equals("Gommeh")) {
            // print out a message saying they cant run this command
            PlexUtils.broadcast(mmString("<red>" + playerSender.getName() + " just tried to use Gommeh's special command!"));
            return messageComponent("gommehBan");
        }

        if (args.length == 0)
        {
            return usage();
        }

        final PlexPlayer plexPlayer = DataUtils.getPlayer(args[0]);

        if (plexPlayer == null)
        {
            throw new PlayerNotFoundException();
        }

        Player player = Bukkit.getPlayer(plexPlayer.getUuid());

        // display messages in chat, maybe wait a little bit between each one like "You hear the skies rumbling around (name)..."

        playerSender.chat("Hey " + player.getName() + ", I have a very special present for you!");
        TimeUnit.MILLISECONDS.sleep(1500);
        player.chat("Oooh, what is it Gommeh?");
        TimeUnit.MILLISECONDS.sleep(2000);
        PlexUtils.broadcast("The skies around " + player.getName() + " begin to rumble...");
        TimeUnit.MILLISECONDS.sleep(500);
        playerSender.chat("It's my banhammer, asshole.");
        player.chat("oh shit...");

        if (plugin.getSystem().equalsIgnoreCase("ranks"))
        {
            if (isAdmin(plexPlayer))
            {
                if (!isConsole(sender))
                {
                    assert playerSender != null;
                    PlexPlayer plexPlayer1 = getPlexPlayer(playerSender);
                    if (!plexPlayer1.getRankFromString().isAtLeast(plexPlayer.getRankFromString()))
                    {
                        return messageComponent("higherRankThanYou");
                    }
                }
            }
        }

        playerSender.chat("YOUVE PISSED ME OFF FOR THE LAST TIME, " + player.getName());
        TimeUnit.MILLISECONDS.sleep(500);
        player.chat("NO GOD NO PLEASE HAVE MERCY ON ME");

        // deop
        if (plugin.getSystem().equalsIgnoreCase("ranks")) player.setOp(false);

        // set gamemode to survival + clear inv
        player.setGameMode(GameMode.SURVIVAL);
        player.getInventory().clear();

        // strike lightning
        strikeLightning(player);
        TimeUnit.SECONDS.sleep(3);
        strikeLightning(player);

        playerSender.chat("FUCK OFF, " + player.getName() + ", YOU PIECE OF SHIT");
        TimeUnit.SECONDS.sleep(1);
        player.chat("NONONONONONONONO");


        plugin.getPunishmentManager().isAsyncBanned(plexPlayer.getUuid()).whenComplete((aBoolean, throwable) ->
        {
            if (aBoolean)
            {
                send(sender, messageComponent("playerBanned"));
                return;
            }
            String reason;
            Punishment punishment = new Punishment(plexPlayer.getUuid(), getUUID(sender));
            punishment.setType(PunishmentType.BAN);
            if (args.length > 1)
            {
                reason = StringUtils.join(args, " ", 1, args.length);
                punishment.setReason(reason);
            }
            else
            {
                punishment.setReason("THOU SHALT NOT PISS OFF GOMMEH");
            }
            punishment.setPunishedUsername(plexPlayer.getName());
            ZonedDateTime date = ZonedDateTime.now(ZoneId.of(TimeUtils.TIMEZONE));
            punishment.setEndDate(date.plusDays(1));
            punishment.setCustomTime(false);
            punishment.setActive(!isAdmin(plexPlayer));
            if (player != null)
            {
                punishment.setIp(player.getAddress().getAddress().getHostAddress().trim());
            }
            plugin.getPunishmentManager().punish(plexPlayer, punishment);
            PlexUtils.broadcast(messageComponent("gommehBanningPlayer", plexPlayer.getName()));
            Bukkit.getScheduler().runTask(Plex.get(), () ->
            {
                if (player != null)
                {
                    BungeeUtil.kickPlayer(player, Punishment.generateBanMessage(punishment));
                }
            });
            PlexLog.debug("(From /gommeh command) PunishedPlayer UUID: " + plexPlayer.getUuid());
        });

        return null;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException
    {
        return args.length == 1 && silentCheckRank(sender, Rank.ADMIN, "plex.ban") ? PlexUtils.getPlayerNameList() : ImmutableList.of();
    }

    public void strikeLightning(@NotNull Player player) {
        final Location targetPos = player.getLocation();
        final World world = player.getWorld();

        for (int x = -1; x <= 1; x++)
        {
            for (int z = -1; z <= 1; z++)
            {
                final Location strike_pos = new Location(world, targetPos.getBlockX() + x, targetPos.getBlockY(), targetPos.getBlockZ() + z);
                world.strikeLightning(strike_pos);
            }
        }
    }
}
