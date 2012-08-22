package org.domain.mobile.android.mymapview;

import java.io.IOException;
import java.util.List;

public interface AreaWriter {
	public void write(List<AreaOverlay> areas) throws RuntimeException, IOException;
}
