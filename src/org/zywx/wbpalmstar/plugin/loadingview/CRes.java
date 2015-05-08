package org.zywx.wbpalmstar.plugin.loadingview;

import java.util.Locale;

import android.content.Context;
import android.content.res.Resources;

public class CRes{
	private static boolean init;
	public static int app_name;
	public static int plugin_loadingview_layout;
	
	public static int plugin_loadingview_color_points;
	public static int plugin_loadingview_single_points;

	public static int plugin_loadingview_single_point;
	
	public static int plugin_loadingview_color_point_width;
	public static int plugin_loadingview_color_point_margin;
	public static int plugin_loadingview_single_point_width;
	public static int plugin_loadingview_single_point_margin;
	
	public static int plugin_loadingview_color_point_colors;
	public static boolean init(Context context){
		if(init){
			return init;
		}
		String packg = context.getPackageName();
		Resources res = context.getResources();
		app_name = res.getIdentifier("app_name", "string", packg);
		
		plugin_loadingview_layout=res.getIdentifier("plugin_loadingview_layout", "layout", packg);
		
		plugin_loadingview_color_points = res.getIdentifier("plugin_loadingview_color_points", "id", packg);
		plugin_loadingview_single_points = res.getIdentifier("plugin_loadingview_single_points", "id", packg);
		
		plugin_loadingview_single_point=res.getIdentifier("plugin_loadingview_single_point", "drawable", packg);

		plugin_loadingview_color_point_width=res.getIdentifier("plugin_loadingview_color_point_width", "dimen", packg);
		plugin_loadingview_color_point_margin=res.getIdentifier("plugin_loadingview_color_point_margin", "dimen", packg);
		plugin_loadingview_single_point_width=res.getIdentifier("plugin_loadingview_single_point_width", "dimen", packg);
		plugin_loadingview_single_point_margin=res.getIdentifier("plugin_loadingview_single_point_margin", "dimen", packg);
		
		plugin_loadingview_color_point_colors=res.getIdentifier("plugin_loadingview_color_point_colors", "array", packg);
		Locale language = Locale.getDefault();
		if(language.equals(Locale.CHINA) 
				|| language.equals(Locale.CHINESE) 
				|| language.equals(Locale.TAIWAN) 
				|| language.equals(Locale.TRADITIONAL_CHINESE)
				|| language.equals(Locale.SIMPLIFIED_CHINESE)
				|| language.equals(Locale.PRC)){
			
		}else{
		}
		init = true;
		return true;
	}
}
