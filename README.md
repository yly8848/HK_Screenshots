# 海康威视 视频截图-windows

## tomcat部署，startup.bat 启动
 - 将 `hk_sdk` 文件夹复制到Tomcat中的`bin`文件夹中即可
 - 建议将打包好的war包直接丢在`webapps`文件夹中
 
## Tomcat 以服务的方式启动
 - 将 `hk_sdk` 文件夹复制到Tomcat中的 `根目录` (与`bin`文件夹同级)
 - 将`war`包放入`webapps`文件夹中
 
 ## 其他方式
 修改`HCNetSDK.java`中所有有关`hk_sdk\HCNetSDK`动态库的链接，可以改为绝对地址