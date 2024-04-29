/**
 * Project: HandyEditor.
 * Author: A.J <804644245@qq.com>
 * Copyright: http://www.catfish-cms.com All rights reserved.
 * Version: 1.6.3
 */
!
function() {
    window.HE = {
        config: {
            width: "100%",
            height: "380px",
            lang: "zh-jian",
            skin: "HandyEditor",
            externalSkin: "",
            autoFloat: !1,
            topOffset: 0,
            autoHeight: !1,
            uploadPhoto: !1,
            uploadPhotoHandler: "php/uploadPhoto.php",
            uploadPhotoSize: 0,
            uploadPhotoType: "gif,png,jpg,jpeg",
            uploadPhotoTypeError: "只能上传图片文件",
            uploadPhotoSizeError: "上传的图片文件超过允许的大小",
            uploadParam: {},
            item: ["bold", "italic", "strike", "underline", "fontSize", "fontName", "paragraph", "color", "backColor", "|", "center", "left", "right", "full", "indent", "outdent", "|", "link", "unlink", "textBlock", "code", "selectAll", "removeFormat", "trash", "|", "image", "expression", "subscript", "superscript", "horizontal", "orderedList", "unorderedList", "|", "undo", "redo", "|", "html", "|", "about"]
        },
        define: {
            version: "1.6.3",
            click: "",
            mtop: 0,
            mheight: 0,
            pre: !1,
            html: !1,
            expressionPath: "http://img.baidu.com/hi/jx2/",
            item: {
                bold: '<i class="he-bold"></i>',
                italic: '<i class="he-italic"></i>',
                strike: '<i class="he-strike"></i>',
                underline: '<i class="he-underline"></i>',
                fontsize: '<i class="he-fontsize"></i>',
                fontname: '<i class="he-font-2"></i>',
                paragraph: '<i class="he-h-sigh"></i>',
                color: '<i class="he-font-1"></i>',
                backcolor: '<i class="he-vkontakte"></i>',
                center: '<i class="he-align-center"></i>',
                left: '<i class="he-align-left"></i>',
                right: '<i class="he-align-right"></i>',
                full: '<i class="he-align-justify"></i>',
                indent: '<i class="he-indent-right"></i>',
                outdent: '<i class="he-indent-left"></i>',
                link: '<i class="he-link"></i>',
                unlink: '<i class="he-unlink"></i>',
                textblock: '<i class="he-imdb"></i>',
                code: '<i class="he-code"></i>',
                selectall: '<i class="he-cursor"></i>',
                removeformat: '<i class="he-check-empty"></i>',
                image: '<i class="he-picture"></i>',
                expression: '<i class="he-smile"></i>',
                subscript: '<i class="he-subscript"></i>',
                superscript: '<i class="he-superscript"></i>',
                horizontal: '<i class="he-minus"></i>',
                orderedlist: '<i class="he-list-numbered"></i>',
                unorderedlist: '<i class="he-list-bullet"></i>',
                cut: '<i class="he-scissors"></i>',
                copy: '<i class="he-docs"></i>',
                paste: '<i class="he-paste"></i>',
                trash: '<i class="he-trash"></i>',
                undo: '<i class="he-reply"></i>',
                redo: '<i class="he-forward"></i>',
                html: '<i class="he-file-code"></i>',
                about: '<i class="he-help-circled-alt"></i>'
            },
            group: {
                text: ["bold", "italic", "strike", "underline", "subscript", "superscript", "color", "backColor"],
                align: ["center", "left", "right", "full", "indent", "outdent", "orderedlist", "unorderedlist"],
                format: ["link", "unlink", "textblock", "code", "selectall", "removeformat", "trash", "horizontal"],
                font: ["fontsize", "fontname", "forecolor", "paragraph"],
                operate: ["cut", "copy", "paste", "html", "about"],
                multimedia: ["image", "expression"]
            },
            font: {
                songti: "SimSun",
                kaiti: "KaiTi",
                heiti: "SimHei",
                yahei: "Microsoft YaHei",
                andalemono: "andale mono",
                arial: "arial",
                arialblack: "arial black",
                comicsansms: "comic sans ms",
                impact: "impact",
                timesnewroman: "times new roman"
            },
            code: {
                js: "JavaScript",
                html: "HTML",
                css: "CSS",
                php: "PHP",
                pl: "Perl",
                py: "Python",
                rb: "Ruby",
                java: "Java",
                vb: "ASP/VB",
                cpp: "C/C++",
                cs: "C#",
                xml: "XML",
                bsh: "Shell",
                other: "Other"
            },
            expression: {
                kiss: "j_0001.gif",
                love: "j_0002.gif",
                yeah: "j_0003.gif",
                aaa: "j_0004.gif",
                beiniu: "j_0005.gif",
                ding: "j_0006.gif",
                douxiong: "j_0007.gif",
                byby: "j_0008.gif",
                han: "j_0009.gif",
                keshui: "j_0010.gif",
                lula: "j_0011.gif",
                paizhuan: "j_0012.gif",
                roulian: "j_0013.gif",
                shengrikuaile: "j_0014.gif",
                daxiao: "j_0015.gif",
                pubuhan: "j_0016.gif",
                jingya: "j_0017.gif",
                choumei: "j_0018.gif",
                shaxiao: "j_0019.gif",
                paomeiyan: "j_0020.gif",
                fanu: "j_0021.gif",
                dajiangyou: "j_0022.gif",
                fuwocheng: "j_0023.gif",
                qifen: "j_0024.gif",
                jiong: "j_0025.gif",
                wen: "j_0026.gif",
                nu: "j_0027.gif",
                shengli: "j_0028.gif",
                hi: "j_0029.gif",
                kiss2: "j_0030.gif",
                bushuo: "j_0031.gif",
                buyao: "j_0032.gif",
                chehua: "j_0033.gif",
                daxin: "j_0034.gif",
                woding: "j_0035.gif",
                dajing: "j_0036.gif",
                feiwen: "j_0037.gif",
                guilian: "j_0038.gif",
                haixiu: "j_0039.gif",
                koushui: "j_0040.gif",
                kuangku: "j_0041.gif",
                lai: "j_0042.gif",
                facai: "j_0043.gif",
                chixigua: "j_0044.gif",
                taolao: "j_0045.gif",
                haixiu2: "j_0046.gif",
                qingzhu: "j_0047.gif",
                wolaile: "j_0048.gif",
                qiaoda: "j_0049.gif",
                yunle: "j_0050.gif",
                shengli2: "j_0051.gif",
                choumei2: "j_0052.gif",
                beidale: "j_0053.gif",
                tanchi: "j_0054.gif",
                yingjie: "j_0055.gif",
                ku: "j_0056.gif",
                weixiao: "j_0057.gif",
                qinwen: "j_0058.gif",
                tiaopi: "j_0059.gif",
                jingkong: "j_0060.gif",
                shuaku: "j_0061.gif",
                fahuo: "j_0062.gif",
                haixiu3: "j_0063.gif",
                hanshui: "j_0064.gif",
                daku: "j_0065.gif",
                deyi: "j_0066.gif",
                bishi: "j_0067.gif",
                kun: "j_0068.gif",
                kuajiang: "j_0069.gif",
                yundao: "j_0070.gif",
                kaixin: "j_0071.gif",
                touxiao: "j_0072.gif",
                daku2: "j_0073.gif",
                dihan: "j_0074.gif",
                tanqi: "j_0075.gif",
                chaozan: "j_0076.gif",
                jiong2: "j_0077.gif",
                feiwen2: "j_0078.gif",
                tianshi: "j_0079.gif",
                sanhua: "j_0080.gif",
                shengqi: "j_0081.gif",
                beiza: "j_0082.gif",
                xiasha: "j_0083.gif",
                suiyitu: "j_0084.gif"
            },
            color: {
                base: ["c00000", "ff0000", "ffc000", "ffff00", "92d050", "00b050", "00b0f0", "0070c0", "002060", "7030a0"],
                topic: [["ffffff", "000000", "eeece1", "1f497d", "4f81bd", "c0504d", "9bbb59", "8064a2", "4bacc6", "f79646"], ["f2f2f2", "7f7f7f", "ddd9c3", "c6d9f0", "dbe5f1", "f2dcdb", "ebf1dd", "e5e0ec", "dbeef3", "fdeada"], ["d8d8d8", "595959", "c4bd97", "8db3e2", "b8cce4", "e5b9b7", "d7e3bc", "ccc1d9", "b7dde8", "fbd5b5"], ["bfbfbf", "3f3f3f", "938953", "548dd4", "95b3d7", "d99694", "c3d69b", "b2a2c7", "92cddc", "fac08f"], ["a5a5a5", "262626", "494429", "17365d", "366092", "953734", "76923c", "5f497a", "31859b", "e36c09"], ["7f7f7f", "0c0c0c", "1d1b10", "0f243e", "244061", "632423", "4f6128", "3f3151", "205867", "974806"]]
            },
            lastfuns: []
        },
        getEditor: function(e, i) {
            for (var n, d, t = document.getElementsByTagName("script"), o = 0; o < t.length; o++) if (n = t[o], (d = document.querySelector ? n.src: n.getAttribute("src", 4)).indexOf("/HandyEditor.") > 0) {
                d = d.substring(0, d.lastIndexOf("/"));
                break
            }
            this.loadcss(d + "/external/css/HandyEditor.css"),
            void 0 !== i && this.extend(this.config, i);
            var a = this.config.skin;
            ".css" == a.substr(a.length - 4) && (this.config.skin = a.substr(0, a.length - 4)),
                this.loadcss(d + "/skin/" + this.config.skin + ".css");
            var r = this.config.externalSkin;
            return "" != r && ".css" == r.substr(r.length - 4) && this.loadcss(this.config.externalSkin),
                this.loadScript(d + "/lang/" + this.config.lang + "/" + this.config.lang + ".js",
                    function() {
                        for (var n in lang) {
                            var d = HE.trim(n.toLowerCase()),
                                t = lang[n];
                            delete lang[n],
                                lang[d] = t
                        }
                        HE.define.lang = lang,
                            lang = null,
                            HE.handyEditor(e, i)
                    }),
                this
        },
        handyEditor: function(id, options) {
            var hetxt = document.getElementById(id);
            this.define.id = hetxt.id,
                hetxt.style.display = "none";
            var hediv = document.createElement("div");
            hediv.className = "HandyEditor",
                hediv.style.width = this.config.width;
            var mkdiv = document.createElement("div"),
                mdiv = document.createElement("div");
            mdiv.id = "HandyEditor_menu",
                mdiv.className = "HandyEditor_menu",
                mdiv.style.position = "relative",
                mdiv.style.outline = "none",
                mdiv.setAttribute("tabindex", 0);
            var menu = "";
            for (var i in this.config.item) {
                var icon = this.trim(this.config.item[i].toLowerCase());
                menu += "|" == icon ? '<span class="HandyEditor_menu_gap"></span>': '<span class="HandyEditor_menu_item" id="HandyEditor_' + this.define.id + "_" + icon + '" style="display: inline-block;" title="' + this.define.lang[icon] + '" draggable="true">' + this.define.item[icon] + "</span>"
            }
            mdiv.innerHTML = menu;
            var ediv = document.createElement("div");
            ediv.className = "HandyEditor_editor",
                ediv.setAttribute("contenteditable", !0),
                this.config.autoHeight ? (ediv.style.minHeight = this.config.height, ediv.style.height = "auto", ediv.style.overflowY = "hidden") : (ediv.style.height = this.config.height, ediv.style.overflow = "auto"),
                this.define.obj = ediv,
                hediv.appendChild(mkdiv);
            var mwdiv = document.createElement("div");
            for (var i in mwdiv.style.zIndex = "9000",
                this.define.mwobj = mwdiv,
                mwdiv.appendChild(mdiv), hediv.appendChild(mwdiv), this.define.mobj = mdiv, hediv.appendChild(ediv), this.define.hobj = hediv, heparent = hetxt.parentNode, heparent.insertBefore(hediv, hetxt), this.define.obj.innerHTML = hetxt.value, this.define.hetxt = hetxt, this.config.item) {
                var icon = this.trim(this.config.item[i].toLowerCase());
                if ("|" != icon) {
                    var listen = document.getElementById("HandyEditor_" + this.define.id + "_" + icon);
                    listen.onclick = this[icon]
                }
            }
            this.define.obj.onclick = this.editorClick,
                this.define.mobj.onclick = this.menukongClick,
            this.config.autoFloat && (window.onscroll = function() {
                document.compatMode && "BackCompat" != document.compatMode ? HE.define.scrollPos = document.documentElement.scrollTop: document.body && (HE.define.scrollPos = document.body.scrollTop);
                var e = HE.getTop(HE.define.hobj) - parseInt(HE.config.topOffset),
                    i = HE.getTop(HE.define.hobj) + HE.define.hobj.offsetHeight - HE.define.mobj.offsetHeight - parseInt(HE.config.topOffset);
                HE.define.scrollPos > i && 2 != HE.define.mtop ? (HE.define.mwobj.style.width = "100%", mwdiv.style.position = "relative", mwdiv.style.top = "0px", mkdiv.style.height = "0px", HE.define.mtop = 2) : HE.define.scrollPos > e && HE.define.scrollPos <= i && 1 != HE.define.mtop ? (HE.define.mwobj.style.width = HE.define.hobj.clientWidth + "px", mwdiv.style.position = "fixed", mwdiv.style.top = HE.config.topOffset + "px", HE.define.mheight = HE.define.mobj.offsetHeight, mkdiv.style.height = HE.define.mheight + "px", HE.define.mtop = 1) : HE.define.scrollPos <= e && 0 != HE.define.mtop && (HE.define.mwobj.style.width = "100%", mwdiv.style.position = "relative", mwdiv.style.top = "0px", mkdiv.style.height = "0px", HE.define.mtop = 0)
            }),
                this.define.obj.onkeydown = function(e) {
                    var i = e || window.event;
                    if (13 == (i.keyCode || i.which || i.charCode)) {
                        HE.informat(["li", "pre", "h1", "h2", "h3", "h4", "h5", "h6"]) || HE.handy("formatBlock", "<p>", !0)
                    }
                    if (i.altKey && i.shiftKey) if (document.getElementById("HandyEditor_infodiv")) HE.define.mobj.removeChild(document.getElementById("HandyEditor_infodiv"));
                    else {
                        var n = document.createElement("div");
                        n.className = "HandyEditor_infodiv",
                            n.id = "HandyEditor_infodiv";
                        var d = "<div><h3>HandyEditor</h3>" + HE.define.lang.version + "：" + HE.define.version + "<p>" + HE.define.lang.cbefore + String.fromCharCode(60, 97, 32, 104, 114, 101, 102, 61, 34, 104, 116, 116, 112, 58, 47, 47, 119, 119, 119, 46, 99, 97, 116, 102, 105, 115, 104, 45, 99, 109, 115, 46, 99, 111, 109, 47, 34, 32, 116, 97, 114, 103, 101, 116, 61, 34, 95, 98, 108, 97, 110, 107, 34, 62, 67, 97, 116, 102, 105, 115, 104, 32, 67, 77, 83, 60, 47, 97, 62) + HE.define.lang.cafter + "</p></div>";
                        n.innerHTML = d,
                            n.style.position = "absolute",
                            n.style.left = "0px",
                            n.style.border = "solid #ddd 1px",
                            n.style.backgroundColor = "#fdfdfd",
                            n.style.padding = "30px",
                            HE.define.mobj.appendChild(n)
                    }
                },
                this.define.obj.onblur = function(e) {
                    HE.sarea()
                };
            var lastfunslen = HE.define.lastfuns.length;
            if (lastfunslen > 0) for (var i = 0; i < lastfunslen; i++) eval(HE.define.lastfuns[i][0] + "(" + HE.define.lastfuns[i][1] + ")")
        },
        resume: function() {
            HE.define.obj.focus(),
                HE.sarea(!0)
        },
        informat: function(e) {
            for (var i = HE.parentNode(), n = HE.parentNodeName(i), d = !1;
                 "p" != n && "div" != n;) {
                if (HE.inArray(n, e)) {
                    d = !0;
                    break
                }
                i = HE.nextParentNode(i),
                    n = HE.parentNodeName(i)
            }
            return d
        },
        getTop: function(e) {
            for (var i = e.offsetTop; e = e.offsetParent;) i += e.offsetTop;
            return i
        },
        menukongClick: function() {
            document.getElementById("HandyEditor_newdiv") && "HandyEditor_menu" == document.activeElement.id && HE.deldiv()
        },
        extend: function(e, i) {
            for (var n in i) e[n] = i[n];
            return e
        },
        inArray: function(e, i) {
            for (var n = 0; n < i.length; n++) if (e === i[n]) return ! 0;
            return ! 1
        },
        loadScript: function(e, i) {
            var n = document.createElement("script");
            n.type = "text/javascript",
            void 0 !== i && (n.readyState ? n.onreadystatechange = function() {
                "loaded" != n.readyState && "complete" != n.readyState || (n.onreadystatechange = null, i())
            }: n.onload = function() {
                i()
            }),
                n.src = e,
                document.body.appendChild(n)
        },
        loadcss: function(e) {
            var i = document.createElement("link");
            i.type = "text/css",
                i.rel = "stylesheet",
                i.href = e;
            var n = document.getElementsByTagName("head");
            n.length ? n[0].appendChild(i) : document.documentElement.appendChild(i)
        },
        hasClass: function(e, i) {
            return !! e.className && e.className.match(new RegExp("(\\s|^)" + i + "(\\s|$)"))
        },
        addClass: function(e, i) {
            HE.hasClass(e, i) || (e.className += " " + i)
        },
        removeClass: function(e, i) {
            if (HE.hasClass(e, i)) {
                var n = new RegExp("(\\s|^)" + i + "(\\s|$)");
                e.className = e.className.replace(n, " ")
            }
        },
        ie: function() {
            return !! (window.ActiveXObject || "ActiveXObject" in window)
        },
        getId: function(e) {
            return "HandyEditor_" + this.define.id + "_" + e
        },
        findGroup: function(e) {
            for (var i in HE.define.group) if (HE.inArray(e, HE.define.group[i])) return i;
            return ""
        },
        editorClick: function() {
            document.getElementById("HandyEditor_newdiv") && HE.deldiv(),
            1 == HE.define.pre && (HE.define.pre = !1);
            for (var e, i = ["b", "i", "strike", "u", "em", "strong"], n = [], d = ["bold", "italic", "strike", "underline"], t = HE.parentNode(), o = HE.parentNodeName(t); HE.inArray(o, i);)"strong" != o && "b" != o || (o = "bold"),
            "em" != o && "i" != o || (o = "italic"),
            "u" == o && (o = "underline"),
                n.push(o),
                t = HE.nextParentNode(t),
                o = HE.parentNodeName(t);
            for (var a in d) e = document.getElementById(HE.getId(d[a])),
            HE.hasClass(e, "HandyEditor_menu_item_valid") && HE.removeClass(e, "HandyEditor_menu_item_valid");
            if (n.length > 0) for (var a in n) e = document.getElementById(HE.getId(n[a])),
            HE.hasClass(e, "HandyEditor_menu_item_valid") || HE.addClass(e, "HandyEditor_menu_item_valid");
            HE.sarea()
        },
        parentNodeName: function(e) {
            return e.tagName.toLowerCase()
        },
        nextParentNode: function(e) {
            return window.getSelection ? e.parentNode: document.selection ? e.parentElement() : void 0
        },
        parentNode: function() {
            if (window.getSelection) {
                var e = window.getSelection().getRangeAt(0).startContainer;
                return 3 == e.nodeType ? e.parentNode: null
            }
            if (document.selection) return document.selection.createRange().parentElement()
        },
        abolish: function() {
            var e = HE.findGroup(HE.define.click);
            for (var i in HE.define.group[e]) {
                var n = document.getElementById(HE.getId(HE.define.group[e][i]));
                HE.hasClass(n, "HandyEditor_menu_item_valid") && (n.click(), HE.removeClass(n, "HandyEditor_menu_item_valid"))
            }
        },
        valid: function(e) {
            var i = document.getElementById(HE.getId(e)),
                n = HE.findGroup(e);
            if ("" != HE.define.click && HE.define.click != e && !HE.inArray(HE.define.click, HE.define.group[n])) {
                var d = HE.findGroup(HE.define.click);
                for (var t in HE.define.group[d]) {
                    var o = document.getElementById(HE.getId(HE.define.group[d][t]));
                    o && HE.hasClass(o, "HandyEditor_menu_item_valid") && HE.removeClass(o, "HandyEditor_menu_item_valid")
                }
            }
            HE.hasClass(i, "HandyEditor_menu_item_valid") ? (HE.removeClass(i, "HandyEditor_menu_item_valid"), HE.define.click = "") : (HE.addClass(i, "HandyEditor_menu_item_valid"), HE.define.click = e)
        },
        newdiv: function(e) {
            if (HE.define.mobj.blur(), document.getElementById("HandyEditor_newdiv")) HE.deldiv();
            else {
                var i = document.getElementById(HE.getId(e)),
                    n = document.createElement("div");
                if (n.className = "HandyEditor_newdiv", n.id = "HandyEditor_newdiv", "fontsize" == e) for (var d = [14, 16, 18, 24, 32, 48], t = '<div class="HandyEditor_newdiv_down" id="HandyEditor_newdiv_down_1" style="font-size:10px;" draggable="true">' + HE.define.lang.fontsize + "</div>", o = 0; o < d.length; o++) t += '<div class="HandyEditor_newdiv_gap"></div><div class="HandyEditor_newdiv_down" id="HandyEditor_newdiv_down_' + (o + 2) + '" style="font-size:' + d[o] + 'px;" draggable="true">' + HE.define.lang.fontsize + "</div>";
                else if ("fontname" == e) {
                    t = "";
                    for (var a in HE.define.font) t += "" == t ? '<div class="HandyEditor_newdiv_down" id="HandyEditor_newdiv_down_' + a + '" style="font-family:' + HE.define.font[a] + ';" draggable="true">' + HE.define.lang[a] + "</div>": '<div class="HandyEditor_newdiv_gap"></div><div class="HandyEditor_newdiv_down" id="HandyEditor_newdiv_down_' + a + '" style="font-family:' + HE.define.font[a] + ';" draggable="true">' + HE.define.lang[a] + "</div>"
                } else if ("paragraph" == e) {
                    for (t = "", o = 1; o < 7; o++) t += "" == t ? '<div class="HandyEditor_newdiv_down" id="HandyEditor_newdiv_down_paragraph_' + o + '" draggable="true"><h' + o + ">" + HE.define.lang.biaoti + "</h" + o + "></div>": '<div class="HandyEditor_newdiv_gap"></div><div class="HandyEditor_newdiv_down" id="HandyEditor_newdiv_down_paragraph_' + o + '" draggable="true"><h' + o + ">" + HE.define.lang.biaoti + "</h" + o + "></div>";
                    t += '<div class="HandyEditor_newdiv_gap"></div><div class="HandyEditor_newdiv_down" id="HandyEditor_newdiv_down_paragraph_7" draggable="true"><p>' + HE.define.lang.zhengwen + "</p></div>"
                } else if ("color" == e) {
                    var r = HE.getColor();
                    t = "<div>" + HE.define.lang.color + ': <input style="border : solid 2px #ddd;padding: 5px;" type="text" id="HandyEditor_newdiv_down_color" /><span class="HandyEditor_menu_item" id="HandyEditor_newdiv_down_colorok" style="display: inline-block;margin-left: 10px;" draggable="true">' + HE.define.lang.affirm + "</span></div><div>" + HE.define.lang.basecolor + '</div><div class="HandyEditor_newdiv_gap"></div><div>' + r[0] + "</div><div>" + HE.define.lang.topicolor + '</div><div class="HandyEditor_newdiv_gap"></div><div>' + r[1] + "</div>"
                } else if ("backcolor" == e) r = HE.getColor(),
                    t = "<div>" + HE.define.lang.backcolor + ': <input style="border : solid 2px #ddd;padding: 5px;" type="text" id="HandyEditor_newdiv_down_backcolor" /><span class="HandyEditor_menu_item" id="HandyEditor_newdiv_down_backcolorok" style="display: inline-block;margin-left: 10px;" draggable="true">' + HE.define.lang.affirm + "</span></div><div>" + HE.define.lang.basecolor + '</div><div class="HandyEditor_newdiv_gap"></div><div>' + r[0] + "</div><div>" + HE.define.lang.topicolor + '</div><div class="HandyEditor_newdiv_gap"></div><div>' + r[1] + "</div>";
                else if ("link" == e) t = "<div>" + HE.define.lang.urladdr + ': <input style="border : solid 1px #ddd;padding: 5px;" type="text" id="HandyEditor_newdiv_down_link" /><span class="HandyEditor_menu_item" id="HandyEditor_newdiv_down_linkok" style="display: inline-block;margin-left: 10px;" draggable="true">' + HE.define.lang.affirm + "</span></div>";
                else if ("code" == e) {
                    t = "";
                    for (var a in HE.define.code) t += "" == t ? '<div class="HandyEditor_newdiv_down" id="HandyEditor_newdiv_down_' + a + '" draggable="true">' + HE.define.code[a] + "</div>": '<div class="HandyEditor_newdiv_gap"></div><div class="HandyEditor_newdiv_down" id="HandyEditor_newdiv_down_' + a + '" draggable="true">' + HE.define.code[a] + "</div>"
                } else if ("image" == e) {
                    t = "<div>" + HE.define.lang.imgaddr + ': <input style="border : solid 1px #ddd;padding: 5px;" type="text" id="HandyEditor_newdiv_down_image" /><span class="HandyEditor_menu_item" id="HandyEditor_newdiv_down_imageok" style="display: inline-block;margin-left: 10px;" draggable="true">' + HE.define.lang.affirm + "</span></div>";
                    if (1 == HE.config.uploadPhoto) {
                        var l = "",
                            s = HE.config.uploadPhotoType.split(",");
                        for (var a in s)"" == l ? l = "image/" + HE.trim(s[a]) : l += ",image/" + HE.trim(s[a]);
                        t += '<div><input id="HandyEditor_newdiv_down_upload_input" type="file" accept="' + l + '" name="file" style="display:none;"><span class="HandyEditor_menu_item" id="HandyEditor_newdiv_down_upload" style="display: inline-block;margin-left: 10px;">' + HE.define.lang.uploadimage + '</span><span id="HandyEditor_newdiv_down_upload_percent" style="display: inline-block;margin-left: 10px;"></span></div>'
                    }
                } else if ("expression" == e) {
                    t = "";
                    var f = "",
                        c = 7,
                        E = 34;
                    for (var a in HE.define.expression) E -= 35,
                        c > 0 ? (f += '<span id="HandyEditor_newdiv_down_expression_' + a + '" class="HandyEditor_newdiv_down_expression" style="background-position: left ' + E + 'px;" title="' + HE.define.lang[a] + '" draggable="true"><img class="HandyEditor_newdiv_down_expression_img" src="' + HE.define.expressionPath + HE.define.expression[a] + '"/></span>', c--) : (t += "<div>" + f + "</div>", f = '<span id="HandyEditor_newdiv_down_expression_' + a + '" class="HandyEditor_newdiv_down_expression" style="background-position: left ' + E + 'px;" title="' + HE.define.lang[a] + '" draggable="true"><img class="HandyEditor_newdiv_down_expression_img" src="' + HE.define.expressionPath + HE.define.expression[a] + '"/></span>', c = 6);
                    t = '<div style="overflow-y:auto;overflow-x:hidden;max-height: 295px;width: 320px;padding: 0px;">' + (t += "<div>" + f + "</div>") + "</div>"
                } else if ("about" == e) t = "<div><h3>HandyEditor</h3>" + HE.define.lang.version + "：" + HE.define.version + "<p>" + HE.define.lang.cbefore + '<a href="http://www.catfish-cms.com/" target="_blank">Catfish CMS</a>' + HE.define.lang.cafter + "</p></div>";
                n.innerHTML = t,
                    n.style.top = i.offsetTop + i.offsetHeight + "px",
                    HE.define.mobj.appendChild(n);
                var H = i.offsetLeft + i.offsetWidth / 2 - n.offsetWidth / 2;
                if (i.offsetLeft < n.offsetWidth / 2 && (H = 5), HE.define.mobj.offsetWidth - i.offsetLeft - i.offsetWidth < n.offsetWidth / 2 && (H = HE.define.mobj.offsetWidth - n.offsetWidth - 5), n.style.left = H + "px", n.onclick = function() {
                    HE.define.mobj.blur()
                },
                "fontsize" == e) for (o = 1; o < 8; o++) {
                    document.getElementById("HandyEditor_newdiv_down_" + o).onclick = HE.fontsizeEx
                } else if ("fontname" == e) for (var a in HE.define.font) {
                    document.getElementById("HandyEditor_newdiv_down_" + a).onclick = HE.fontnameEx
                } else if ("paragraph" == e) for (o = 1; o < 8; o++) {
                    document.getElementById("HandyEditor_newdiv_down_paragraph_" + o).onclick = HE.paragraphEx
                } else if ("color" == e) {
                    for (var a in HE.define.color.base) {
                        document.getElementById("HandyEditor_newdiv_down_colorblock_base_" + HE.define.color.base[a]).onclick = function() {
                            var e = document.getElementById("HandyEditor_newdiv_down_color");
                            e.value = "#" + this.title,
                                e.style.borderColor = "#" + this.title
                        }
                    }
                    for (var a in HE.define.color.topic) for (var o in HE.define.color.topic[a]) {
                        document.getElementById("HandyEditor_newdiv_down_colorblock_topic_" + a + "_" + HE.define.color.topic[a][o]).onclick = function() {
                            var e = document.getElementById("HandyEditor_newdiv_down_color");
                            e.value = "#" + this.title,
                                e.style.borderColor = "#" + this.title
                        }
                    }
                    document.getElementById("HandyEditor_newdiv_down_colorok").onclick = HE.colorokEx
                } else if ("backcolor" == e) {
                    for (var a in HE.define.color.base) {
                        document.getElementById("HandyEditor_newdiv_down_colorblock_base_" + HE.define.color.base[a]).onclick = function() {
                            var e = document.getElementById("HandyEditor_newdiv_down_backcolor");
                            e.value = "#" + this.title,
                                e.style.borderColor = "#" + this.title
                        }
                    }
                    for (var a in HE.define.color.topic) for (var o in HE.define.color.topic[a]) {
                        document.getElementById("HandyEditor_newdiv_down_colorblock_topic_" + a + "_" + HE.define.color.topic[a][o]).onclick = function() {
                            var e = document.getElementById("HandyEditor_newdiv_down_backcolor");
                            e.value = "#" + this.title,
                                e.style.borderColor = "#" + this.title
                        }
                    }
                    document.getElementById("HandyEditor_newdiv_down_backcolorok").onclick = HE.backcolorokEx
                } else if ("link" == e) {
                    document.getElementById("HandyEditor_newdiv_down_linkok").onclick = HE.linkokEx
                } else if ("code" == e) for (var a in HE.define.code) {
                    document.getElementById("HandyEditor_newdiv_down_" + a).onclick = HE.codeEx
                } else if ("image" == e) {
                    if (document.getElementById("HandyEditor_newdiv_down_imageok").onclick = HE.imageokEx, 1 == HE.config.uploadPhoto) {
                        document.getElementById("HandyEditor_newdiv_down_upload").onclick = HE.imageupload;
                        var u = document.getElementById("HandyEditor_newdiv_down_upload_input");
                        u.onchange = function() {
                            var e = 1,
                                i = u.value.lastIndexOf("."),
                                n = u.value.substr(i + 1),
                                d = HE.config.uploadPhotoType.split(",");
                            for (var t in d) if (HE.trim(d[t].toLowerCase()) == n.toLowerCase()) {
                                e = 0;
                                break
                            }
                            var o = parseInt(HE.config.uploadPhotoSize);
                            0 != o && (o *= 1024) < u.files[0].size && (e = 2),
                                0 == e ? HE.uploadFile(u.files[0]) : 1 == e ? alert(HE.config.uploadPhotoTypeError) : 2 == e && alert(HE.config.uploadPhotoSizeError)
                        }
                    }
                } else if ("expression" == e) for (var a in HE.define.expression) {
                    document.getElementById("HandyEditor_newdiv_down_expression_" + a).onclick = HE.expressionEx
                }
            }
        },
        imageupload: function() {
            document.getElementById("HandyEditor_newdiv_down_upload_input").click()
        },
        getColor: function() {
            var e = "";
            for (var i in HE.define.color.base) e += '<span id="HandyEditor_newdiv_down_colorblock_base_' + HE.define.color.base[i] + '" class="HandyEditor_newdiv_down_colorblock" style="background-color:#' + HE.define.color.base[i] + '" title="' + HE.define.color.base[i] + '"> </span>';
            var n = "";
            for (var i in HE.define.color.topic) {
                var d = "";
                for (var t in HE.define.color.topic[i]) d += '<span id="HandyEditor_newdiv_down_colorblock_topic_' + i + "_" + HE.define.color.topic[i][t] + '" class="HandyEditor_newdiv_down_colorblock" style="background-color:#' + HE.define.color.topic[i][t] + '" title="' + HE.define.color.topic[i][t] + '"> </span>';
                n += "<div>" + d + "</div>"
            }
            return [e, n]
        },
        deldiv: function() {
            HE.define.mobj.removeChild(document.getElementById("HandyEditor_newdiv"))
        },
        sarea: function(e) {
            var i;
            e = e || !1,
                window.getSelection ? (i = window.getSelection(), e ? (i.removeAllRanges(), i.addRange(HE.define.select)) : HE.define.select = i.getRangeAt(0)) : e ? (document.body.createTextRange().select(), document.selection.empty(), document.selection.addRange(HE.define.select)) : HE.define.select = document.selection.createRange(),
            e && (HE.define.select = null)
        },
        addbr: function() {
            var e = document.createElement("br");
            HE.define.obj.appendChild(e)
        },
        trim: function(e) {
            return e.replace(/(^\s*)|(\s*$)/g, "")
        },
        handy: function(e, i, n) {
            1 == (n = n || !1) ? HE.define.obj.focus() : HE.resume(),
                i = i || null,
                document.execCommand(e, !1, i)
        },
        getText: function() {
            return 1 == HE.define.html && (HE.define.obj.innerHTML = HE.define.tarobj.value),
                HE.define.obj.innerText
        },
        getHtml: function() {
            return 1 == HE.define.html && (HE.define.obj.innerHTML = HE.define.tarobj.value),
                HE.define.obj.innerHTML
        },
        set: function(e) {
            void 0 === HE.define.id ? HE.define.lastfuns.push(["HE.doset", "'" + e.replace(/\\/g, "\\\\").replace(/([^\\]?)\'/g, "$1\\'") + "'"]) : HE.doset(e)
        },
        doset: function(e) {
            1 == HE.define.html && (HE.define.tarobj.value = e),
                HE.define.hetxt.value = e,
                HE.define.obj.innerHTML = e
        },
        sync: function() {
            1 == HE.define.html && (HE.define.obj.innerHTML = HE.define.tarobj.value),
                HE.define.hetxt.value = HE.define.obj.innerHTML
        },
        show: function() {
            1 == HE.define.html && (HE.define.tarobj.value = HE.define.hetxt.value),
                HE.define.obj.innerHTML = HE.define.hetxt.value
        },
        clean: function() {
            HE.define.obj.innerHTML = "",
                HE.define.hetxt.value = "",
                HE.define.tarobj.value = ""
        },
        bold: function() {
            HE.valid("bold"),
                HE.handy("bold")
        },
        italic: function() {
            HE.valid("italic"),
                HE.handy("italic")
        },
        strike: function() {
            HE.valid("strike"),
                HE.handy("strikeThrough")
        },
        underline: function() {
            HE.valid("underline"),
                HE.handy("underline")
        },
        fontsize: function() {
            HE.newdiv("fontsize")
        },
        fontname: function() {
            HE.newdiv("fontname")
        },
        color: function() {
            HE.newdiv("color")
        },
        backcolor: function() {
            HE.newdiv("backcolor")
        },
        center: function() {
            HE.handy("justifyCenter")
        },
        left: function() {
            HE.handy("justifyLeft")
        },
        right: function() {
            HE.handy("justifyRight")
        },
        full: function() {
            HE.handy("justifyFull")
        },
        indent: function() {
            HE.handy("indent")
        },
        outdent: function() {
            HE.handy("outdent")
        },
        link: function() {
            HE.newdiv("link")
        },
        unlink: function() {
            HE.handy("unlink")
        },
        selectall: function() {
            HE.handy("selectAll")
        },
        removeformat: function() {
            HE.abolish(),
                HE.handy("removeFormat")
        },
        image: function() {
            HE.newdiv("image")
        },
        expression: function() {
            HE.newdiv("expression")
        },
        subscript: function() {
            HE.valid("subscript"),
                HE.handy("subscript")
        },
        superscript: function() {
            HE.valid("superscript"),
                HE.handy("superscript")
        },
        horizontal: function() {
            HE.handy("insertHorizontalRule")
        },
        orderedlist: function() {
            HE.handy("insertOrderedList")
        },
        unorderedlist: function() {
            HE.handy("insertUnorderedList")
        },
        undo: function() {
            HE.handy("undo")
        },
        redo: function() {
            HE.handy("redo")
        },
        about: function() {
            HE.newdiv("about")
        },
        fontsizeEx: function() {
            var e = this.id.split("_");
            HE.deldiv(),
                HE.handy("fontSize", parseInt(e[e.length - 1]))
        },
        fontnameEx: function() {
            var e = this.id.split("_");
            HE.deldiv(),
                HE.handy("fontName", HE.define.font[e[e.length - 1]])
        },
        cut: function() {
            HE.handy("cut")
        },
        copy: function() {
            HE.handy("copy")
        },
        paste: function() {
            HE.handy("paste")
        },
        trash: function() {
            HE.handy("delete")
        },
        linkokEx: function() {
            var e = document.getElementById("HandyEditor_newdiv_down_link");
            HE.deldiv(),
                HE.handy("createLink", e.value)
        },
        imageokEx: function() {
            var e = document.getElementById("HandyEditor_newdiv_down_image");
            HE.deldiv(),
                HE.handy("insertImage", e.value)
        },
        uploadFile: function(e) {
            var i = document.getElementById("HandyEditor_newdiv_down_upload_percent"),
                n = new FormData;
            n.append("file", e);
            var d = HE.config.uploadParam;
            for (var t in d) n.append(t, d[t]);
            var o = new XMLHttpRequest;
            o.open("post", HE.config.uploadPhotoHandler, !0),
                o.upload.addEventListener("progress",
                    function(e) {
                        if (e.lengthComputable) {
                            var n = (e.loaded / e.total * 100).toFixed(0);
                            i.innerHTML = n + "%"
                        }
                    },
                    !1),
                o.addEventListener("readystatechange",
                    function() {
                        var e = o;
                        if (4 == e.readyState) if (200 == e.status) {
                            setTimeout(function() {
                                    i.innerHTML = "",
                                        document.getElementById("HandyEditor_newdiv_down_imageok").click()
                                },
                                300);
                            var n = e.response.replace("\\", "/");
                            if (/^(http:\/\/|https:\/\/|\/)?(([\w\-]+\.)*[\w\-]+(\:\d+)?\/)?([\w\-]+\/)*[\w\-]+\.[A-Za-z]{2,4}$/i.test(n)) document.getElementById("HandyEditor_newdiv_down_image").value = n;
                            else alert(e.response)
                        } else alert(HE.define.lang.uploadimageerror)
                    }),
                o.send(n)
        },
        colorokEx: function() {
            var e = document.getElementById("HandyEditor_newdiv_down_color").value;
            HE.deldiv(),
                HE.handy("foreColor", e)
        },
        backcolorokEx: function() {
            var e = document.getElementById("HandyEditor_newdiv_down_backcolor").value;
            HE.deldiv(),
                HE.handy("backColor", e)
        },
        textblock: function() {
            0 == HE.define.pre ? (HE.handy("formatBlock", "<pre>"), HE.define.pre = !0) : (HE.addbr(), HE.define.pre = !1)
        },
        code: function() {
            0 == HE.define.pre ? (HE.newdiv("code"), HE.define.pre = !0) : (HE.addbr(), HE.define.pre = !1)
        },
        codeEx: function() {
            var e = this.id.split("_"),
                i = e[e.length - 1];
            HE.deldiv();
            for (var n = HE.define.obj.getElementsByTagName("pre"), d = 0; d < n.length; d++) HE.hasClass(n[d], "HandyEditor_newdiv_pre_lock") || HE.addClass(n[d], "HandyEditor_newdiv_pre_lock");
            HE.handy("formatBlock", "<pre>"),
                n = HE.define.obj.getElementsByTagName("pre");
            for (d = 0; d < n.length; d++) HE.hasClass(n[d], "HandyEditor_newdiv_pre_lock") || ("other" == i ? HE.addClass(n[d], "prettyprint") : HE.addClass(n[d], "prettyprint lang-" + i));
            for (d = 0; d < n.length; d++) HE.hasClass(n[d], "HandyEditor_newdiv_pre_lock") && HE.removeClass(n[d], "HandyEditor_newdiv_pre_lock")
        },
        expressionEx: function() {
            var e = this.children[0].getAttribute("src");
            HE.deldiv(),
                HE.handy("insertImage", e)
        },
        paragraph: function() {
            HE.newdiv("paragraph")
        },
        paragraphEx: function() {
            var e = this.id.split("_"),
                i = e[e.length - 1];
            HE.deldiv(),
                7 != i ? HE.handy("formatBlock", "<h" + i + ">") : HE.handy("formatBlock", "<p>")
        },
        html: function() {
            if (0 == HE.define.html) {
                HE.define.select || (HE.define.obj.focus(), HE.sarea());
                var e = document.createElement("div");
                e.style.width = HE.define.obj.offsetWidth - 2 * HE.define.obj.clientLeft + "px",
                    e.style.height = HE.define.obj.clientHeight + "px",
                    e.style.position = "absolute",
                    e.style.top = HE.define.obj.offsetTop + HE.define.obj.clientTop + "px",
                    e.style.left = HE.define.obj.offsetLeft + HE.define.obj.clientLeft + "px",
                    e.style.border = "0px";
                var i = document.createElement("textarea");
                i.className = "HandyEditor_newtextarea_textarea",
                    i.style.width = "100%",
                    i.style.height = "100%",
                    i.style.border = "0px",
                    i.style.padding = "10px",
                    i.style.outline = "none",
                    e.appendChild(i);
                var n = HE.define.hetxt.parentNode;
                for (var d in n.insertBefore(e, HE.define.hetxt), HE.define.taobj = e, HE.define.tarobj = i, HE.define.inh = HE.define.obj.innerHTML, HE.define.inh = HE.define.inh.replace(/<p>(\S)/g, "<p>\n    $1"), HE.define.inh = HE.define.inh.replace(/(\S)<br><\/p>/g, "$1</p>"), HE.define.inh = HE.define.inh.replace(/(\S)<\/p>/g, "$1\n</p>"), HE.define.inh = HE.define.inh.replace(/<\/p><p>/g, "</p>\n<p>"), HE.define.inh = HE.define.inh.replace(/<\/p></g, "</p>\n<"), HE.define.inh = HE.define.inh.replace(/><p>/g, ">\n<p>"), HE.define.inh = HE.define.inh.replace(/">(\S)/g, '">\n$1'), HE.define.inh = HE.define.inh.replace(/(\S)<\/pre>/g, "$1\n</pre>"), HE.define.inh = HE.define.inh.replace(/><li>/g, ">\n    <li>"), HE.define.inh = HE.define.inh.replace(/><\/ol>/g, ">\n</ol>"), HE.define.inh = HE.define.inh.replace(/><div>/g, ">\n<div>"), HE.define.tarobj.value = HE.define.inh, HE.define.html = !0, HE.hasClass(this, "HandyEditor_menu_item_valid") || HE.addClass(this, "HandyEditor_menu_item_valid"), HE.config.item) {
                    if ("|" != (t = HE.trim(HE.config.item[d].toLowerCase())) && "html" != t && "about" != t)(o = document.getElementById("HandyEditor_" + HE.define.id + "_" + t)).onclick = null,
                    HE.hasClass(o, "HandyEditor_menu_item_invalid") || HE.addClass(o, "HandyEditor_menu_item_invalid")
                }
                HE.define.tarobj.onclick = function() {
                    document.getElementById("HandyEditor_newdiv") && HE.deldiv()
                }
            } else {
                HE.define.inh != HE.define.tarobj.value && (HE.define.obj.innerHTML = HE.define.tarobj.value);
                n = HE.define.taobj.parentNode;
                for (var d in n.removeChild(HE.define.taobj), HE.define.html = !1, HE.define.obj.focus(), HE.sarea(!0), HE.hasClass(this, "HandyEditor_menu_item_valid") && HE.removeClass(this, "HandyEditor_menu_item_valid"), HE.config.item) {
                    var t, o;
                    if ("|" != (t = HE.trim(HE.config.item[d].toLowerCase())) && "html" != t && "about" != t)(o = document.getElementById("HandyEditor_" + HE.define.id + "_" + t)).onclick = HE[t],
                    HE.hasClass(o, "HandyEditor_menu_item_invalid") && HE.removeClass(o, "HandyEditor_menu_item_invalid")
                }
            }
        }
    }
} ();