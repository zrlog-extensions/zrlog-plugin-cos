package com.zrlog.plugin.cos.service;

import com.zrlog.plugin.IOSession;
import com.zrlog.plugin.api.IPluginService;
import com.zrlog.plugin.api.ScheduledCapability;
import com.zrlog.plugin.api.Service;
import com.zrlog.plugin.cos.timer.SyncTemplateStaticResourceRunnable;
import com.zrlog.plugin.data.codec.MsgPacket;
import com.zrlog.plugin.data.codec.MsgPacketStatus;
import com.zrlog.plugin.message.CapabilityInvokeResult;

import java.util.HashMap;
import java.util.Map;

@Service("cos.syncStaticResources")
@ScheduledCapability(
        key = "cos.syncStaticResources",
        label = "同步 COS 静态资源",
        description = "同步模板静态资源到腾讯云 COS",
        defaultCron = "*/5 * * * *",
        timeoutSeconds = 300
)
public class CosStaticSyncService implements IPluginService {

    @Override
    public void handle(IOSession session, MsgPacket msgPacket) {
        CapabilityInvokeResult result = new CapabilityInvokeResult();
        Map<String, Object> data = new HashMap<>();
        try {
            new SyncTemplateStaticResourceRunnable(session).run();
            result.setSuccess(true);
            data.put("message", "COS static resources sync completed");
        } catch (Exception e) {
            result.setSuccess(false);
            result.setErrorMessage(e.getMessage());
            data.put("message", e.getMessage());
        }
        result.setData(data);
        session.sendJsonMsg(result, msgPacket.getMethodStr(), msgPacket.getMsgId(),
                result.isSuccess() ? MsgPacketStatus.RESPONSE_SUCCESS : MsgPacketStatus.RESPONSE_ERROR);
    }
}
