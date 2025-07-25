/*
 * Copyright (c) 2023, Zoinkwiz <https://github.com/Zoinkwiz>
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
package net.runelite.client.plugins.microbot.questhelper.questhelpers;

import net.runelite.client.plugins.microbot.questhelper.runeliteobjects.extendedruneliteobjects.QuestCompletedWidget;
import lombok.Getter;
import net.runelite.api.QuestState;

import javax.inject.Inject;

public abstract class PlayerMadeQuestHelper extends ComplexStateQuestHelper
{
	@Inject
	QuestCompletedWidget questCompletedWidget;

	@Getter
	protected int itemWidget = -1;

	@Getter
	protected int rotationX = 0;
	@Getter
	protected int rotationY = 0;
	@Getter
	protected int rotationZ = 0;
	@Getter
	protected int zoom = 0;

	@Override
	public void init()
	{
		super.init();
	}

	@Override
	public void shutDown()
	{
		super.shutDown();
		if (getQuest().getState(client, configManager) == QuestState.FINISHED)
		{
			runeliteObjectManager.createChatboxMessage("Quest completed!");
			questCompletedWidget.createWidget(client, getQuest().getName(), getQuestRewardsText(), getItemWidget(), rotationX, rotationY, rotationZ, zoom);
		}
	}
}
