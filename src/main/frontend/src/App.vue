<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';

interface CosConfig {
  access_key?: string;
  secret_key?: string;
  host?: string;
  bucket?: string;
  private_bucket?: string;
  appId?: string;
  region?: string;
  syncTemplate?: string;
  supportHttps?: string;
  version?: string;
}

const cos = ref<CosConfig>({
  syncTemplate: 'off',
  supportHttps: 'off'
});
const version = ref<string>('');
const loading = ref(false);

onMounted(() => {
  fetch('info')
    .then(res => res.json())
    .then(data => {
      cos.value = data;
      version.value = 'v' + data.version;
    })
    .catch(err => {
      console.error("Failed to load plugin info", err);
    });
});

const getFormData = () => {
    return new URLSearchParams({
        access_key: cos.value.access_key || '',
        secret_key: cos.value.secret_key || '',
        host: cos.value.host || '',
        bucket: cos.value.bucket || '',
        private_bucket: cos.value.private_bucket || '',
        appId: cos.value.appId || '',
        region: cos.value.region || '',
        syncTemplate: cos.value.syncTemplate || 'off',
        supportHttps: cos.value.supportHttps || 'off'
    }).toString();
}

const submitForm = () => {
  loading.value = true;
  fetch('update', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    },
    body: getFormData()
  })
  .then(res => res.json())
  .then(data => {
    if (data.success || data.status == 200) {
      ElMessage({
        message: '操作成功...',
        type: 'success',
      });
    } else {
      ElMessage({
        message: '发生了一些异常...',
        type: 'error',
      });
    }
  })
  .catch(err => {
    ElMessage({
      message: '请求失败',
      type: 'error',
    });
    console.error(err);
  })
  .finally(() => {
    loading.value = false;
  });
};
</script>

<template>
  <div class="admin-container">
    <div class="page-header">
        <h3>
          腾讯云存储设置
          <small>{{ version }}</small>
          <a href="https://blog.zrlog.com/cos-install" target="_blank" class="help-link">如何寻找这些信息？</a>
        </h3>
      </div>
      
      <el-form :model="cos" label-width="150px" class="settings-form">
        <el-form-item label="AccessKey">
          <el-input v-model="cos.access_key" placeholder=""></el-input>
        </el-form-item>

        <el-form-item label="SecretKey">
          <el-input v-model="cos.secret_key" placeholder=""></el-input>
        </el-form-item>

        <el-form-item label="域名">
          <el-input v-model="cos.host" placeholder="" style="max-width: 400px;"></el-input>
        </el-form-item>
        
        <el-form-item label="仓库名">
          <el-input v-model="cos.bucket" placeholder="" style="max-width: 300px;"></el-input>
        </el-form-item>
        
        <el-form-item label="私有仓库名">
          <el-input v-model="cos.private_bucket" placeholder="存储敏感信息，如数据库备份文件" style="max-width: 300px;"></el-input>
        </el-form-item>
        
        <el-form-item label="AppId">
          <el-input v-model="cos.appId" placeholder="" style="max-width: 300px;"></el-input>
        </el-form-item>
        
        <el-form-item label="Region">
          <el-input v-model="cos.region" placeholder="" style="max-width: 300px;"></el-input>
        </el-form-item>

        <el-form-item label="主题静态文件同步">
          <el-switch
            v-model="cos.syncTemplate"
            active-value="on"
            inactive-value="off"
            style="margin-right: 10px;"
          />
          <span style="color: #606266; font-size: 14px;">（需要主题支持）</span>
        </el-form-item>

        <el-form-item label="已支持 HTTPS">
          <el-switch
            v-model="cos.supportHttps"
            active-value="on"
            inactive-value="off"
          />
        </el-form-item>
        
        <el-divider></el-divider>
        
        <el-form-item>
          <el-button type="primary" @click="submitForm" :loading="loading">
            提交
          </el-button>
        </el-form-item>
      </el-form>
  </div>
</template>

<style>
.page-header {
  border-bottom: 1px solid #ebeef5;
  margin-bottom: 24px;
  padding-bottom: 12px;
}

.page-header h3 {
  margin: 0;
  color: #303133;
  font-size: 22px;
  display: flex;
  align-items: center;
}

.page-header small {
  color: #909399;
  font-size: 14px;
  margin-left: 10px;
  flex: 1;
}

.help-link {
  font-size: 14px;
  font-weight: normal;
  color: #409EFF;
  text-decoration: none;
}

.help-link:hover {
  text-decoration: underline;
}

.settings-form {
  margin-top: 20px;
}
</style>
