package org.lkdt.modules.radar.supports.radarDataService.radarCamUnion.DO;

import static org.bytedeco.javacpp.avutil.av_log_set_level;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

import javax.imageio.ImageIO;

import org.bytedeco.javacpp.avcodec;
import org.bytedeco.javacpp.avutil;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.FrameRecorder;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.lkdt.common.util.StringUtils;
import org.lkdt.modules.radar.entity.ZcLdEventManage;
import org.lkdt.modules.radar.supports.radarDataService.radarCamUnion.service.RecordCall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jna.Pointer;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import sun.misc.BASE64Encoder;

@Data
public class CameraDO extends BaseCamera {

    public CameraDO() {
    }

    /**
     *
     * @param bInit 初始化相机资源
     * @param cameraId 摄像机ID
     * @param cameraType 摄像机类型
     * @param isBindRadar 是否绑定雷达
     * @param radarId 雷达ID
     * @param cacheBytesList 数据缓存
     * @param status 摄像机状态
     */
    public CameraDO(boolean bInit, String cameraId, String cameraType, String ip, int port, String username, String password,
                    boolean isBindRadar, String radarId, List<byte[]> cacheBytesList, Enum status, String savePath, String savePathSnapPic){
        this.bInit = bInit;
        this.cameraId = cameraId;
        this.cameraType = cameraType;
        this.ip = ip;
        this.port = port;
        this.username = username;
        this.password = password;
        this.isBindRadar = isBindRadar;
        this.radarId = radarId;
        //this.cacheBytesList = cacheBytesList;
        this.status = status;
        this.SNAP_PIC_DIR_base = savePathSnapPic + this.SNAP_PIC_DIR_base;
        this.EVENT_STORAGE_DIR_base = savePath + this.EVENT_STORAGE_DIR_base;
        this.SNAP_PIC_DIR = this.SNAP_PIC_DIR_base + cameraId + File.separator;
        this.EVENT_STORAGE_DIR = this.EVENT_STORAGE_DIR_base + cameraId + File.separator;


    }

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    protected final int cacheSize = 100;

    /**摄像机ID*/
    @Setter(AccessLevel.NONE)
    protected String cameraId;

    /**摄像机类型*/
    @Setter(AccessLevel.NONE)
    protected String cameraType;

    /**摄像机IP*/
    @Setter(AccessLevel.NONE)
    protected String ip;

    /**端口号*/
    @Setter(AccessLevel.NONE)
    protected int port;

    /**用户名*/
    @Setter(AccessLevel.NONE)
    protected String username;

    /**密码*/
    @Setter(AccessLevel.NONE)
    protected String password;

    /**是否绑定雷达*/
    @Setter(AccessLevel.NONE)
    protected boolean isBindRadar;

    /**雷达编号*/
    @Setter(AccessLevel.NONE)
    protected String radarId;

    /**数据缓存*/
//    @Getter(AccessLevel.NONE)
//    @Setter(AccessLevel.NONE)
    //volatile protected List<byte[]> cacheBytesList = new CopyOnWriteArrayList<>();

    /**图片高*/
    @Setter(AccessLevel.NONE)
    volatile protected int height;

    /**图片宽*/
    @Setter(AccessLevel.NONE)
    volatile protected int width;

    /**事件锁*/
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    protected ReentrantLock lock = new ReentrantLock();

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    protected ReentrantLock forceLock = new ReentrantLock();

    /**事件数据缓存*/
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    volatile protected ArrayBlockingQueue<byte[]> eventCaches = new ArrayBlockingQueue<>(cacheSize*2);

    //事件控制
//    private volatile boolean eventCachesBool = false;

    //private byte[] currentBytes;

    /**摄像机状态 0：初始化，1：正常，2：网络异常，3：强制终止*/
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    volatile public Enum status;

    /**摄像机时间戳*/
    @Setter(AccessLevel.NONE)
    protected long timeStatus;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private volatile long lpPlayHandle;

    /**是否保存图片*/
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    protected boolean isSavePic = false;

    /**是否保存视频*/
    @Setter(AccessLevel.NONE)
    protected boolean isSaveVideo = true;

    /**截图路径base*/
    //protected String SNAP_PIC_DIR_base = "C:\\radar-server\\cachePic\\";
    protected String SNAP_PIC_DIR_base = "cachePic\\";

    /**截图路径*/
    protected String SNAP_PIC_DIR = "";

