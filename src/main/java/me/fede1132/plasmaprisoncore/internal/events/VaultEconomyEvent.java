package me.fede1132.plasmaprisoncore.internal.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class VaultEconomyEvent extends Event implements Cancellable {
    private static final HandlerList handlerList = new HandlerList();
    private boolean cancel;
    private final Player player;
    private final double currentAmount;
    private final double newAmount;
    public VaultEconomyEvent(boolean isAsync, Player player, double currentAmount, double newAmount) {
        super(isAsync);
        this.player = player;
        this.currentAmount = currentAmount;
        this.newAmount = newAmount;
    }

    public Player getPlayer() {
        return player;
    }

    public double getCurrentAmount() {
        return currentAmount;
    }

    public double getNewAmount() {
        return newAmount;
    }

    @Override
    public String getEventName() {
        return "VaultEconomyEvent";
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
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
}
