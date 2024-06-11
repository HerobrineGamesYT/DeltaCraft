package net.herobrine.deltacraft.events;

import net.herobrine.deltacraft.objects.inventory.Order;
import net.herobrine.deltacraft.objects.puzzles.utils.OrderStatus;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class OrderStatusUpdateEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final OrderStatus status;
    private final Order order;
    public OrderStatusUpdateEvent(OrderStatus status, Order order) {
        this.status = status;
        this.order = order;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public OrderStatus getStatus(){return status;}
    public Order getOrder(){return order;}


    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
