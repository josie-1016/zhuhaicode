package com.weiyan.atp.app.controller;

import com.weiyan.atp.constant.OrgApplyTypeEnum;
import com.weiyan.atp.data.bean.PlatOrgApply;
import com.weiyan.atp.data.bean.Result;
import com.weiyan.atp.data.request.web.*;
import com.weiyan.atp.service.OrgRepositoryService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author : 魏延thor
 * @since : 2020/6/16
 */
@RequestMapping("/org")
@RestController
@Slf4j
@CrossOrigin //支持跨域访问
public class OrgController {
    private final OrgRepositoryService orgRepositoryService;

    public OrgController(OrgRepositoryService orgRepositoryService) {
        this.orgRepositoryService = orgRepositoryService;
    }

    /**
     * 申请创建组织
     */
    @PostMapping("/apply/creation")
    public Result<Object> applyCreateOrg(@RequestBody @Validated CreateOrgRequest request) {
        return orgRepositoryService.applyCreateOrg2(request).getResult(str -> str);
    }

    /**
     * 审批/同意加入组织
     * 自动提交分享给其他人的秘密
     */
    @PostMapping("/apply/creation/approval")
    public Result<Object> approveCreateOrgApply(@RequestBody @Validated ApproveOrgApplyRequest request) {
        orgRepositoryService.approveOrgApply2(OrgApplyTypeEnum.CREATION, request);
        return Result.success();
    }


    /**
     * 申请声明属性
     */
    @PostMapping("/apply/attribute")
    public Result<Object> applyDeclareAttr(@RequestBody @Validated DeclareOrgAttrRequest request) {
        return orgRepositoryService.applyDeclareOrgAttr2(request).getResult(str -> str);
    }

    //门限申请
    @PostMapping("/apply/threshold")
    public Result<Object> applyThreshold(@RequestBody ThresholdApplyRequest request) {
        return orgRepositoryService.applyThresholdFile(request).getResult(str -> str);
    }

    /**
     * 审批/同意声明属性
     * 自动提交分享给其他人的秘密
     */
    @PostMapping("/apply/attribute/approval")
    public Result<Object> approveDeclareAttrApply(@RequestBody @Validated ApproveOrgApplyRequest request) {
        orgRepositoryService.approveOrgApply2(OrgApplyTypeEnum.ATTRIBUTE, request);
        return Result.success();
    }
//    同意申请下载文件
    @PostMapping("apply/ThresholdFile/approval")
    public Result<Object> approvalThresholdFileApply(String orgName , String fileName ,String uid ,String fromUid){
        //调用门限申请
        orgRepositoryService.submitThresholdPartPK(orgName,fileName,uid,fromUid);
        return Result.success();
    }

    /**
     * 检查是否能生成part-pk，即其他成员是不是都给自己分享了秘密
     * 创建组织/声明属性
     */
    @GetMapping("/part-pk")
    public Result<Object> canGeneratePartPk() {
        return Result.success();
    }

    /**
     * 提交自己生成的part pk
     * 创建组织/声明属性
     */
    @PostMapping("/part-pk")
    public Result<Object> submitPartPk(String type, String orgName, String fileName, String attrName) {
        orgRepositoryService.submitPartPk2(OrgApplyTypeEnum.valueOf(type), orgName, fileName, attrName);
        return Result.success();
    }
    //同意门限PublicK生成
    @PostMapping("/threshold/approve-part-sk")
    public Result<Object> submitThresholdPartSk(String orgName , String userName){
        orgRepositoryService.approveThresholdApply(orgName,userName);
        return Result.success();
    }
    //申请门限publicK生成
    @PostMapping("/threshold/apply-part-sk")
    public Result<Object> applyThresholdPartSk(String orgName ,String userName){
        orgRepositoryService.applyThresholdOrg(orgName,userName);
        return Result.success();
    }

    /**
     * 让合约整合part pk生成组织公钥或属性公钥
     * 创建组织/声明属性
     */
    @PostMapping("/complete-pk")
    public Result<Object> mixPartPk(String type, String orgName, String attrName, String fileName) {
        orgRepositoryService.mixPartPk2(OrgApplyTypeEnum.valueOf(type), orgName, attrName, fileName);
        return Result.success();
    }
    @PostMapping("/Threshold/complete-pk")
    public  Result<Object> mixThresholdPartPk(String orgName,String userName){
        orgRepositoryService.mixThresholdPartSk(orgName,userName);
        return Result.success();
    }

    /**
     * 查询组织
     */
    @GetMapping("/")
    public Result<Object> queryOrg(String orgName) {
        return Result.okWithData(orgRepositoryService.queryOrg(orgName));
    }

    /**
     * 查询组织申请或组织属性申请
     */
    @GetMapping("/apply")
    public Result<PlatOrgApply> queryOrgApply(String orgName, String type, String attrName) {
        return Result.okWithData(
            orgRepositoryService.queryOrgApply(orgName, OrgApplyTypeEnum.valueOf(type), attrName));
    }
}
