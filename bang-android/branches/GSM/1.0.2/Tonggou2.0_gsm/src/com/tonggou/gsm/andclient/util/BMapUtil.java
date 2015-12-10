package com.tonggou.gsm.andclient.util;

import java.util.ArrayList;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.Geometry;
import com.baidu.mapapi.map.Graphic;
import com.baidu.mapapi.map.MKMapStatus;
import com.baidu.mapapi.map.Symbol;
import com.baidu.mapapi.utils.CoordinateConvert;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.tonggou.gsm.andclient.bean.GeoPointParcel;

public class BMapUtil {

	public static GeoPoint convertGeoPoint(BDLocation location) {
		return convertGeoPoint( location.getLatitude(), location.getLongitude());
	}
	
	public static GeoPoint convertGeoPoint( double lat, double lng ) {
		return new GeoPointParcel( convertDot(lat), convertDot(lng) );
	}
	
	/**
	 * dot * 1E6
	 * @param dot
	 * @return
	 */
	public static int convertDot( double dot ) {
		return Double.valueOf(dot * 1E6).intValue();
	}
	
	public static boolean isLocationEquals(BDLocation l, BDLocation r) {
		return convertGeoPoint(l).equals(convertGeoPoint(r));
	}
	
	/**
	 * {@link #calculateSpanAndCenter(GeoPoint[], Integer[], GeoPoint)}
	 * @param points
	 * @param span
	 * @param centerPoint
	 */
	public static void calculateSpanAndCenter(final ArrayList<GeoPoint> points, final Integer[] span, final GeoPoint centerPoint ) {
		GeoPoint[] pointArr = new GeoPoint[points.size()];
		calculateSpanAndCenter(points.toArray(pointArr), span, centerPoint );
	}
	
	/**
	 * 计算给定坐标点的视野范围(span),以及中心点的 Geo 坐标
	 * <p>NOTE: 参数 span,centerPoint 都是地址传递，通过传递的方式来得到计算后的结果。调用该方法不会修改这两个参数的引用，<br>
	 * 其中参数 span 为二维数组。 span[0] 为 经度 (latitude)范围， span[1] 为 纬度(longtitude)范围
	 * @param points	数据源
	 * @param span		二维数组，经纬度跨度范围.span[0] 为 经度 (latitude)范围， span[1] 为 纬度(longtitude)范围
	 * @param centerPoint 中心点的Geo坐标
	 */
	public static void calculateSpanAndCenter(final GeoPoint[] points, final Integer[] span, final GeoPoint centerPoint) {
		if( points == null || points.length<=0 ) {
			return;
		}
		final int SIZE = points.length;
    	GeoPoint point = points[0];
    	int minLat = point.getLatitudeE6();
    	int minLng = point.getLongitudeE6();
    	int maxLat = minLat;
    	int maxLng = minLng;
    	
    	// 判断最大的经纬度和最小的经纬度
    	for( int i=1; i<SIZE; i++) {
    		GeoPoint p = points[i];
    		int lat = p.getLatitudeE6();
    		int lng = p.getLongitudeE6();
    		if( minLat > lat ) {
    			minLat = lat;
    		} else if( maxLat < lat ){
    			maxLat = lat;
    		} 
    		if( minLng > lng ) {
    			minLng = lng;
    		} else if( maxLng < lng ){
    			maxLng = lng;
    		}
    	}
    	span[0] = maxLat - minLat;
    	span[1] = maxLng - minLng;
    	centerPoint.setLatitudeE6((maxLat + minLat)/2);
    	centerPoint.setLongitudeE6((maxLng + minLng)/2);
	}
	
	public static MKMapStatus newMapStatusWithGeoPointAndZoom(GeoPoint p, float zoom) {
        MKMapStatus status = new MKMapStatus();
        status.targetGeo = p;
        status.zoom = zoom;
        return status;
    }
	
	/**
	 * 将 Wgs84 坐标转为 Baidu 坐标
	 * @param point
	 * @return
	 */
	public static GeoPoint convertWgs84ToBaidu(GeoPoint point) {
		return CoordinateConvert.fromWgs84ToBaidu(point);
	}
	
	/**
	 * 将 Wgs84 坐标转为 Baidu 坐标
	 * @param lat
	 * @param lng
	 * @return
	 */
	public static GeoPoint convertWgs84ToBaidu( double lat, double lng) {
		return CoordinateConvert.fromWgs84ToBaidu( convertGeoPoint(lat, lng) );
	}
	
	/**
     * 绘制折线，该折线状态随地图状态变化
     * @return 折线对象
     */
    public static Graphic drawLine(GeoPoint[] linePoints){
	    //构建线
  		Geometry lineGeometry = new Geometry();
  		lineGeometry.setPolyLine(linePoints);
  		//设定样式
  		Symbol lineSymbol = new Symbol();
  		Symbol.Color lineColor = lineSymbol.new Color();
  		lineColor.red = 86;
  		lineColor.green = 60;
  		lineColor.blue = 245;
  		lineColor.alpha = 0xFF;
  		lineSymbol.setLineSymbol(lineColor, 10);
  		//生成Graphic对象
  		Graphic lineGraphic = new Graphic(lineGeometry, lineSymbol);
  		return lineGraphic;
    }
}
