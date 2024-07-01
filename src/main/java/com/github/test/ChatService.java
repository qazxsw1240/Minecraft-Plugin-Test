package com.github.test;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChatService implements Listener, ChatRenderer {

    private static final ChatService INSTANCE = new ChatService();

    private ChatService() {
    }

    public static ChatService getInstance() {
        return INSTANCE;
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        event.renderer(this);
        player.sendMessage(event.message());
    }

    @Override
    public Component render(
            Player source,
            Component sourceDisplayName,
            Component message,
            Audience viewer) {
        return sourceDisplayName
                .append(Component.text(": "))
                .append(message);
    }
}
