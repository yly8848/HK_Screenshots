package com.yly.hk.web;

import com.yly.hk.service.ConnectionInfo;
import com.yly.hk.service.Connections;
import com.yly.hk.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author yly
 * @date 2020/9/2
 */
@RequestMapping("/img")
@RestController
public class ImageController {

    @Autowired
    private ImageService imageService;
    @Autowired
    private Connections connections;

    @Value("${file.time}")
    private int time;

    @RequestMapping("/get")
    public void get(int idx, HttpServletResponse response, HttpServletRequest request){
        ConnectionInfo connectionInfo = connections.getItem(idx);
        if (connectionInfo == null){
            System.out.println("获取配置信息失败！");
            return;
        }
        response.setContentType("image/jpeg");
        File file = new File(connectionInfo.getPath());

        try {
            // 文件不存在 或 文件超过指定时间，重新获取
            if (!file.exists() || System.currentTimeMillis() - file.lastModified() > time){
                imageService.getImage(connectionInfo);
            }
            if (!file.exists()){
                return;
            }
            ServletOutputStream outputStream = response.getOutputStream();
            BufferedImage read = ImageIO.read(file);
            ImageIO.write(read, "JPEG", outputStream);
            // 缩略图 可能会OOM，需要配置 -Dthumbnailator.conserveMemoryWorkaround=true
//            Thumbnails.of(file).scale(1f).outputQuality(1).toOutputStream(outputStream);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.format("  URI: %s  最大内存: %sm  已分配内存: %sm  已分配内存中的剩余空间: %sm  最大可用内存: %sm\n",
                request.getRequestURI(), Runtime.getRuntime().maxMemory()/1024/1024, Runtime.getRuntime().totalMemory()/1024/1024, Runtime.getRuntime().freeMemory()/1024/1024,
                (Runtime.getRuntime().maxMemory()-Runtime.getRuntime().totalMemory()+Runtime.getRuntime().freeMemory())/1024/1024);
    }

}
