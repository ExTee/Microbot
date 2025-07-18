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
package net.runelite.client.plugins.microbot.questhelper.helpers.quests.makinghistory;

import net.runelite.client.plugins.microbot.questhelper.collections.ItemCollections;
import net.runelite.client.plugins.microbot.questhelper.collections.KeyringCollection;
import net.runelite.client.plugins.microbot.questhelper.panel.PanelDetails;
import net.runelite.client.plugins.microbot.questhelper.questhelpers.BasicQuestHelper;
import net.runelite.client.plugins.microbot.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.microbot.questhelper.requirements.ComplexRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.Requirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.conditional.Conditions;
import net.runelite.client.plugins.microbot.questhelper.requirements.item.ItemRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.item.KeyringRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.quest.QuestRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.util.ComplexRequirementBuilder;
import net.runelite.client.plugins.microbot.questhelper.requirements.util.LogicType;
import net.runelite.client.plugins.microbot.questhelper.requirements.util.Operation;
import net.runelite.client.plugins.microbot.questhelper.requirements.var.VarbitRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.zone.Zone;
import net.runelite.client.plugins.microbot.questhelper.requirements.zone.ZoneRequirement;
import net.runelite.client.plugins.microbot.questhelper.rewards.ExperienceReward;
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

public class MakingHistory extends BasicQuestHelper
{
	//Items Required
	ItemRequirement spade, saphAmulet, ghostSpeakAmulet, enchantedKey, chest, journal, scroll, letter, enchantedKeyHighlighted, ectoTokens;
	ComplexRequirement portPhasmatysEntry;

	//Items Recommended
	ItemRequirement ardougneTeleport, ectophial, ringOfDueling, passage, rellekaTeleport, runRestoreItems;

	Requirement talkedtoBlanin, talkedToDron, talkedToMelina, talkedToDroalak, inCastle, gotKey, gotChest,
		gotScroll, handedInJournal, handedInScroll, finishedFrem, finishedKey, finishedGhost, handedInEverything;

	QuestStep talkToJorral, talkToSilverMerchant, dig, openChest, talkToBlanin, talkToDron, talkToDroalak,
		talkToMelina, returnToDroalak, returnToJorral, continueTalkingToJorral, goUpToLathas, talkToLathas, finishQuest;

	ConditionalStep dronSteps, ghostSteps, keySteps;

