<template>
  <div>
    <div>账号切换</div>
    <button @click="admin()" type="default" size="mini">admin</button>
    <button @click="lrsjl()" type="default" size="mini">李人事经理</button>
    <button @click="zhangsan()" type="default" size="mini">张三</button>
    <button @click="lisi()" type="default" size="mini">李四</button>
    <div>当前token：{{ token }}</div>
  </div>
</template>

<script>
export default {
  name: "Test",

  data() {
    return {
      token: ''
    };
  },

  created(){
    this.token = window.localStorage.getItem('token')
  },

  methods: {
    admin() {
      window.localStorage.setItem('token', '');
      let token = 'eyJhbGciOiJIUzUxMiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAAAKtWKi5NUrJScgwN8dANDXYNUtJRSq0oULIyNDe2MDO2MLcw1VEqLU4t8kwBikGYeYm5qUAtiSm5mXlKtQAiM6l_QgAAAA.L6i7qAQa9_MpIafakzVkr7ZLBz8ydABCDWuzEolqzWcAN1-QdJUvThEdDcRIe6wFDEanY3EyBbqjWLXJBIlMtA'
      window.localStorage.setItem('token', token);
      this.token = window.localStorage.getItem('token')
    },

    lrsjl() {
      window.localStorage.setItem('token', '');
      let token = 'eyJhbGciOiJIUzUxMiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAAAKtWKi5NUrJScgwN8dANDXYNUtJRSq0oULIyNDM3NjMwNDY10VEqLU4t8kxRsjKGMPMSc1OBWnKKirNylGoBjn551kIAAAA.HiQAK2uUQCFlQilO3mR3p5TURrlMrjN7zrR1IcFqhU8Q-VyxUiRJrGIuSwSKOC1uyQH_4JXL99yGDq74OnKvXQ'
      window.localStorage.setItem('token', token);
      this.token = window.localStorage.getItem('token')
    },

    zhangsan() {
      window.localStorage.setItem('token', '');
      let token = 'eyJhbGciOiJIUzUxMiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAAAKtWKi5NUrJScgwN8dANDXYNUtJRSq0oULIyNDe2MDO2sDQy1VEqLU4t8kxRsjKHMPMSc1OBWqoyEvPSixPzlGoB4foOnkUAAAA.J3cr6vV8vlMcB737lDt_PkPSiEWjcecVZebFXjMnn7Sx8CqOmKvf3EqyOzJFwx0Ne-yQvn_OLZ1Y_4VjMQyskQ'
      window.localStorage.setItem('token', token);
      this.token = window.localStorage.getItem('token')
    },

    lisi() {
      window.localStorage.setItem('token', '');
      let token = 'eyJhbGciOiJIUzUxMiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAAAKtWKi5NUrJScgwN8dANDXYNUtJRSq0oULIyNDe2MDO2sDA30lEqLU4t8kxRsjKBMPMSc1OBWnIyizOVagFRTQArQQAAAA.TP8ZbxWAa-6I_UcxjjJo3NTMZLv200XTIoJYD3thv-k8gk8vLr2YptEyts4tLliV08eY4OvMMSFGLa_S-O7lWw'
      window.localStorage.setItem('token', token);
      this.token = window.localStorage.getItem('token')
    }
  }
}
</script>
