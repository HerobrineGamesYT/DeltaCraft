package net.herobrine.deltacraft.items;

import net.herobrine.deltacraft.items.AbilityTypes;
import net.herobrine.deltacraft.items.ItemAbilities;
import net.herobrine.deltacraft.items.ItemTypes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ItemAbilityInfo {

    ItemAbilities ability();
    ItemTypes item();



}
