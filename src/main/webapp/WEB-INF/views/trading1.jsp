<%--
  Created by IntelliJ IDEA.
  User: xuxu
  Date: 2025/3/31
  Time: 18:46
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>多维价格趋势分析</title>

<%--    <%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>--%>
<%--    <%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>--%>
<%--    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>--%>
<%--    <%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>--%>
<%--    <%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>--%>
<%--    <%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>--%>
<%--    <c:set var="ctx" value="${pageContext.request.contextPath}"/>--%>
<%--    <c:set var="datePattern">MM/dd/yyyy</c:set>--%>

<%--    <script src="${ctx }/resources/jquery-1.9.1.min.js" type="text/javascript"></script>--%>

    <script src="https://cdn.jsdelivr.net/npm/echarts@5.4.2/dist/echarts.min.js"></script>
<%--    <script src="${ctx }/resources/echarts.min.js" type="text/javascript" ></script>--%>
    <style>
        /* 保持原有样式不变 */
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            background-color: #f5f7fa;
            font-family: 'Helvetica Neue', Arial, sans-serif;
            padding: 40px 20px;
        }

        .container {
            max-width: 1200px;
            margin: 0 auto;
            background: white;
            border-radius: 12px;
            box-shadow: 0 8px 24px rgba(0, 0, 0, 0.05);
            padding: 40px;
        }

        h1 {
            text-align: center;
            color: #2c3e50;
            margin-bottom: 30px;
            font-weight: 300;
            font-size: 2.4em;
        }

        #chart-container {
            height: 600px;
            min-height: 300px;
        }

        .chart-description {
            text-align: center;
            color: #7f8c8d;
            margin-top: 20px;
            font-size: 0.9em;
        }

        .section {
            background: #f8f9fa;
            border-radius: 12px;
            height: 40px;
            padding: 0px;
            position: relative;
            width: 40%;
            left: 30%;
        }

        /* 输入框容器 */
        .input-container {
            position: relative;
            margin-top: 16px;
        }

        .message-input {
            width: 100%;
            padding: 12px 48px 12px 16px; /* 右侧留出图标空间 */
            border: 1px solid #dee2e6;
            border-radius: 8px;
        }

        /* 发送按钮定位 */
        .send-button {
            position: absolute;
            right: 12px;
            top: 50%;
            transform: translateY(-50%);
            padding: 6px;
            background: none;
            border: none;
            cursor: pointer;
        }

        .send-icon {
            width: 24px;
            height: 24px;
            fill: #0d6efd;
            transition: fill 0.2s;
        }

        .send-button:hover .send-icon {
            fill: #0b5ed7;
        }

        .comment-list {
            background: #f8f9fa;
            padding: 16px;
            border-radius: 8px;
        }

        .comment {
            padding: 12px;
            background: white;
            border-radius: 8px;
            margin-bottom: 12px;
        }

        .status-bar {
            display: flex;
            justify-content: space-between;
            color: #6c757d;
            font-size: 14px;
            margin-bottom: 16px;
        }
    </style>
</head>
<body>
<div class="container">
    <h1>多维价格趋势分析</h1>
    <div class="section">
        <div class="input-container">
            <input type="text" id="symbol" class="message-input" placeholder="输入股票代码,如:00700.HK ...">
            <button class="send-button" onclick="queryLines()" aria-label="发送">
                <svg class="send-icon" viewBox="0 0 24 24">
                    <path d="M2.01 21L23 12 2.01 3 2 10l15 2-15 2z"/>
                </svg>
            </button>
        </div>
    </div>
    <div id="chart-container"></div>
    <p class="chart-description">鼠标悬停查看详细信息 | 双击图例切换显示</p>
</div>