    /**事件存储路径base*/
    //protected String EVENT_STORAGE_DIR_base = "C:\\radar-server\\eventStorage\\";
    protected String EVENT_STORAGE_DIR_base = "eventStorage\\";

    /**事件存储路径*/
    protected String EVENT_STORAGE_DIR = "";

    /**路径名*/
    protected String eventVideo = "eventVideo";

    /**路径名*/
    protected String eventPicture = "eventPicture";

    public String getPath() {
        String path = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        if(System.getProperty("os.name").contains("dows")) {
            path = path.substring(1,path.length());
        }
        if(path.contains("jar")) {
            path = path.substring(0,path.lastIndexOf("."));
            return path.substring(0,path.lastIndexOf("/"));
        }
        return path.replace("target/classes/", "");
    }

    protected boolean bInit = false;
    protected volatile long loginID;
    volatile protected NetDEVSDK.TagNETDEVDeviceInfo tagNETDEVDeviceInfo;
    volatile protected NetDEVSDKExt.TagNETDEVDeviceInfo tagNETDEVDeviceInfoExt;

    protected void valid(){
        //验证缓存路径：part1
        File file = new File(SNAP_PIC_DIR_base);
        if(!file.exists()){
            if(!file.mkdirs()) {
                new RuntimeException("摄像头文件缓存路径创建失败！" + file.getPath());
            }
        }

        //验证缓存路径：part2
        File file2 = new File(SNAP_PIC_DIR);
        if(!file2.exists()){
            if(!file2.mkdirs()){
                new RuntimeException("摄像头文件缓存路径创建失败！" + file2.getPath());
            }
        }

        //验证事件存储路径：part1
        File eventStorageFile = new File(EVENT_STORAGE_DIR_base);
        if(!eventStorageFile.exists()){
            if(!eventStorageFile.mkdirs()) {
                new RuntimeException("事件存储路径创建失败！" + eventStorageFile.getPath());
            }
        }

        //验证事件存储路径：part2
        File eventStorageFile2 = new File(EVENT_STORAGE_DIR);
        if(!eventStorageFile2.exists()){
            if(!eventStorageFile2.mkdirs()){
                new RuntimeException("摄像头文件缓存路径创建失败！" + eventStorageFile2.getPath());
            }
        }
    }

    public void stopRealPlay(){
        boolean bool = NetDEVSDK.INSTANCE.NETDEV_StopRealPlay(lpPlayHandle);
        logger.info("【{}】停止预览资源：{}", cameraId, bool);

        if(!bool){
            int initStatus = NetDEVSDK.INSTANCE.NETDEV_GetLastError();
            logger.error("【{}】停止预览失败错误码：{}", cameraId, initStatus);
        }
    }

    //初始化相机资源
    public void initCam(){
        boolean bInit = NetDEVSDK.INSTANCE.NETDEV_Init();
        logger.info("【{}】初始化相机资源：{}", cameraId, bInit);

        if(!bInit){
            int initStatus = NetDEVSDK.INSTANCE.NETDEV_GetLastError();
            logger.error("【{}】初始化失败错误码：{}", cameraId, initStatus);
        }
    }

    //销毁相机资源
    public void destroyCam(){
        boolean bDestroy = NetDEVSDK.INSTANCE.NETDEV_Cleanup();
        logger.info("【{}】销毁相机资源：{}", cameraId, bDestroy);

        if(!bDestroy){
            int initStatus = NetDEVSDK.INSTANCE.NETDEV_GetLastError();
            logger.error("【{}】销毁相机失败错误码：{}", cameraId, initStatus);
        }

    }

    //登录
    public void login(){
        valid();
        tagNETDEVDeviceInfo = new NetDEVSDK.TagNETDEVDeviceInfo();
        loginID = NetDEVSDK.INSTANCE.NETDEV_Login(this.ip, this.port, this.username, this.password, tagNETDEVDeviceInfo);
        if(loginID == 0){

            int loginCode = NetDEVSDK.INSTANCE.NETDEV_GetLastError();
            logger.error("【{}】登录失败错误码：{}", cameraId, loginCode);

        } else {
            logger.info("【{}】登录：成功，用户ID：{}，通道号：{}", cameraId, loginID, tagNETDEVDeviceInfo.dwChannelNum);

            this.status = CamStatus.NORMAL;

        }
    }


