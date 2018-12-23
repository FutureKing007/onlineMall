app.controller("loginController",function ($scope,$controller,baseService) {
    /** 指定继承baseController */
    $controller("baseController",{$scope:$scope});

    $scope.loadVCode = function () {
        $scope.vcodeURL = "/code?n=" + Math.random();
        baseService.sendGet($scope.vcodeURL)
    };

    // $scope.submit = function () {
    //     baseService.sendPost("/user/login",$("#loginForm").serialize())
    //         .then(function (response) {
    //                 alert(response);
    //             }
    //         ,function (response) {
    //         alert("系统出错")
    //     })
    // };

    $scope.submit = function () {
        // var response =
        // alert(response)
        $("#loginForm").submit()
    }
});