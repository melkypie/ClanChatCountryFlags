/*
 * Copyright (c) 2020, melky <https://github.com/melkypie>
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

package melky.worldflags;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("worldflags")
public interface WorldFlagsConfig extends Config
{
	@ConfigItem(
		keyName = "showClanFlags",
		name = "Show chat channel flags",
		description = "Replaces the string W with a flag of the region the world is in inside chat channels",
		position = 1
	)
	default boolean showClanFlags()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showFriendsFlags",
		name = "Show friends flags",
		description = "Replaces the string W with a flag of the region the world is in inside friends list",
		position = 2
	)
	default boolean showFriendsFlags()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showClanChannelFlags",
		name = "Show clan channel flags",
		description = "Replaces the string W with a flag of the region the world is in inside clan channels",
		position = 3
	)
	default boolean showClanChannelFlags()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showGuestChannelFlags",
		name = "Show guest channel flags",
		description = "Replaces the string W with a flag of the region the world is in inside guest clan channels",
		position = 4
	)
	default boolean showGuestChannelFlags()
	{
		return true;
	}
}
