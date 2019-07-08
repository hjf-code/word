layui.use(['layer', 'form'], function () {
    let layer = layui.layer;
    let form = layui.form;
    let know = 0;
    let $know = $("#know");
    let $notKnow = $("#notKnow");
    let $next = $("#next");
    let $update = $("#update");
    let $delete = $("#delete");
    let lastColor;
    let nextWord;

    next(2, function (r) {
        return putWord(r);
    });

    /**
     * 获得下一个单词
     *
     * @param know 1: 认识, 0: 不认识, 2: 初始加载
     * @param func 成功的回调函数
     */
    function next(know, func) {
        $.ajax({
            url: "word/next/" + know,
            type: "GET",
            dataType: "json",
            success: function (r) {
                if (!r.code) {
                    func(r);
                } else {
                    layer.msg(r.msg, {icon: 2});
                }
            }
        });
    }

    /**
     * 放置下一个单词
     * @param r ajax返回结果
     * @returns {boolean}
     */
    function putWord(r) {
        // 总数
        let totalCount = r.totalCount;
        // 进度
        let schedules = r.schedules;
        // 下一个单词
        nextWord = r.nextWord;
        if (totalCount === 0) {
            layer.msg("今天没有要背的单词！", {icon: 5});
            return false;
        }

        let lastWidth = 100;
        for (let i = 0; i < schedules.length; i++) {
            let schedule = schedules[i];
            let progress = $("#progress" + i);
            if (schedule === 0) {
                progress.hide();
            } else {
                progress.show();
                let width = Math.floor(schedule * 100 / totalCount);
                if (i !== schedules.length - 1) {
                    lastWidth -= width;
                } else {
                    width = lastWidth;
                }
                progress.text(schedule);
                progress.css("width", width + "%");
                progress.attr("aria-valuenow", width);
            }
        }

        if (nextWord) {
            $know.show();
            $notKnow.show();
            $update.show();
            $delete.show();
            let word = $("#word");
            let translation = $("#translation");
            word.text(nextWord.word);
            translation.css("background-color", "#212529");
            let dayCount = nextWord.dayCount;
            let time = 0;
            switch (dayCount) {
                case 0:
                    time = 1;
                    break;
                case 1:
                    time = 2;
                    break;
                case 2:
                    time = 3;
                    break;
                case 4:
                    time = 4;
                    break;
                case 7:
                    time = 5;
                    break;
                case 15:
                    time = 6;
                    break;
                case 31:
                    time = 7;
                    break;
                case 107:
                    time = 8;
                    break;
            }
            $("#startDate").text(nextWord.startDate.slice(0, 10) + "开始");
            $("#dayCount").text("已过去" + dayCount + "天");
            $("#time").text("当前第" + time + "次背诵");
            $("#sound").text("[" + nextWord.sound + "]");
            translation.text(nextWord.translation);
            $("#remark").text(nextWord.remark ? nextWord.remark : "No pains no gains! ");

            let schedule = nextWord.schedule;
            let color = "secondary";
            switch (schedule) {
                case 0:
                    color = "secondary";
                    break;
                case 1:
                    color = "warning";
                    break;
                case 2:
                    color = "info";
                    break;
                case 4:
                    color = "danger";
                    break;
            }
            let $card = $("#card");
            let $cardFooter = $("#card-footer");
            if (lastColor) {
                $card.removeClass("border-" + lastColor);
                $cardFooter.removeClass("border-" + lastColor);
                word.parent().removeClass("text-" + lastColor + " border-" + lastColor);
            }
            $card.addClass("border-" + color);
            $cardFooter.addClass("border-" + color);
            word.parent().addClass("text-" + color + " border-" + color);

            lastColor = color;
        } else {
            layer.msg("已完成今日任务，真棒！", {icon: 6});
            $update.hide();
            $delete.hide();
        }
    }

    /**
     * 认识或不认识按钮点击
     */
    function knowOrNotKnow() {
        $("#translation").css("background-color", "rgba(255,255,255,0.3)");
        $next.show();
        $know.hide();
        $notKnow.hide();
    }

    $know.click(function () {
        know = 1;
        knowOrNotKnow();
    });

    $notKnow.click(function () {
        know = 0;
        knowOrNotKnow();
    });

    $next.click(function () {
        next(know, function (r) {
            let b = putWord(r);
            $next.hide();
            return b;
        });
    });

    // 可以按左右键操作
    $(document).keydown(function (event) {
        let code = event.key;
        if (code === "ArrowLeft") {
            if (!$notKnow.is(':hidden')) {
                $notKnow.click();
            } else if (!$next.is(':hidden')) {
                $next.click();
            }
        } else if (code === "ArrowRight") {
            if (!$know.is(':hidden')) {
                $know.click();
            } else if (!$next.is(':hidden')) {
                $next.click();
            }
        } else if (code === "Escape") {
            layer.closeAll();
        }
    });

    $("#insert").click(function () {
        // 手动清空id
        $("#id").val("");
        // 模拟点击清空按钮
        $("#restButton").click();
        // 弹出页面层
        layer.open({
            type: 1, // 1表示页面层
            title: '猪猪爱背单词', //标题
            closeBtn: 0, // 去掉关闭按钮
            shade: 0.6, // 遮罩层透明度
            shadeClose: true, // 点击遮罩关闭弹出层
            skin: 'layer-me', // 为弹出层增加样式
            area: ['95%', '77%'], // 宽高
            anim: 4,
            content: $("#addOrEdit") // 页面层内容
        });
    });

    $update.click(function () {
        $.ajax({
            url: "word/one/" + nextWord.id,
            type: "GET",
            dataType: "json",
            success: function (r) {
                let word = r.data;
                form.val("formWord", {
                    "id": word.id,
                    "word": word.word,
                    "sound": word.sound,
                    "translation": word.translation,
                    "remark": word.remark
                });
                layer.open({
                    type: 1, // 1表示页面层
                    title: '猪猪爱背单词', //标题
                    closeBtn: 0, // 去掉关闭按钮
                    shade: 0.6, // 遮罩层透明度
                    shadeClose: true, // 点击遮罩关闭弹出层
                    skin: 'layer-me', // 为弹出层增加样式
                    area: ['95%', '77%'], // 宽高
                    anim: 4,
                    content: $("#addOrEdit") // 页面层内容
                });
            }
        });


    });

    $delete.click(function () {
        layer.confirm('确定删除？', function (index) {
            layer.close(index);
            $.ajax({
                url: "word/one/" + nextWord.id,
                type: "DELETE",
                dataType: "json",
                success: function (r) {
                    if (!r.code) {
                        layer.msg("删除成功！", {icon: 1});
                        putWord(r);
                    } else {
                        layer.msg(r.msg, {icon: 2});
                    }
                }
            });
        });
    });

    form.on('submit(submitBtn)', function (data) {
        let fields = data.field;
        let msg;
        let type;
        if (fields.id) {
            msg = "修改";
            type = "PUT";
        } else {
            msg = "新增";
            type = "POST";
        }
        $.ajax({
            url: "word/one",
            type: type,
            data: JSON.stringify(fields),
            dataType: "json",
            contentType: "application/json; charset=utf-8",
            success: function (r) {
                if (!r.code) {
                    layer.msg(msg + "成功！", {icon: 1});
                    layer.closeAll('page');
                    if (fields.id) {
                        // 若是修改, 则更新当前背诵单词
                        putWord(r);
                    }
                } else {
                    layer.msg(r.msg, {icon: 2});
                }
            }
        });
        return false;
    });
});