<script>

    function queryLines() {


        option.dataset =
            {
                source: generateTrendData()
            }

        myChart.setOption(option);
    }

    function getLines(symbol) {
        let data = [];
        $.ajax({
            url: '${ctx }/ajax/queryLines',
            type: 'GET',
            async: false,
            contentType: 'application/json',
            data: {symbol: symbol},
            success: function (response) {
                for (let i = 0; i < response.length; i++) {
                    data.push({
                        date: response[i].date,
                        current: response[i].closePrice,
                        predict: response[i].consPrice
                    });
                }
            }
        });
        return data;

    }

    // 改进的模拟数据生成器（带趋势）
    function generateTrendData() {
        let baseDate = +new Date('2024-05-01');
        let data = [];
        // let symbol = $("#symbol").val();
        // console.log("symbol:" + symbol);
        // if (symbol != "") {
        //     return getLines(symbol);
        // }


        let basePrice = 200; // 初始基准价格

        for (let i = 0; i < 60; i++) { // 生成60天数据
            // 基础价格波动（模拟市场变化）
            basePrice += (Math.random() - 0.4) * 8;

            let date = new Date(baseDate + i * 86400000);
            let current = basePrice + Math.random() * 6 - 3;
            let predict = current + (Math.random() * 10 - 2) + i * 0.3;
            let aggressive = current * (1 + i / 200) + Math.random() * 15;
            let conservative = current * (1 + i / 300) + Math.random() * 8;
            let closing = current + Math.random() * 5 - 2.5;
            let settlement = current + (Math.random() * 7 - 3.5);

            data.push({
                date: date,
                current: Number(current.toFixed(2)),
                predict: Number(predict.toFixed(2)),
                aggressive: Number(aggressive.toFixed(2)),
                conservative: Number(conservative.toFixed(2)),
                closing: Number(closing.toFixed(2)),
                settlement: Number(settlement.toFixed(2))
            });
        }
        return data;
    }


    // 图表配置
    let chartDom = document.getElementById('chart-container');
    let myChart = echarts.init(chartDom);

    let option = {
        dataset: {
            source: generateTrendData()
        },
        tooltip: {
            trigger: 'axis',
            backgroundColor: 'rgba(255,255,255,0.95)',
            borderWidth: 0,
            padding: [10, 20],
            formatter: function (params) {
                let date = new Date(params[0].value.date);
                let dateStr = date.toLocaleDateString();
                let res = `<div style="font-weight:600;margin-bottom:8px">${dateStr}</div>`;
                params.forEach(function (item) {
                    res += `<div style="display:flex;align-items:center">
                            <span style="display:inline-block;width:10px;height:10px;background:${item.color};border-radius:50%;margin-right:8px"></span>
                            ${item.seriesName}: <strong>¥${item.value[item.dimensionNames[item.encode.y[0]]]}</strong>
                        </div>`;
                });
                return res;
            }
        },
        legend: {
            top: 30,
            textStyle: {color: '#7f8c8d'}
        },
        xAxis: {
            type: 'time',
            splitLine: {lineStyle: {color: '#f0f0f0'}},
            axisLabel: {
                formatter: function (value) {
                    return new Date(value).toLocaleDateString();
                }
            }
        },
        yAxis: {
            type: 'value',
            axisLabel: {formatter: '¥{value}'},
            splitLine: {lineStyle: {color: '#f0f0f0'}}
        },
        series: [
            createSeries('当前价格', 'current', '#5470c6'),
            createSeries('预测价格', 'predict', '#91cc75', 'dashed'),
            // createSeries('激进价格', 'aggressive', '#fac858'),
            // createSeries('保守价格', 'conservative', '#ee6666'),
            // createSeries('收盘价格', 'closing', '#73c0de'),
            // createSeries('结束价格', 'settlement', '#3ba272')
        ],
        dataZoom: [{type: 'inside', start: 30, end: 70}],
        animationDuration: 2000
    };

    // 系列生成函数
    function createSeries(name, field, color, lineStyle) {
        return {
            name: name,
            type: 'line',
            smooth: true,
            symbol: 'none',
            color: color,
            lineStyle: lineStyle ? {type: lineStyle} : {},
            encode: {
                x: 'date',
                y: field
            },
            showSymbol: false,
            endLabel: {
                show: true,
                formatter: '{a}',
                fontSize: 12,
                color: color
            }
        };
    }

    // 响应式配置
    window.addEventListener('resize', function () {
        myChart.resize();
    });

    option && myChart.setOption(option);
</script>
</body>
</html>

