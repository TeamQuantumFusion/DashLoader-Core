package dev.quantumfusion.dashloader.core.client.config;

import java.util.Map;

public class DashConfig {
	public String[] disabledOptions;

	// ==================================== Screen ====================================
	public boolean debugMode;
	public boolean disableWatermark;

	// Colors
	public String backgroundColor;
	public String foregroundColor;
	public Map<String, String> colorVariables;

	// Progress bar
	public int progressBarHeight;
	public int progressBarSpeedDivision;
	public String[] progressColors;
	public String progressTrackColor;

	// Lines
	public int lineSpeed;
	public int lineWidth;
	public int lineMinHeight;
	public int lineMaxHeight;
	public int lineSpeedDifference;
	public String lineDirection;
	public Map<String, Integer> lineColors;


}
