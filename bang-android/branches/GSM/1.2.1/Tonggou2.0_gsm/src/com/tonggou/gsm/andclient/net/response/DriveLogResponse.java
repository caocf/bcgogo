
package com.tonggou.gsm.andclient.net.response;

import java.util.ArrayList;

import android.os.Parcel;

import com.tonggou.gsm.andclient.bean.DriveLogDTO;

/**
 * 行车日志响应结果
 * @author lwz
 *
 */
public class DriveLogResponse extends BaseResponse {

	private static final long serialVersionUID = 6698723156429352219L;
	
    private ArrayList<DriveLogDTO> driveLogDTOs = new ArrayList<DriveLogDTO>();
    private float worstOilWear;	// 最差平均油耗
    private float bestOilWear;	// 最好平均油耗
    private long subtotalTravelTime;	// 行车时长
    private float subtotalDistance;		// 行驶里程
    private float subtotalOilMoney;	// 总金额
    private float totalOilWear;			// 总油耗
    private float subtotalOilCost;		// 综合平均油耗
    private float subtotalOilWear;		// 总平均油耗
    
    public DriveLogResponse() {
    }
    
    DriveLogResponse(Parcel in) {
    	in.readTypedList(driveLogDTOs, DriveLogDTO.CREATOR);
    	worstOilWear = in.readFloat();
    	bestOilWear = in.readFloat();
    	subtotalTravelTime = in.readLong();
    	subtotalDistance = in.readFloat();
    	subtotalOilMoney = in.readFloat();
    	totalOilWear = in.readFloat();
    	subtotalOilCost = in.readFloat();
    	subtotalOilWear = in.readFloat();
    }
    
//    public static final Parcelable.Creator<DriveLogResponse> CREATOR = new Creator<DriveLogResponse>() {
//		
//		@Override
//		public DriveLogResponse[] newArray(int size) {
//			return new DriveLogResponse[size];
//		}
//		
//		@Override
//		public DriveLogResponse createFromParcel(Parcel source) {
//			return new DriveLogResponse(source);
//		}
//	};
//	
//    @Override
//	public int describeContents() {
//		return 0;
//	}
//
//	@Override
//	public void writeToParcel(Parcel dest, int flags) {
//		dest.writeTypedList(driveLogDTOs);
//		dest.writeFloat(worstOilWear);
//		dest.writeFloat(bestOilWear);
//		dest.writeLong(subtotalTravelTime);
//		dest.writeFloat(subtotalDistance);
//		dest.writeFloat(subtotalOilMoney);
//		dest.writeFloat(totalOilWear);
//		dest.writeFloat(subtotalOilCost);
//		dest.writeFloat(subtotalOilWear);
//	}

	public ArrayList<DriveLogDTO> getDriveLogDTOs() {
        return driveLogDTOs;
    }

    public void setDriveLogDTOs(ArrayList<DriveLogDTO> driveLogDTOs) {
        this.driveLogDTOs = driveLogDTOs;
    }

	public float getWorstOilWear() {
		return worstOilWear;
	}

	public void setWorstOilWear(float worstOilWear) {
		this.worstOilWear = worstOilWear;
	}

	public float getBestOilWear() {
		return bestOilWear;
	}

	public void setBestOilWear(float bestOilWear) {
		this.bestOilWear = bestOilWear;
	}

	public long getSubtotalTravelTime() {
		return subtotalTravelTime;
	}

	public void setSubtotalTravelTime(long subtotalTravelTime) {
		this.subtotalTravelTime = subtotalTravelTime;
	}

	public float getSubtotalDistance() {
		return subtotalDistance;
	}

	public void setSubtotalDistance(float subtotalDistance) {
		this.subtotalDistance = subtotalDistance;
	}

	public float getSubtotalOilMoney() {
		return subtotalOilMoney;
	}

	public void setSubtotalOilMoney(float subtotalOilMoney) {
		this.subtotalOilMoney = subtotalOilMoney;
	}

	public float getTotalOilWear() {
		return totalOilWear;
	}

	public void setTotalOilWear(float totalOilWear) {
		this.totalOilWear = totalOilWear;
	}

	public float getSubtotalOilCost() {
		return subtotalOilCost;
	}

	public void setSubtotalOilCost(float subtotalOilCost) {
		this.subtotalOilCost = subtotalOilCost;
	}

	public float getSubtotalOilWear() {
		return subtotalOilWear;
	}

	public void setSubtotalOilWear(float subtotalOilWear) {
		this.subtotalOilWear = subtotalOilWear;
	}
	
	/**
	 * 得到没有 任何记录的对象，但是包括其他的信息
	 * <p>可以用来传递对象单个 记录对象,需要传递时，请使用 {@link #addSingleDriveLog(DriveLogDTO)} 
	 * @return
	 */
	public DriveLogResponse copyWithoutLogs() {
		DriveLogResponse response = new DriveLogResponse();
		response.setWorstOilWear(worstOilWear);
		response.setBestOilWear(bestOilWear);
		response.setSubtotalTravelTime(subtotalTravelTime);
		response.setSubtotalDistance(subtotalDistance);
		response.setSubtotalOilMoney(subtotalOilMoney);
		response.setTotalOilWear(totalOilWear);
		response.setSubtotalOilCost(subtotalOilCost);
		response.setSubtotalOilWear(subtotalOilWear);
		return response;
	}
	
	public void addSingleDriveLog(DriveLogDTO driveLog) {
		driveLogDTOs.clear();
		driveLogDTOs.add(driveLog);
	}

}
