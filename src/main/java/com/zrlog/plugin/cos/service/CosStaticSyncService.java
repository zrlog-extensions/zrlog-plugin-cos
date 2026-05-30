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
        SyncTemplateStaticResourceRunnable runnable = new SyncTemplateStaticResourceRunnable(session);
        runnable.run();

        CapabilityInvokeResult result = new CapabilityInvokeResult();
        result.setSuccess(runnable.isSuccess());
        result.setErrorMessage(runnable.isSuccess() ? "" : runnable.getMessage());
        Map<String, Object> data = new HashMap<>();
        data.put("filesCount", runnable.getFilesCount());
        data.put("message", runnable.getMessage());
        result.setData(data);
        session.sendJsonMsg(result, msgPacket.getMethodStr(), msgPacket.getMsgId(),
                result.isSuccess() ? MsgPacketStatus.RESPONSE_SUCCESS : MsgPacketStatus.RESPONSE_ERROR);
    }
}
