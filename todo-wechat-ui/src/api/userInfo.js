import request from '@/utils/request'


export default {

  bindPhone(bindPhoneVo) {
    return request({
      url: `/todo/wechat/bindPhone`,
      method: 'post',
      data: bindPhoneVo
    })
  },

  getCurrentUser() {
    return request({
      url: `/system/user/getCurrentUser`,
      method: 'get',
    })
  },
}
