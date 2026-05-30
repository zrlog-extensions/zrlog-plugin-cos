package com.zrlog.plugin.cos.service;

import com.zrlog.plugin.api.Capability;
import com.zrlog.plugin.api.Service;

@Service("uploadToPrivateService")
@Capability(
        key = "cos.uploadPrivate",
        type = "service",
        label = "上传 COS 私有资源",
        description = "上传私有附件或备份文件到腾讯云 COS 私有存储",
        exposure = {"internal"},
        timeoutSeconds = 120
)
public class UploadToPrivateService extends UploadService {

    @Override
    public String getBucketKeyName() {
        return "private_bucket";
    }
}
