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
package net.runelite.client.plugins.microbot.questhelper.helpers.quests.demonslayer;

import net.runelite.client.plugins.microbot.questhelper.questhelpers.QuestHelper;
import net.runelite.client.plugins.microbot.questhelper.steps.ConditionalStep;
import net.runelite.client.plugins.microbot.questhelper.steps.QuestStep;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.api.widgets.Widget;

import java.util.HashMap;

public class IncantationStep extends ConditionalStep
{
	private final HashMap<Integer, String> words = new HashMap<Integer, String>()
	{{
		put(0, "Carlem");
		put(1, "Aber");
		put(2, "Camerinthum");
		put(3, "Purchai");
		put(4, "Gabindo");
	}};

	private String[] incantationOrder;
	private int incantationPosition = 0;
	private final QuestStep incantationStep;

	public IncantationStep(QuestHelper questHelper, QuestStep incantationStep)
	{
		super(questHelper, incantationStep);
		this.incantationStep = incantationStep;
		this.steps.get(null).getText().add("Incantation is currently unknown.");
	}

	@Override
	/**
	 * {@inheritDoc}
	 */
	public void onWidgetLoaded(WidgetLoaded event)
	{
		int groupId = event.getGroupId();
		if (groupId == InterfaceID.CHAT_RIGHT)
		{
			clientThread.invokeLater(this::resetIncarnationIfRequired);
		}
		else if (groupId == InterfaceID.CHATMENU)
		{
			clientThread.invokeLater(this::updateChoiceIfRequired);
		}

		super.onWidgetLoaded(event);
	}

	/**
	 * This checks for the first dialog for banishing Delrith and resets if required.
	 * As a player can cancel the chat by clicking off and have start the whole incantation again.
	 */
	private void resetIncarnationIfRequired()
	{
		Widget widget = client.getWidget(InterfaceID.ChatRight.TEXT);
		if (widget == null)
		{
			return;
		}
		String text = widget.getText();
		String RESET_INCANTATION_TEXT = "Now what was that incantation again?";
		if (RESET_INCANTATION_TEXT.equals(text))
		{
			incantationPosition = 0;
		}
	}

	/**
	 * Updates the choices highlighted as the incantation progresses.
	 */
	private void updateChoiceIfRequired()
	{
		if (!shouldUpdateChoice())
		{
			return;
		}

		// As the incantation have all the same dialogs we want to reset the choices after each dialog
		// as we want only the correct one to be highlighted
		choices.resetChoices();
		addDialogStep(incantationOrder[incantationPosition]);
		incantationPosition++;
	}

	private boolean shouldUpdateChoice()
	{
		Widget widget = client.getWidget(InterfaceID.CHATMENU, 1);
		if (widget == null)
		{
			return false;
		}

		Widget[] children = widget.getChildren();
		if (children == null || children.length < 3)
		{
			return false;
		}

		Widget childWidget = widget.getChild(2);
		return childWidget != null && "Aber".equals(childWidget.getText());
	}

	@Override
	/**
	 * {@inheritDoc}
	 */
	protected void updateSteps()
	{
		if (incantationOrder != null || (client.getVarbitValue(VarbitID.DELRITH_INCANTATION_1) == 0 && client.getVarbitValue(VarbitID.DELRITH_INCANTATION_2) == 0))
		{
			startUpStep(incantationStep);
			return;
		}
		incantationOrder = new String[]{
			words.get(client.getVarbitValue(VarbitID.DELRITH_INCANTATION_1)),
			words.get(client.getVarbitValue(VarbitID.DELRITH_INCANTATION_2)),
			words.get(client.getVarbitValue(VarbitID.DELRITH_INCANTATION_3)),
			words.get(client.getVarbitValue(VarbitID.DELRITH_INCANTATION_4)),
			words.get(client.getVarbitValue(VarbitID.DELRITH_INCANTATION_5)),
		};
		String incantString = "Say the following in order: " + String.join(", ", incantationOrder);
		steps.get(null).getText().set(1, incantString);
		startUpStep(incantationStep);
	}
}
