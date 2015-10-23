package org.zywx.wbpalmstar.plugin.loadingview.vo;

import org.zywx.wbpalmstar.base.BUtility;

import java.io.Serializable;
import java.util.List;

public class OpenDataVO implements Serializable{
    private static final long serialVersionUID = 4186897091786785965L;
    private int styleId = -1;
    private int pointNum;
    private List<String> pointColor;

    public int getStyleId() {
        return styleId;
    }

    public void setStyleId(int styleId) {
        this.styleId = styleId;
    }

    public int getPointNum() {
        return pointNum;
    }

    public void setPointNum(int pointNum) {
        this.pointNum = pointNum;
    }

    public int[] getPointColor() {
        int[] colors = new int[pointColor.size()];
        for (int i = 0; i < colors.length; i++){
            colors[i] = BUtility.parseColor(pointColor.get(i));
        }
        return colors;
    }

    public void setPointColor(List<String> pointColor) {
        this.pointColor = pointColor;
    }
}
