package com.zrlog.plugin.cos.service;

import com.fzb.io.api.BucketManageAPI;
import com.fzb.io.yunstore.QCloudBucketManageImpl;
import com.fzb.io.yunstore.QCloudBucketVO;
import com.google.gson.Gson;
import com.zrlog.plugin.IOSession;
import com.zrlog.plugin.api.Capability;
import com.zrlog.plugin.api.IPluginService;
import com.zrlog.plugin.api.Service;
import com.zrlog.plugin.common.IdUtil;
import com.zrlog.plugin.common.response.UploadFileResponse;
import com.zrlog.plugin.common.response.UploadFileResponseEntry;
import com.zrlog.plugin.cos.entry.UploadFile;
import com.zrlog.plugin.data.codec.ContentType;
import com.zrlog.plugin.data.codec.MsgPacket;
import com.zrlog.plugin.data.codec.MsgPacketStatus;
import com.zrlog.plugin.message.CapabilityInvokeResult;
import com.zrlog.plugin.type.ActionType;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service("uploadService")
@Capability(
        key = "cos.upload",
        type = "service",
        label = "上传到腾讯云 COS",
        description = "上传文章附件和生成资源到腾讯云 COS。",
        exposure = {"internal"},
        timeoutSeconds = 120
)
public class UploadService implements IPluginService {

    private static final Logger LOGGER = Logger.getLogger(UploadService.class);

    @Override
    public void handle(final IOSession ioSession, final MsgPacket requestPacket) {
        Map<String, Object> rawRequest = new Gson().fromJson(requestPacket.getDataStr(), Map.class);
        Map<String, Object> request = requestPayload(requestPacket, rawRequest);
        List<String> fileInfoList = fileInfoList(request.get("fileInfo"));
        List<UploadFile> uploadFileList = new ArrayList<>();
        for (String fileInfo : fileInfoList) {
            UploadFile uploadFile = new UploadFile();
            uploadFile.setFile(new File(fileInfo.split(",")[0]));
            String fileKey = fileInfo.split(",")[1];
            if (fileKey.startsWith("/")) {
                uploadFile.setFileKey(fileKey.substring(1));
            } else {
                uploadFile.setFileKey(fileKey);
            }
            uploadFileList.add(uploadFile);
        }
        UploadFileResponse uploadFileResponse = upload(ioSession, uploadFileList);
        List<Map<String, Object>> responseList = new ArrayList<>();
        for (UploadFileResponseEntry entry : uploadFileResponse) {
            Map<String, Object> map = new HashMap<>();
            map.put("url", entry.getUrl());
            responseList.add(map);
        }
        if (Objects.equals(ActionType.CAPABILITY_INVOKE.name(), requestPacket.getMethodStr())) {
            CapabilityInvokeResult result = new CapabilityInvokeResult();
            result.setSuccess(true);
            Map<String, Object> data = new HashMap<>();
            data.put("items", responseList);
            result.setData(data);
            ioSession.sendJsonMsg(result, requestPacket.getMethodStr(), requestPacket.getMsgId(), MsgPacketStatus.RESPONSE_SUCCESS);
        } else {
            ioSession.sendMsg(ContentType.JSON, responseList, requestPacket.getMethodStr(), requestPacket.getMsgId(), MsgPacketStatus.RESPONSE_SUCCESS);
        }
    }

    private Map<String, Object> requestPayload(MsgPacket requestPacket, Map<String, Object> rawRequest) {
        if (rawRequest == null) {
            return new HashMap<>();
        }
        Object payload = rawRequest.get("payload");
        if (Objects.equals(ActionType.CAPABILITY_INVOKE.name(), requestPacket.getMethodStr()) && payload instanceof Map) {
            return (Map<String, Object>) payload;
        }
        return rawRequest;
    }

    private List<String> fileInfoList(Object rawFileInfo) {
        List<String> fileInfoList = new ArrayList<>();
        if (rawFileInfo instanceof List) {
            for (Object item : (List) rawFileInfo) {
                if (item != null) {
                    fileInfoList.add(item.toString());
                }
            }
        }
        return fileInfoList;
    }

    public UploadFileResponse upload(IOSession session, final List<UploadFile> uploadFileList) {
        final UploadFileResponse response = new UploadFileResponse();
        if (uploadFileList != null && !uploadFileList.isEmpty()) {
            final Map<String, Object> keyMap = new HashMap<>();
            keyMap.put("key", "access_key,secret_key,host,appId,region,supportHttps");
            int msgId = IdUtil.getInt();
            session.sendJsonMsg(keyMap, ActionType.GET_WEBSITE.name(), msgId, MsgPacketStatus.SEND_REQUEST, null);
            MsgPacket packet = session.getResponseMsgPacketByMsgId(msgId);
            Map<String, String> responseMap = new Gson().fromJson(packet.getDataStr(), Map.class);
            QCloudBucketVO bucket = new QCloudBucketVO(getBucketName(session), responseMap.get("access_key"),
                    responseMap.get("secret_key"), responseMap.get("host"),
                    Long.valueOf(responseMap.get("appId")), responseMap.get("region"));
            BucketManageAPI man = new QCloudBucketManageImpl(bucket);
            for (UploadFile uploadFile : uploadFileList) {
                LOGGER.info("upload file " + uploadFile.getFile());
                UploadFileResponseEntry entry = new UploadFileResponseEntry();
                try {
                    boolean supportHttps = responseMap.get("supportHttps") != null && "on".equalsIgnoreCase(responseMap.get("supportHttps"));
                    entry.setUrl(man.create(uploadFile.getFile(), uploadFile.getFileKey(), true, supportHttps));
                } catch (Exception e) {
                    LOGGER.error("upload error", e);
                    entry.setUrl(uploadFile.getFileKey());
                }
                response.add(entry);
            }
            LOGGER.info("upload file finish");
        }
        return response;
    }

    private String getBucketName(IOSession session) {
        final Map<String, Object> keyMap = new HashMap<>();
        keyMap.put("key", getBucketKeyName());
        int msgId = IdUtil.getInt();
        session.sendJsonMsg(keyMap, ActionType.GET_WEBSITE.name(), msgId, MsgPacketStatus.SEND_REQUEST, null);
        MsgPacket packet = session.getResponseMsgPacketByMsgId(msgId);
        Map<String, String> responseMap = new Gson().fromJson(packet.getDataStr(), Map.class);
        return responseMap.get(getBucketKeyName());
    }

    public String getBucketKeyName() {
        return "bucket";
    }
}
