
package com.tonggou.gsm.andclient.net.response;

import java.util.ArrayList;

import android.os.Parcel;

import com.tonggou.gsm.andclient.bean.DriveLogDTO;
import com.tonggou.gsm.andclient.bean.VideoPictureDTO;

/**
 * 视频照片响应结果
 * @author peter
 *
 */
public class QueryVideoRecordResponse extends BaseResponse {
	private static final long serialVersionUID = 238003148336620645L;

	private ArrayList<VideoPictureDTO> data = new ArrayList<VideoPictureDTO>();
	private int type;
	private long date;
	private String video;
	private int progress;
    private long vid;
	private String picture[];
	private String place;

	public ArrayList<VideoPictureDTO> getVideoRecordDTOs() {
        return data;
    }

    public void setVideoRecordDTOs(ArrayList<VideoPictureDTO> driveLogDTOs) {
        this.data = driveLogDTOs;
    }

	public QueryVideoRecordResponse () {
		
	}

	public QueryVideoRecordResponse (Parcel in) {
		in.readTypedList(data, VideoPictureDTO.CREATOR);

		type = in.readInt();
		date = in.readLong();
		video = in.readString();
		progress = in.readInt();
		vid = in.readLong();
		in.readStringArray(picture);
		place = in.readString();
	}

	public String getRecordPlace() {
		return place;
	}

	public void setRecordPlace(String area) {
		this.place = area;
	}

	public String[] getPicturePath() {
		return picture;
	}

	public void setPicturePath(String[] path) {
		this.picture = path;
	}

	public String getVideoPath() {
		return video;
	}
	public void setVideoPath(String code) {
		this.video = code;
	}

	public int getUploadProgress() {
		return progress;
	}

	public void setUploadProgress(int progress) {
		this.progress = progress;
	}

	public int getVideoType() {
		return type;
	}

	public void setVideoType(int type) {
		this.type = type;
	}

	public Long getRecordDate() {
 		return date;
	}

	public void setRecordDate(long date) {
		this.date = date;
	}

	public long getVideoId() {
		return vid;
	}

	public void setVideoId(long id) {
		vid = id;
	}

	/**
	 * 得到没有 任何记录的对象，但是包括其他的信息
	 * <p>可以用来传递对象单个 记录对象,需要传递时，请使用 {@link #addSingleDriveLog(DriveLogDTO)}
	 * @return
	 */
	public QueryVideoRecordResponse copyWithoutLogs() {
		QueryVideoRecordResponse response = new QueryVideoRecordResponse();

		response.setVideoType(type);
		response.setRecordDate(date);
		response.setVideoPath(video);
		response.setUploadProgress(progress);
		response.setVideoId(vid);
		response.setPicturePath(picture);
		response.setRecordPlace(place);

		return response;
	}

	public void addSingleDriveLog(VideoPictureDTO videoDTO) {
		data.clear();
		data.add(videoDTO);
	}
	
}