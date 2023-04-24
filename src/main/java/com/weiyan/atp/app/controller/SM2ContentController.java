package com.weiyan.atp.app.controller;

import com.weiyan.atp.data.bean.Result;
import com.weiyan.atp.data.response.web.PlatSM2ContentsResponse;
import com.weiyan.atp.service.AttrService;
import com.weiyan.atp.service.SM2ContentService;
import com.weiyan.atp.service.DABEService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.FileCopyUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;
import javax.servlet.ServletOutputStream;


@RestController
@RequestMapping("/SM2content")
@Slf4j
@CrossOrigin //支持跨域访问
public class SM2ContentController {
    private final SM2ContentService sm2contentService;
    private final AttrService attrService;
    private final DABEService dabeService;


    @Value("${atp.path.shareSM2Data}")
    private String shareSM2DataPath;

    @Value("${atp.path.encryptSM2Data}")
    private String encryptSM2DataPath;

    @Value("${atp.path.cipherSM2Data}")
    private String cipherSM2DataPath;

    @Value("${atp.path.dabeUser}")
    private String userPath;


    public SM2ContentController(SM2ContentService sm2contentService, AttrService attrService, DABEService dabeService) {
        this.sm2contentService = sm2contentService;
        this.attrService = attrService;
        this.dabeService = dabeService;
    }

    //下载解密后的原文
    @GetMapping("/download")
    public void decryptContent(String userName,String cipher,String sharedUser,String fileName,String toName,HttpServletRequest request, HttpServletResponse response) throws Exception {
        System.out.println("/SM2content/decryption 解密定向传输文件");
        File dest=null;
        if(userName.equals(sharedUser)){
            // 传输原始文件
            dest = new File(new File(shareSM2DataPath).getAbsolutePath()+ "/" + sharedUser+"/"+fileName);
        }else if(userName.equals(toName)){
            dest = new File(new File(shareSM2DataPath).getAbsolutePath()+ "/" + sharedUser+"/"+fileName);
        }else{
            dest = new File(new File(encryptSM2DataPath).getAbsolutePath()+ "/" + sharedUser+"/"+fileName);
        }
        System.out.println(dest);
        FileInputStream fis = new FileInputStream(dest);
        response.setContentType("application/force-download");
        response.setHeader("content-disposition","attachment;fileName="+ URLEncoder.encode(fileName,"UTF-8"));
        ServletOutputStream os = response.getOutputStream();
        FileCopyUtils.copy(fis,os);
    }


    @GetMapping("/list")
    public Result<PlatSM2ContentsResponse> queryContents(String fromUserName, String toName,
                                                      int pageSize, String bookmark) {
        System.out.println("/SM2content/list 查询定向传输文件");
        PlatSM2ContentsResponse res=sm2contentService.queryPlatContents(fromUserName,toName,pageSize,bookmark);
        return Result.okWithData(res);
    }

    @PostMapping("/upload")
    public Result<Object> upload(String userName,MultipartFile file,String toName) throws IOException {
        System.out.println("/SM2content/upload 加密定向传输文件");

        if(file.isEmpty()){
            return Result.internalError("file is empty");
        }
        //获取文件的原始名
        String filename = file.getOriginalFilename();
        System.out.println(filename);

        //存储原始文件
        //根据相对路径获取绝对路径
        File dest = new File(new File(shareSM2DataPath).getAbsolutePath()+ "/" + userName+"/"+filename);
        System.out.println(dest.getPath());
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdir();
        }
        FileUtils.copyInputStreamToFile(file.getInputStream(), dest);

        //原始文件转换为String数据进行加密
        String data = FileUtils.readFileToString(
                dest,
                StandardCharsets.UTF_8);
        // 数据加密并上链
        String cipher=sm2contentService.encContent(data,filename,userName,toName);
        FileUtils.write(new File(encryptSM2DataPath +userName+"/"+ filename), cipher,
               StandardCharsets.UTF_8);

        return Result.success();
    }

    //下载密文
    @GetMapping("/cipher")
    public void cipher(String userName,String fileName, String sharedUser,HttpServletRequest request, HttpServletResponse response) throws Exception {
        //获取文件的绝对路径
        File dest = new File(new File(encryptSM2DataPath).getAbsolutePath()+ "/" + sharedUser+"/"+fileName);
        //获取输入流对象（用于读文件）
        FileInputStream fis = new FileInputStream(dest);
        response.setContentType("application/force-download");
        //设置响应头,attachment表示以附件的形式下载，inline表示在线打开
        response.setHeader("content-disposition","attachment;fileName="+ URLEncoder.encode(fileName,"UTF-8"));
        //获取输出流对象（用于写文件）
        ServletOutputStream os = response.getOutputStream();
        //下载文件,使用spring框架中的FileCopyUtils工具
        FileCopyUtils.copy(fis,os);
    }

}