    //退出登录
    public boolean loginOut(){
        boolean bool = NetDEVSDK.INSTANCE.NETDEV_Logout(loginID);
        logger.info("【{}】退出登录，loginId:{}", cameraId, loginID);
        return bool;
    }

//    private class VideoRun implements Runnable{
//
//        @Override
//        public void run() {
//            try{
//
//                logger.info("【{}】事件录屏开始", cameraId);
//
//                NetDEVSDK.NETDEV_PREVIEWINFO_S pstPreviewInfo = NetDEVSDK.getNewNETDEV_PREVIEWINFO_S_instance(tagNETDEVDeviceInfo.dwChannelNum);
//
//                long lpUserData = 0;
//                lpPlayHandle = NetDEVSDK.INSTANCE.NETDEV_RealPlay(loginID, pstPreviewInfo, (long lpPlayHandleOut, Pointer pucBuffer, int dwBufSize, int dwMediaDataType, long lpUserParam) -> {
//                    //currentBytes = pucBuffer.getByteArray(0, dwBufSize);
//                    //this.timeStatus = Calendar.getInstance().getTimeInMillis();
//                }, lpUserData);
//
//                logger.info("【{}】loginID：{}，事件录屏句柄：{}，状态：{}，布尔：{}", cameraId, loginID, lpPlayHandle, status, eventCachesBool);
//                Thread.sleep(600);
//
//                while(CamStatus.NORMAL == status && eventCachesBool && lpPlayHandle != 0){
//
//                    String fileName = SNAP_PIC_DIR + Calendar.getInstance().getTimeInMillis();
//
//                    String formatName = "jpg";
//                    String extendName = ".jpg";
//
//                    //截图
//                    boolean capPicBool = NetDEVSDK.INSTANCE.NETDEV_CapturePicture(lpPlayHandle, fileName, 1);
//
//                    if(!capPicBool){
//                        int errorCapture = NetDEVSDK.INSTANCE.NETDEV_GetLastError();
//                        //logger.error("【{}】截图失败错误码：{}", cameraId, errorCapture);
//                        System.out.printf("【%s】截图失败错误码：%s\n", cameraId, errorCapture);
//                        Thread.sleep(50);
//                    } else {
//                        //经过测试播放5秒之后截图成功
//                        //System.out.printf("【%s】截图成功\n", cameraId);
//
//                        //截图成功
//                        ByteOutputStream byteOutputStream = null;
//                        try{
//                            File snapPicFile = new File(fileName + extendName);
//                            if(snapPicFile.exists()){
//                                BufferedImage bufferedImage = ImageIO.read(snapPicFile);
//                                height = bufferedImage.getHeight();
//                                width = bufferedImage.getWidth();
//                                byteOutputStream = new ByteOutputStream();
//                                ImageIO.write(bufferedImage, formatName, byteOutputStream);
//                                byte[] bytes = byteOutputStream.getBytes();
//
//                                //删除临时文件
//                                snapPicFile.delete();
//
//                                //缓存
//                                if(cacheBytesList.size() >= cacheSize){
//                                    cacheBytesList.remove(0);
//                                }
//                                cacheBytesList.add(bytes);
//                                //有事件
//                                if(eventCachesBool){
//                                    eventCaches.offer(bytes);
//                                    if(eventCaches.size() >= 200){
//                                        eventCachesBool = false;
//                                    }
//                                }
//
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        } finally {
//                            if(byteOutputStream != null){
//                                byteOutputStream.close();
//                            }
//                        }
//
//                    }
//                    //break;
//
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                logger.info("【{}】事件录屏结束", cameraId);
//            }
//
//        }
//    }

    /**
     * true:缓存成功
     * false:尝试摄像机资源受限
     * @return
     */
    public boolean tryEventVideoCache(String eventType, RecordCall recordCall){
        if(this.lock.tryLock() && this.status == CamStatus.NORMAL){
            try {
                //启动录屏
//                VideoRun videoRun = new VideoRun();
//                CameraComponent.recordThreadPool.submit(videoRun);
//                this.eventCaches.clear();
//                //this.eventCaches.addAll(this.cacheBytesList);
//                //事件标记
//                this.eventCachesBool = true;
//
//                long startTime = Calendar.getInstance().getTimeInMillis();
//                while(this.eventCaches.size() < 200){
//                    long endTime = Calendar.getInstance().getTimeInMillis();
//                    //验证
//                    if(endTime - startTime > 60000){
//                        //异常结束
//                        logger.error("【{}】事件录屏超时，异常结束, 播放ID:{}, 已缓存大小:{}", cameraId, lpPlayHandle, this.eventCaches.size());
//                        //解除事件标记
//                        this.eventCachesBool = false;
//                        this.status = CamStatus.FORCE_STOP;
//                        //停止预览
//                        stopRealPlay();
//                        //退出登录
//                        loginOut();
//                        //重新登录
//                        login();
//                        return false;
//                    }
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//                //解除事件标记
//                this.eventCachesBool = false;


                logger.info("【{}】事件录屏开始", cameraId);

                NetDEVSDK.NETDEV_PREVIEWINFO_S pstPreviewInfo = NetDEVSDK.getNewNETDEV_PREVIEWINFO_S_instance(tagNETDEVDeviceInfo.dwChannelNum);

                long lpUserData = 0;
                lpPlayHandle = NetDEVSDK.INSTANCE.NETDEV_RealPlay(loginID, pstPreviewInfo, (long lpPlayHandleOut, Pointer pucBuffer, int dwBufSize, int dwMediaDataType, long lpUserParam) -> {
                    //currentBytes = pucBuffer.getByteArray(0, dwBufSize);
                    //this.timeStatus = Calendar.getInstance().getTimeInMillis();
                }, lpUserData);

                logger.info("【{}】事件录屏loginID：{}，事件录屏ID：{}，状态：{}", cameraId, loginID, lpPlayHandle, status);
                Thread.sleep(600);

                if(lpPlayHandle == 0){
                    logger.info("【{}】事件录屏失败，loginID：{}，事件录屏ID：{}，状态：{}", cameraId, loginID, lpPlayHandle, status);
                    return false;
                }

                int errorCount = 0;
                while(CamStatus.NORMAL == status){

                    String fileName = SNAP_PIC_DIR + Calendar.getInstance().getTimeInMillis();

                    String formatName = "jpg";
                    String extendName = ".jpg";

                    //截图
                    boolean capPicBool = NetDEVSDK.INSTANCE.NETDEV_CapturePicture(lpPlayHandle, fileName, 1);


                    if(!capPicBool){
                        int errorCapture = NetDEVSDK.INSTANCE.NETDEV_GetLastError();
                        //logger.error("【{}】截图失败错误码：{}", cameraId, errorCapture);
                        System.out.printf("【%s】事件录屏截图失败错误码：%s\n", cameraId, errorCapture);
                        if(errorCount++ >= 100){
                            break;
                        }
                        Thread.sleep(50);
                    } else {
                        //经过测试播放5秒之后截图成功
                        //System.out.printf("【%s】截图成功\n", cameraId);

                        //截图成功
                        ByteOutputStream byteOutputStream = null;
                        try{
                            File snapPicFile = new File(fileName + extendName);
                            if(snapPicFile.exists()){
                                BufferedImage bufferedImage = ImageIO.read(snapPicFile);
                                height = bufferedImage.getHeight();
                                width = bufferedImage.getWidth();
                                byteOutputStream = new ByteOutputStream();
                                ImageIO.write(bufferedImage, formatName, byteOutputStream);
                                byte[] bytes = byteOutputStream.getBytes();

                                //删除临时文件
                                snapPicFile.delete();

                                eventCaches.offer(bytes);
                                if(eventCaches.size() >= 200){
                                    break;
                                }

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            break;
                        } finally {
                            if(byteOutputStream != null){
                                byteOutputStream.close();
                            }
                        }

                    }
                }
                boolean bool = NetDEVSDK.INSTANCE.NETDEV_StopRealPlay(lpPlayHandle);
                logger.info("【{}】事件录屏停止播放：{}", cameraId, bool);
                ////////////////////////////////////////////////////////////////
                //开始事件数据存储
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
                String eventVideo = "eventVideo";
                File eventVideoFile = new File(this.EVENT_STORAGE_DIR + eventVideo);
                if(!eventVideoFile.exists()){
                    if(!eventVideoFile.mkdir()){
                        logger.error("【{}】创建视频文件夹异常1", this.cameraId);
                        return false;
                    }
                }
                String eventPicture = "eventPicture";
                File eventPictureFile = new File(this.EVENT_STORAGE_DIR + eventPicture);
                if(!eventPictureFile.exists()){
                    if(!eventPictureFile.mkdir()){
                        logger.error("【{}】创建图片文件夹异常1", this.cameraId);
                        return false;
                    }
                }
                String dateStr___ = sdf.format(new Date());
                String videoDir = this.EVENT_STORAGE_DIR + eventVideo + File.separator + dateStr___ + " $[" + eventType + "]$.mp4";
                String pictureDir = this.EVENT_STORAGE_DIR + eventPicture + File.separator;
                FFmpegFrameRecorder recorder = null;
                try {
                    av_log_set_level(avutil.AV_LOG_ERROR);
                    recorder = new FFmpegFrameRecorder(videoDir, this.width, this.height);
                    recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264); // 28
                    //recorder.setVideoCodec(avcodec.AV_CODEC_ID_FLV1); // 28
                    //recorder.setVideoCodec(avcodec.AV_CODEC_ID_MPEG4); // 13
                    recorder.setFormat("mp4");
                    //recorder.setFormat("flv");
                    //recorder.setFormat("mov,mp4,m4a,3gp,3g2,mj2,h264,ogg,MPEG4");
                    recorder.setFrameRate(25);
                    recorder.setPixelFormat(0); // yuv420p
                    recorder.start();
                    //
                    //OpenCVFrameConverter.ToIplImage conveter = new OpenCVFrameConverter.ToIplImage();
                    Java2DFrameConverter converter = new Java2DFrameConverter();
                    ByteArrayInputStream bais;
                    // 循环所有图片
                    while(true) {
                        byte[] bytesQ = this.eventCaches.poll();
                        if (bytesQ == null) {
                            break;
                        }

                        bais = new ByteArrayInputStream(bytesQ);
                        BufferedImage read = ImageIO.read(bais);
                        recorder.record(converter.getFrame(read));

                        //保存图片【默认不保存】
                        if(isSavePic){
                            FileOutputStream fileOutputStream = null;
                            BufferedOutputStream bos = null;
                            try{

                                File picDir = new File(pictureDir + dateStr___ + File.separator);
                                if(!picDir.exists()){
                                    if(!picDir.mkdir()){
                                        logger.error("【{}】创建图片文件夹异常inner", this.cameraId);
                                        return false;
                                    }
                                }

                                File outFile = new File(pictureDir + dateStr___ + File.separator + Calendar.getInstance().getTimeInMillis() + ".jpg");
                                fileOutputStream = new FileOutputStream(outFile);
                                bos = new BufferedOutputStream(fileOutputStream);
                                bos.write(bytesQ);
                                bos.flush();

                                bos.close();
                                fileOutputStream.close();

                                bais.close();
                            } catch (IOException io) {
                                io.printStackTrace();
                            } finally {
                                if(bos != null){
                                    bos.close();
                                }
                                if(fileOutputStream != null){
                                    fileOutputStream.close();
                                }
                                if(bais != null){
                                    bais.close();
                                }

                            }
                        }


                    }
                    logger.info("【{}】事件录屏正常结束：{}", cameraId, videoDir);
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error("【{}】创建视频异常2", this.cameraId);
                    return false;
                } finally {
                    if(recorder != null){
                        try {
                            recorder.stop();
                            recorder.release();
                        } catch (FrameRecorder.Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                if(recordCall != null){
                    recordCall.callback(eventType, videoDir);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(this.lock.isHeldByCurrentThread()){
                    this.lock.unlock();
                }
            }
            //结束
            ////////////////////////////////////////////////////////////////
            return true;
        }
        //logger.error("【{}】尝试摄像机资源受限", this.cameraId);
        return false;
    }

    /**
     * 录屏
     * @param zcLdEventManage
     * @return
     */
    @Deprecated
    public boolean forceEventVideoCache(ZcLdEventManage zcLdEventManage){
        if(this.forceLock.tryLock() && this.status == CamStatus.NORMAL){
            try {
                //启动录屏
                logger.info("【{}】事件强制录屏开始", cameraId);

                NetDEVSDK.NETDEV_PREVIEWINFO_S pstPreviewInfo = NetDEVSDK.getNewNETDEV_PREVIEWINFO_S_instance(tagNETDEVDeviceInfo.dwChannelNum);

                long lpUserData = 0;
                long lpPlayHandle = NetDEVSDK.INSTANCE.NETDEV_RealPlay(loginID, pstPreviewInfo, (long lpPlayHandleOut, Pointer pucBuffer, int dwBufSize, int dwMediaDataType, long lpUserParam) -> {
                    //currentBytes = pucBuffer.getByteArray(0, dwBufSize);
                    //this.timeStatus = Calendar.getInstance().getTimeInMillis();
                }, lpUserData);

                logger.info("【{}】事件强制录屏loginID：{}，事件强制录屏ID：{}，状态：{}", cameraId, loginID, lpPlayHandle, status);
                Thread.sleep(600);

                if(lpPlayHandle == 0){
                    logger.info("【{}】事件强制录屏失败，loginID：{}，事件强制录屏ID：{}，状态：{}", cameraId, loginID, lpPlayHandle, status);
                    return false;
                }

                int errorCount = 0;
                int height = 0;
                int width = 0;
                ArrayBlockingQueue<byte[]> eventCaches = new ArrayBlockingQueue<>(cacheSize*2);
                while(CamStatus.NORMAL == status){

                    String fileName = SNAP_PIC_DIR + Calendar.getInstance().getTimeInMillis();

                    String formatName = "jpg";
                    String extendName = ".jpg";

                    //截图
                    boolean capPicBool = NetDEVSDK.INSTANCE.NETDEV_CapturePicture(lpPlayHandle, fileName, 1);


                    if(!capPicBool){
                        int errorCapture = NetDEVSDK.INSTANCE.NETDEV_GetLastError();
                        //logger.error("【{}】截图失败错误码：{}", cameraId, errorCapture);
                        System.out.printf("【%s】事件强制录屏截图失败错误码：%s\n", cameraId, errorCapture);
                        if(errorCount++ >= 100){
                            break;
                        }
                        Thread.sleep(50);
                    } else {
                        //经过测试播放5秒之后截图成功
                        //System.out.printf("【%s】截图成功\n", cameraId);

                        //截图成功
                        ByteOutputStream byteOutputStream = null;
                        try{
                            File snapPicFile = new File(fileName + extendName);
                            if(snapPicFile.exists()){
                                BufferedImage bufferedImage = ImageIO.read(snapPicFile);
                                height = bufferedImage.getHeight();
                                width = bufferedImage.getWidth();
                                byteOutputStream = new ByteOutputStream();
                                ImageIO.write(bufferedImage, formatName, byteOutputStream);
                                byte[] bytes = byteOutputStream.getBytes();

                                //删除临时文件
                                snapPicFile.delete();

                                eventCaches.offer(bytes);
                                if(eventCaches.size() >= cacheSize*2){
                                    break;
                                }

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            break;
                        } finally {
                            if(byteOutputStream != null){
                                byteOutputStream.close();
                            }
                        }

                    }
                }
                boolean bool = NetDEVSDK.INSTANCE.NETDEV_StopRealPlay(lpPlayHandle);
                logger.info("【{}】事件强制录屏停止播放：{}", cameraId, bool);
                ////////////////////////////////////////////////////////////////
                //开始事件数据存储
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
                //String eventVideo = "eventVideo";
                File eventVideoFile = new File(this.EVENT_STORAGE_DIR + eventVideo);
                if(!eventVideoFile.exists()){
                    if(!eventVideoFile.mkdir()){
                        logger.error("【{}】强制录屏创建视频文件夹异常1", this.cameraId);
                        return false;
                    }
                }
                //String eventPicture = "eventPicture";
                File eventPictureFile = new File(this.EVENT_STORAGE_DIR + eventPicture);
                if(!eventPictureFile.exists()){
                    if(!eventPictureFile.mkdir()){
                        logger.error("【{}】强制录屏创建图片文件夹异常1", this.cameraId);
                        return false;
                    }
                }
                String dateStr___ = sdf.format(new Date());
                String videoDir = this.EVENT_STORAGE_DIR + eventVideo + File.separator + dateStr___ + " $[" + zcLdEventManage.getEventType() + "]$.mp4";
                String pictureDir = this.EVENT_STORAGE_DIR + eventPicture + File.separator;
                FFmpegFrameRecorder recorder = null;
                try {
                    av_log_set_level(avutil.AV_LOG_ERROR);
                    recorder = new FFmpegFrameRecorder(videoDir, width, height);
                    recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264); // 27
                    recorder.setFormat("mp4");
                    recorder.setFrameRate(25);
                    recorder.setPixelFormat(0); // yuv420p
                    recorder.start();
                    Java2DFrameConverter converter = new Java2DFrameConverter();
                    ByteArrayInputStream bais;
                    // 循环所有图片
                    while(true) {
                        byte[] bytesQ = eventCaches.poll();
                        if (bytesQ == null) {
                            break;
                        }

                        bais = new ByteArrayInputStream(bytesQ);
                        BufferedImage read = ImageIO.read(bais);
                        recorder.record(converter.getFrame(read));

                        //保存图片【默认不保存】
                        if(isSavePic){
                            FileOutputStream fileOutputStream = null;
                            BufferedOutputStream bos = null;
                            try{

                                File picDir = new File(pictureDir + dateStr___ + File.separator);
                                if(!picDir.exists()){
                                    if(!picDir.mkdir()){
                                        logger.error("【{}】强制录屏创建图片文件夹异常inner", this.cameraId);
                                        return false;
                                    }
                                }

                                File outFile = new File(pictureDir + dateStr___ + File.separator + Calendar.getInstance().getTimeInMillis() + ".jpg");
                                fileOutputStream = new FileOutputStream(outFile);
                                bos = new BufferedOutputStream(fileOutputStream);
                                bos.write(bytesQ);
                                bos.flush();

                                bos.close();
                                fileOutputStream.close();

                                bais.close();
                            } catch (IOException io) {
                                io.printStackTrace();
                            } finally {
                                if(bos != null){
                                    bos.close();
                                }
                                if(fileOutputStream != null){
                                    fileOutputStream.close();
                                }
                                if(bais != null){
                                    bais.close();
                                }

                            }
                        }


                    }
                    logger.info("【{}】事件强制录屏正常结束：{}", cameraId, videoDir);

                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error("【{}】强制录屏创建视频异常2", this.cameraId);
                    return false;
                } finally {
                    if(recorder != null){
                        try {
                            recorder.stop();
                            recorder.release();
                        } catch (FrameRecorder.Exception e) {
                            e.printStackTrace();
                        }
                    }

                }

                //事件推送
                //tryEventSend(zcLdEventManage, videoDir);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(this.lock.isHeldByCurrentThread()){
                    this.lock.unlock();
                }
            }
            //结束
            ////////////////////////////////////////////////////////////////
            return true;
        }
        //logger.error("【{}】尝试摄像机资源受限", this.cameraId);
        return false;
    }

    private volatile String currentBase64 = null;
    private volatile long longLoginIdExt = 0;
    private volatile boolean liveVideoExt = false;
    private volatile long longLpPlayHandleExt = 0;
    private Thread threadExt;
    private ReentrantLock lockVideoExt = new ReentrantLock();


    //登录
    public void loginExt(){
        tagNETDEVDeviceInfoExt = new NetDEVSDKExt.TagNETDEVDeviceInfo();
        longLoginIdExt = NetDEVSDKExt.INSTANCE.NETDEV_Login(this.ip, this.port, this.username, this.password, tagNETDEVDeviceInfoExt);
        if(longLoginIdExt == 0){
            int loginCode = NetDEVSDKExt.INSTANCE.NETDEV_GetLastError();
            logger.error("【{}】Ext登录失败错误码：{}", cameraId, loginCode);
        } else {
            logger.info("【{}】Ext登录：成功，用户ID：{}，通道号：{}", cameraId, longLoginIdExt, tagNETDEVDeviceInfoExt.dwChannelNum);
        }
    }

    /**
     * 开始播放-扩展
     */
    public boolean createVideoPic(){
        logger.info("【{}】Ext实时播放启动", cameraId);
        threadExt = new Thread(new Runnable() {
            @Override
            public void run() {
                if(lockVideoExt.tryLock()) {

                    try{
                        liveVideoExt = true;
                        NetDEVSDKExt.TagNETDEVDeviceInfo deviceInfo = new NetDEVSDKExt.TagNETDEVDeviceInfo();
                        longLoginIdExt = NetDEVSDKExt.INSTANCE.NETDEV_Login(ip, port, username, password, deviceInfo);

                        if(longLoginIdExt == 0){

                            int loginCode = NetDEVSDKExt.INSTANCE.NETDEV_GetLastError();
                            logger.error("【{}】Ext实时播放登录失败错误码：{}", cameraId, loginCode);

                            currentBase64 = null;
                            longLoginIdExt = 0;
                            liveVideoExt = false;

                            return;
                        } else {
                            logger.info("【{}】Ext实时播放登录：成功，用户ID：{}，通道号：{}", cameraId, longLoginIdExt, deviceInfo.dwChannelNum);

                            NetDEVSDKExt.NETDEV_PREVIEWINFO_S pst = NetDEVSDKExt.getNewNETDEV_PREVIEWINFO_S_instance(deviceInfo.dwChannelNum);

                            long lpUserData = 0;
                            longLpPlayHandleExt = NetDEVSDKExt.INSTANCE.NETDEV_RealPlay(longLoginIdExt, pst, (long lpPlayHandleOut, Pointer pucBuffer, int dwBufSize, int dwMediaDataType, long lpUserParam) -> {
                                //currentBytes = pucBuffer.getByteArray(0, dwBufSize);
                                //this.timeStatus = Calendar.getInstance().getTimeInMillis();
                            }, lpUserData);

                            Thread.sleep(3000);

                            logger.info("【{}】Ext实时播放 ，用户ID：{}，通道号：{}，播放ID：{}", cameraId, longLoginIdExt, deviceInfo.dwChannelNum, longLpPlayHandleExt);

                            long play = 0;
                            //实时显示
                            while(liveVideoExt){

                                if(longLpPlayHandleExt != 0){

                                    String fileName = SNAP_PIC_DIR + "temp$video";

                                    String formatName = "jpg";
                                    String extendName = ".jpg";

                                    //截图
                                    boolean capPicBool = NetDEVSDKExt.INSTANCE.NETDEV_CapturePicture(longLpPlayHandleExt, fileName, 1);

                                    if(!capPicBool){
                                        liveVideoExt = false;
                                        int errorCapture = NetDEVSDKExt.INSTANCE.NETDEV_GetLastError();
                                        System.out.printf("【%s】Ext截图失败错误码：%s\n", cameraId, errorCapture);
                                    } else {

                                        play++;

                                        if(play == 1){
                                            logger.info("【{}】Ext实时播放中", cameraId);
                                        }

                                        //截图成功
                                        ByteOutputStream byteOutputStream = null;
                                        try{
                                            File snapPicFile = new File(fileName + extendName);
                                            if(snapPicFile.exists()){
                                                BufferedImage bufferedImage = ImageIO.read(snapPicFile);
                                                byteOutputStream = new ByteOutputStream();
                                                ImageIO.write(bufferedImage, formatName, byteOutputStream);
                                                byte[] bytes = byteOutputStream.getBytes();

                                                BASE64Encoder encoder = new BASE64Encoder();
                                                currentBase64 = encoder.encode(bytes).trim().replaceAll("\n", "").replaceAll("\r", "");

                                                //删除临时文件
                                                snapPicFile.delete();

                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        } finally {
                                            if(byteOutputStream != null){
                                                byteOutputStream.close();
                                            }
                                        }

                                    }
                                    //break;
                                } else {
                                    liveVideoExt = false;
                                }
                                //Thread.sleep(20);
                            }


                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {

                        if(longLpPlayHandleExt != 0){
                            boolean boolStopRealPlay = NetDEVSDKExt.INSTANCE.NETDEV_StopRealPlay(longLpPlayHandleExt);
                            if(boolStopRealPlay){
                                logger.info("【{}】Ext停止播放成功", cameraId);
                            } else {
                                int errorCapture = NetDEVSDKExt.INSTANCE.NETDEV_GetLastError();
                                logger.info("【{}】Ext停止播放失败，错误码：{}", cameraId, errorCapture);
                            }
                        }

                        logger.info("【{}】Ext实时播放停止1", cameraId);
                        if(lockVideoExt.isHeldByCurrentThread()){
                            logger.info("【{}】Ext实时播放停止1=2", cameraId);
                            lockVideoExt.unlock();
                        }
                        logger.info("【{}】Ext实时播放停止2", cameraId);

                    }
                }

            }
        });
        threadExt.start();

        int await = 0;
        while(++await <= 10){
            if(StringUtils.isNotEmpty(this.currentBase64)){
                return true;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return false;

    }

    /**
     * 取消播放
     */
    public void cancelVideoPic() {
        logger.info("【{}】Ext停止播放操作", cameraId);
        this.liveVideoExt = false;
        this.currentBase64 = null;
        this.longLpPlayHandleExt = 0;
        this.longLoginIdExt = 0;
    }

}
