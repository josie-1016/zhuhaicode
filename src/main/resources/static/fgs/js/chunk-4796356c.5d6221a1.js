(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-4796356c"],{"438b0":function(e,t,a){},"4e89":function(e,t,a){"use strict";a("d88d")},"604e":function(e,t,a){"use strict";a.d(t,"a",(function(){return s}));a("0721");var n=a("b775"),r=a("4f14"),o=a.n(r),s={encrypt:function(e){var t=e.userName,a=e.tags,r=e.file,o=e.policy,s=new FormData;return s.append("fileName",t),s.append("file",r),s.append("tags",a),s.append("policy",o),new Promise((function(e,t){Object(n["a"])({url:"/content/upload",method:"post",headers:{"Content-Type":"multipart/form-data"},data:s,timeout:0}).then((function(a){200===a.code?e(a.data):t(a)})).catch(t)}))},files:function(e){var t=e.userName,a=e.tag,r=e.size,o=e.bookmark,s={userName:t,tag:a,size:r,bookmark:o},i={fromUserName:s.userName,tag:s.tag,pageSize:s.size||10,bookmark:s.bookmark};return new Promise((function(e,t){Object(n["a"])({url:"/content/list",method:"get",data:i,params:i}).then((function(a){200===a.code?e(a.data):t(a)})).catch(t)}))},decrypt:function(e){var t=e.user,a=e.cipher,r=e.sharedUser,o=e.fileName,s=e.tags,i={userName:t,fileName:o,cipher:a,tags:s,sharedUser:r};return new Promise((function(e,t){Object(n["a"])({url:"/content/decryption",method:"post",data:i}).then((function(a){200===a.code?e(a.data):t(a)})).catch(t)}))},download:function(e){var t=e.fileName,a=e.sharedUser,n={fileName:t,sharedUser:a};return new Promise((function(e,t){o.a.request({baseURL:"/abe/",url:"/content/download",method:"get",data:n,params:n,responseType:"blob"}).then((function(a){200===a.status?e(a.data):t(a)})).catch(t)}))},downloadCipher:function(e){var t=e.userName,a=e.fileName,n=e.sharedUser,r={userName:t,fileName:a,sharedUser:n};return new Promise((function(e,t){o.a.request({baseURL:"/abe/",url:"/content/cipher",method:"get",data:r,params:r,responseType:"blob"}).then((function(a){200===a.status?e(a.data):t(a)})).catch(t)}))}}},6957:function(e,t,a){"use strict";a("438b0")},"6b79":function(e,t,a){"use strict";a.d(t,"a",(function(){return n}));a("106c");var n={methods:{formatTime:function(e){var t="-";try{t=new Date(1e3*e);var a=t.getFullYear(),n=t.getMonth()+1,r=t.getDate(),o=t.getHours(),s=t.getMinutes(),i=t.getSeconds(),c="".concat(a,"-").concat(n,"-").concat(r," ").concat(o,":").concat(s,":").concat(i);return c}catch(l){console.log(l)}return t}}}},"752a":function(e,t,a){"use strict";var n=function(){var e=this,t=e.$createElement,a=e._self._c||t;return a("el-table",{attrs:{data:e.files}},[a("el-table-column",{attrs:{"show-overflow-tooltip":"",label:"文件名",prop:"fileName"}}),a("el-table-column",{attrs:{"show-overflow-tooltip":"",label:"密文哈希",prop:"cipher"}}),a("el-table-column",{attrs:{"show-overflow-tooltip":"",label:"上传者",prop:"sharedUser"}}),a("el-table-column",{attrs:{"show-overflow-tooltip":"",label:"上传时间",prop:"timeStamp",width:"250"},scopedSlots:e._u([{key:"default",fn:function(t){return[e._v(" "+e._s(t.row.timeStamp)+" ")]}}])}),a("el-table-column",{attrs:{"show-overflow-tooltip":"",label:"IP",prop:"ip",width:"130"}}),a("el-table-column",{attrs:{"show-overflow-tooltip":"",label:"加密策略",prop:"policy"}}),a("el-table-column",{attrs:{"show-overflow-tooltip":"",label:"标签",prop:"tags"},scopedSlots:e._u([{key:"default",fn:function(t){return e._l(e.filterEmpty(t.row.tags),(function(t,n){return a("el-tag",{key:n,attrs:{size:"small",effect:"plain"}},[e._v(" "+e._s(t)+" ")])}))}}])}),a("el-table-column",{attrs:{label:"操作",align:"right",width:"200"},scopedSlots:e._u([{key:"default",fn:function(t){return[a("el-button",{attrs:{size:"mini",type:"success"},on:{click:function(a){return e.decryDownload(t.row)}}},[e._v(" 解密下载 ")]),a("el-button",{attrs:{size:"mini"},on:{click:function(a){return e.cipherDownload(t.row)}}},[e._v(" 下载密文 ")])]}}])})],1)},r=[],o=a("604e"),s=a("07a4"),i=a("eeef"),c=a("f778"),l=a("6b79"),u={name:"FilesTable",mixins:[i["a"],c["a"],l["a"]],props:{files:{type:Array,default:void 0}},methods:{decryDownload:function(e){var t=this,a=s["a"].userName(),n=e.cipher,r=e.sharedUser,i=e.fileName,c=e.tags;o["a"].decrypt({user:a,cipher:n,sharedUser:r,fileName:i,tags:c}).then((function(){return o["a"].download({fileName:i,sharedUser:r}).then((function(e){t.saveFile(i,e)})).catch((function(e){t.$message({message:e.message,duration:5e3,type:"error"})}))})).catch((function(e){t.$message({message:e.message,duration:5e3,type:"error"})}))},cipherDownload:function(e){var t=this,a=s["a"].userName(),n=e.sharedUser,r=e.fileName;o["a"].downloadCipher({userName:a,fileName:r,sharedUser:n}).then((function(e){t.saveFile("cipher_hash_"+r,e)})).catch((function(e){t.$message({message:e.message,duration:5e3,type:"error"})}))}}},f=u,d=(a("6957"),a("52e0")),m=Object(d["a"])(f,n,r,!1,null,"3b06acfe",null);t["a"]=m.exports},"87ff":function(e,t,a){"use strict";a.r(t);var n=function(){var e=this,t=e.$createElement,a=e._self._c||t;return a("Card",{attrs:{title:"我的文件"}},[a("FilesTable",{attrs:{files:e.files}})],1)},r=[],o=a("ae8d"),s=a("752a"),i=a("604e"),c=a("07a4"),l=a("eeef"),u=a("f778"),f={name:"Mine",mixins:[l["a"],u["a"]],components:{Card:o["a"],FilesTable:s["a"]},data:function(){return{files:[],bookmark:""}},mounted:function(){var e=this,t="",a=c["a"].userName(),n=this.bookmark;i["a"].files({userName:a,tag:t,bookmark:n}).then((function(t){e.files=t.contents,e.bookmark=t.bookmark})).catch(console.log)}},d=f,m=(a("4e89"),a("52e0")),p=Object(m["a"])(d,n,r,!1,null,"48947b9e",null);t["default"]=p.exports},ae8d:function(e,t,a){"use strict";var n=function(){var e=this,t=e.$createElement,a=e._self._c||t;return a("div",{staticClass:"card"},[e.title?a("div",{staticClass:"card-head"},[e._v(" "+e._s(e.title)+" "),a("div",{staticStyle:{float:"right"}},[e._t("op")],2)]):e._e(),e._t("default")],2)},r=[],o={name:"Card",props:{title:{type:String,default:""}}},s=o,i=(a("e24e"),a("52e0")),c=Object(i["a"])(s,n,r,!1,null,"88a16ad0",null);t["a"]=c.exports},d88d:function(e,t,a){},e24e:function(e,t,a){"use strict";a("e826")},e826:function(e,t,a){},eeef:function(e,t,a){"use strict";a.d(t,"a",(function(){return n}));a("0721"),a("7fa7"),a("042e"),a("c189"),a("3435");var n={methods:{saveFile:function(e,t){var a=window.URL.createObjectURL(new Blob([t])),n=document.createElement("a");n.href=a,n.setAttribute("download",e),document.body.appendChild(n),n.click()}}}},f778:function(e,t,a){"use strict";a.d(t,"a",(function(){return n}));a("5cf2"),a("0721"),a("ac30"),a("f6a5"),a("8281");var n={methods:{filterEmpty:function(e){if(void 0==e)return"";if(Array.isArray(e)){var t=e.join(" ").split(" ").filter((function(e){return e}));return t}return e}}}}}]);
//# sourceMappingURL=chunk-4796356c.5d6221a1.js.map