	//Zones
	Zone castle;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToJorral);

		keySteps = new ConditionalStep(this, talkToSilverMerchant);
		keySteps.addStep(chest, openChest);
		keySteps.addStep(gotKey, dig);
		keySteps.setLockingCondition(finishedKey);

		dronSteps = new ConditionalStep(this, talkToBlanin);
		dronSteps.addStep(talkedtoBlanin, talkToDron);
		dronSteps.setLockingCondition(finishedFrem);

		ghostSteps = new ConditionalStep(this, talkToDroalak);
		ghostSteps.addStep(talkedToMelina, returnToDroalak);
		ghostSteps.addStep(talkedToDroalak, talkToMelina);
		ghostSteps.setLockingCondition(finishedGhost);

		ConditionalStep getItems = new ConditionalStep(this, keySteps);
		getItems.addStep(handedInEverything, continueTalkingToJorral);
		getItems.addStep(new Conditions(finishedKey, finishedFrem, finishedGhost), returnToJorral);
		getItems.addStep(new Conditions(finishedKey, finishedFrem), ghostSteps);
		getItems.addStep(finishedKey, dronSteps);

		steps.put(1, getItems);

		ConditionalStep goTalkToLathas = new ConditionalStep(this, goUpToLathas);
		goTalkToLathas.addStep(inCastle, talkToLathas);

		steps.put(2, goTalkToLathas);

		steps.put(3, finishQuest);

		return steps;
	}

	@Override
	protected void setupRequirements()
	{
		spade = new ItemRequirement("Spade", ItemID.SPADE).isNotConsumed();
		saphAmulet = new ItemRequirement("Sapphire amulet", ItemID.STRUNG_SAPPHIRE_AMULET);
		ghostSpeakAmulet = new ItemRequirement("Ghostspeak amulet", ItemCollections.GHOSTSPEAK).equipped().isNotConsumed();
		ghostSpeakAmulet.setTooltip("You can also wear the Morytania Legs 2 or higher.");
		ardougneTeleport = new ItemRequirement("Teleports to Ardougne", ItemID.POH_TABLET_ARDOUGNETELEPORT, 3);
		ardougneTeleport.addAlternates(ItemID.ARDY_CAPE_EASY, ItemID.ARDY_CAPE_MEDIUM, ItemID.ARDY_CAPE_HARD, ItemID.ARDY_CAPE_ELITE);

		ectophial = new ItemRequirement("Ectophial, or method of getting to Port Phasmatys", ItemID.ECTOPHIAL).isNotConsumed();
		ectophial.addAlternates(ItemID.TELETAB_FENK, ItemID.TABLET_KHARYLL);
		ectophial.addAlternates(ItemCollections.SLAYER_RINGS); // Slayer Tower
		ectophial.addAlternates(ItemID.MORYTANIA_LEGS_MEDIUM, ItemID.MORYTANIA_LEGS_HARD, ItemID.MORYTANIA_LEGS_ELITE);
		ectophial.setTooltip("You will need 2 ecto-tokens if you have not completed Ghosts Ahoy.");
		ectophial.appendToTooltip("If you do not have 2 ecto-tokens bring 1 bone, 1 pot and 1 bucket to earn 5 ecto-tokens at the Ectofunctus.\n");
		ectophial.appendToTooltip("You can use the Fairy Ring 'ALQ' or the Morytania Legs 2 or higher to get to Port Phasmatys.");
		ectophial.appendToTooltip("Slayer rings and the Kharyrll teleport can also be used.");

		ectoTokens = new ItemRequirement("Ecto-tokens", ItemID.ECTOTOKEN, 2);
		ectoTokens.setTooltip("You do not need two ecto-tokens if you have completed Ghosts Ahoy.");
		ectoTokens.appendToTooltip("If you do not have 2 ecto-tokens bring 1 bone, 1 pot and 1 bucket to earn 5 ecto-tokens at the Ectofunctus.");
		ectoTokens.appendToTooltip("Additionally, you can also enter Port Phasmatys via Charter ship, but that costs up to 4,100 coins.");

		portPhasmatysEntry = ComplexRequirementBuilder.or("2 x Ecto-tokens, or 4100 coins to travel there via Charter" +
			" Ship")
			.with(ectoTokens)
			.with(new QuestRequirement(QuestHelperQuest.GHOSTS_AHOY, QuestState.FINISHED))
			.with(new ItemRequirement("Coins", ItemCollections.COINS, 4100))
			.build();
		portPhasmatysEntry.setTooltip(ectoTokens.getTooltip());

		ringOfDueling = new ItemRequirement("Ring of Dueling", ItemCollections.RING_OF_DUELINGS);
		enchantedKey = new KeyringRequirement("Enchanted key", configManager, KeyringCollection.ENCHANTED_KEY);
		enchantedKey.setTooltip("You can get another from the silver merchant in East Ardougne's market");

		enchantedKeyHighlighted = new ItemRequirement("Enchanted key", ItemID.MAKINGHISTORY_KEY);
		enchantedKeyHighlighted.setTooltip("You can get another from the silver merchant in East Ardougne's market");
		enchantedKeyHighlighted.setHighlightInInventory(true);

		journal = new ItemRequirement("Journal", ItemID.MAKINGHISTORY_JOURNAL);
		chest = new ItemRequirement("Chest", ItemID.MAKINGHISTORY_CHEST);
		chest.setTooltip("You can dig up another from north of Castle Wars");
		chest.setHighlightInInventory(true);
		scroll = new ItemRequirement("Scroll", ItemID.MAKINGHISTORY_SCROLL1);
		scroll.setTooltip("You can get another from Droalak in Port Phasmatys");
		letter = new ItemRequirement("Letter", ItemID.MAKINGHISTORY_LETTER2);
		letter.setTooltip("You can get another from King Lathas in East Ardougne castle");
		passage = new ItemRequirement("Necklace of passage", ItemCollections.NECKLACE_OF_PASSAGES);
		rellekaTeleport = new ItemRequirement("Relleka Teleport", ItemID.NZONE_TELETAB_RELLEKKA);
		rellekaTeleport.addAlternates(ItemCollections.ENCHANTED_LYRE);
		rellekaTeleport.addAlternates(ItemID.FREMENNIK_BOOTS_MEDIUM, ItemID.FREMENNIK_BOOTS_HARD, ItemID.FREMENNIK_BOOTS_ELITE);
		rellekaTeleport.setTooltip("You can also use Fairy Rings (DKS or AJR) if you have those unlocked.");
		rellekaTeleport.appendToTooltip("You can also teleport to Camelot and run North.");
		runRestoreItems = new ItemRequirement("Potions/Items to restore run energy", ItemCollections.RUN_RESTORE_ITEMS);

	}

	@Override
	protected void setupZones()
	{
		castle = new Zone(new WorldPoint(2570, 3283, 1), new WorldPoint(2590, 3310, 1));
	}

	public void setupConditions()
	{
		talkedtoBlanin = new Conditions(LogicType.OR, new VarbitRequirement(1385, 1), new VarbitRequirement(1385, 2));
		talkedToDron = new VarbitRequirement(VarbitID.MAKINGHISTORY_WARR_PROG, 3, Operation.GREATER_EQUAL);

		talkedToDroalak = new Conditions(LogicType.OR, new VarbitRequirement(1386, 2), new VarbitRequirement(1386, 1));
		talkedToMelina = new Conditions(LogicType.OR, new VarbitRequirement(1386, 4), new VarbitRequirement(1386, 3));
		gotScroll = new VarbitRequirement(1386, 5);
		handedInScroll = new VarbitRequirement(1386, 6);

		inCastle = new ZoneRequirement(castle);
		gotKey = new VarbitRequirement(VarbitID.MAKINGHISTORY_TRADER_PROG, 1, Operation.GREATER_EQUAL);
		gotChest = new VarbitRequirement(VarbitID.MAKINGHISTORY_TRADER_PROG, 2, Operation.GREATER_EQUAL);
		handedInJournal = new VarbitRequirement(1384, 4);
		handedInEverything = new Conditions(handedInJournal, handedInScroll, talkedToDron);
		finishedFrem = talkedToDron;
		finishedGhost = new Conditions(LogicType.OR, handedInScroll, gotScroll);
		finishedKey = new Conditions(LogicType.OR, handedInJournal, journal);
	}

	public void setupSteps()
	{
		talkToJorral = new NpcStep(this, NpcID.MAKINGHISTORY_JORRAL, new WorldPoint(2436, 3346, 0),
			"Talk to Jorral at the outpost south of the Tree Gnome Stronghold. You can teleport there with a Necklace" +
				" of Passage.");
		talkToJorral.addDialogStep("The Outpost");
		talkToJorral.addDialogStep("Tell me more.");
		talkToJorral.addDialogStep("Ok, I'll make a stand for history!");
		talkToSilverMerchant = new NpcStep(this, NpcID.SILVER_MERCHANT_ARDOUGNE, new WorldPoint(2658, 3316, 0), "Talk to the Silver Merchant in the East Ardougne Market.");
		talkToSilverMerchant.addDialogStep("Ask about the outpost.");
		dig = new DigStep(this, new WorldPoint(2442, 3140, 0), "Dig at the marked spot north of Castle Wars.", enchantedKey);
		openChest = new DetailedQuestStep(this, "Use the enchanted key on the chest.", enchantedKeyHighlighted, chest);
		talkToBlanin = new NpcStep(this, NpcID.MAKINGHISTORY_BLANIN, new WorldPoint(2673, 3670, 0), "Talk to Blanin in south east Rellekka.");
		talkToDron = new NpcStep(this, NpcID.MAKINGHISTORY_DRON, new WorldPoint(2661, 3698, 0), "Talk to Dron in north Rellekka.");
		talkToDron.addDialogStep("I'm after important answers.");
		talkToDron.addDialogStep("Why, you're the famous warrior Dron!");
		talkToDron.addDialogStep("An iron mace");
		talkToDron.addDialogStep(1, "Breakfast");
		talkToDron.addDialogStep(2, "Lunch");
		talkToDron.addDialogStep("Bunnies");
		talkToDron.addDialogStep("Red");
		talkToDron.addDialogStep("36");
		talkToDron.addDialogStep("8");
		talkToDron.addDialogStep("Fifth and Fourth");
		talkToDron.addDialogStep("North East side of town");
		talkToDron.addDialogStep("Blanin");
		talkToDron.addDialogStep("Fluffy");
		talkToDron.addDialogStep("12, but what does that have to do with anything?");


		talkToDroalak = new NpcStep(this, NpcID.MAKINGHISTORY_DROALAK, new WorldPoint(3659, 3468, 0), "Enter Port Phasmatys and talk to Droalak outside the general store.", ghostSpeakAmulet, portPhasmatysEntry);
		talkToMelina = new NpcStep(this, NpcID.MAKINGHISTORY_MELINA, new WorldPoint(3674, 3479, 0), "Give Melina a sapphire amulet in the house north east of the general store.", saphAmulet, ghostSpeakAmulet);
		returnToDroalak = new NpcStep(this, NpcID.MAKINGHISTORY_DROALAK, new WorldPoint(3659, 3468, 0), "Return to Droalak outside the general store.");
		returnToJorral = new NpcStep(this, NpcID.MAKINGHISTORY_JORRAL, new WorldPoint(2436, 3346, 0), "Return to Jorral north of West Ardougne with the scroll and journal.", scroll, journal);
		continueTalkingToJorral = new NpcStep(this, NpcID.MAKINGHISTORY_JORRAL, new WorldPoint(2436, 3346, 0), "Return to Jorral north of West Ardougne.");
		returnToJorral.addSubSteps(continueTalkingToJorral);
		goUpToLathas = new ObjectStep(this, ObjectID.STAIRS, new WorldPoint(2572, 3296, 0), "Talk to King Lathas in East Ardougne castle.");
		talkToLathas = new NpcStep(this, NpcID.KINGLATHAS_VIS, new WorldPoint(2578, 3293, 1), "Talk to King Lathas in East Ardougne castle.");
		talkToLathas.addDialogStep("Talk about the outpost.");
		talkToLathas.addSubSteps(goUpToLathas);
		finishQuest = new NpcStep(this, NpcID.MAKINGHISTORY_JORRAL, new WorldPoint(2436, 3346, 0), "Return to Jorral north of West Ardougne with the letter to finish the quest.", letter);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(spade);
		reqs.add(saphAmulet);
		reqs.add(ghostSpeakAmulet);
		return reqs;
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(ardougneTeleport);
		reqs.add(ectophial);
		reqs.add(ringOfDueling);
		reqs.add(passage);
		reqs.add(rellekaTeleport);
		reqs.add(runRestoreItems);
		return reqs;
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(3);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Arrays.asList(
				new ExperienceReward(Skill.CRAFTING, 1000),
				new ExperienceReward(Skill.PRAYER, 1000));
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
				new ItemReward("Coins", ItemID.COINS, 750),
				new ItemReward("An Enchanted Key", ItemID.MAKINGHISTORY_KEY, 1));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", Collections.singletonList(talkToJorral)));
		PanelDetails chestPanel = new PanelDetails("Find the book", Arrays.asList(talkToSilverMerchant, dig, openChest), spade);
		chestPanel.setLockingStep(keySteps);
		PanelDetails fremPanel = new PanelDetails("Fremennik tale", Arrays.asList(talkToBlanin, talkToDron));
		fremPanel.setLockingStep(dronSteps);
		PanelDetails ghostPanel = new PanelDetails("Find the scroll", Arrays.asList(talkToDroalak, talkToMelina, returnToDroalak), saphAmulet, ghostSpeakAmulet, portPhasmatysEntry);
		ghostPanel.setLockingStep(ghostSteps);

		PanelDetails finishingPanel = new PanelDetails("Finishing off", Arrays.asList(returnToJorral, talkToLathas, finishQuest));

		allSteps.add(chestPanel);
		allSteps.add(fremPanel);
		allSteps.add(ghostPanel);
		allSteps.add(finishingPanel);
		return allSteps;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return Arrays.asList(new QuestRequirement(QuestHelperQuest.PRIEST_IN_PERIL, QuestState.FINISHED),
			new QuestRequirement(QuestHelperQuest.THE_RESTLESS_GHOST, QuestState.IN_PROGRESS));
	}
}
