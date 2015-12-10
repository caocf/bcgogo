package com.tonggou.gsm.andclient.util;

import java.util.List;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.CoordinateConverter.CoordType;
import com.baidu.mapapi.utils.DistanceUtil;
import com.tonggou.gsm.andclient.bean.LatLngParcel;

public class BMapUtil {
	int zoomLevel[] = {2000000,1000000,500000,200000,100000,
			50000,25000,20000,10000,5000,2000,1000,500,100,50,20,10};

	public static String distanceWithUserAndVehicle = "";

	public static void setDistanceWithUserAndVehicle(LatLng a, LatLng b) {
		double d = ((double)(DistanceUtil.getDistance(a, b) / 1000.000));
		distanceWithUserAndVehicle = String.format("%.2f", d);
	}

	public static LatLngParcel convertGeoPoint(BDLocation location) {
		return convertGeoPoint( location.getLatitude(), location.getLongitude());
	}

	public static LatLngParcel convertGeoPoint( double lat, double lng ) {
		return new LatLngParcel( lat, lng );
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
	 * {@link #calculateSpanAndCenter(LatLngParcel[], LatLng[], LatLngParcel)}
	 * @param points
	 * @param span
	 * @param centerPoint
	 */
	public static void calculateSpanAndCenter(final List<LatLngParcel> points, final LatLng[] span, final LatLngParcel centerPoint ) {
		LatLngParcel[] pointArr = new LatLngParcel[points.size()];
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
	public static void calculateSpanAndCenter(final LatLngParcel[] points, final LatLng[] span, final LatLngParcel centerPoint) {
		if( points == null || points.length<=0 ) {
			return;
		}

		final int SIZE = points.length;
		LatLngParcel point = points[0];
		double minLat = point.getLatitude();
		double minLng = point.getLongitude();
		double maxLat = minLat;
		double maxLng = minLng;

		// 判断最大的经纬度和最小的经纬度
		for( int i=1; i<SIZE; i++) {
			LatLngParcel p = points[i];
			double lat = p.getLatitude();
			double lng = p.getLongitude();
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
		span[0] = new LatLng(minLat, minLng);
		span[1] = new LatLng(maxLat, maxLng);
		centerPoint.setLatitude((maxLat + minLat)/2.0);
		centerPoint.setLongitude((maxLng + minLng)/2.0);
	}

	public static LatLngParcel latLngToParcel(LatLng ll) {
		return new LatLngParcel(ll.latitude, ll.longitude);
	}

	public static LatLng[] convertLatLngParcelArrayToLatLngArray(LatLngParcel[] llp) {
		LatLng[] ll = new LatLng[llp.length];
		for (int i = 0 ; i< llp.length ;i ++) {
			ll[i] = llp[i].getLatLng();
		}

		return ll;
	}

	public static MapStatusUpdate newMapStatusWithLatLngAndZoom(LatLng p, float zoom) {
		MapStatusUpdate mapStatus = MapStatusUpdateFactory.newLatLngZoom(p, zoom);
		return mapStatus;
	}

	public static MapStatusUpdate newMapStatusWithLatLngArray(LatLng[] ll) {
		LatLngBounds.Builder builder = new LatLngBounds.Builder();
		builder.include(ll[1]);
		builder.include(ll[0]);
		LatLngBounds bounds = builder.build();

		MapStatusUpdate mapStatus = MapStatusUpdateFactory.newLatLngBounds(bounds);
		return mapStatus;
	}

	public static LatLng convertWgs84ToBaidu(LatLng point) {
		CoordinateConverter converter = new CoordinateConverter();
		converter.from(CoordType.GPS);

		return converter.coord(point).convert();
	}

	/**
	 * 将 Wgs84 坐标转为 Baidu 坐标
	 * @param point
	 * @return
	 */
	public static LatLng convertWgs84ToBaidu(LatLngParcel point) {
		CoordinateConverter converter = new CoordinateConverter();
		converter.from(CoordType.GPS);

		return converter.coord(point.getLatLng()).convert();
	}

	/**
	 * 将 Wgs84 坐标转为 Baidu 坐标
	 * @param lat
	 * @param lng
	 * @return
	 */
	public static LatLng convertWgs84ToBaidu( double lat, double lng) {
		CoordinateConverter converter = new CoordinateConverter();
		converter.from(CoordType.GPS);

		return converter.coord(new LatLng(lat, lng)).convert();
	}

	/**
	* 绘制折线，该折线状态随地图状态变化
	* @return 折线对象
	*/
	public static OverlayOptions drawLine(List<LatLng> pointList){
		OverlayOptions polylineOption = new PolylineOptions()
		.points(pointList)
		.color(0xFF563CF5)
		.width(10);
		return polylineOption;
	}

	public static int getInfoWindowYOffset (int resolution) {
		if (resolution >= 1920)
			return -95;
		if (resolution >= 1812)
			return -92;
		else if (resolution >= 1280)
			return -65;
		else if (resolution >= 800)
			return -45;
		else
			return -35;
	}
}