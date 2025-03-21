import request from '@/utils/request'

const api_name = '/tasks/task'

export default {

  list() {
    return request({
      url: `${api_name}/list`,
      method: 'get'
    })
  },

  startUp(processFormVo) {
    return request({
      url: `${api_name}/startup`,
      method: 'post',
      data: processFormVo
    })
  },

  findPending(page, limit) {
    return request({
      url: `${api_name}/findPending/`+page+`/`+ limit,
      method: 'get'
    })
  },

  show(id) {
    return request({
      url: `${api_name}/show/`+id,
      method: 'get'
    })
  },

  approve(approvalVo) {
    return request({
      url: `${api_name}/approve`,
      method: 'post',
      data: approvalVo
    })
  },


  findProcessed(page, limit) {
    return request({
      url: `${api_name}/findProcessed/`+page+`/`+ limit,
      method: 'get'
    })
  },

  findUnStarted(status,page, limit) {
    return request({
      url: `${api_name}/findUnStarted/`+status+`/`+page+`/`+ limit,
      method: 'get',
    })
  },

  findUnFinished(status,page, limit) {
    return request({
      url: `${api_name}/findUnFinished/`+status+`/`+page+`/`+ limit,
      method: 'get',
    })
  },

  findFinished(status,page, limit) {
    return request({
      url: `${api_name}/findFinished/`+status+`/`+page+`/`+ limit,
      method: 'get',
    })
  },



}
