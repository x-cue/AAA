package com.xcue;

import com.xcue.mods.notafk.NotAfkMod;
import net.fabricmc.api.ClientModInitializer;

public class AAAClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		// Start NotAfkMod
		new NotAfkMod().onInitializeClient();
	}
}