package me.fede1132.plasmaprisoncore.addons.basics.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.math.BigDecimal;

public class TokenChangeEvent extends Event implements Cancellable {
    public enum ChangeAction {
        ADD,
        REMOVE,
        SET
    }
    private boolean cancelled;
    private final static HandlerList handlerList = new HandlerList();
    // vars
    private final Player player;
    private final ChangeAction action;
    private BigDecimal difference;

    public TokenChangeEvent(Player player, ChangeAction action, BigDecimal difference) {
        this.player = player;
        this.action = action;
        this.difference = difference;
    }

    public Player getPlayer() {
        return player;
    }

    public ChangeAction getAction() {
        return action;
    }

    public BigDecimal getDifference() {
        return difference;
    }

    public void setDifference(BigDecimal newAmount) {
        this.difference = newAmount;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled=b;
    }
}
