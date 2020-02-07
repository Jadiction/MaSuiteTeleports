package dev.masa.masuiteteleports.core.handlers;

import dev.masa.masuitecore.core.channels.BungeePluginChannel;
import dev.masa.masuiteteleports.bungee.MaSuiteTeleports;
import dev.masa.masuiteteleports.core.services.TeleportRequestServiceOLD;
import dev.masa.masuiteteleports.core.objects.TeleportRequestType;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class TeleportHandler {

    public static List<TeleportRequestServiceOLD> requests = new ArrayList<>();
    public static HashMap<UUID, Boolean> lock = new HashMap<>();
    public static HashSet<UUID> toggles = new HashSet<>();

    private MaSuiteTeleports plugin;

    public TeleportHandler(MaSuiteTeleports plugin) {
        this.plugin = plugin;
    }

    public void teleportPlayerToPlayer(ProxiedPlayer sender, ProxiedPlayer receiver) {
        TeleportRequestServiceOLD request = getTeleportRequest(receiver);
        if (request == null || !request.getSender().equals(sender)) {
            teleport(sender, receiver);
            return;
        }
        if (request.getType().equals(TeleportRequestType.REQUEST_HERE)) {
            teleport(receiver, sender);
        } else if (request.getType().equals(TeleportRequestType.REQUEST_TO)) {
            teleport(sender, receiver);
        }
    }


    public void teleport(ProxiedPlayer sender, ProxiedPlayer receiver) {
        BungeePluginChannel bpc = new BungeePluginChannel(plugin, receiver.getServer().getInfo(),
                "MaSuiteTeleports",
                "PlayerToPlayer",
                sender.getName(),
                receiver.getName()
        );
        if (!sender.getServer().getInfo().getName().equals(receiver.getServer().getInfo().getName())) {
            sender.connect(plugin.getProxy().getServerInfo(receiver.getServer().getInfo().getName()));
           plugin.getProxy().getScheduler().schedule(plugin, bpc::send, plugin.config.load(null, "config.yml").getInt("teleportation-delay"), TimeUnit.MILLISECONDS);
        } else {
            bpc.send();
        }
    }

    public static TeleportRequestServiceOLD getTeleportRequest(ProxiedPlayer player) {
        for (TeleportRequestServiceOLD request : requests) {
            if (request.getReceiver().equals(player)) {
                return request;
            }
        }
        return null;
    }

}
