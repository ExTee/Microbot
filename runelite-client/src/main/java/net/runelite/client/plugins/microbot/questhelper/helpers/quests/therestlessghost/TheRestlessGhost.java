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
package net.runelite.client.plugins.microbot.questhelper.helpers.quests.therestlessghost;

import net.runelite.client.plugins.microbot.questhelper.panel.PanelDetails;
import net.runelite.client.plugins.microbot.questhelper.questhelpers.BasicQuestHelper;
import net.runelite.client.plugins.microbot.questhelper.requirements.Requirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.conditional.Conditions;
import net.runelite.client.plugins.microbot.questhelper.requirements.conditional.NpcCondition;
import net.runelite.client.plugins.microbot.questhelper.requirements.conditional.ObjectCondition;
import net.runelite.client.plugins.microbot.questhelper.requirements.item.ItemRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.var.VarbitRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.zone.Zone;
import net.runelite.client.plugins.microbot.questhelper.requirements.zone.ZoneRequirement;
import net.runelite.client.plugins.microbot.questhelper.rewards.ExperienceReward;
import net.runelite.client.plugins.microbot.questhelper.rewards.ItemReward;
import net.runelite.client.plugins.microbot.questhelper.rewards.QuestPointReward;
import net.runelite.client.plugins.microbot.questhelper.steps.ConditionalStep;
import net.runelite.client.plugins.microbot.questhelper.steps.NpcStep;
import net.runelite.client.plugins.microbot.questhelper.steps.ObjectStep;
import net.runelite.client.plugins.microbot.questhelper.steps.QuestStep;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;

import java.util.*;

public class TheRestlessGhost extends BasicQuestHelper
{
	//Items Required
	private ItemRequirement ghostspeakAmulet, skull;

	//Items Recommended
	private ItemRequirement lumbridgeTeleports, passage;

	private Requirement ghostSpawned, coffinOpened, inBasement, hasSkull;

	private QuestStep talkToAereck, talkToUrhney, speakToGhost, openCoffin, searchCoffin, enterWizardsTowerBasement, searchAltarAndRun, exitWizardsTowerBasement,
		openCoffinToPutSkullIn, putSkullInCoffin;

