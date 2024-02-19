DeltaCraft is a Dungeons Minigame full of surprises.

![image](https://github.com/HerobrineGamesYT/DeltaCraft/assets/74119793/01891ce2-be7b-47ae-83bb-ce2e6972c73a)

I started work on this project mid-2023. It is currently unfinished, but there are a few complex systems I developed for the project that I would like to showcase here on this GitHub README.
I also have created a design plan for the project which you can check out here for more details on that side of things: https://docs.google.com/document/d/1mUifZ_70YPZHPjOcVTJim0I4ZGMtd8xHpW5iJzlzi6s/edit?usp=sharing 

From here on out, I'll keep this focused on the systems that are currently developed within the project, and explaining the code behind it.

**Items and Abilities**

DeltaCraft contains a custom items system. All Items are stored within the ItemTypes enum, and have stats. They may or may not have an ability attached to them as well. 
There are multiple ability types, RIGHT_CLICK, LEFT_CLICK, and SNEAK. All available abilities, complete with a cost and cooldown, are configured in the ItemAbilities enum.
This allows the game to build an item with all of the correct lore and stats attached to it. 
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
Example: https://streamable.com/cu6gbq


**NPCs and Dialogue**




