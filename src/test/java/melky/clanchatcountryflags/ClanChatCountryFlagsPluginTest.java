package melky.clanchatcountryflags;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class ClanChatCountryFlagsPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(ClanChatCountryFlagsPlugin.class);
		RuneLite.main(args);
	}
}