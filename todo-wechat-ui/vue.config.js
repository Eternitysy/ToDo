const port = process.env.port || process.env.npm_config_port || 9091 // 端口

// 解决内网穿透，添加该文件
module.exports = {
  devServer: {
    host: '0.0.0.0',
    port: port,
    hot: true,
    disableHostCheck: true,
    proxy: {
      // detail: https://cli.vuejs.org/config/#devserver-proxy
      [process.env.VUE_APP_BASE_API]: {
        target: `http://localhost:9090`,
        changeOrigin: true,
        pathRewrite: {
          ['^' + process.env.VUE_APP_BASE_API]: ''
        }
      }
    },
  },
}
