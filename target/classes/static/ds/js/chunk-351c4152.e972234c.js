(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-351c4152"],{7413:function(e,t,n){"use strict";n("c870")},a55b:function(e,t,n){"use strict";n.r(t);var i=function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("div",{staticClass:"sign"},[n("div",{staticClass:"sign-container"},[n("el-card",{staticClass:"sign-card"},[n("div",{staticClass:"clearfix",attrs:{slot:"header"},slot:"header"},[n("span",[e._v("登录")]),n("router-link",{staticClass:"sign-card-header--button",attrs:{to:{name:"signup"}}},[e._v(" 前往注册 ")])],1),n("el-form",{ref:"loginForm",attrs:{model:e.login,rules:e.loginRules,"label-position":"top"}},[n("el-form-item",{attrs:{prop:"name",label:"用户名"}},[n("el-input",{attrs:{placeholder:"请输入用户名",maxlength:"11"},model:{value:e.login.name,callback:function(t){e.$set(e.login,"name",t)},expression:"login.name"}})],1),!1===e.useCert?n("el-form-item",{attrs:{prop:"password",label:"密码"}},[n("el-input",{attrs:{placeholder:"请输入密码","show-password":""},nativeOn:{keyup:function(t){return!t.type.indexOf("key")&&e._k(t.keyCode,"enter",13,t.key,"Enter")?null:e.onLoginSubmit.apply(null,arguments)}},model:{value:e.login.password,callback:function(t){e.$set(e.login,"password",t)},expression:"login.password"}})],1):n("el-form-item",{attrs:{prop:"cert",label:"证书"}},[n("el-input",{directives:[{name:"show",rawName:"v-show",value:!1,expression:"false"}],model:{value:e.login.cert,callback:function(t){e.$set(e.login,"cert",t)},expression:"login.cert"}}),n("el-button",{on:{click:e.selectFile}},[e._v(" 上传证书文件 ")]),n("div",{staticClass:"text"},[e._v(" "+e._s(e.certFileName)+" ")])],1),n("el-form-item",[!1===e.useCert?n("el-button",{attrs:{type:"text"},on:{click:function(t){e.useCert=!0}}},[e._v("使用证书登录")]):n("el-button",{attrs:{type:"text"},on:{click:function(t){e.useCert=!1}}},[e._v("使用密码登录")])],1),n("el-form-item",[n("el-button",{staticStyle:{width:"100%"},attrs:{type:"primary",loading:e.loading},on:{click:e.onLoginSubmit}},[e._v(" 登录 ")])],1)],1)],1)],1)])},r=[],s=(n("bdf9"),n("0721"),n("ac30"),n("f6a5"),n("63e0")),a={name:"Login",components:{},data:function(){return{login:{name:"",password:"",cert:""},loginRules:{name:[{required:!0,trigger:"blur",message:"用户名不能为空"}],password:[{required:!0,trigger:"blur",message:"密码不能为空"}],cert:[{required:!0,trigger:"blur",message:"证书不能为空"}]},certFileName:"",useCert:!1,loading:!1}},created:function(){var e=this.$route.query["name"];e&&(this.login.name=e)},methods:{onLoginSubmit:function(){var e=this;this.$refs.loginForm.validate((function(t){t&&(e.loading=!0,s["a"].login(e.login,e.useCert).then((function(){e.$message({message:"登录成功",type:"success"}),e.jumpTo()})).catch((function(t){e.$message({message:t,type:"error"})})).finally((function(){e.loading=!1})))}))},selectFile:function(){var e=this,t=document.createElement("input");t.type="file",t.onchange=function(t){var n=t.target.files[0];e.certFileName=n.name;var i=new FileReader;i.readAsText(n,"UTF-8"),i.onload=function(t){var n=t.target.result,i=JSON.parse(n);i.serialNumber&&(e.login.cert=i.serialNumber.split("-")[1])}},t.click()},jumpTo:function(){var e=this.$route.query["redirect"];e?this.$router.push({path:e}):this.$router.push({path:"/"})}}},o=a,l=(n("7413"),n("52e0")),c=Object(l["a"])(o,i,r,!1,null,"386d5412",null);t["default"]=c.exports},c870:function(e,t,n){}}]);
//# sourceMappingURL=chunk-351c4152.e972234c.js.map