import request from '@/utils/request'
import axios from "axios";

const instance = axios.create({
  baseURL: process.env.VUE_APP_API_URL || "", // 配置你的后端 baseURL
  timeout: 1000000, // 设置超时时间为10秒
});
// 查询任务列表
export function listTask(query) {
  return request({
    url: '/tasks/task/list',
    method: 'get',
    params: query
  })
}

// 查询任务列表（排除节点）
export function listTaskExcludeChild(taskId) {
  return request({
    url: '/tasks/task/list/exclude/' + taskId,
    method: 'get'
  })
}

// 查询任务详细
export function getTask(taskId) {
  return request({
    url: '/tasks/task/' + taskId,
    method: 'get'
  })
}

// 新增任务
export function addTask(data) {
  return request({
    url: '/tasks/task',
    method: 'post',
    data: data
  })
}

// 新增多条任务
export function addTasks(data) {
  return request({
    url: '/tasks/task/addTasks',
    method: 'post',
    data: data
  })
}

// 修改任务
export function updateTask(data) {
  return request({
    url: '/tasks/task',
    method: 'put',
    data: data
  })
}

// 删除任务
export function delTask(taskId) {
  return request({
    url: '/tasks/task/' + taskId,
    method: 'delete'
  })
}

// 调用Ai聊天
export function chat(message,tokenMax) {
  return request({
    url: '/tasks/ai/chat',
    method: 'post',
    params: {
      message: message,
      maxTokens: tokenMax
    }
  })
}
export function chatStream(message) {
  return request({
    url: '/deepseekApi/chatStream',
    method: 'post',
    params: {
      message: message,
    },
    headers: {
      'Accept': 'text/event-stream',
    },
  })
}

// 调用Ai拆分任务
export function generateTask(message,tokenMax) {
  return request({
    url: '/tasks/ai/generateTask',
    method: 'get',
    params: {
      message: message,
      maxTokens: tokenMax
    }
  })
}
