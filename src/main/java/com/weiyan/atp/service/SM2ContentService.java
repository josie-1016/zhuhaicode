package com.weiyan.atp.service;

import com.weiyan.atp.data.bean.ChaincodeResponse;
import com.weiyan.atp.data.request.web.ShareContentRequest;
import com.weiyan.atp.data.response.web.PlatSM2ContentsResponse;

import javax.validation.constraints.NotEmpty;
import java.io.IOException;

public interface SM2ContentService {

    PlatSM2ContentsResponse queryPlatContents(String fromUserName, String toName,
                                              int pageSize, String bookmark);

    String encContent(String data,String filename,String userName,String toName) throws IOException;
}
