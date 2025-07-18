/*
 * Copyright (c) 2021, Zoinkwiz <https://github.com/Zoinkwiz>
 * All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *     list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
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
package net.runelite.client.plugins.microbot.questhelper.bank.banktab;

import net.runelite.client.plugins.microbot.questhelper.questhelpers.QuestUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BankTabItems
{
	@Getter
	@Setter

	private String name;

	@Getter
	private final List<BankTabItem> items;

	@Getter
	private final List<BankTabItem> recommendedItems;

	public BankTabItems(String name, BankTabItem... items)
	{
		this.name = name;
		this.items = QuestUtil.toArrayList(items);
		this.recommendedItems = new ArrayList<>();
	}

	public BankTabItems(String name, List<BankTabItem> items)
	{
		this.name = name;
		this.items = items;
		this.recommendedItems = new ArrayList<>();
	}

	public BankTabItems(String name, List<BankTabItem> items, List<BankTabItem> recommendedItems)
	{
		this.name = name;
		this.items = items;
		this.recommendedItems = recommendedItems;
	}

	public void addItems(BankTabItem... items)
	{
		this.items.addAll(Arrays.asList(items));
	}

	public void addRecommendedItems(BankTabItem... items)
	{
		this.recommendedItems.addAll(Arrays.asList(items));
	}
}
