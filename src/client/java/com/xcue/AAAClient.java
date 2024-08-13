package com.xcue;

import com.xcue.lib.AAAMod;
import com.xcue.mods.notafk.NotAfkMod;
import com.xcue.mods.notpeacefulskilling.NotPeacefulSkillingMod;
import net.fabricmc.api.ClientModInitializer;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Supplier;

public class AAAClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This is the entry-point for the client

		// Queue of mods (will initialize in the order they are created)
		// Suppliers are nice, they are essentially a factory function that will create
		// The object only when running .get()
		Queue<Supplier<AAAMod>> mods = new LinkedList<>()
		{{
			// This is a fancy way to create a new collection where you can quickly
			// Reference it. Outside of these braces, you would need to type mods.add
			add(NotAfkMod::new);
			add(NotPeacefulSkillingMod::new);
			//   ^ Shorthand lambda expression/func --> same as doing () -> new NotAfkMod()
			// You can use lambdas like that where you reference a class and which method to call
			// It's very useful!
		}};

		// Initialize the mods in order
		while (mods.peek() != null) {
			// This is a weird way to do it, but I think it's neat, and it re-introduces
			// you to collections
			mods.poll().get().init();
		}
	}
}