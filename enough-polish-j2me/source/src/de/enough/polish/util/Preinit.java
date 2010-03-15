//#condition polish.usePolishGui
package de.enough.polish.util;

import de.enough.polish.benchmark.Benchmark;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Screen;
import de.enough.polish.ui.UiAccess;

public class Preinit implements Runnable {
	boolean threaded;
	
	Screen screen;
	
	public static void preinit(Screen screen) throws IllegalArgumentException{
		//#debug benchmark
		Benchmark.start("prefetch : " + screen);
		if(screen.isShown()) {
			synchronized(screen.getPaintLock()) {
				UiAccess.init(screen);
			}
		} else {
			UiAccess.init(screen);
		}
		//#debug benchmark
		Benchmark.stop("prefetch : " + screen," : done" );
	}
	
	public Preinit(Screen screen) {
		this.screen = screen;
	}

	public void run() {
		preinit(this.screen);
	}
}
