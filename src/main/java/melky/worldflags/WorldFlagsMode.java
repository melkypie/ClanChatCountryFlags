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

import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.ScriptID;
import net.runelite.api.widgets.WidgetInfo;

@Getter
@RequiredArgsConstructor
public enum WorldFlagsMode
{
	CHAT_CHANNEL(ScriptID.FRIENDS_CHAT_CHANNEL_REBUILD, WidgetInfo.FRIENDS_CHAT_LIST, 1, "^W.*$", "W"),
	CLAN_CHANNEL(4397, WidgetInfo.CLAN_MEMBER_LIST, 1, "^W.*$", "W"),
	GUEST_CHANNEL(4399, WidgetInfo.CLAN_GUEST_MEMBER_LIST, 1, "^W.*$", "W"),
	FRIENDS(ScriptID.FRIENDS_UPDATE, WidgetInfo.FRIEND_LIST_NAMES_CONTAINER, 2, "^World\\s?.*$", "World "),
	;

	private final int scriptID;
	private final WidgetInfo containerWidget;
	private final int widgetStartPosition;
	private final String worldMatchRegex;
	private final String worldReplaceRegex;
	public static final Map<Integer, WorldFlagsMode> byScriptID = Maps.uniqueIndex(Arrays.asList(values()), WorldFlagsMode::getScriptID);
}
