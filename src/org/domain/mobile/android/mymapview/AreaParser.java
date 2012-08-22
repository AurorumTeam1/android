package org.domain.mobile.android.mymapview;

import java.io.IOException;
import java.util.List;

public interface AreaParser {
	List<AreaOverlay> parse() throws RuntimeException, IOException;
}
