DeltaCraft is a Dungeons Minigame full of surprises.

![image](https://github.com/HerobrineGamesYT/DeltaCraft/assets/74119793/01891ce2-be7b-47ae-83bb-ce2e6972c73a)

I started work on this project mid-2023. It is currently unfinished, but there are a few complex systems I developed for the project that I would like to showcase here on this GitHub README.
I also have created a design plan for the project which you can check out here for more details on that side of things: https://docs.google.com/document/d/1mUifZ_70YPZHPjOcVTJim0I4ZGMtd8xHpW5iJzlzi6s/edit?usp=sharing 

From here on out, I'll keep this focused on the systems that are currently developed within the project, and explaining some of the code behind it. The current source is available in this repository.

Dependencies: 

[Mine] HerobrinePVP-CORE (required for all minigames)

[Mine] GameCore (required for all minigames)

[3rd Party] - NoteBlockAPI (HerobrinePVP-Core Dependency - I'm using a version that I modified to support pling/custom instruments in 1.8 and slightly optimize playback in certain cases.)

[3rd Party] - Citizens (used for custom NPCs)

[3rd Party] - WorldEdit (planned to be used for certain world-based operations, such as placing schematics for dungeon rooms- subject to change)

**Items and Abilities**

DeltaCraft contains a custom items system. All Items are stored within the ItemTypes enum, and have stats. They may or may not have an ability attached to them as well. 
There are multiple ability types, RIGHT_CLICK, LEFT_CLICK, and SNEAK. All available abilities, complete with a cost and cooldown, are configured in the ItemAbilities enum.
This allows the game to build an item with all of the correct lore and stats attached to it. When an item is created, the stats, name etc is all stored within the ItemStack's NBT data.
The real magic happens in the AbilityManager class, which is created with an instance of DeltaCraft. It will initialize all of the ability classes associated with each item.

Each ability class extends the ItemAbility superclass, which provides a basis for how abilities work - such as the cooldown and mana systems and provides a method that is required in each subclass.

This method is doAbility - which simply just contains the code for the ability's action, given that all the preconditions are met. All the checks for this are in the superclass, so literally, the only thing needed to code in here is the action.
This is a very sleek and scalable system and supports an unlimited amount of abilities.

There is also a SpecialCase interface- which is used when there is an ability that only works in specific "special cases". The methods this adds to an ability class are:

doesCasePass - The method that contains the check for the special case.
doNoPass - The method that runs when the special case is not passed.

The doAbility method does NOT change function- the ItemAbility superclass also checks for whether or not an ability has a special case- and will run the above two methods as necessary before executing the doAbility method.

A prime example of this is in the Teleport Wand ability. The special case here ensures that you do not teleport if blocks are too close to you. 
The Instant Transmission ability also sends a warning message to the player if their teleportation was cut short due to obstructing blocks.
Example: https://streamable.com/hdb5jw


**NPCs and Dialogue**

Each custom mob in Deltacraft is a "Character". Each NPC has a custom class that extends the abstract class "Character"- which contains the basic logic for entity management. Subclass instances are also created and managed by the CustomEntityManager.

The custom entities in DeltaCraft make use of the Citizens API- which allows me to create custom traits for their behavior. I have created 3 - the SequentialDialogueTrait, AggressiveTrait, and DimentioTrait.

*Sequential Dialogue Trait*

This trait allows for me to create NPCs with dialogue.

You can setup an individual NPC's dialogue map with the setupDialogueMap method.

The method takes an array of dialogue lines, the tick at which it will be said, and also the character and arena.

It looks like this:
![image](https://github.com/HerobrineGamesYT/DeltaCraft/assets/74119793/f87e25ec-3be7-49ce-8a26-1c14aa2edcee)

You can also set up an NPC's voice, by giving it a Minecraft sound effect with a specific pitch.

The class supports a few different types of dialogue: 

Click Say: When enabled, the NPC will speak its dialogue map whenever it is clicked by a player (Used for NPCs like the Dungeon Master)

Active Dialogue: When enabled, the NPC will speak its dialogue map whenever it is spawned (Used for Bosses)

Timed Say: Give an NPC a dialogue map, and it will say whatever you want using that sequence.

Say: Give an NPC something to say, and it will say it with all of your configured settings (character/voice).

![image](https://github.com/HerobrineGamesYT/DeltaCraft/assets/74119793/73ae6ec3-4837-44da-b7e9-5a381192ee6f)

The dialogue system will use the character type to automatically get the name of the NPC, and also determine whether or not it is a boss. 

For Boss NPCs, it will automatically change the color of the message, the prefix, and also make the dialogue display above the NPC's head with a hologram. This is similar to the Hypixel Skyblock dungeon bosses.

![image](https://github.com/HerobrineGamesYT/DeltaCraft/assets/74119793/81b6994f-6efa-4e68-951c-3b8f597996ce)


![image](https://github.com/HerobrineGamesYT/DeltaCraft/assets/74119793/edbfccb7-156d-4030-8360-039141f95592)


*Aggressive Trait && Dimentio Trait*

The Aggressive Trait is meant to be used for regular mobs who are aggressive towards the player. 

The Dimentio Trait is used for the behavior of the Dimentio Boss NPC specifically.

These types of NPCs have a custom targeting system, and also utilize the custom attacks system. This system is stored in the "attack" package- the abstract class "Attack" contains logic for multiple attack types such as particle beams and skulls- which is used by Dimentio in his DIMENTIO_BEAM and DIMENTIO_STAR attacks. This system can be used to create new attacks for any custom entity for the game!

Logic for NPC AI is stored inside of these traits- and debug messages are used to be able to see what the AI is doing on a tick-by-tick basis. A mod that compacts the Minecraft chat is recomended to make these easier to read.

An example of the above systems can be observed in this clip of the Dimentio boss: https://streamable.com/3uawtk


**Player Stats & Equipment**

All players in Deltacraft have custom stats... Which may be familiar to all Hypixel Skyblock players.

Your stats are based off of a base value of 1000 HP and 50 defense, and your equipment- which is determined by what class you're playing. The equipment system also supports held items - so for example, a mage weapon could have a stat bonus of +100 Intelligence, and it would increase your intelligence automatically while being held. When you stop holding the item, the game will also remember what your mana was at before you stopped holding the item - so as long as you haven't used anymore mana, when you switch back it will continue regenerating your mana from where you left off. I observed this behavior in Hypixel Skyblock and decided to try to implement it into this project as well.

![image](https://github.com/HerobrineGamesYT/DeltaCraft/assets/74119793/ac3bbb61-c47f-48dc-ac7f-b65a6f0a93a1)


![image](https://github.com/HerobrineGamesYT/DeltaCraft/assets/74119793/e981c3cd-3d21-4e5b-b847-f89a40794583)


Health: The current amount of health a player has. You will regenerate 1-3% of your health every second, up to the maximum.

Max Health: The maximum amount of health a player can have. This is to cap health regeneration.

The vanilla health bar will also scale automatically based on your Health and Max Health values.

Defense: Reduces player damage. 

Damage Reduction  % = Defense / (Defense + 100)

Incoming Damage = Damage - (Damage*Damage Reduction %)

All damage (including fall damage) will automatically use the stats system and the above calculation.

Mana: Used when a player executes an ability that requires it.

Intelligence: The maximum amount of Mana a player can hold.

Strength: Increases how much damage you deal. (For now, I'm simply using Damage * Strength. It will be changed in the future)

**Objects**

DeltaCraft has a custom objects system- which is intended to be used all accross the whole gamemode.

The two currently developed objects are the Portal CLuster and the Pure Heart.

*Portal Cluster*

The Portal Cluster will be used all accross the Dungeon to create a way to teleport between different rooms. It will also be used during the Dimentio boss fight to teleport players to the next area.

This is what a Portal Cluster currently looks like:
![image](https://github.com/HerobrineGamesYT/DeltaCraft/assets/74119793/79e7a6df-ea6e-46de-bca9-1a4da107c4d8)

All Portal Clusters have a set destination, and a teleport cooldown of 3s. Optionally, you can enable a second Portal Cluster to spawn at its destination set to go back where you came from.

You can see the Portal Cluster in action 48 seconds into this showcase clip: https://streamable.com/epydan


*Pure Heart*

The Pure Heart will be used exclusivley within the Dimentio Boss fight. There are 8 different colored variations of the Pure Heart, and players will be able to obtain them as they progress through the boss fight. Once all 8 are obtained and placed into *Pure Heart Pillars*, Dimentio will go into a Fight State and become vulnerable.

The Pure Heart Object floats and slowly twirls around, waiting to be picked up by the player, similar to a Dungeon Key.

![image](https://github.com/HerobrineGamesYT/DeltaCraft/assets/74119793/a0226a8c-6d28-4626-a53a-0ceada65443a)
![image](https://github.com/HerobrineGamesYT/DeltaCraft/assets/74119793/10af2ae4-f3c5-4bec-9b8f-c887ef12a92d)


