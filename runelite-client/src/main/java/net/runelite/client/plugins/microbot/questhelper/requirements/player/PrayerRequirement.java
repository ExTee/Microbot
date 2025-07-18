/*
 *
 *  * Copyright (c) 2021, Zoinkwiz <https://github.com/Zoinkwiz>
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  *
 *  * 1. Redistributions of source code must retain the above copyright notice, this
 *  *    list of conditions and the following disclaimer.
 *  * 2. Redistributions in binary form must reproduce the above copyright notice,
 *  *    this list of conditions and the following disclaimer in the documentation
 *  *    and/or other materials provided with the distribution.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package net.runelite.client.plugins.microbot.questhelper.requirements.player;

import net.runelite.client.plugins.microbot.questhelper.requirements.AbstractRequirement;
import net.runelite.api.Client;
import net.runelite.api.Prayer;

import javax.annotation.Nonnull;

/**
 * Requirement that checks if a specified {@link Prayer} is active
 */
public class PrayerRequirement extends AbstractRequirement
{
	private final Prayer prayer;
	private final String text;

	/**
	 * Checks if the {@link Prayer} is currently active.
	 *
	 * @param text the display text
	 * @param prayer the {@link Prayer} to check
	 */
	public PrayerRequirement(String text, Prayer prayer)
	{
		assert(prayer != null);
		this.prayer = prayer;
		this.text = text;
	}

	@Override
	public boolean check(Client client)
	{
		int currentPrayer = client.getVarbitValue(prayer.getVarbit());
		return currentPrayer == 1;
	}

	@Nonnull
	@Override
	public String getDisplayText()
	{
		return text;
	}
}
