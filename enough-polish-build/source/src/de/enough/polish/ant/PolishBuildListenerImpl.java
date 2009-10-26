package de.enough.polish.ant;

import java.io.File;

public class PolishBuildListenerImpl implements PolishBuildListener {

	public void notifyBuildEvent(String name, Object data) {
		System.out.println("Event=" + name + ", data=" + data );
		if (name == EVENT_PREPROCESS_SOURCE_DIR ) {
			File dir = (File) data;
			System.out.println("Preprocessing source dir=" + dir.getAbsolutePath() );
		}
	}

}
