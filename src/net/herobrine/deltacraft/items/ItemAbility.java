package net.herobrine.deltacraft.items;

import net.herobrine.deltacraft.DeltaCraft;
import net.herobrine.gamecore.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.lang.Class;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.UUID;

public abstract class ItemAbility implements Listener {


    // ability item is using
    protected ItemAbilities ability;
    //item type of item using ability, given when initializing the ability class.
    protected ItemTypes item;

    //arena id
    protected int id;

    // There will be 1 Item Ability class per DeltaCraft arena, per item that uses its ability...

    protected HashMap<UUID, Long> cooldown;

    protected Arena arena;
    public ItemAbility(ItemAbilities ability, ItemTypes item, int id) {
        this.ability = ability;
        this.item = item;
        this.id = id;
        this.cooldown = new HashMap<UUID, Long>();
        this.arena = Manager.getArena(id);
        Bukkit.getPluginManager().registerEvents(this, DeltaCraft.getInstance());

    }


    // The doAbility method is REQUIRED for all abilities. To make things flexible, an ability will have 2-3 listeners
    // for each ability type. They will check if the ability type is correct and act based upon that. This way, we don't have to add more code when
    // changing the ability type, but having it in one method will allow us to not duplicate our code at the same time and make each ability class
    // unnecessarily long.
    // **WHAT ABOUT PASSIVE ABILITIES??**

    // Passive Abilities are considered special cases. We will rarely use them except for certain armor bonuses. In a lot of cases, an Item Ability for "Passive"
    // could even just be flavor text for the real functional usage of the item. Abilities of this type will not use multiple listeners and will be
    // implemented on a case-by-case basis.
    public abstract void doAbility(Player player);


    public void executeAbility(Player player) {
        if(!shouldDoAbility(player)) return;
        doAbility(player);
    }

    public boolean hasManaCost() {return ability.getManaCost() > 0;}

    public boolean hasCooldown() {return ability.getCooldown() > 0;}

    public HashMap<UUID, Long> getCooldown() {return cooldown;}

    public int getId() {return id;}


    public ItemAbilities getAbility() {return ability;}

    public ItemTypes getItem() {return item;}


    public boolean shouldDoAbility(Player player) {
        if (!arena.getState().equals(GameState.LIVE)) {
            player.sendMessage(ChatColor.RED + "The game isn't live, so you can't use the ability!");
            return false;
        }


        int health = arena.getDeltaGame().getStats(player).getHealth();
        int intelligence = arena.getDeltaGame().getStats(player).getIntelligence();
        int mana = arena.getDeltaGame().getStats(player).getMana();

        if (this.hasManaCost() && arena.getDeltaGame().getStats(player).getMana() < this.getAbility().getManaCost()) {
            player.sendMessage(ChatColor.RED + "Not enough mana!");
            player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1f, 2f);
            GameCoreMain.getInstance().sendActionBar(player, "&c&lNOT ENOUGH MANA");
            return false;
        }

        if (this.hasCooldown() && getCooldown().containsKey(player.getUniqueId())) {


            if(System.currentTimeMillis() - getCooldown().get(player.getUniqueId()) < this.getAbility().getCooldown()) {
                player.sendMessage(ChatColor.RED + "This ability is currently on cooldown!");
                player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1f, 2f);
                GameCoreMain.getInstance().sendActionBar(player, "&c&lON COOLDOWN");

                return false;
            }
        }


        if (this.getAbility().hasSpecialCase()) {
            try {
                Class<? extends ItemAbility> subClass = this.getClass().asSubclass(this.getClass());

                Method doesCasePassMethod = subClass.getDeclaredMethod("doesCasePass", Player.class);
                Method noPassMethod = subClass.getDeclaredMethod("doNoPass", Player.class);

                boolean passCase = (boolean) doesCasePassMethod.invoke(this, player);

                if(!passCase) {
                   noPassMethod.invoke(this, player);
                   return false;
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                player.sendMessage(ChatColor.RED + "There was an error when checking for the special case! " + e.getCause());
                e.printStackTrace();
            }
        }

        if (this.hasManaCost()) {
            arena.getDeltaGame().getStats(player).setMana(mana - this.getAbility().getManaCost());
            GameCoreMain.getInstance().sendActionBar(player, "&c" + health + "❤   " + "&b-" + this.getAbility().getManaCost() + " Mana (" + this.getAbility().getDisplay() + "&b)   " + mana + "/" + intelligence + "✎ Mana");
        }
        if (this.hasCooldown()) getCooldown().put(player.getUniqueId(), System.currentTimeMillis());

        return true;
    }

    // We'll unregister all the listeners when needed using this.
    public void remove() {HandlerList.unregisterAll(this);}

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (!Manager.isPlaying(player)) return;

        if (!arena.getGame(arena.getID()).equals(Games.DELTARUNE)) return;
        if (arena.getID() != this.getId()) return;

        boolean shouldAct = player.getItemInHand().isSimilar(ItemTypes.build(this.getItem()));

        boolean isRightClick = e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK);

        boolean isLeftClick = e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.LEFT_CLICK_BLOCK);

        if (this.getAbility().getType().equals(AbilityTypes.RIGHT_CLICK) && isRightClick && shouldAct) executeAbility(player);

        if (this.getAbility().getType().equals(AbilityTypes.LEFT_CLICK) && isLeftClick && shouldAct) executeAbility(player);

    }


    @EventHandler
    public void onSneak(PlayerToggleSneakEvent e) {

    }


}
