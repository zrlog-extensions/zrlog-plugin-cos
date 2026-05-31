package com.zrlog.plugin.cos;

import com.zrlog.plugin.RunConstants;
import com.zrlog.plugin.type.RunType;
import com.zrlog.plugin.common.PluginNativeImageUtils;
import com.zrlog.plugin.cos.controller.CosController;
import com.zrlog.plugin.cos.service.CosStaticSyncService;
import com.zrlog.plugin.cos.service.UploadService;
import com.zrlog.plugin.cos.service.UploadToPrivateService;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

public class GraalvmAgentApplication {


    public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException {
        RunConstants.runType = RunType.AGENT;
        //upload need set content-type
        PluginNativeImageUtils.usedGsonObject();
        //RefreshObjectCachesResponse refreshObjectCachesResponse = new RefreshObjectCachesResponse();
        //refreshObjectCachesResponse.setRefreshTaskId("");
        //refreshObjectCachesResponse.setRequestId("");
        UploadService.class.newInstance();
        UploadToPrivateService.class.newInstance();
        CosStaticSyncService.class.newInstance();
        String basePath = System.getProperty("user.dir").replace("\\target", "").replace("/target", "");
        //PathKit.setRootPath(basePath);
        File file = new File(basePath + "/src/main/resources");
        PluginNativeImageUtils.doLoopResourceLoad(file.listFiles(), file.getPath() + "/", "/");
        //Application.nativeAgent = true;
        PluginNativeImageUtils.exposeController(Collections.singletonList(CosController.class));
        Application.main(args);

    }
}
