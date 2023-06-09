package com.weiyan.atp.service;

import com.weiyan.atp.constant.OrgApplyTypeEnum;
import com.weiyan.atp.data.bean.ChaincodeResponse;
import com.weiyan.atp.data.bean.PlatOrg;
import com.weiyan.atp.data.bean.PlatOrgApply;
import com.weiyan.atp.data.request.web.*;

import javax.validation.constraints.NotEmpty;
import java.io.IOException;

public interface OrgRepositoryService {
    /**
     * 申请创建组织
     */
    ChaincodeResponse applyCreateOrg(CreateOrgRequest request);

    ChaincodeResponse applyCreateOrg2(CreateOrgRequest request);

    ChaincodeResponse applyThresholdOrg(String orgName ,String uid);

    /**
     * 申请声明属性
     */
    ChaincodeResponse applyDeclareOrgAttr(DeclareOrgAttrRequest request);
    ChaincodeResponse applyDeclareOrgAttr2(DeclareOrgAttrRequest request);

    ChaincodeResponse applyThresholdFile(ThresholdApplyRequest request);
    /**
     * 同意加入组织/声明属性
     */
    void approveOrgApply(OrgApplyTypeEnum type, ApproveOrgApplyRequest request);
    void approveOrgApply2(OrgApplyTypeEnum type, ApproveOrgApplyRequest request);

    void approveThresholdApply(String orgName , String uid );


    /**
     * 提交自己的part pk
     */
    void submitPartPk(OrgApplyTypeEnum type, String orgName, String fileName, String attrName);
    void submitPartPk2(OrgApplyTypeEnum type, String orgName, String fileName, String attrName);
    void submitThresholdPartPK(String orgName, String fileName,String uid ,String fromUid);
    /**
     * 整合part pk
     */
    void mixPartPk(OrgApplyTypeEnum type, String orgName, String attrName, String fileName);
    void mixPartPk2(OrgApplyTypeEnum type, String orgName, String attrName, String fileName);
    //合公钥生成门限
    void mixThresholdPartSk(String orgName ,String uid);

    //合私钥解密
    void Thresholdmixdownload(String orgName ,String uid ,String fileName) throws IOException;
    PlatOrg queryOrg(@NotEmpty String orgName);

    PlatOrgApply queryOrgApply(@NotEmpty String orgName, OrgApplyTypeEnum type, String attrName);

    PlatOrgApply queryThresholdFileApply(String orgName ,String fileName , String fromUid);


}