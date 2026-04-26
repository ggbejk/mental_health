﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿<script setup>
import { ref, watch } from 'vue'
import axios from 'axios'

const props = defineProps({
  visible: Boolean,
  student: Object
})

const emit = defineEmits(['close', 'success'])

const form = ref({
  contactMethod: '电话',
  contactPurpose: '',
  contactContent: '',
  contactResult: '',
  followUpPlan: ''
})

const contactMethods = ['电话', '微信', 'QQ', '视频', '面谈', '其他']
const contactPurposes = ['常规沟通', '预警通知', '情况反馈', '家校合作', '紧急联系', '其他']
const submitting = ref(false)

const resetForm = () => {
  form.value = {
    contactMethod: '电话',
    contactPurpose: '',
    contactContent: '',
    contactResult: '',
    followUpPlan: ''
  }
}

watch(() => props.visible, (val) => {
  if (val) {
    resetForm()
  }
})

const handleClose = () => {
  emit('close')
}

const handleSubmit = async () => {
  if (!form.value.contactPurpose) {
    alert('请选择联系目的')
    return
  }
  if (!form.value.contactContent.trim()) {
    alert('请填写联系内容')
    return
  }

  submitting.value = true
  try {
    await axios.post('/api/counselor/parent-contact/save', {
      studentId: props.student.id,
      counselorId: props.student.counselorId,
      contactMethod: form.value.contactMethod,
      contactPurpose: form.value.contactPurpose,
      contactContent: form.value.contactContent,
      contactResult: form.value.contactResult,
      followUpPlan: form.value.followUpPlan,
      contactTime: new Date()
    })
    alert('联系记录保存成功')
    emit('success')
    handleClose()
  } catch (error) {
    console.error('保存失败', error)
    alert(error?.response?.data?.message || '保存失败')
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <div v-if="visible" class="modal-overlay" @click.self="handleClose">
    <div class="modal-content">
      <div class="modal-header">
        <h3>联系家长</h3>
        <button class="close-btn" @click="handleClose">×</button>
      </div>

      <div class="modal-body">
        <div class="student-info" v-if="student">
          <div class="info-row">
            <span class="label">学生：</span>
            <span class="value">{{ student.realName }}</span>
          </div>
          <div class="info-row" v-if="student.parentName">
            <span class="label">家长：</span>
            <span class="value">{{ student.parentName }} ({{ student.parentRelation || '家长' }})</span>
          </div>
          <div class="info-row" v-if="student.parentPhone">
            <span class="label">电话：</span>
            <span class="value">{{ student.parentPhone }}</span>
          </div>
        </div>

        <div class="form-item">
          <label>联系方式</label>
          <select v-model="form.contactMethod">
            <option v-for="method in contactMethods" :key="method" :value="method">
              {{ method }}
            </option>
          </select>
        </div>

        <div class="form-item">
          <label>联系目的 <span class="required">*</span></label>
          <select v-model="form.contactPurpose">
            <option value="">请选择</option>
            <option v-for="purpose in contactPurposes" :key="purpose" :value="purpose">
              {{ purpose }}
            </option>
          </select>
        </div>

        <div class="form-item">
          <label>联系内容 <span class="required">*</span></label>
          <textarea
            v-model="form.contactContent"
            rows="4"
            placeholder="请详细描述联系内容和沟通情况..."
          ></textarea>
        </div>

        <div class="form-item">
          <label>联系结果</label>
          <select v-model="form.contactResult">
            <option value="">请选择</option>
            <option value="已接通，家长已知晓情况">已接通，家长已知晓情况</option>
            <option value="未接通，已留言">未接通，已留言</option>
            <option value="号码错误/空号">号码错误/空号</option>
            <option value="家长不配合">家长不配合</option>
            <option value="其他">其他</option>
          </select>
        </div>

        <div class="form-item">
          <label>后续跟进计划</label>
          <textarea
            v-model="form.followUpPlan"
            rows="3"
            placeholder="请描述后续跟进计划..."
          ></textarea>
        </div>
      </div>

      <div class="modal-footer">
        <button class="cancel-btn" @click="handleClose">取消</button>
        <button class="submit-btn" @click="handleSubmit" :disabled="submitting">
          {{ submitting ? '保存中...' : '保存记录' }}
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 18px;
  width: 560px;
  max-width: 90vw;
  max-height: 90vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #eee;
}

.modal-header h3 {
  margin: 0;
  color: #2f5d50;
  font-size: 18px;
}

.close-btn {
  background: none;
  border: none;
  font-size: 28px;
  color: #999;
  cursor: pointer;
  padding: 0;
  line-height: 1;
}

.modal-body {
  padding: 24px;
  overflow-y: auto;
  flex: 1;
}

.student-info {
  background: #f6ffed;
  border: 1px solid #d9f7be;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 20px;
}

.info-row {
  display: flex;
  gap: 12px;
  margin-bottom: 8px;
}

.info-row:last-child {
  margin-bottom: 0;
}

.info-row .label {
  color: #666;
}

.info-row .value {
  color: #2f8f62;
  font-weight: 500;
}

.form-item {
  margin-bottom: 18px;
}

.form-item label {
  display: block;
  margin-bottom: 8px;
  color: #444;
  font-weight: 500;
}

.required {
  color: #e54d4d;
}

.form-item select,
.form-item textarea {
  width: 100%;
  border: 1px solid #dfe8e3;
  border-radius: 10px;
  padding: 12px 14px;
  font-size: 14px;
  font-family: inherit;
}

.form-item textarea {
  resize: vertical;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 24px;
  border-top: 1px solid #eee;
}

.cancel-btn {
  padding: 10px 20px;
  background: #f5f5f5;
  color: #666;
  border: none;
  border-radius: 8px;
  cursor: pointer;
}

.submit-btn {
  padding: 10px 20px;
  background: #2f8f62;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
}

.submit-btn:disabled {
  background: #95de64;
  cursor: not-allowed;
}
</style>
