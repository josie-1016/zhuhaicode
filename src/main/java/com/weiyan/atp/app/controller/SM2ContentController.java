package com.weiyan.atp.app.controller;

import com.weiyan.atp.constant.BaseException;
import com.weiyan.atp.data.bean.ChaincodeResponse;
import com.weiyan.atp.data.bean.DABEUser;
import com.weiyan.atp.data.bean.Result;
import com.weiyan.atp.data.request.web.DecryptContentRequest;
import com.weiyan.atp.data.request.web.RevokeUserAttrRequest;
import com.weiyan.atp.data.request.web.ShareContentRequest;
import com.weiyan.atp.data.response.intergration.EncryptionResponse;
import com.weiyan.atp.data.response.web.PlatContentsResponse;
import com.weiyan.atp.service.AttrService;
import com.weiyan.atp.service.ContentService;
import com.weiyan.atp.service.DABEService;
import com.weiyan.atp.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;

@RestController
@RequestMapping("/SM2content")
@Slf4j
@CrossOrigin //支持跨域访问
public class SM2ContentController {
    private final ContentService contentService;
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


    public SM2ContentController(ContentService contentService, AttrService attrService, DABEService dabeService) {
        this.contentService = contentService;
        this.attrService = attrService;
        this.dabeService = dabeService;
    }

    @PostMapping("/decryption")
    public Result<String> decryptContent(@RequestBody @Validated DecryptContentRequest request,HttpServletRequest req) {
        System.out.println("/SM2content/decryption 解密定向传输文件");
        return Result.okWithData(null);
//        String ipAddress = SecurityUtils.getIpAddr(req);
//        System.out.println("DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD");
//        System.out.println(request.toString());
//        request.setIp(ipAddress);
//        ChaincodeResponse response = null;
//        if(!request.getUserName().equals(request.getSharedUser())){
//
//              String filePath = encryptDataPath +request.getSharedUser()+"/"+ request.getFileName();
// //           String filePath = "atp\\data\\enc\\深圳市气象局\\深圳市\\ 深圳市福田区\\ 气象\\ 福田区-气象数据.xlsx";
//            System.out.println("ppppppppppppppppppp");
//            System.out.println(filePath);
//            try {
//                String cipher= FileUtils.readFileToString(new File(filePath), StandardCharsets.UTF_8);
//                System.out.println("ccccccccccccccccccccccccccccc");
//                System.out.println(cipher);
//                request.setCipher(cipher);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            response = contentService.decryptContent2(request.getCipher(), request.getUserName(), request.getFileName(), request.getSharedUser());
//            System.out.println(response.getMessage());
//        }else{
//            response = new ChaincodeResponse();
//            response.setStatus(ChaincodeResponse.Status.SUCCESS);
//        }
//
//        if(response.isFailed()){
//            throw new BaseException("decryption error: " + response.getMessage());
//        }
//        return Result.okWithData(null);
    }


    @GetMapping("/list")
    public Result<PlatContentsResponse> queryContents(String fromUserName, String tag,
                                                      int pageSize, String bookmark) {
        System.out.println("/SM2content/list 查询定向传输文件");
        return Result.success();
//       // fromUserName = "深圳市";
//        PlatContentsResponse res = contentService.queryPlatContents(fromUserName, tag, pageSize, bookmark);
//        return Result.okWithData(res);
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


        return Result.success();
//
////

//
//        EncryptionResponse encryptionResponse = contentService.encContent2(request);
//
//        FileUtils.write(new File(encryptDataPath +request.getFileName()+"/"+ filename), encryptionResponse.getCipher(),
//                StandardCharsets.UTF_8);

    }

    //下载解密后的原文
    @GetMapping("/download")
    public  void download(String fileName, String sharedUser,HttpServletRequest request, HttpServletResponse response) throws Exception {
//        //获取文件的绝对路径
//        File dest = new File(new File(shareDataPath).getAbsolutePath()+ "/" + sharedUser+"/"+fileName);
//        //获取输入流对象（用于读文件）
//        FileInputStream fis = new FileInputStream(dest);
////        //获取文件后缀（.txt）
////        String extendFileName = fileName.substring(fileName.lastIndexOf('.'));
//        //动态设置响应类型，根据前台传递文件类型设置响应类型
//        //response.setContentType(request.getSession().getServletContext().getMimeType(extendFileName));
//        //response.setContentType("content-type:octet-stream");
//        response.setContentType("application/force-download");
//        //设置响应头,attachment表示以附件的形式下载，inline表示在线打开
//        response.setHeader("content-disposition","attachment;fileName="+ URLEncoder.encode(fileName,"UTF-8"));
//        //获取输出流对象（用于写文件）
//        ServletOutputStream os = response.getOutputStream();
//        //下载文件,使用spring框架中的FileCopyUtils工具
//        FileCopyUtils.copy(fis,os);
//        //FileUtils.copyFile(dest,os);
//        return Result.success();
    }

    //下载密文
    @GetMapping("/cipher")
    public  void cipher(String userName,String fileName, String sharedUser,HttpServletRequest request, HttpServletResponse response) throws Exception {
//        ChaincodeResponse resp = contentService.getCipher(userName, fileName, sharedUser);
//        if(resp.isFailed()){
//            throw new BaseException("download cipher error: " + resp.getMessage());
//        }
//        //保存密文
//        FileUtils.write(new File(cipherDataPath +sharedUser+"/"+ fileName), resp.getMessage(),
//                StandardCharsets.UTF_8);
//
//        //获取文件的绝对路径
//        File dest = new File(new File(cipherDataPath).getAbsolutePath()+ "/" + sharedUser+"/"+fileName);
//        //获取输入流对象（用于读文件）
//        FileInputStream fis = new FileInputStream(dest);
//        //获取文件后缀（.txt）
//        //String extendFileName = fileName.substring(fileName.lastIndexOf('.'));
//        //动态设置响应类型，根据前台传递文件类型设置响应类型
//        //response.setContentType(request.getSession().getServletContext().getMimeType(extendFileName));
//        //response.setContentType("content-type:octet-stream");
//        response.setContentType("application/force-download");
//        //设置响应头,attachment表示以附件的形式下载，inline表示在线打开
//        response.setHeader("content-disposition","attachment;fileName="+ URLEncoder.encode(fileName,"UTF-8"));
//        //获取输出流对象（用于写文件）
//        ServletOutputStream os = response.getOutputStream();
//        //下载文件,使用spring框架中的FileCopyUtils工具
//        FileCopyUtils.copy(fis,os);
//        //FileUtils.copyFile(dest,os);
//        //return Result.success();
    }

}
