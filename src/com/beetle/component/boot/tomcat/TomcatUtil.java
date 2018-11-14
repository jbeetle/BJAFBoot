package com.beetle.component.boot.tomcat;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author hengyunabc
 *
 */
public abstract class TomcatUtil {

	public static File createTempDir(String prefix, int port) throws IOException {
		File tempDir = File.createTempFile(prefix + ".", "." + port);
		// File tempDir = new File(prefix);
		tempDir.delete();
		tempDir.mkdir();
		tempDir.deleteOnExit();
		return tempDir;
	}

	public static File getDir(String path) throws IOException {
		File tempDir = new File(path);
		if (tempDir.exists()) {
			return tempDir;
		}
		tempDir.mkdir();
		return tempDir;
	}
}
