package org.lkdt.modules.radar.supports.radarDataService.radarCamUnion.DO;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;

import java.io.IOException;

public interface NetDEVSDKExt extends StdCallLibrary {

	public static String libraryPath = "C:\\\\NETDEVSDK_Win64_V2.5.0.9Ext\\\\bin\\\\NetDEVSDK";
	
//	public static String libraryPath = "NetDEVSDK";

 	NetDEVSDKExt INSTANCE = (NetDEVSDKExt) Native.loadLibrary(libraryPath, NetDEVSDKExt.class);


	public boolean NETDEV_Init();

	public boolean NETDEV_Cleanup();

	public long NETDEV_Login(String ip, int port, String username, String password, TagNETDEVDeviceInfo tagNETDEVDeviceInfo);

	public boolean NETDEV_Logout(long loginId);

	public int NETDEV_GetLastError();

	public long NETDEV_RealPlay(long lpUserID, NETDEV_PREVIEWINFO_S pstPreviewInfo, NETDEV_SOURCE_DATA_CALLBACK_PF cbPlayDataCallBack, long lpUserData);

	public boolean NETDEV_StopRealPlay(long lpPlayHandle);

	public static interface NETDEV_SOURCE_DATA_CALLBACK_PF extends StdCallCallback{
		//public void invoke(long lpPlayHandle, Pointer arg, int dwBufSize, int dwMediaDataType, long lpUserParam);
		public void invoke(long lpPlayHandle, Pointer pucBuffer, int dwBufSize, int dwMediaDataType, long lpUserParam) throws IOException;
	}



	/**
	 * 预览截图
	 * @param lpPlayHandle 起流句柄
	 * @param pszFileName 文件路径（包括文件名，不用包括扩展名）
	 * @param dwCaptureMode 保存图像格式 0【bmp】, 1【jpg】
	 * @return
	 */
	public boolean NETDEV_CapturePicture(long lpPlayHandle, String pszFileName, int dwCaptureMode);

	/**
	 * 查询视频通道信息列表  Query channel info list
	 * @param lpUserID           用户登录句柄 User login ID
	 * @param pdwChlCount        通道数 Number of channels
	 * @param pstVideoChlList    通道能力集列表 List of channel info list
	 * @return TRUE表示成功,其他表示失败 TRUE means success, and any other value means failure.
	 * @note
	 */
	//public boolean NETDEV_QueryVideoChlDetailListEx(long lpUserID, IntByReference pdwChlCount, NETDEV_VIDEO_CHL_DETAIL_INFO_EX_S[] pstVideoChlList);
	public boolean NETDEV_QueryVideoChlDetailListEx(long lpUserID, IntByReference pdwChlCount, NETDEV_VIDEO_CHL_DETAIL_INFO_EX_S[] pstVideoChlList);

	/**
	 * 无预览截图【测试未通过，原因未知】
	 * @param lpUserID 用户登录句柄
	 * @param dwChannelID 通道号
	 * @param dwStreamType 码流类型 0【主】, 1【辅】, 2【三流】
	 * @param pszFileName 保存图像的文件路径
	 * @param dwCaptureMode 保存图像格式 0【bmp】, 1【jpg】
	 * @return
	 */
	public boolean NETDEV_CaptureNoPreview(long lpUserID, int dwChannelID, int dwStreamType, String pszFileName, int dwCaptureMode);

	public static class TagNETDEVDeviceInfo extends Structure {

		public int dwDevType;

		public int wAlarmInPortNum;

		public int wAlarmOutPortNum;

		public int dwChannelNum;

		public byte[] byRes = new byte[48];

	}


