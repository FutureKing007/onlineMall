/** 定义控制器层 */
app.controller('userController', function($scope,$timeout,baseService){
    $scope.user = {};

    $scope.save = function () {
        if (!$scope.user.username) {
            alert("用户名不能为空");
            return;
        }

        if (!$scope.user.password) {
            alert("密码不能为空");
            return;
        }

        if ($scope.user.password != $scope.password) {
            alert("两次密码不一致")
            return;
        }

        baseService.sendPost("/user/save?smsCode="+$scope.smsCode , $scope.user)
            .then(function (response) {
                if (response.data) {
                    alert("注册成功");
                    $scope.user = {};
                    $scope.password = "";
                    $scope.smsCode = "";
                } else {
                    alert("注册失败")
                }
            });
    };

    $scope.disabled = false;
    $scope.tip = "获取短信验证码";

    $scope.sendCode = function () {
        $scope.disabled = true;
        $scope.countDown(90);
        if ($scope.user.phone) {
            baseService.sendPost("/user/sendCode?phone=" + $scope.user.phone)
                .then(function (response) {
                    alert(response.data ? "发送成功" : "发送失败")
                });
        } else {
            alert("请输入手机号码")
        }
    };
    
    //倒计时
    $scope.countDown = function (seconds) {
        seconds --;
        var timer = $timeout(function () {
            $scope.countDown(seconds);
        },1000);
        $scope.tip = seconds + " 秒后,可重新发送";
        if(seconds <= 0) {
            $scope.disabled = false;
            $scope.tip = "获取短信验证码";
            $timeout.cancel(timer);
        }
    }
});