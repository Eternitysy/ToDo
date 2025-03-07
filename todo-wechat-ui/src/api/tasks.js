import request from '@/utils/request'

export default {
  // 查询任务列表
  listTask(query) {
    return request({
      url: '/tasks/task/list',
      method: 'get',
      params: query
    })
  },

// 查询任务列表（排除节点）
  listTaskExcludeChild(taskId) {
    return request({
      url: '/tasks/task/list/exclude/' + taskId,
      method: 'get'
    })
  },

// 查询任务详细
  getTask(taskId) {
    return request({
      url: '/tasks/task/' + taskId,
      method: 'get'
    })
  },

// 新增任务
  addTask(data) {
    return request({
      url: '/tasks/task',
      method: 'post',
      data: data
    })
  },

// 新增多条任务
  addTasks(data) {
    return request({
      url: '/tasks/task/addTasks',
      method: 'post',
      data: data
    })
  },

// 修改任务
  updateTask(data) {
    return request({
      url: '/tasks/task',
      method: 'put',
      data: data
    })
  },

// 删除任务
  delTask(taskId) {
    return request({
      url: '/tasks/task/' + taskId,
      method: 'delete'
    })
  }


}

