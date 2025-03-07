import axios from "axios";

// 创建axios实例
const service = axios.create({
  baseURL: "http://667519b.r22.cpolar.top", // api 的 base_url
  timeout: 30000 // 请求超时时间
});

// http request 拦截器
service.interceptors.request.use(config => {
    let token = window.localStorage.getItem("token") || "";
    if (token != "") {
      config.headers["token"] = token;
    }
    return config;
  },
  err => {
    return Promise.reject(err);
  });
// http response 拦截器
service.interceptors.response.use(response => {
    // 未设置状态码则默认成功状态
    const code = response.data.code || response.status;
    console.log(code)
    if (code == 208) {
      // debugger
      // 替换# 后台获取不到#后面的参数
      let url = window.location.href.replace('#', 'sy')
      window.location = 'http://667519b.r22.cpolar.top/todo/wechat/authorize?returnUrl=' + url
    } else {
      if (code == 200) {
          console.log(response.data)
        return response.data;
      } else {
        // 209没有权限 系统会自动跳转授权登录的，已在App.vue处理过，不需要提示
        if (code != 209) {
          alert(response.data.message || "error");
        }
        return Promise.reject(response);
      }
    }
  },
  error => {
    return Promise.reject(error.response);   // 返回接口返回的错误信息
  });

export default service;
