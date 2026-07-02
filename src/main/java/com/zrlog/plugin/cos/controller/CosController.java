package com.zrlog.plugin.cos.controller;

import com.google.gson.Gson;
import com.zrlog.plugin.IOSession;
import com.zrlog.plugin.common.IdUtil;
import com.zrlog.plugin.data.codec.ContentType;
import com.zrlog.plugin.data.codec.HttpRequestInfo;
import com.zrlog.plugin.data.codec.MsgPacket;
import com.zrlog.plugin.data.codec.MsgPacketStatus;
import com.zrlog.plugin.cos.service.CosStorageConfig;
import com.zrlog.plugin.type.ActionType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xiaochun on 2016/2/13.
 */
public class CosController {

    private static final String CONFIG_KEYS = "access_key,host,secret_key,private_bucket,bucket,syncTemplate,appId,region,supportHttps";

    private final IOSession session;
    private final MsgPacket requestPacket;
    private final HttpRequestInfo requestInfo;
    private final Gson gson = new Gson();

    public CosController(IOSession session, MsgPacket requestPacket, HttpRequestInfo requestInfo) {
        this.session = session;
        this.requestPacket = requestPacket;
        this.requestInfo = requestInfo;
    }

    public void update() {
        session.sendMsg(new MsgPacket(requestConfig(), ContentType.JSON, MsgPacketStatus.SEND_REQUEST,
                IdUtil.getInt(), ActionType.SET_WEBSITE.name()), msgPacket -> {
                    session.sendMsg(new MsgPacket(StorageApiResponse.success(), ContentType.JSON, MsgPacketStatus.RESPONSE_SUCCESS,
                            requestPacket.getMsgId(), requestPacket.getMethodStr()));
                });
    }

    public void info() {
        response(loadConfig());
    }

    public void index() {
        Map<String, Object> data = new HashMap<>();
        data.put("theme", isDarkMode() ? "dark" : "light");
        data.put("data", gson.toJson(StorageApiResponse.success(pageData())));
        session.responseHtml("/templates/index", data, requestPacket.getMethodStr(),
                requestPacket.getMsgId());
    }

    public void json() {
        response(StorageApiResponse.success(pageData()));
    }

    private StorageInfoResponse<CosStorageConfig> pageData() {
        StorageInfoResponse<CosStorageConfig> data = new StorageInfoResponse<CosStorageConfig>();
        data.setDark(isDarkMode());
        data.setColorPrimary(getAdminColorPrimary());
        data.setPlugin(session.getPlugin());
        data.setProvider(provider());
        data.setConfig(loadConfig());
        return data;
    }

    private StorageProvider provider() {
        return new StorageProvider("cos", "腾讯云 COS 对象存储设置", "https://blog.zrlog.com/cos-install",
                "Region", true, true, false, true);
    }

    private CosStorageConfig loadConfig() {
        CosStorageConfig config = session.getResponseSync(ContentType.JSON, WebsiteKeyRequest.of(CONFIG_KEYS), ActionType.GET_WEBSITE, CosStorageConfig.class);
        if (config == null) {
            config = new CosStorageConfig();
        }
        config.normalizeForPage(session.getPlugin().getVersion());
        return config;
    }

    private CosStorageConfig requestConfig() {
        CosStorageConfig config = new CosStorageConfig();
        config.setAccessKey(paramValue("access_key"));
        config.setSecretKey(paramValue("secret_key"));
        config.setHost(paramValue("host"));
        config.setBucket(paramValue("bucket"));
        config.setPrivateBucket(paramValue("private_bucket"));
        config.setAppId(paramValue("appId"));
        config.setRegion(paramValue("region"));
        config.setSupportHttps(paramValue("supportHttps"));
        config.setSyncTemplate(paramValue("syncTemplate"));
        return config;
    }

    private void response(Object data) {
        session.sendMsg(ContentType.JSON, data, requestPacket.getMethodStr(), requestPacket.getMsgId(), MsgPacketStatus.RESPONSE_SUCCESS);
    }

    private String paramValue(String key) {
        if (requestInfo.getParam() == null || requestInfo.getParam().get(key) == null || requestInfo.getParam().get(key).length == 0) {
            return "";
        }
        return requestInfo.getParam().get(key)[0];
    }

    private boolean isDarkMode() {
        return requestInfo.isDarkMode();
    }

    private String getAdminColorPrimary() {
        return requestInfo.getAdminColorPrimary();
    }
}
