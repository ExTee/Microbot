/*
 * Copyright (c) 2020, Zoinkwiz
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.microbot.questhelper.helpers.quests.shadowofthestorm;

import net.runelite.client.plugins.microbot.questhelper.bank.banktab.BankSlotIcons;
import net.runelite.client.plugins.microbot.questhelper.collections.ItemCollections;
import net.runelite.client.plugins.microbot.questhelper.panel.PanelDetails;
import net.runelite.client.plugins.microbot.questhelper.questhelpers.BasicQuestHelper;
import net.runelite.client.plugins.microbot.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.microbot.questhelper.requirements.Requirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.conditional.Conditions;
import net.runelite.client.plugins.microbot.questhelper.requirements.item.ItemOnTileRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.item.ItemRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.player.SkillRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.quest.QuestRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.util.Operation;
import net.runelite.client.plugins.microbot.questhelper.requirements.var.VarbitRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.zone.Zone;
import net.runelite.client.plugins.microbot.questhelper.requirements.zone.ZoneRequirement;
import net.runelite.client.plugins.microbot.questhelper.rewards.ItemReward;
import net.runelite.client.plugins.microbot.questhelper.rewards.QuestPointReward;
import net.runelite.client.plugins.microbot.questhelper.steps.*;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;

import java.util.*;

public class ShadowOfTheStorm extends BasicQuestHelper
{
	//Items Required
	ItemRequirement darkItems, silverlight, strangeImplement, blackMushroomInk, pestle, vial, silverBar, silverlightHighlighted, blackMushroomHighlighted,
		silverlightDyedEquipped, sigilMould, silverlightDyed, strangeImplementHighlighted, sigil, book, bookHighlighted,
		sigilHighlighted, sigil2;

	//Items Recommended
	ItemRequirement combatGear, coinsForCarpet, alKharidTeleport;

	Requirement inRuin, inThroneRoom,talkedToGolem, talkedToMatthew, inCircleSpot, sigilNearby, evilDaveMoved, baddenMoved,
		reenMoved, golemMoved, golemRejected, golemReprogrammed,
		inSecondCircleSpot;

	DetailedQuestStep talkToReen, talkToBadden, pickMushroom, dyeSilverlight, goIntoRuin, pickUpStrangeImplement, talkToEvilDave, enterPortal,
		talkToDenath, talkToJennifer, talkToMatthew, smeltSigil, talkToGolem, readBook, enterRuinAfterBook, enterPortalAfterBook,
		talkToMatthewAfterBook, standInCircle, pickUpSigil, leavePortal, tellDaveToReturn, pickUpImplementAfterRitual,
		goUpToBadden, talkToBaddenAfterRitual, talkToReenAfterRitual, talkToTheGolemAfterRitual, useImplementOnGolem, pickUpSigil2,
		talkToGolemAfterReprogramming, enterRuinAfterRecruiting, enterPortalAfterRecruiting, talkToMatthewToStartFight, killDemon,
		standInCircleAgain, enterRuinNoDark, enterRuinForRitual, enterPortalForRitual, enterRuinForDave, enterPortalForFight,
		enterRuinForFight, unequipDarklight;

	DetailedOwnerStep searchKiln;

	IncantationStep readIncantation, incantRitual;

	//Zones
	Zone ruin, throneRoom, circleSpot, secondCircleSpot;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		Map<Integer, QuestStep> steps = new HashMap<>();
		initializeRequirements();
		setupConditions();
		setupSteps();

		steps.put(0, talkToReen);
		steps.put(10, talkToBadden);

		ConditionalStep infiltrateCult = new ConditionalStep(this, pickMushroom);
		infiltrateCult.addStep(new Conditions(silverlightDyed, strangeImplement, inRuin), talkToEvilDave);
		infiltrateCult.addStep(new Conditions(silverlightDyed, inRuin), pickUpStrangeImplement);
		infiltrateCult.addStep(silverlightDyed, goIntoRuin);
		infiltrateCult.addStep(blackMushroomHighlighted, dyeSilverlight);
		steps.put(20, infiltrateCult);

		ConditionalStep goTalkToDenath = new ConditionalStep(this, enterRuinNoDark);
		goTalkToDenath.addStep(inThroneRoom, talkToDenath);
		goTalkToDenath.addStep(inRuin, enterPortal);
		steps.put(30, goTalkToDenath);

		ConditionalStep completeSubTasks = new ConditionalStep(this, enterRuinNoDark);
		completeSubTasks.addStep(new Conditions(book, sigil, inThroneRoom), talkToMatthewAfterBook);
		completeSubTasks.addStep(new Conditions(book, sigil, inRuin), enterPortalAfterBook);
		completeSubTasks.addStep(new Conditions(book, sigil), enterRuinAfterBook);
		completeSubTasks.addStep(new Conditions(talkedToGolem, sigil), searchKiln);
		completeSubTasks.addStep(new Conditions(talkedToMatthew, sigil), talkToGolem);
		completeSubTasks.addStep(new Conditions(talkedToMatthew, sigilMould), smeltSigil);
		completeSubTasks.addStep(new Conditions(inThroneRoom, sigilMould), talkToMatthew);
		completeSubTasks.addStep(inThroneRoom, talkToJennifer);
		completeSubTasks.addStep(inRuin, enterPortal);
		steps.put(40, completeSubTasks);
		steps.put(50, completeSubTasks);
		steps.put(60, completeSubTasks);

		ConditionalStep startRitual = new ConditionalStep(this, enterRuinForRitual);
		startRitual.addStep(inThroneRoom, talkToMatthewAfterBook);
		startRitual.addStep(inRuin, enterPortalForRitual);
		steps.put(70, startRitual);

		ConditionalStep performRitual = new ConditionalStep(this, enterRuinForRitual);
		performRitual.addStep(inCircleSpot, readIncantation);
		performRitual.addStep(inThroneRoom, standInCircle);
		performRitual.addStep(inRuin, enterPortalForRitual);
		steps.put(80, performRitual);

		ConditionalStep prepareForSecondRitual = new ConditionalStep(this, enterRuinForDave);
		prepareForSecondRitual.addStep(sigilNearby, pickUpSigil);
		prepareForSecondRitual.addStep(new Conditions(evilDaveMoved, baddenMoved, reenMoved, golemMoved, inThroneRoom), talkToMatthewToStartFight);
		prepareForSecondRitual.addStep(new Conditions(evilDaveMoved, baddenMoved, reenMoved, golemMoved, inRuin), enterPortalAfterRecruiting);
		prepareForSecondRitual.addStep(new Conditions(evilDaveMoved, baddenMoved, reenMoved, golemMoved), enterRuinAfterRecruiting);
		prepareForSecondRitual.addStep(new Conditions(evilDaveMoved, baddenMoved, reenMoved, golemReprogrammed), talkToGolemAfterReprogramming);
		prepareForSecondRitual.addStep(new Conditions(evilDaveMoved, baddenMoved, reenMoved, golemRejected), useImplementOnGolem);
		prepareForSecondRitual.addStep(new Conditions(evilDaveMoved, baddenMoved, reenMoved), talkToTheGolemAfterRitual);
		prepareForSecondRitual.addStep(new Conditions(evilDaveMoved, baddenMoved), talkToReenAfterRitual);
		prepareForSecondRitual.addStep(new Conditions(strangeImplement, evilDaveMoved, inRuin), goUpToBadden);
		prepareForSecondRitual.addStep(new Conditions(evilDaveMoved, inRuin), pickUpImplementAfterRitual);
		prepareForSecondRitual.addStep(new Conditions(evilDaveMoved), talkToBaddenAfterRitual);
		prepareForSecondRitual.addStep(inRuin, tellDaveToReturn);
		prepareForSecondRitual.addStep(inThroneRoom, leavePortal);

		steps.put(90, prepareForSecondRitual);
		steps.put(100, prepareForSecondRitual);

		ConditionalStep summonAgrith = new ConditionalStep(this, enterRuinAfterRecruiting);
		summonAgrith.addStep(inSecondCircleSpot, incantRitual);
		summonAgrith.addStep(inThroneRoom, standInCircleAgain);
		summonAgrith.addStep(inRuin, enterPortalAfterRecruiting);
		steps.put(110, summonAgrith);

		ConditionalStep defeatAgrith = new ConditionalStep(this, enterRuinForFight);
		defeatAgrith.addStep(inThroneRoom, killDemon);
		defeatAgrith.addStep(inRuin, enterPortalForFight);
		steps.put(120, defeatAgrith);

		steps.put(124, unequipDarklight);

		return steps;
	}

	@Override
	protected void setupZones()
	{
		ruin = new Zone(new WorldPoint(2706, 4881, 0), new WorldPoint(2738, 4918, 0));
		throneRoom = new Zone(new WorldPoint(2709, 4879, 2), new WorldPoint(2731, 4919, 2));
		circleSpot = new Zone(new WorldPoint(2718, 4902, 2), new WorldPoint(2718, 4902, 2));
		secondCircleSpot = new Zone(new WorldPoint(2720, 4903, 2), new WorldPoint(2720, 4903, 2));
	}

	@Override
	protected void setupRequirements()
	{
		darkItems = new ItemRequirement("pieces of black clothing", ItemID.AGRITH_DESERT_SHIRT_DYED, 3, true).isNotConsumed().doNotAggregate();
		darkItems.addAlternates(ItemID.AGRITH_DESERT_ROBE_DYED, ItemID.BLACK_CHAINBODY, ItemID.BLACK_PLATEBODY, ItemID.BLACK_PLATELEGS, ItemID.BLACK_FULL_HELM, ItemID.BLACK_MED_HELM, ItemID.HUNDRED_GAUNTLETS_LEVEL_5, ItemID.PRIEST_GOWN, ItemID.PRIEST_ROBE, ItemID.MYSTIC_HAT_DARK,
			ItemID.MYSTIC_ROBE_BOTTOM_DARK, ItemID.MYSTIC_ROBE_TOP_DARK, ItemID.SECRET_GHOST_BOOTS, ItemID.SECRET_GHOST_CLOAK, ItemID.SECRET_GHOST_GLOVES, ItemID.SECRET_GHOST_HAT, ItemID.SECRET_GHOST_TOP, ItemID.SECRET_GHOST_BOTTOM, ItemID.BLACKROBEBOTTOM, ItemID.BLACKROBETOP, ItemID.ANTISANTA_BOOTS,
			ItemID.ANTISANTA_GLOVES, ItemID.ANTISANTA_JACKET, ItemID.ANTISANTA_MASK, ItemID.ANTISANTA_PANTS, ItemID.BLACKWIZHAT, ItemID.BLACK_CAPE, ItemID.BLACK_PARTYHAT, ItemID.HALLOWEENMASK_BLACK, ItemID.DRAGONMASK_BLACK, ItemID.BLACK_UNICORN_MASK, ItemID.BLACK_DEMON_MASK,
			ItemID.BLACK_DRAGONHIDE_BODY, ItemID.BLACK_DRAGONHIDE_CHAPS, ItemID.BLACK_DRAGON_VAMBRACES, ItemID.BLACK_ROBE, ItemID.GRACEFUL_BOOTS_HALLOWED, ItemID.GRACEFUL_CAPE_HALLOWED, ItemID.GRACEFUL_GLOVES_HALLOWED, ItemID.GRACEFUL_HOOD_HALLOWED, ItemID.GRACEFUL_LEGS_HALLOWED, ItemID.GRACEFUL_TOP_HALLOWED);
		silverlight = new ItemRequirement("Silverlight", ItemID.SILVERLIGHT).isNotConsumed();
		silverlight.setTooltip("You can get another from Father Reen in Al Kharid if you've lost it");
		silverlight.addAlternates(ItemID.AGRITH_SILVERLIGHT_DYED); // silverlight dyed black
		strangeImplement = new ItemRequirement("Strange implement", ItemID.GOLEM_GOLEMKEY);
		strangeImplement.setTooltip("You can find another in the underground of Uzer");
		strangeImplementHighlighted = new ItemRequirement("Strange implement", ItemID.GOLEM_GOLEMKEY);
		strangeImplementHighlighted.setHighlightInInventory(true);
		strangeImplementHighlighted.setTooltip("You can find another in the underground of Uzer");
		blackMushroomInk = new ItemRequirement("Black mushroom ink", ItemID.GOLEM_INK);
		pestle = new ItemRequirement("Pestle and mortar", ItemID.PESTLE_AND_MORTAR).isNotConsumed();
		vial = new ItemRequirement("Vial", ItemID.VIAL_EMPTY);
		silverBar = new ItemRequirement("Silver bar", ItemID.SILVER_BAR);
		blackMushroomHighlighted = new ItemRequirement("Black mushroom", ItemID.GOLEM_MUSHROOM);
		blackMushroomHighlighted.setHighlightInInventory(true);
		silverlightHighlighted = new ItemRequirement("Silverlight", ItemID.SILVERLIGHT);
		silverlightHighlighted.setHighlightInInventory(true);
		silverlightDyed = new ItemRequirement("Silverlight (dyed)", ItemID.AGRITH_SILVERLIGHT_DYED);
		silverlightDyedEquipped = new ItemRequirement("Silverlight (dyed)", ItemID.AGRITH_SILVERLIGHT_DYED, 1, true);
		sigilMould = new ItemRequirement("Demonic sigil mould", ItemID.AGRITH_SIGIL_MOULD);
		combatGear = new ItemRequirement("Combat gear + potions", -1, -1).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
		coinsForCarpet = new ItemRequirement("Coins or more for carpet rides", ItemCollections.COINS, 400);
		sigil = new ItemRequirement("Demonic sigil", ItemID.AGRITH_SIGIL);
		sigil.setTooltip("You can make another if needed with the demonic sigil mould");
		sigil2 = new ItemRequirement("Demonic sigil", ItemID.AGRITH_SIGIL, 2);
		sigil2.setTooltip("You can make another if needed with the demonic sigil mould (which you can get from Jennifer)");
		sigilHighlighted = new ItemRequirement("Demonic sigil", ItemID.AGRITH_SIGIL);
		sigilHighlighted.setHighlightInInventory(true);
		book = new ItemRequirement("Demonic tome", ItemID.AGRITH_BOOK);
		bookHighlighted = new ItemRequirement("Demonic tome", ItemID.AGRITH_BOOK);
		bookHighlighted.setHighlightInInventory(true);

		// Recommended
		alKharidTeleport = new ItemRequirement("Al Kharid Teleport", ItemCollections.AMULET_OF_GLORIES);
		alKharidTeleport.addAlternates(ItemCollections.RING_OF_DUELINGS);
	}

	private void setupConditions()
	{
		inRuin = new ZoneRequirement(ruin);
		inThroneRoom = new ZoneRequirement(throneRoom);
		inCircleSpot = new ZoneRequirement(circleSpot);
		inSecondCircleSpot = new ZoneRequirement(secondCircleSpot);

		talkedToMatthew = new VarbitRequirement(VarbitID.AGRITH_QUEST, 50, Operation.GREATER_EQUAL);
		talkedToGolem = new VarbitRequirement(VarbitID.AGRITH_QUEST, 60, Operation.GREATER_EQUAL);
		sigilNearby = new ItemOnTileRequirement(sigil);
		evilDaveMoved = new VarbitRequirement(VarbitID.AGRITH_CONVINCED_DAVE, 2, Operation.GREATER_EQUAL);
		baddenMoved = new VarbitRequirement(VarbitID.AGRITH_BADDEN_UZER, 2, Operation.GREATER_EQUAL);
		reenMoved = new VarbitRequirement(VarbitID.AGRITH_REEN_UZER, 2, Operation.GREATER_EQUAL);
		golemRejected = new VarbitRequirement(VarbitID.AGRITH_CONVINCED_GOLEM, 1, Operation.GREATER_EQUAL);
		golemReprogrammed = new VarbitRequirement(VarbitID.AGRITH_CONVINCED_GOLEM, 2, Operation.GREATER_EQUAL);
		golemMoved = new VarbitRequirement(VarbitID.AGRITH_CONVINCED_GOLEM, 3, Operation.GREATER_EQUAL);
	}

	private void setupSteps()
	{
		talkToReen = new NpcStep(this, NpcID.AGRITH_REEN, new WorldPoint(3270, 3159, 0), "Talk to Father Reen outside Al Kharid bank.");
		talkToReen.addDialogStep("That's me!");
		talkToReen.addDialogStep("Yes.");
		talkToBadden = new NpcStep(this, NpcID.AGRITH_BADDEN, new WorldPoint(3490, 3090, 0), "Talk to Father Badden in Uzer.", silverlight, darkItems);
		talkToBadden.addDialogSteps("Uzer", "Reen sent me.", "So what do you want me to do?", "How can I do that?");
		pickMushroom = new ObjectStep(this, ObjectID.GOLEM_BLACK_MUSHROOMS, new WorldPoint(3495, 3088, 0), "Pick up some black mushrooms.");
		dyeSilverlight = new DetailedQuestStep(this, "Use the black mushrooms on Silverlight.", silverlightHighlighted, blackMushroomHighlighted);
		goIntoRuin = new ObjectStep(this, ObjectID.GOLEM_INSIDESTAIRS_TOP, new WorldPoint(3493, 3090, 0), "Enter the Uzer ruins.", silverlightDyedEquipped, darkItems);

		pickUpStrangeImplement = new DetailedQuestStep(this, new WorldPoint(2713, 4913, 0), "Pick up the strange implement in the north west corner of the ruin.", strangeImplement);
		talkToEvilDave = new NpcStep(this, NpcID.AGRITH_DAVE, new WorldPoint(2721, 4911, 0), "Talk to Evil Dave with the dyed Silverlight and 3 dark clothing items equipped.", silverlightDyedEquipped, darkItems);
		talkToEvilDave.addDialogSteps("I want to join your group.", "I'm evil!");
		enterPortal = new ObjectStep(this, ObjectID.GOLEM_PORTAL, new WorldPoint(2722, 4913, 0), "Enter the portal.");
		enterRuinNoDark = new ObjectStep(this, ObjectID.GOLEM_INSIDESTAIRS_TOP, new WorldPoint(3493, 3090, 0), "Enter the Uzer ruins.");
		talkToDenath = new NpcStep(this, NpcID.AGRITH_DENATH, new WorldPoint(2720, 4912, 2), "Talk to Denath next to the throne.");
		talkToDenath.addDialogStep("What do I have to do?");
		talkToDenath.addSubSteps(enterRuinNoDark, enterPortal);
		talkToJennifer = new NpcStep(this, NpcID.AGRITH_JENNIFER, new WorldPoint(2723, 4901, 2), "Talk to Jennifer.");
		talkToJennifer.addDialogStep("Do you have the demonic sigil mould?");
		talkToMatthew = new NpcStep(this, NpcID.AGRITH_MATTHEW, new WorldPoint(2727, 4897, 2), "Talk to Matthew.");
		talkToMatthew.addDialogStep("Do you know what happened to Josef?");
		smeltSigil = new DetailedQuestStep(this, "Travel to any furnace with the sigil mould and silver bar and smelt a sigil.", silverBar, sigilMould);
		smeltSigil.addTeleport(alKharidTeleport);
		talkToGolem = new NpcStep(this, NpcID.GOLEM_FIXED_GOLEM, new WorldPoint(3485, 3088, 0), "Talk to the Golem in Uzer.", silverlightDyed, sigil, combatGear);
		talkToGolem.addDialogStep("Uzer");
		talkToGolem.addDialogStep("Did you see anything happen last night?");
		searchKiln = new SearchKilns(this);
		readBook = new DetailedQuestStep(this, "Read the book.", bookHighlighted);
		enterRuinAfterBook = new ObjectStep(this, ObjectID.GOLEM_INSIDESTAIRS_TOP, new WorldPoint(3493, 3090, 0), "Enter the Uzer ruins.", silverlightDyed, book, sigil);
		enterPortalAfterBook = new ObjectStep(this, ObjectID.GOLEM_PORTAL, new WorldPoint(2722, 4913, 0), "Enter the portal.", book, sigil);

		talkToMatthewAfterBook = new NpcStep(this, NpcID.AGRITH_MATTHEW, new WorldPoint(2727, 4897, 2), "Bring the book to Matthew in the Demon Throne Room. Afterwards Denath should start the ritual. If he doesn't, leave and re-enter the throne room and talk to Matthew again.", book);
		talkToMatthewAfterBook.addSubSteps(enterRuinAfterBook, enterPortalAfterBook);

		enterRuinForRitual = new ObjectStep(this, ObjectID.GOLEM_INSIDESTAIRS_TOP, new WorldPoint(3493, 3090, 0), "Enter the Uzer ruins.", sigil, silverlightDyed, combatGear);
		enterPortalForRitual = new ObjectStep(this, ObjectID.GOLEM_PORTAL, new WorldPoint(2722, 4913, 0), "Enter the portal.");

		standInCircle = new DetailedQuestStep(this, new WorldPoint(2718, 4902, 2), "Stand in the correct spot in the circle.", sigil);
		readIncantation = new IncantationStep(this, true);
		pickUpSigil = new ItemStep(this, "Pick up the sigil.", sigil);
		leavePortal = new ObjectStep(this, ObjectID.AGRITH_PORTAL_CLOSING, new WorldPoint(2720, 4883, 2), "Leave the throne room.");
		enterRuinForDave = new ObjectStep(this, ObjectID.GOLEM_INSIDESTAIRS_TOP, new WorldPoint(3493, 3090, 0), "Talk to Evil Dave in the Uzer ruins.");
		tellDaveToReturn = new NpcStep(this, NpcID.AGRITH_DAVE, new WorldPoint(2721, 4900, 0), "Talk to Evil Dave.");
		tellDaveToReturn.addDialogStep("You've got to get back to the throne room!");
		tellDaveToReturn.addSubSteps(enterRuinForDave);

		pickUpImplementAfterRitual = new DetailedQuestStep(this, new WorldPoint(2713, 4913, 0), "Pick up the strange implement in the north west corner of the ruin.", strangeImplement);
		pickUpSigil2 = new ItemStep(this, "Pick up the sigil Tanya dropped.", sigil);

		goUpToBadden = new ObjectStep(this, ObjectID.GOLEM_INSIDESTAIRS_BASE, new WorldPoint(2722, 4885, 0), "Leave the ruins.");
		talkToBaddenAfterRitual = new NpcStep(this, NpcID.AGRITH_BADDEN, new WorldPoint(3490, 3090, 0), "Talk to Father Badden in Uzer.", sigil2);
		talkToBaddenAfterRitual.addSubSteps(goUpToBadden);

		talkToReenAfterRitual = new NpcStep(this, NpcID.AGRITH_REEN, new WorldPoint(3490, 3090, 0), "Talk to Father Reen in Uzer.", sigil2);
		talkToReenAfterRitual.addDialogStep("Oh, don't be so simple-minded!");
		talkToTheGolemAfterRitual = new NpcStep(this, NpcID.GOLEM_FIXED_GOLEM, new WorldPoint(3485, 3088, 0), "Talk to the Golem in Uzer.", strangeImplement);
		useImplementOnGolem = new NpcStep(this, NpcID.GOLEM_FIXED_GOLEM, new WorldPoint(3485, 3088, 0), "Use the strange implement on the Golem in Uzer.", strangeImplementHighlighted);
		useImplementOnGolem.addIcon(ItemID.GOLEM_GOLEMKEY);

		talkToGolemAfterReprogramming = new NpcStep(this, NpcID.GOLEM_FIXED_GOLEM, new WorldPoint(3485, 3088, 0), "Talk to the Golem again.", sigil2);
		enterRuinAfterRecruiting = new ObjectStep(this, ObjectID.GOLEM_INSIDESTAIRS_TOP, new WorldPoint(3493, 3090, 0), "Enter the Uzer ruins.", silverlightDyed, sigil, combatGear);
		enterPortalAfterRecruiting = new ObjectStep(this, ObjectID.GOLEM_PORTAL, new WorldPoint(2722, 4913, 0), "Enter the portal.", silverlightDyed, sigil, combatGear);

		talkToMatthewToStartFight = new NpcStep(this, NpcID.AGRITH_MATTHEW, new WorldPoint(2727, 4897, 2), "Talk to Matthew in the throne room.", silverlightDyed, sigil, combatGear);
		talkToMatthewToStartFight.addSubSteps(enterRuinAfterRecruiting, enterPortalAfterRecruiting);
		talkToMatthewToStartFight.addDialogStep("Yes.");
		standInCircleAgain = new DetailedQuestStep(this, new WorldPoint(2720, 4903, 2), "Stand in the correct spot in the circle.", sigil);
		incantRitual = new IncantationStep(this, false);
		enterRuinForFight = new ObjectStep(this, ObjectID.GOLEM_INSIDESTAIRS_TOP, new WorldPoint(3493, 3090, 0), "Enter the Uzer ruins to finish fighting Agrith-Naar.", silverlightDyed, combatGear);
		enterPortalForFight = new ObjectStep(this, ObjectID.GOLEM_PORTAL, new WorldPoint(2722, 4913, 0), "Enter the portal to finish fighting Agrith-Naar.", silverlightDyed, combatGear);
		killDemon = new NpcStep(this, NpcID.AGRITH_NAAR, "Kill Agrith-Naar. You can hurt him with any weapon, BUT YOU MUST DEAL THE FINAL BLOW WITH SILVERLIGHT.", silverlightDyedEquipped, combatGear);
		killDemon.addSubSteps(enterRuinForFight, enterPortalForFight);

		unequipDarklight = new DetailedQuestStep(this, "Unequip Darklight to complete the quest!");
	}

	@Override
	public List<String> getNotes()
	{
		return Arrays.asList("You will need 3 black items for a part of the quest. Potential items would be:",
			"- Desert shirt/robe dyed with black mushroom ink", "- Black armour", "- Priest gown top/bottom", "- Black wizard hat",
			"- Dark mystic", "- Ghostly robes", "- Shade robes", "- Black dragonhide", "- Black cape", "- One of the various black holiday event items", "- Graceful outfit (dyed black)");
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Collections.singletonList("Agrith-Naar (level 100)");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(silverlight, darkItems, silverBar);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(combatGear, coinsForCarpet, alKharidTeleport);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new QuestRequirement(QuestHelperQuest.THE_GOLEM, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.DEMON_SLAYER, QuestState.FINISHED));
		req.add(new SkillRequirement(Skill.CRAFTING, 30, true));
		return req;
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
				new ItemReward("10,000 Experience Lamp (Any combat skill except Prayer)", ItemID.THOSF_REWARD_LAMP, 1), //4447 is placeholder for filter
				new ItemReward("Darklight", ItemID.DARKLIGHT, 1));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		allSteps.add(new PanelDetails("Starting off", Collections.singletonList(talkToReen)));
		allSteps.add(new PanelDetails("Infiltrate the cult", Arrays.asList(talkToBadden, pickMushroom, dyeSilverlight, goIntoRuin, pickUpStrangeImplement, talkToEvilDave, talkToDenath, talkToJennifer, talkToMatthew), silverlight, darkItems));
		allSteps.add(new PanelDetails("Uncovering the truth", Arrays.asList(smeltSigil, talkToGolem, searchKiln, readBook, talkToMatthewAfterBook, standInCircle, readIncantation), silverBar, silverlightDyed, combatGear));
		allSteps.add(new PanelDetails("Defeating Agrith-Naar", Arrays.asList(pickUpSigil, leavePortal, pickUpSigil2, tellDaveToReturn, talkToBaddenAfterRitual, talkToReenAfterRitual, talkToTheGolemAfterRitual, useImplementOnGolem, talkToGolemAfterReprogramming,
			talkToMatthewToStartFight, standInCircleAgain, incantRitual, killDemon, unequipDarklight), silverlightDyed, combatGear));
		return allSteps;
	}
}
