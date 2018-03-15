package com.melissadata.kettle.versioncheck;

public class VersionCheckThread extends Thread {
	VersionsMeta	verMeta;

	public VersionCheckThread(VersionsMeta verMeta) {
		this.verMeta = verMeta;
	}

	@Override
	public void run() {
		verMeta.sendUsageNotify();
	};
}