	public static class NETDEV_PREVIEWINFO_S extends Structure {
		/**通道ID*/
		public int dwChannelID;
		/**码流类型0,1,2*/
		public int dwStreamType;
		/**传输协议0:udp,1:tcp*/
		public int dwLinkMode;
		/**播放窗口句柄*/
		public Long hPlayWnd;
		/**图像播放流畅性优先类型 0：实时性优先Real-time first ，1：流畅性优先Fluency first ，3：均衡Balance ，4：RTMP流畅性优先RTMP fluency first */
		public int dwFluency;
		/**流模式 0x0000音频加视频 0x8000:视频*/
		public int dwStreamMode;
		/**启流模式 0服务器分配，1强制分发*/
		public int dwLiveMode;
		/**分发能力 0不启用分发，1启用金山云分发，2启用宇视云分发，3启用腾讯云分发*/
		public int dwDisTributeCloud;
		/**是否支持分发*/
		public boolean dwallowDistribution;
		/**传输类型 0一体机转发,1下行设备直连*/
		public int dwTransType;
		/**保留字段*/
		public byte[] byRes = new byte[240];
	}

	/**
	 *
	 * @param dwChannelNum 通道号
	 * @return
	 */
	public static NETDEV_PREVIEWINFO_S getNewNETDEV_PREVIEWINFO_S_instance(int dwChannelNum){
		NetDEVSDKExt.NETDEV_PREVIEWINFO_S pstPreviewInfo = new NetDEVSDKExt.NETDEV_PREVIEWINFO_S();
		pstPreviewInfo.dwChannelID = dwChannelNum;
		//pstPreviewInfo.dwStreamType = 1;
		pstPreviewInfo.dwStreamType = 2;
		pstPreviewInfo.dwLinkMode = 1;
		pstPreviewInfo.hPlayWnd = null;
		pstPreviewInfo.dwFluency = 0;
		pstPreviewInfo.dwStreamMode = 0x8000;
		pstPreviewInfo.dwLiveMode = 0;
		pstPreviewInfo.dwDisTributeCloud = 0;
		pstPreviewInfo.dwallowDistribution  = false;
		pstPreviewInfo.dwTransType = 1;
		return pstPreviewInfo;
	}

	/**
	 * 视频通道详细信息
	 */
	public class NETDEV_VIDEO_CHL_DETAIL_INFO_EX_S extends Structure {
		/** 通道ID  Channel ID */
		public int dwChannelID;
		/** 是否支持云台 Whether ptz is supported */
		public int bPtzSupported;
		/** 通道状态  Channel status NETDEV_CHANNEL_STATUS_E */
		public int enStatus;
		/** 流个数  Number of streams 当enStatus为NETDEV_CHL_STATUS_UNBIND时，此值无效*/
		public int dwStreamNum;
		/** 通道类型，(note: 该字段仅对混合NVR有效)，参考 NETDEV_CHANNEL_TYPE_E*/
		public int enChannelType;
		/** 视频输入制式，参考NETDEV_CHANNEL_TYPE_E，当ChannelType为NETDEV_CHL_TYPE_DIGITAL时，此值无效 (note: 该字段仅对混合NVR有效) */
		public int enVideoFormat;
		/** IP地址类型 ,参考NETDEV_HOSTTYPE_E*/
		public int enAddressType;
		/** IP地址 IP address*/
		public byte[] szIPAddr =new byte[64];
		/** 端口号 */
		public int dwPort;
		/** 通道名称 Channel Name */
		public byte[] szChnName =new byte[64];;
		/** 是否允许流分发*/
		public int allowDistribution;
		/** 通道接入的设备类型，参见枚举NETDEV_CHANNEL_CAMERA_TYPE_E. Channel device Type see#NETDEV_CHANNEL_CAMERA_TYPE_E */
		public int dwDeviceType;
		/** 厂商，范围[0,31] */
		public byte[] szManufacturer =new byte[32];
		/** 设备型号，范围[0,31]  */
		public byte[] szDeviceModel =new byte[32];
		/** 接入协议类型 参见NETDEV_ACCESS_PROTOCOL_E */
		public int udwAccessProtocol;
		/** 保留字段  Reserved field*/
		public byte[] byRes = new byte[20];
	}
}
