import axios from "axios";

// 获取微信授权URL
export function getWeChatAuthUrl() {
    const appId = "YOUR_WECHAT_APPID"; // 公众号的 AppID
    const redirectUri = encodeURIComponent(window.location.origin + "/auth");
    return `https://open.weixin.qq.com/connect/oauth2/authorize?appid=${appId}&redirect_uri=${redirectUri}&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect`;
}

// 通过 code 换取 openid
export async function getOpenId(code) {
    const res = await axios.get(`/api/wechat/auth?code=${code}`);
    return res.data.openid;
}
