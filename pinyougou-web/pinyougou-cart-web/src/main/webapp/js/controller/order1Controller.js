app.controller("order1Controller",function($scope,$controller,$interval,$location,baseService) {
    $controller("cartController",{$scope:$scope});

    $scope.findAddressByUser = function () {
        baseService.sendGet("/order/findAddressByUser")
            .then(function (response) {
                $scope.addressList = response.data;
                $scope.addressSelected = response.data[0];
            });
    };

    $scope.selectAddress = function (address) {
        $scope.addressSelected = address;
    };

    $scope.isSelected = function (address) {
        return $scope.addressSelected == address;
    };

    $scope.order = {paymentType:"1"};

    $scope.selectPaymentType = function (paymentType) {
        $scope.order.paymentType = paymentType;
    };

    $scope.saveOrder = function () {
        //设置收件人地址
        $scope.order.receiverAreaName = $scope.addressSelected.address;
        // 设置收件人手机号码
        $scope.order.receiverMobile = $scope.addressSelected.mobile;
        // 设置收件人
        $scope.order.receiver = $scope.addressSelected.contact;

        //发送异步请求
        baseService.sendPost("/order/save", $scope.order)
            .then(function(response) {
                if(response.data) {
                    location.href = "/order/pay.html";
                    // if($scope.order.paymentType == "1") {
                    //     location.href = "/order/paysuccess.html";
                    // }
                }else {
                    alert("订单提交失败...");
                }
        });
    };

    $scope.genPayCode = function () {
        baseService.sendPost("/order/genPayCode").then(function (response) {
            $scope.outTradeNo = response.data.outTradeNo;
            $scope.money = (response.data.totalFee / 100).toFixed(2);
            $scope.codeUrl = response.data.codeUrl;
            //发送异步请求
            //获取订单交易号
            //获取金额
            // var qr = new QRious({
            //     element: document.getElementById("qrious"),
            //     size: 250,
            //     level: 'H',
            //     value: response.data.codeUrl
            // });
            //生成二维码
        });

        //第一个参数: 调的函数
        //第二个参数: 时间间隔
        //第三个参数: 调的次数
        var timer = $interval(function () {
            baseService.sendPost("/order/queryPayStatus?outTradeNo=" + $scope.outTradeNo)
                .then(function (response) {
                    if (response.data.status == 1) {
                        location.href = "/order/paysuccess.html?money=" + $scope.money;
                        $interval.cancel(timer);
                    }
                    if (response.data.status == 3) {
                        location.href = "/order/payfail.html";
                        $interval.cancel(timer);
                    }
                });

        }, 3000, 60);

        timer.then(function () {
            alert("微信的二维码已经失效");
        });
    };

    $scope.getMoney = function () {
        return $location.search().money;
    }

});