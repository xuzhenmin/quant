<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>股票价格预测</title>
    <script src="https://cdn.jsdelivr.net/npm/echarts@5.4.3/dist/echarts.min.js"></script>
    <style>
        .container {
            width: 1200px;
            margin: 0 auto;
            padding: 20px;
        }
        .search-box {
            margin-bottom: 20px;
            text-align: center;
        }
        .search-box input {
            width: 200px;
            padding: 8px;
            margin-right: 10px;
        }
        .search-box button {
            padding: 8px 16px;
            background-color: #4CAF50;
            color: white;
            border: none;
            cursor: pointer;
        }
        .search-box button:hover {
            background-color: #45a049;
        }
        #chart {
            width: 100%;
            height: 600px;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="search-box">
            <input type="text" id="stockCode" placeholder="请输入股票代码（如：00700.HK）">
            <button onclick="loadData()">查询</button>
        </div>
        <div id="chart"></div>
    </div>

    <script>
        let myChart = echarts.init(document.getElementById('chart'));
        
        function loadData() {
            const stockCode = document.getElementById('stockCode').value;
            if (!stockCode) {
                alert('请输入股票代码');
                return;
            }

            fetch('/stock/prediction/data', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: 'stockCode=' + encodeURIComponent(stockCode)
            })
            .then(response => response.json())
            .then(data => {
                updateChart(data);
            })
            .catch(error => {
                console.error('Error:', error);
                alert('获取数据失败');
            });
        }

        function updateChart(data) {
            const option = {
                title: {
                    text: data.stockCode + ' 价格预测',
                    left: 'center'
                },
                tooltip: {
                    trigger: 'axis',
                    formatter: function(params) {
                        let result = params[0].axisValue + '<br/>';
                        params.forEach(param => {
                            result += param.seriesName + ': ' + param.value.toFixed(2) + '<br/>';
                        });
                        // 添加涨跌幅
                        if (data.changePercents && data.changePercents[params[0].dataIndex] !== undefined) {
                            const changePercent = data.changePercents[params[0].dataIndex];
                            result += '涨跌幅: ' + changePercent.toFixed(2) + '%<br/>';
                        }
                        // 添加预测说明
                        if (data.explanations && data.explanations[params[0].dataIndex]) {
                            result += '<br/>预测说明:<br/>' + data.explanations[params[0].dataIndex].replace(/\n/g, '<br/>');
                        }
                        return result;
                    }
                },
                legend: {
                    data: ['价格', 'EMA5', 'EMA10'],
                    top: 30
                },
                grid: {
                    left: '3%',
                    right: '4%',
                    bottom: '3%',
                    containLabel: true
                },
                xAxis: {
                    type: 'category',
                    data: data.dates,
                    axisLabel: {
                        rotate: 45,
                        formatter: function(value) {
                            // 确保value是字符串类型
                            if (typeof value !== 'string') {
                                return value;
                            }
                            // 如果已经是YYYYMMDD格式，直接返回
                            if (/^\d{8}$/.test(value)) {
                                return value;
                            }
                            // 尝试解析其他格式的日期
                            try {
                                const date = new Date(value);
                                if (isNaN(date.getTime())) {
                                    return value;
                                }
                                return date.getFullYear() + 
                                       String(date.getMonth() + 1).padStart(2, '0') + 
                                       String(date.getDate()).padStart(2, '0');
                            } catch (e) {
                                return value;
                            }
                        }
                    }
                },
                yAxis: {
                    type: 'value',
                    scale: true
                },
                series: [
                    {
                        name: '价格',
                        type: 'line',
                        data: data.prices,
                        smooth: true,
                        itemStyle: {
                            color: function(params) {
                                // 根据是否是预测数据设置不同颜色
                                return data.isHistorical[params.dataIndex] ? '#91cc75' : '#5470c6';
                            }
                        },
                        lineStyle: {
                            color: function(params) {
                                return data.isHistorical[params.dataIndex] ? '#91cc75' : '#5470c6';
                            }
                        }
                    },
                    {
                        name: 'EMA5',
                        type: 'line',
                        data: data.ema5,
                        smooth: true,
                        itemStyle: {
                            color: '#fac858'
                        }
                    },
                    {
                        name: 'EMA10',
                        type: 'line',
                        data: data.ema10,
                        smooth: true,
                        itemStyle: {
                            color: '#ee6666'
                        }
                    }
                ]
            };

            myChart.setOption(option);
        }

        // 初始加载
        window.onload = function() {
            loadData();
        };
    </script>
</body>
</html> 