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

import com.google.inject.Provides;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.IndexedSprite;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.WorldService;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.Text;
import net.runelite.http.api.worlds.World;
import net.runelite.http.api.worlds.WorldResult;

@Slf4j
@PluginDescriptor(
	name = "World country flags"
)
public class WorldFlagsPlugin extends Plugin
{
	private int modIconsStart = -1;

	@Inject
	private Client client;

	@Inject
	private WorldService worldService;

	@Inject
	private ClientThread clientThread;

	@Inject
	private WorldFlagsConfig config;

	@Provides
	WorldFlagsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(WorldFlagsConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		clientThread.invoke(() -> {
			loadRegionIcons();
			for (WorldFlagsMode flagMode : WorldFlagsMode.values())
			{
				toggleWorldsToFlags(flagMode);
			}
		});
	}

	@Override
	protected void shutDown() throws Exception
	{
		clientThread.invoke(() -> {
			for (WorldFlagsMode flagMode : WorldFlagsMode.values())
			{
				toggleWorldsToFlags(flagMode, true);
			}
		});
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			clientThread.invoke(this::loadRegionIcons);
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!event.getGroup().equals("worldflags"))
		{
			return;
		}

		switch (event.getKey())
		{
			case "showClanFlags":
				clientThread.invoke(() -> toggleWorldsToFlags(WorldFlagsMode.CHAT_CHANNEL));
				break;
			case "showClanChannelFlags":
				clientThread.invoke(() -> toggleWorldsToFlags(WorldFlagsMode.CLAN_CHANNEL));
				break;
			case "showGuestChannelFlags":
				clientThread.invoke(() -> toggleWorldsToFlags(WorldFlagsMode.GUEST_CHANNEL));
				break;
			case "showFriendsFlags":
				clientThread.invoke(() -> toggleWorldsToFlags(WorldFlagsMode.FRIENDS));
				break;
		}
	}

	@Subscribe
	public void onScriptPostFired(ScriptPostFired event)
	{
		WorldFlagsMode flagsMode = WorldFlagsMode.byScriptID.getOrDefault(event.getScriptId(), null);

		if (flagsMode != null)
		{
			clientThread.invoke(() -> toggleWorldsToFlags(flagsMode));
		}
	}

	private void loadRegionIcons()
	{
		final IndexedSprite[] modIcons = client.getModIcons();

		if (modIconsStart != -1 || modIcons == null)
		{
			return;
		}

		final WorldRegionFlag[] worldRegions = WorldRegionFlag.values();
		final IndexedSprite[] newModIcons = Arrays.copyOf(modIcons, modIcons.length + worldRegions.length);
		modIconsStart = modIcons.length;

		for (int i = 0; i < worldRegions.length; i++)
		{
			final WorldRegionFlag worldRegion = worldRegions[i];

			final BufferedImage image = worldRegion.loadImage();
			final IndexedSprite sprite = ImageUtil.getImageIndexedSprite(image, client);
			newModIcons[modIconsStart + i] = sprite;
		}

		log.debug("Loaded region icons");
		client.setModIcons(newModIcons);
	}

	private void toggleWorldsToFlags(WorldFlagsMode flagMode)
	{
		toggleWorldsToFlags(flagMode, false);
	}

	private void toggleWorldsToFlags(WorldFlagsMode flagMode, boolean forceDisable)
	{
		Widget containerWidget = client.getWidget(flagMode.getContainerWidget());
		if (containerWidget == null || containerWidget.getChildren() == null)
		{
			return;
		}
		boolean flagsEnable = false;
		switch (flagMode)
		{
			case CHAT_CHANNEL:
				flagsEnable = config.showClanFlags();
				break;
			case CLAN_CHANNEL:
				flagsEnable = config.showClanChannelFlags();
				break;
			case GUEST_CHANNEL:
				flagsEnable = config.showGuestChannelFlags();
				break;
			case FRIENDS:
				flagsEnable = config.showFriendsFlags();
				break;
		}

		if (forceDisable || (!flagsEnable))
		{
			changeFlagsToWorlds(containerWidget, flagMode);
		}
		else
		{
			changeWorldsToFlags(containerWidget, flagMode);
		}
	}

	private void changeWorldsToFlags(Widget containerWidget, WorldFlagsMode flagMode)
	{
		final WorldResult worldResult = worldService.getWorlds();
		// Iterate every 3 widgets starting at 1, since the order of widgets is name, world, icon (for clan chat)
		// Iterate every 3 widget starting at 2, since the order is name, previous name icon, world (for friends)
		for (int i = flagMode.getWidgetStartPosition(); i < containerWidget.getChildren().length; i += 3)
		{
			final Widget listWidget = containerWidget.getChild(i);
			String worldString = Text.removeTags(listWidget.getText());
			// In case the string already contains a country flag
			String regex = flagMode.getWorldMatchRegex();
			if (!worldString.matches(regex))
			{
				continue;
			}
			worldString = worldString.replace(flagMode.getWorldReplaceRegex(), "");
			final int worldNumber = Integer.parseInt(worldString);

			final World targetPlayerWorld = worldResult.findWorld(worldNumber);
			if (targetPlayerWorld == null)
			{
				continue;
			}

			final int worldRegionId = targetPlayerWorld.getLocation(); // 0 - us, 1 - gb, 3 - au, 7 - de
			final int regionModIconId = WorldRegionFlag.getByRegionId(worldRegionId).ordinal() + modIconsStart;

			listWidget.setText(worldString + " <img=" + (regionModIconId) + ">");
		}
	}

	private void changeFlagsToWorlds(Widget containerWidget, WorldFlagsMode flagMode)
	{
		// Iterate every 3 widgets starting at 1, since the order of widgets is name, world, icon (for clan chat)
		// Iterate every 3 widget starting at 2, since the order is name, previous name icon, world (for friends)
		for (int i = flagMode.getWidgetStartPosition(); i < containerWidget.getChildren().length; i += 3)
		{
			final Widget listWidget = containerWidget.getChild(i);
			final String worldString = removeColorTags(listWidget.getText());
			// In case the string already has been changed back to World
			if (!worldString.matches("^\\d+\\s?<img=\\d+>$") || !listWidget.getName().equals(""))
			{
				continue;
			}
			final String worldNum = listWidget.getText().replaceAll("\\s?<img=\\d+>$", "");
			listWidget.setText((flagMode.getWorldReplaceRegex()) + worldNum);
		}
	}

	private String removeColorTags(String text)
	{
		return text.replaceAll("<(/)?col(=([0-9]|[a-z]){6})*>", "");
	}
}
