(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-3eac68ce"],{"604e":function(e,t,a){"use strict";a.d(t,"a",(function(){return s}));a("0721");var r=a("b775"),o=a("4f14"),n=a.n(o),s={encrypt:function(e){var t=e.userName,a=e.tags,o=e.file,n=e.policy,s=new FormData;return s.append("fileName",t),s.append("file",o),s.append("tags",a),s.append("policy",n),new Promise((function(e,t){Object(r["a"])({url:"/content/upload",method:"post",headers:{"Content-Type":"multipart/form-data"},data:s,timeout:0}).then((function(a){200===a.code?e(a.data):t(a)})).catch(t)}))},files:function(e){var t=e.userName,a=e.tag,o=e.size,n=e.bookmark,s={userName:t,tag:a,size:o,bookmark:n},i={fromUserName:s.userName,tag:s.tag,pageSize:s.size||10,bookmark:s.bookmark};return new Promise((function(e,t){Object(r["a"])({url:"/content/list",method:"get",data:i,params:i}).then((function(a){200===a.code?e(a.data):t(a)})).catch(t)}))},decrypt:function(e){var t=e.user,a=e.cipher,o=e.sharedUser,n=e.fileName,s=e.tags,i={userName:t,fileName:n,cipher:a,tags:s,sharedUser:o};return new Promise((function(e,t){Object(r["a"])({url:"/content/decryption",method:"post",data:i}).then((function(a){200===a.code?e(a.data):t(a)})).catch(t)}))},download:function(e){var t=e.fileName,a=e.sharedUser,r={fileName:t,sharedUser:a};return new Promise((function(e,t){n.a.request({baseURL:"/abe/",url:"/content/download",method:"get",data:r,params:r,responseType:"blob"}).then((function(a){200===a.status?e(a.data):t(a)})).catch(t)}))},downloadCipher:function(e){var t=e.userName,a=e.fileName,r=e.sharedUser,o={userName:t,fileName:a,sharedUser:r};return new Promise((function(e,t){n.a.request({baseURL:"/abe/",url:"/content/cipher",method:"get",data:o,params:o,responseType:"blob"}).then((function(a){200===a.status?e(a.data):t(a)})).catch(t)}))}}},"6a41":function(e,t,a){"use strict";a.r(t);var r=function(){var e=this,t=e.$createElement,a=e._self._c||t;return a("Card",{attrs:{title:"上传文件"}},[a("div",{staticClass:"grid-cols"},[a("el-form",{ref:"uploadForm",attrs:{"label-position":"left",rules:e.uploadRules,model:e.form,"label-width":"100px"}},[a("el-form-item",{attrs:{prop:"file",label:"* 选择文件"}},[a("input",{attrs:{type:"file",id:"input-file"}})]),a("el-form-item",{attrs:{prop:"policy",label:"策略表达式"}},[a("el-input",{attrs:{placeholder:"(A AND B AND (C OR D))"},model:{value:e.form.policy,callback:function(t){e.$set(e.form,"policy",t)},expression:"form.policy"}})],1),a("el-form-item",{attrs:{prop:"tags",label:"标签"}},[a("el-input",{attrs:{placeholder:"城市 系统 业务 备注（空格隔开）"},model:{value:e.form.tags,callback:function(t){e.$set(e.form,"tags",t)},expression:"form.tags"}})],1),a("el-form-item",[a("el-button",{attrs:{type:"primary"},on:{click:e.upload}},[e._v("上传到服务器")])],1)],1)],1)])},o=[],n=a("723b"),s=(a("ac30"),a("f6a5"),a("d065"),a("ae8d")),i=a("70f6"),c=a("604e"),l=a("07a4"),u={name:"Upload",components:{Card:s["a"]},data:function(){return{form:{tags:"",policy:""},options:["shanghai","myc","edu","test"],uploadRules:{policy:[{required:!0,trigger:"blur",message:"请填写上传策略"}],tags:[{required:!0,trigger:"blur",message:"请设置标签"}]}}},watch:{},mounted:function(){return Object(n["a"])(regeneratorRuntime.mark((function e(){return regeneratorRuntime.wrap((function(e){while(1)switch(e.prev=e.next){case 0:case"end":return e.stop()}}),e)})))()},methods:{selecedFile:function(e){this.file=e.file},handleRemove:function(e){this.$refs.upload.abort(e)},upload:function(){var e=this,t=document.querySelector("input[type=file]").files[0];if(console.log(t),t){var a=this.form.tags.split(" ");a.length<3?i["Notification"].error({title:"拒绝",message:"请补充完整的标签",duration:2e3}):this.$refs.uploadForm.validate((function(r){if(r){var o=l["a"].userName(),n=e.form.policy;c["a"].encrypt({file:t,userName:o,tags:a,policy:n}).then((function(e){Object(i["Message"])({message:"上传成功",duration:5e3,type:"success"}),console.log(e)})).catch((function(e){Object(i["Message"])({message:e.message,duration:5e3,type:"error"})}))}}))}else i["Notification"].error({title:"拒绝",message:"请添加文件",duration:2e3})}}},f=u,d=(a("74c6"),a("52e0")),p=Object(d["a"])(f,r,o,!1,null,"99522fc8",null);t["default"]=p.exports},"6cfc":function(e,t,a){},"74c6":function(e,t,a){"use strict";a("6cfc")},ae8d:function(e,t,a){"use strict";var r=function(){var e=this,t=e.$createElement,a=e._self._c||t;return a("div",{staticClass:"card"},[e.title?a("div",{staticClass:"card-head"},[e._v(" "+e._s(e.title)+" "),a("div",{staticStyle:{float:"right"}},[e._t("op")],2)]):e._e(),e._t("default")],2)},o=[],n={name:"Card",props:{title:{type:String,default:""}}},s=n,i=(a("e24e"),a("52e0")),c=Object(i["a"])(s,r,o,!1,null,"88a16ad0",null);t["a"]=c.exports},e24e:function(e,t,a){"use strict";a("e826")},e826:function(e,t,a){}}]);
//# sourceMappingURL=chunk-3eac68ce.b6f11ccb.js.map