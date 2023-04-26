package com.weiyan.atp.app.controller;

import com.weiyan.atp.data.bean.BulletProof;
import com.weiyan.atp.data.bean.ChaincodeResponse;
import com.weiyan.atp.data.bean.Result;
import com.weiyan.atp.data.request.web.CreateBatchBulletProofRequest;
import com.weiyan.atp.data.request.web.CreateBulletProofRequest;
import com.weiyan.atp.data.request.web.CreateCommitRequest;
import com.weiyan.atp.data.request.web.VerifyBulletProofRequest;
import com.weiyan.atp.data.response.web.BulletProofResponse;
import com.weiyan.atp.service.BulletProofService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author : taojingyi
 * @since : 2023/3/10
 */
@RequestMapping("/zk")
@RestController
@Slf4j
@CrossOrigin //支持跨域访问
public class BulletProofController {
    private final BulletProofService bulletProofService;
    public BulletProofController(BulletProofService bulletProofService) {
        this.bulletProofService = bulletProofService;
    }

//    @PostMapping("/commit")
//    public Result<Object> commit(@RequestBody @Validated CreateBulletProofRequest request) {
//        int tagSize = request.getDataTags().size();
//        if (tagSize == 0 || tagSize > 10) {
//            return Result.internalError("tags length error");
//        }
//        ChaincodeResponse response = bulletProofService.createBulletProof(request);
////        BulletProof bulletProof = bpService.createBulletProof(request.getValue(), request.getRange());
////
////        ChaincodeResponse response2 = bulletProofService.createBulletProof(request.getUserName(), request.getUserType(), bulletProof);
//        return Result.okWithData(response.getResult(str->str));
//    }

    @PostMapping("/create")
    public Result<Object> createZK(@ModelAttribute @Validated CreateBulletProofRequest request) {
        if(request.getRange() == null){
            int tagSize = request.getTags().size();
            if (tagSize == 0 || tagSize > 10) {
                return Result.internalError("tags length error");
            }
        }
        ChaincodeResponse response = bulletProofService.createBulletProof(request);
//        BulletProof bulletProof = bpService.createBulletProof(request.getValue(), request.getRange());
//
//        ChaincodeResponse response2 = bulletProofService.createBulletProof(request.getUserName(), request.getUserType(), bulletProof);
        return Result.okWithData(response.getResult(str->str));
    }

    @PostMapping("/createBatch")
    public Result<Object> createZKBatch(@ModelAttribute @Validated CreateBatchBulletProofRequest request) {
        if(request.getRange() == null){
            int tagSize = request.getTags().size();
            if (tagSize == 0 || tagSize > 10) {
                return Result.internalError("tags length error");
            }
        }
        ChaincodeResponse response = bulletProofService.createBatchBulletProof(request);
//        BulletProof bulletProof = bpService.createBulletProof(request.getValue(), request.getRange());
//
//        ChaincodeResponse response2 = bulletProofService.createBulletProof(request.getUserName(), request.getUserType(), bulletProof);
        return Result.okWithData(response.getResult(str->str));
    }

    @GetMapping("/list")
    public Result<BulletProofResponse> queryZK(String userName, String pid, String tag, int pageSize, String bookmark) {
        //返回查询到的列表
        BulletProofResponse res = bulletProofService.queryBulletProof(userName, pid, tag, pageSize, bookmark);
        return Result.okWithData(res);
    }

    @PostMapping("/verify")
    public Result<Object> verifyZK(@ModelAttribute @Validated VerifyBulletProofRequest request) {
        ChaincodeResponse response = bulletProofService.verifyBulletProof(request);
//        BulletProof bulletProof = bpService.createBulletProof(request.getValue(), request.getRange());
//
//        ChaincodeResponse response2 = bulletProofService.createBulletProof(request.getUserName(), request.getUserType(), bulletProof);
        return Result.okWithData(response.getResult(str->str));
    }
}