	//Zones
	private Zone basement;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToAereck);
		steps.put(1, talkToUrhney);

		ConditionalStep talkToGhost = new ConditionalStep(this, openCoffin);
		talkToGhost.addStep(ghostSpawned, speakToGhost);
		talkToGhost.addStep(coffinOpened, searchCoffin);
		steps.put(2, talkToGhost);

		ConditionalStep getSkullForGhost = new ConditionalStep(this, enterWizardsTowerBasement);
		getSkullForGhost.addStep(inBasement, searchAltarAndRun);
		steps.put(3, getSkullForGhost);

		ConditionalStep returnSkullToGhost = new ConditionalStep(this, enterWizardsTowerBasement);
		returnSkullToGhost.addStep(new Conditions(inBasement, hasSkull), exitWizardsTowerBasement);
		returnSkullToGhost.addStep(new Conditions(hasSkull, coffinOpened), putSkullInCoffin);
		returnSkullToGhost.addStep(hasSkull, openCoffinToPutSkullIn);
		returnSkullToGhost.addStep(inBasement, searchAltarAndRun);
		steps.put(4, returnSkullToGhost);

		return steps;
	}

	@Override
	protected void setupZones()
	{
		basement = new Zone(new WorldPoint(3094, 9553, 0), new WorldPoint(3125, 9582, 0));
	}

	public void setupConditions()
	{
		ghostSpawned = new NpcCondition(NpcID.GHOSTX);
		coffinOpened = new ObjectCondition(ObjectID.OPENGHOSTCOFFIN);
		inBasement = new ZoneRequirement(basement);
		hasSkull = new VarbitRequirement(2130, 1);
	}

	@Override
	protected void setupRequirements()
	{
		lumbridgeTeleports = new ItemRequirement("Lumbridge teleports", ItemID.POH_TABLET_LUMBRIDGETELEPORT, 2);
		ghostspeakAmulet = new ItemRequirement("Ghostspeak amulet", ItemID.AMULET_OF_GHOSTSPEAK, 1, true).isNotConsumed();
		ghostspeakAmulet.setTooltip("If you've lost it you can get another from Father Urhney in his hut in the south east of Lumbridge Swamp");
		skull = new ItemRequirement("Ghost's skull", ItemID.GHOSTSKULL);
		skull.setTooltip("Check your bank if you don't have this item on you.");
		passage = new ItemRequirement("Necklace of passage", ItemID.NECKLACE_OF_PASSAGE_5);
		passage.addAlternates(ItemID.NECKLACE_OF_PASSAGE_1, ItemID.NECKLACE_OF_PASSAGE_2, ItemID.NECKLACE_OF_PASSAGE_3, ItemID.NECKLACE_OF_PASSAGE_4);
	}

	public void setupSteps()
	{
		talkToAereck = new NpcStep(this, NpcID.FATHER_AERECK, new WorldPoint(3243, 3206, 0), "Talk to Father Aereck in the Lumbridge Church.");
		talkToAereck.addDialogStep("I'm looking for a quest!");
		talkToAereck.addDialogStep("Ok, let me help then.");

		talkToUrhney = new NpcStep(this, NpcID.FATHER_URHNEY, new WorldPoint(3147, 3175, 0), "Talk to Father Urhney in the south west of Lumbridge Swamp.");
		talkToUrhney.addDialogStep("Father Aereck sent me to talk to you.");
		talkToUrhney.addDialogStep("He's got a ghost haunting his graveyard.");

		openCoffin = new ObjectStep(this, ObjectID.SHUTGHOSTCOFFIN, new WorldPoint(3250, 3193, 0), "Open the coffin in the Lumbridge Graveyard to spawn the ghost.", ghostspeakAmulet);
		openCoffin.addDialogStep("Yep, now tell me what the problem is.");
		openCoffin.addDialogStep("Yep, clever aren't I?.");
		openCoffin.addDialogStep("Yes, ok. Do you know WHY you're a ghost?");
		openCoffin.addDialogStep("Yes, ok. Do you know why you're a ghost?");

		searchCoffin = new ObjectStep(this, ObjectID.OPENGHOSTCOFFIN, new WorldPoint(3250, 3193, 0), "Search the coffin in the Lumbridge Graveyard to spawn the ghost.", ghostspeakAmulet);
		searchCoffin.addDialogStep("Yep, now tell me what the problem is.");
		searchCoffin.addDialogStep("Yep, clever aren't I?.");
		searchCoffin.addDialogStep("Yes, ok. Do you know WHY you're a ghost?");
		searchCoffin.addDialogStep("Yes, ok. Do you know why you're a ghost?");

		speakToGhost = new NpcStep(this, NpcID.GHOSTX, new WorldPoint(3250, 3195, 0), "Speak to the Ghost that appears whilst wearing your Ghostspeak Amulet.", ghostspeakAmulet);
		speakToGhost.addDialogStep("Yep, now tell me what the problem is.");
		speakToGhost.addDialogStep("Yep, clever aren't I?.");
		speakToGhost.addDialogStep("Yes, ok. Do you know WHY you're a ghost?");
		speakToGhost.addDialogStep("Yes, ok. Do you know why you're a ghost?");

		enterWizardsTowerBasement = new ObjectStep(this, ObjectID.WIZARDS_TOWER_LADDERTOP, new WorldPoint(3104, 3162, 0), "Enter the Wizards' Tower basement.");
		searchAltarAndRun = new ObjectStep(this, ObjectID.RESTLESS_GHOST_ALTAR, new WorldPoint(3120, 9567, 0), "Search the Altar. A skeleton (level 13) will appear and attack you, but you can just run away.");
		exitWizardsTowerBasement = new ObjectStep(this, ObjectID.WIZARDS_TOWER_LADDER, new WorldPoint(3103, 9576, 0), "Leave the basement.", skull);
		openCoffinToPutSkullIn = new ObjectStep(this, ObjectID.SHUTGHOSTCOFFIN, new WorldPoint(3250, 3193, 0), "Open the ghost's coffin in Lumbridge graveyard.", skull);
		putSkullInCoffin = new ObjectStep(this, ObjectID.OPENGHOSTCOFFIN, new WorldPoint(3250, 3193, 0), "Search the coffin.", skull);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		ArrayList<ItemRequirement> recommended = new ArrayList<>();
		recommended.add(lumbridgeTeleports);
		recommended.add(passage);
		return recommended;
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Collections.singletonList(new ExperienceReward(Skill.PRAYER, 1125));
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Collections.singletonList(new ItemReward("Ghostspeak Amulet", ItemID.AMULET_OF_GHOSTSPEAK, 1));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		allSteps.add(new PanelDetails("Talk to Father Aereck", Collections.singletonList(talkToAereck)));
		allSteps.add(new PanelDetails("Get a ghostspeak amulet", Collections.singletonList(talkToUrhney)));
		allSteps.add(new PanelDetails("Talk to the ghost", Arrays.asList(openCoffin, searchCoffin, speakToGhost)));
		allSteps.add(new PanelDetails("Return the ghost's skull", Arrays.asList(enterWizardsTowerBasement, searchAltarAndRun, exitWizardsTowerBasement, openCoffinToPutSkullIn, putSkullInCoffin)));
		return allSteps;
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Collections.singletonList("A skeleton (level 13) you can run away from");
	}
}
