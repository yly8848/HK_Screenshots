package com.yly.hk.service;


import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import org.springframework.stereotype.Service;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yly
 * @date 2020/9/2
 */
@Service
public class ImageService {

    private final static HCNetSDK hCNetSDK = HCNetSDK.INSTANCE;

    public void getImage(ConnectionInfo connectionInfo) {

        boolean initSuc = hCNetSDK.NET_DVR_Init();
        if (!initSuc) {
            int i = hCNetSDK.NET_DVR_GetLastError();
            System.out.println("初始化失败：" + i);
        }
        System.out.println("初始化成功！");

        //用户句柄
        NativeLong lUserID;
        //用户参数
        HCNetSDK.NET_DVR_CLIENTINFO mStrClientInfo;
        //设备信息
        HCNetSDK.NET_DVR_DEVICEINFO_V30 mStrDeviceInfo;

        mStrDeviceInfo = new HCNetSDK.NET_DVR_DEVICEINFO_V30();
        lUserID = hCNetSDK.NET_DVR_Login_V30(connectionInfo.getIp(),
                (short) connectionInfo.getPort(), connectionInfo.getUserName(),
                connectionInfo.getPassword(), mStrDeviceInfo);


        long userID = lUserID.longValue();
        if (userID == -1) {
            int i = hCNetSDK.NET_DVR_GetLastError();
            System.out.println("登陆失败！错误码：" + i);
            return;
        }

        //获取通道号
        int iChannelNum = getChannel(lUserID, hCNetSDK, mStrDeviceInfo);

        mStrClientInfo = new HCNetSDK.NET_DVR_CLIENTINFO();
        mStrClientInfo.lChannel = new NativeLong(iChannelNum);

        IntByReference a = new IntByReference();
        HCNetSDK.NET_DVR_JPEGPARA jpeg = new HCNetSDK.NET_DVR_JPEGPARA();
        // 设置图片的分辨率: 抓图
        jpeg.wPicSize = 2;
        // 设置图片质量： 一般
        jpeg.wPicQuality = 2;
        // 这里的尺寸可能无效
        byte[] bytes = new byte[1024 * 1024];
        boolean b = hCNetSDK.NET_DVR_CaptureJPEGPicture_NEW(lUserID, mStrClientInfo.lChannel, jpeg,
                bytes, 1024 * 1024, a);


        if (!b) {
            System.out.println("截图失败：" + hCNetSDK.NET_DVR_GetLastError());
        } else {
            System.out.println("截图成功");
        }

        // 存储本地，写入到文件
        try(BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(connectionInfo.getPath()))) {
            outputStream.write(bytes, 0, a.getValue());
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 注销
        hCNetSDK.NET_DVR_Logout_V30(lUserID);
        hCNetSDK.NET_DVR_Cleanup();
    }


    public int getChannel(NativeLong lUserID, HCNetSDK hCNetSDK, HCNetSDK.NET_DVR_DEVICEINFO_V30 mStrDeviceInfo) {
        IntByReference ibrBytesReturned = new IntByReference(0);
        //IP参数
        HCNetSDK.NET_DVR_IPPARACFG mStrIpparaCfg;
        mStrIpparaCfg = new HCNetSDK.NET_DVR_IPPARACFG();
        mStrIpparaCfg.write();
        Pointer lpIpParaConfig = mStrIpparaCfg.getPointer();
        boolean bRet = hCNetSDK.NET_DVR_GetDVRConfig(lUserID, HCNetSDK.NET_DVR_GET_IPPARACFG,
                new NativeLong(0), lpIpParaConfig, mStrIpparaCfg.size(), ibrBytesReturned);
        mStrIpparaCfg.read();

        List<Integer> channelList = new ArrayList<>();

        if (bRet) {
            for (int i = 0; i < mStrDeviceInfo.byChanNum; i++) {
                if (mStrIpparaCfg.byAnalogChanEnable[i] == 1) {
                    System.out.println("Camera通道：" + (i + mStrDeviceInfo.byStartChan));
                    channelList.add(i + mStrDeviceInfo.byStartChan);
                }
            }
            for (int i = 0; i < HCNetSDK.MAX_IP_CHANNEL; i++) {
                if (mStrIpparaCfg.struIPChanInfo[i].byEnable == 1) {
                    System.out.println("IP通道：" + (i + mStrDeviceInfo.byStartChan));
                    // 子字符串中获取通道号,IP通道号要加32
                    channelList.add(i + mStrDeviceInfo.byStartChan + 24);
                }
            }
        } else {
            // 没有 IP 通道
            for (int i = 0; i < mStrDeviceInfo.byChanNum; i++) {
                System.out.println("Camera通道：" + (i + mStrDeviceInfo.byStartChan));
                channelList.add(i + mStrDeviceInfo.byStartChan);
            }
        }
        return channelList.get(0);
    }


}
