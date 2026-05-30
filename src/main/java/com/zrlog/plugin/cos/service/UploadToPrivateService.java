package com.zrlog.plugin.cos.service;

import com.zrlog.plugin.api.Capability;
import com.zrlog.plugin.api.Service;

@Service("uploadToPrivateService")
@Capability(
        key = "cos.uploadPrivate",
        type = "service",
        label = "上传到腾讯云 COS 私有存储",
        description = "上传备份文件等私有资源到腾讯云 COS 私有存储桶。",
        exposure = {"internal"},
        timeoutSeconds = 120
)
public class UploadToPrivateService extends UploadService {

    @Override
    public String getBucketKeyName() {
        return "private_bucket";
    }
}
